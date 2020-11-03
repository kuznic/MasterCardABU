package postilion.realtime.mastercardabu;

import postilion.realtime.sdk.jdbc.JdbcManager;
import java.sql.*;
import java.util.*;

/**
 * This utility checks if the required views:
 * pc_cards, pc_card_accounts and pc_accounts
 * are present
 * @author Vin-Anuonye Chukwuemeka
 * @version 1.0
 * @throws SQLException,NullPointerException
 * @since 23-10-2020
 */

public class AbuTables {
    private Connection conn;
    private final Set<String> icaBinsInDiffViews = new HashSet<String>();//Set, to hold bins present in pc_cards_view
    private final Set<String> icBinsToSet = new HashSet<String>();//set to hold bins passed as parameters
    private final Set<String> missingIssuersInDiffViews = new HashSet<String>();//Set, to hold the issuers that need to be added to the different views
    private PreparedStatement fetchIssuer;
    private final Scanner scanner = new Scanner(System.in);

    {
        try {
            conn = JdbcManager.getConnection("postcard");
        } catch (SQLException e) {
            System.out.println( e.getMessage());
        }
    }

    /**
     * This method creates the pc_cards_abu table
     * and stored procedures
     */

    public void createAbuTable()
    {
        try

        {

            conn.setAutoCommit(true);
            System.out.println();
            Statement create_pc_cards_abu_table = conn.createStatement();
            System.out.println("Creating pc_cards_abu table..........");
            create_pc_cards_abu_table.execute(AbuSqlScripts.TBL_PC_CARDS_ABU);
            System.out.println("Creating index ix_pc_cards_abu on pc_cards_abu table..............");
            create_pc_cards_abu_table.execute(AbuSqlScripts.IX_PC_CARDS_ABU);
            System.out.println("Creating index ix_pc_cards_abu_2 on pc_cards_abu table..............");
            create_pc_cards_abu_table.execute(AbuSqlScripts.IX_PC_CARDS_ABU_2);
            System.out.println("Creating get_all_active_cards stored procedure...........");
            create_pc_cards_abu_table.execute(AbuSqlScripts.SP_GET_ALL_ACTIVE_CARDS);
            System.out.println("Creating get_closed_cards stored procedure..........");
            create_pc_cards_abu_table.execute(AbuSqlScripts.SP_GET_CLOSED_CARDS);
            System.out.println("Creating get_all_chained_cards stored procedure........");
            create_pc_cards_abu_table.execute(AbuSqlScripts.SP_GET_ALL_CHAINED_CARDS);
            System.out.println("Creating update_status_chain_and_hierarchy stored procedure........");
            create_pc_cards_abu_table.execute(AbuSqlScripts.SP_UPDATE_STATUS_CHAIN_AND_HIERARCHY);
            System.out.println("Creating update_closed_card_status stored procedure.........");
            create_pc_cards_abu_table.execute(AbuSqlScripts.SP_UPDATE_CLOSED_CARD_STATUS);





        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());

        }
        catch (NullPointerException e){
            System.out.println("Unable to connect to the database, please check that sql service is started");
        }


    }

    /**
     * This method checks for the pc_cards view
     * and indicates which issuers are missing
     */
    public void checkPcCardsView()
    {
        Exception invalidInput = new Exception("Invalid Input found");
        try
        {
            //check to see if pc_cards view exist and if the issuers for the card programs that have the ICA bins are part of the view

            PreparedStatement checkPcCardsView = conn.prepareStatement(AbuSqlScripts.CHECK_PC_CARDS_VIEW);

            //Passing parameters to check if present in the pc_cards view
            System.out.println();
            System.out.println();
            System.out.println("*****************************************************************");
            System.out.println("\tPlease note that only BINs hosted on the bank's");
            System.out.println("\tPostcard database are within scope of this");
            System.out.println("\tABU Solution. Do not pass any BIN that doesn't");
            System.out.println("\texist on the bank's hosted postcard");
            System.out.println("******************************************************************");
            System.out.println();
            System.out.println();



            System.out.println("Enter ICA BINs on bank's postcard separated by commas :");
            String bins = scanner.nextLine();
            String[] ica_bins = bins.split(",");

            //Check that valid input have been provided
            if(ica_bins.length == 0)
            {
                throw invalidInput;

            }
            for(String s: ica_bins)
            {
                if((!s.matches("[0-9]+")) || (s.toCharArray().length < 5))
                {
                    throw invalidInput;
                }
            }

            Collections.addAll(icBinsToSet, ica_bins);//convert bin array to hashed set

            //fetching a bin set from pc_cards view based on passed BINs parameter
            System.out.println();
            System.out.println("Checking if pc_cards view exists..........");
            for (String s : icBinsToSet) {
                checkPcCardsView.setString(1, s);
                ResultSet rs = checkPcCardsView.executeQuery();
                while (rs.next()) {
                    icaBinsInDiffViews.add(rs.getString(1));
                }
            }




            //check if all the issuers for ICA BINs provided are present in the pc_cards view
            if(icaBinsInDiffViews.equals(icBinsToSet) && icBinsToSet.size()> 0)
            {
                System.out.println();
                System.out.println("pc_cards view exists and all the required issuers for the ICA BINs are part of the view");
            }

            else {
                missingIssuersInDiffViews.addAll(icBinsToSet);
                missingIssuersInDiffViews.removeAll(icaBinsInDiffViews);
                fetchIssuer = conn.prepareStatement(AbuSqlScripts.FETCH_ISSUERS);
                for(String s : missingIssuersInDiffViews){
                    fetchIssuer.setString(1,s);
                    ResultSet rn = fetchIssuer.executeQuery();
                    System.out.println("pc_cards view exists...");

                    //check if any BIN that was provided exists in postcard
                    if (!rn.next())
                    {

                        System.out.println("BIN " + s + " does not exist in the postcard database, and will be ignored!");
                    }
                    while (rn.next()){
                        System.out.println("The issuer for BIN " + s + " :issuer_nr " + rn.getString(1) + " is not present in the pc_cards view" );

                    }
                }
            }
            //System.out.println(ica_bins_in_pc_cards.toString());//for testing

        }
        catch(SQLException e){
            if(e.getErrorCode() == 0 && e.getSQLState().equals("24000"))
            {
                System.out.println("Error: pc_cards view does not exist!");
                e.printStackTrace();
            }
        }
        catch (NullPointerException e){
            System.out.println("Unable to connect to the database, please check that sql service is started");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            checkPcCardsView();
        }
    }

    /**
     * This method checks for the pc_card_accounts view
     * and indicates which issuers are missing
     */
    public void checkPcCardAccountsView()
    {
        try
        {
            //check to see if pc_card_accounts view exist and if the issuers for the card programs that have the ICA bins are part of the view
            PreparedStatement checkPcCardAccountsView = conn.prepareStatement(AbuSqlScripts.CHECK_PC_CARD_ACCOUNTS_VIEW);

            //fetching a bin set from pc_card_accounts view based on passed BINs parameter
            icaBinsInDiffViews.clear();//clear after been used in pc_cards_view
            for (String s : icBinsToSet) {
                checkPcCardAccountsView.setString(1, s);
                ResultSet rs = checkPcCardAccountsView.executeQuery();
                while (rs.next()) {
                    icaBinsInDiffViews.add(rs.getString(1));
                }
            }

            //check if all the ICA BINs issuers are present in the pc_cards view
            if(icaBinsInDiffViews.equals(icBinsToSet))
            {
                System.out.println();
                System.out.println("pc_card_accounts view exists and all the required issuers are part of the view");
            }
            else {
                missingIssuersInDiffViews.addAll(icBinsToSet);
                missingIssuersInDiffViews.removeAll(icaBinsInDiffViews);
                for(String s : missingIssuersInDiffViews){
                    fetchIssuer.setString(1,s);
                    ResultSet rn = fetchIssuer.executeQuery();
                    while (rn.next()){
                        System.out.println("The issuer for BIN " + s + " :issuer_nr " + rn.getString(1) + " is not present in the pc_card_accounts view" );

                    }
                }

            }

        }
        catch(SQLException e){
            if(e.getErrorCode() == 0 && e.getSQLState().equals("24000"))
            {
                System.out.println("Error: pc_card_accounts view does not exist!");
            }


        }

    }

    /**
     * This method copies data from pc_cards
     * to the pc_cards_abu table
     */
    public void copyRecordsToAbuTable()
    {
        try
        {
            System.out.println();
            System.out.println("Is this a test or production environment? TEST/PROD?");
            PreparedStatement populatePcCardsAbuTableTest = conn.prepareStatement(AbuSqlScripts.POPULATE_PC_CARDS_ABU_TABLE_TEST);
            PreparedStatement populatePcCardsAbuTableProd = conn.prepareStatement(AbuSqlScripts.POPULATE_PC_CARDS_ABU_TABLE_PROD);
            String typeOfEnvironment = scanner.nextLine().toUpperCase();
            System.out.println();
            if(typeOfEnvironment.equalsIgnoreCase("TEST") || typeOfEnvironment.equalsIgnoreCase("T"))
            {
                System.out.println("Copying data from pc_cards to pc_cards_abu table...............");
                for(String s: icBinsToSet)
                {
                    populatePcCardsAbuTableTest.setString(1,s);
                    System.out.println(populatePcCardsAbuTableTest.executeUpdate() + " records copied!");
                }
            }

            else if(typeOfEnvironment.equalsIgnoreCase("PROD") || typeOfEnvironment.equalsIgnoreCase("P"))
            {
                System.out.println("Copying data from pc_cards to pc_cards_abu table...............");
                for(String s: icBinsToSet)
                {
                    populatePcCardsAbuTableProd.setString(1,s);
                    System.out.println(populatePcCardsAbuTableProd.executeUpdate() + " records copied!");
                }
            }

            else
            {
                throw new Exception("Error:Invalid input!");
            }
        }
        catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            copyRecordsToAbuTable();
        }


    }

    /**
     * This methods creates triggers on the issuers
     * for the ICA BINs
     */

    public void createTriggersOnIcaIssuerTables()
    {
        try
        {
            Set<String> issuersForTrigger = new HashSet<String>();//Set to hold the issuers that the trigger will be created on

            //fetch the issuers that will have triggers created on them
            for(String s: icBinsToSet){
                PreparedStatement getIssuer = conn.prepareStatement(AbuSqlScripts.FETCH_ISSUERS);
                getIssuer.setString(1,s);
                ResultSet rt = getIssuer.executeQuery();

                while(rt.next())
                {
                    issuersForTrigger.add(rt.getString(1));
                }
            }

            //create the triggers
            System.out.println();
            for(String s:issuersForTrigger){
                PreparedStatement dropTrigger = conn.prepareStatement(AbuSqlScripts.DROP_TRIGGERS_PART_1 + s +"_A]"+ AbuSqlScripts.DROP_TRIGGERS_PART_2
                        + "[dbo].[tr_at_update_mastercard_abu_" + s + "_A]" + AbuSqlScripts.DROP_TRIGGERS_PART_3);

                dropTrigger.execute();

                System.out.println("Creating trigger " + "[dbo].[tr_at_update_mastercard_abu_" + s + "_A].......");
                PreparedStatement createTrigger = conn.prepareStatement(AbuSqlScripts.TR_PC_CARDS_PART_1 + s  +"_A]" +
                        AbuSqlScripts.TR_PC_CARDS_PART_2 + s + "_A]"+ AbuSqlScripts.TR_PC_CARDS_PART_3);
                createTrigger.executeUpdate();
            }


        }
        catch(SQLException ex){
            System.out.println("While attempting to create trigger, the following error occurred\n" +
                    " within the create_trigger_on_ICA_issuer_tables method:");
            System.out.println(ex.getMessage());
        }

    }

    /**
     * This method will be used to create the stored procedure that
     * will be used auto-populate the pc_cards_abu table after initial
     * records have been copied
     */
    public void createProcedureToAutoPopulateAbuTable()
    {
        try
        {
            System.out.println();
            PreparedStatement dropSPInsertNewRecordsInABUTable = conn.prepareStatement(AbuSqlScripts.DROP_SP_INSERT_NEW_RECORDS_IN_ABU_TABLE);
            dropSPInsertNewRecordsInABUTable.execute();

            String[] abu_bins= icBinsToSet.toArray(new String[0]);
            StringBuilder bin_buffer = new StringBuilder();

            for(int i = 0; i< abu_bins.length; i++)
            {
                if (i == (abu_bins.length  - 1))
                {
                    bin_buffer.append("'").append(abu_bins[i]).append("'");
                }
                else
                {
                    bin_buffer.append("'").append(abu_bins[i]).append("',");
                }
            }

            System.out.println("Creating stored proc abu_insert_new_records.................");
            PreparedStatement createSPInsertNewRecordsInABUTable = conn.prepareStatement(AbuSqlScripts.SP_INSERT_NEW_RECORDS_IN_ABU_TABLE_PART_1 + bin_buffer
                    + AbuSqlScripts.SP_INSERT_NEW_RECORDS_IN_ABU_TABLE_PART_2);
            createSPInsertNewRecordsInABUTable.execute();

            conn.close();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * This method is used to copy new card records to the pc_cards_abu table
     * on the test environment alone
     *
     */

    public void insertNewRecordsForReasonCodeN(String ICA_bins)
    {
        try
        {
            conn=JdbcManager.getConnection("postcard");
            String [] binList = ICA_bins.split(",");
            //PreparedStatement populatePcCardsAbuTableTestForCodeN = conn.prepareStatement(AbuSqlScripts.populatePcCardsAbuTableTestForCodeN);
            PreparedStatement populatePcCardsAbuTableTestForCodeN = conn.prepareStatement(AbuSqlScripts.POPULATE_PC_CARDS_ABU_TABLE_TEST_FOR_CODE_N);
            for(String s: binList)
            {
                System.out.println("Copying records for BIN " + s + " from pc_cards to pc_cards_abu............");
                populatePcCardsAbuTableTestForCodeN.setString(1,s);
                System.out.println(populatePcCardsAbuTableTestForCodeN.executeUpdate() + " added");
                conn.commit();
            }
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }


    }

    /**
     * This method is used to update the hold_rsp_code
     * of the records in their corresponding issuer tables
     * in order to make records available for the generation of the Closed reason code
     */
    public void updateRecordsForReasonCodeC()
    {
        Map<String,String> issuerPan = new HashMap<String, String>();//This HashMap will hold issuer_nr and pan present in
        try
        {
            conn = JdbcManager.getConnection("postcard");
            PreparedStatement updateRecordsForReasonCodeC = conn.prepareStatement(AbuSqlScripts.FETCH_RECORDS_FROM_PC_CARDS_ABU);
            ResultSet rs = updateRecordsForReasonCodeC.executeQuery();

            while (rs.next())
            {
                issuerPan.put(rs.getString(1), rs.getString(2));
            }
            rs.close();

            for (Map.Entry<String, String> entry : issuerPan.entrySet())
            {
             updateRecordsForReasonCodeC = conn.prepareStatement("update pc_cards_" + entry.getKey() +"_A\n" + "set hold_rsp_code = 41 where pan in ('" +
                     entry.getValue() +"')");
             updateRecordsForReasonCodeC.executeUpdate();

            }
            conn.commit();
            conn.close();


        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    /**
     * This method is used to insert new records
     * in the pc_cards issuer table and pc_cards_abu table
     * for reason code R
     */

    public void insertNewRecordForReasonCodeR()
    {
        try
        {
            Scanner scanner = new Scanner(System.in);
            Map<String, String> issuerPan = new HashMap<String, String>();//hashed map to hold issuer number and new PAN
            String replacementPan = null;//holds new pan
            String panToBeReplaced = null;
            conn = JdbcManager.getConnection("postcard");
            PreparedStatement fetchClosedPan = conn.prepareStatement(AbuSqlScripts.FETCH_CLOSED_RECORD);//fetch a closed record
            ResultSet rs = fetchClosedPan.executeQuery();




            while(rs.next())
            {
                String issuerNumber = rs.getString(1);
                panToBeReplaced = rs.getString(2);
                System.out.println("Provide new pan that will serve as replacement for the following closed record with PAN: " + panToBeReplaced);
                replacementPan = scanner.nextLine();
                issuerPan.put(issuerNumber,replacementPan);
                System.out.println("got here");
            }

            rs.close();

            for (Map.Entry<String, String> entry : issuerPan.entrySet())
            {
                PreparedStatement insertNewCardRecordInIssuerCardTable  = conn.prepareStatement("INSERT INTO PC_CARDS_" + entry.getKey() +"_A" + AbuSqlScripts.INSERT_INTO_PC_CARDS_ISSUER_TABLE);
                insertNewCardRecordInIssuerCardTable.setString(1,replacementPan);
                insertNewCardRecordInIssuerCardTable.setString(2,panToBeReplaced);
                System.out.println();
                System.out.println("Inserting new record in pc_cards_" + entry.getKey() +"_A table.....");
                insertNewCardRecordInIssuerCardTable.executeUpdate();

                PreparedStatement insertNewCardAccountRecord = conn.prepareStatement("INSERT INTO PC_CARD_ACCOUNTS_" + entry.getKey() +"_A"  + AbuSqlScripts.INSERT_INTO_PC_CARD_ACCOUNTS_ISSUER_TABLE);
                insertNewCardAccountRecord.setString(1,replacementPan);
                insertNewCardAccountRecord.setString(2,panToBeReplaced);
                System.out.println();
                System.out.println("Inserting new record in pc_card_accounts_" + entry.getKey() +"_A table.....");
                insertNewCardAccountRecord.executeUpdate();

                PreparedStatement insertNewCardRecordInAbuTable = conn.prepareStatement(AbuSqlScripts.INSERT_INTO_PC_CARDS_ABU_TABLE);
                insertNewCardRecordInAbuTable.setString(1,replacementPan);
                System.out.println();
                System.out.println("Inserting new record in pc_cards_abu table.......");
                insertNewCardRecordInAbuTable.executeUpdate();

            }
            conn.commit();
            conn.close();



        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }


    }
}

