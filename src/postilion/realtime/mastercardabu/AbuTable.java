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

public class AbuTable  {
    private Connection conn;
    private final Set<String> ica_bins_in_diff_views = new HashSet<String>();//Set, to hold bins present in pc_cards_view
    private final Set<String> ic_bins_to_set = new HashSet<String>();//set to hold bins passed as parameters
    private final Set<String> missing_issuers_in_diff_views = new HashSet<String>();//Set, to hold the issuers that need to be added to the different views
    private PreparedStatement fetch_issuer;
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
            create_pc_cards_abu_table.execute(ABUScripts.TABLE_pc_cards_abu);
            System.out.println("Creating index 1 on pc_cards_abi table..............");
            create_pc_cards_abu_table.execute(ABUScripts.IX_pc_cards_abu);
            System.out.println("Creating index 2 on pc_cards_abi table..............");
            create_pc_cards_abu_table.execute(ABUScripts.IX_pc_cards_abu_2);
            System.out.println("Creating get_all_active_cards stored procedure...........");
            create_pc_cards_abu_table.execute(ABUScripts.SP_get_all_active_cards);
            System.out.println("Creating get_closed_cards stored procedure..........");
            create_pc_cards_abu_table.execute(ABUScripts.SP_get_closed_cards);
            System.out.println("Creating get_all_chained_cards stored procedure........");
            create_pc_cards_abu_table.execute(ABUScripts.SP_get_all_chained_cards);
            System.out.println("Creating update_status_chain_and_hierarchy store procedure........");
            create_pc_cards_abu_table.execute(ABUScripts.SP_update_status_chain_and_hierarchy);
            System.out.println("Creating update_closed_card_status stored procedure.........");
            create_pc_cards_abu_table.execute(ABUScripts.SP_update_closed_card_status);





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
    public void check_pc_cards_view()
    {
        Exception invalidInput = new Exception("Invalid Input found");
        try
        {
            //check to see if pc_cards view exist and if the issuers for the card programs that have the ICA bins are part of the view

            PreparedStatement check_pc_cards_view = conn.prepareStatement(ABUScripts.check_pc_cards_view);

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

            Collections.addAll(ic_bins_to_set, ica_bins);//convert bin array to hashed set

            //fetching a bin set from pc_cards view based on passed BINs parameter
            System.out.println();
            System.out.println("Checking if pc_cards view exists..........");
            for (String s : ic_bins_to_set) {
                check_pc_cards_view.setString(1, s);
                ResultSet rs = check_pc_cards_view.executeQuery();
                while (rs.next()) {
                    ica_bins_in_diff_views.add(rs.getString(1));
                }
            }




            //check if all the issuers for ICA BINs provided are present in the pc_cards view
            if(ica_bins_in_diff_views.equals(ic_bins_to_set) && ic_bins_to_set.size()> 0)
            {
                System.out.println();
                System.out.println("pc_cards view exists and all the required issuers for the ICA BINs are part of the view");
            }

            else {
                missing_issuers_in_diff_views.addAll(ic_bins_to_set);
                missing_issuers_in_diff_views.removeAll(ica_bins_in_diff_views);
                fetch_issuer = conn.prepareStatement(ABUScripts.fetch_issuers);
                for(String s : missing_issuers_in_diff_views){
                    fetch_issuer.setString(1,s);
                    ResultSet rn = fetch_issuer.executeQuery();
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
            check_pc_cards_view();
        }
    }

    /**
     * This method checks for the pc_card_accounts view
     * and indicates which issuers are missing
     */
    public void check_pc_card_accounts_view()
    {
        try
        {
            //check to see if pc_card_accounts view exist and if the issuers for the card programs that have the ICA bins are part of the view
            PreparedStatement check_pc_card_accounts_view = conn.prepareStatement(ABUScripts.check_pc_card_accounts_view);

            //fetching a bin set from pc_card_accounts view based on passed BINs parameter
            ica_bins_in_diff_views.clear();//clear after been used in pc_cards_view
            for (String s : ic_bins_to_set) {
                check_pc_card_accounts_view.setString(1, s);
                ResultSet rs = check_pc_card_accounts_view.executeQuery();
                while (rs.next()) {
                    ica_bins_in_diff_views.add(rs.getString(1));
                }
            }

            //check if all the ICA BINs issuers are present in the pc_cards view
            if(ica_bins_in_diff_views.equals(ic_bins_to_set))
            {
                System.out.println();
                System.out.println("pc_card_accounts view exists and all the required issuers are part of the view");
            }
            else {
                missing_issuers_in_diff_views.addAll(ic_bins_to_set);
                missing_issuers_in_diff_views.removeAll(ica_bins_in_diff_views);
                for(String s : missing_issuers_in_diff_views){
                    fetch_issuer.setString(1,s);
                    ResultSet rn = fetch_issuer.executeQuery();
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
    public void copy_records_to_abu_table()
    {
        try
        {
            System.out.println();
            System.out.println("Is this a test or production environment? TEST/PROD?");
            PreparedStatement populate_pc_cards_abu_table_test = conn.prepareStatement(ABUScripts.populate_pc_cards_abu_table_test);
            PreparedStatement populate_pc_cards_abu_table_prod = conn.prepareStatement(ABUScripts.populate_pc_cards_abu_table_prod);
            String type_of_environment = scanner.nextLine().toUpperCase();
            System.out.println();
            if(type_of_environment.equalsIgnoreCase("TEST") || type_of_environment.equalsIgnoreCase("T"))
            {
                System.out.println("Copying data from pc_cards to pc_cards_abu table...............");
                for(String s:ic_bins_to_set)
                {
                    populate_pc_cards_abu_table_test.setString(1,s);
                    System.out.println(populate_pc_cards_abu_table_test.executeUpdate() + " records copied!");
                }
            }

            else if(type_of_environment.equalsIgnoreCase("PROD") || type_of_environment.equalsIgnoreCase("P"))
            {
                System.out.println("Copying data from pc_cards to pc_cards_abu table...............");
                for(String s:ic_bins_to_set)
                {
                    populate_pc_cards_abu_table_prod.setString(1,s);
                    System.out.println(populate_pc_cards_abu_table_prod.executeUpdate() + " records copied!");
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
            copy_records_to_abu_table();
        }


    }

    /**
     * This methods creates triggers on the issuers
     * for the ICA BINs
     */

    public void create_triggers_on_ICA_issuer_tables()
    {
        try
        {
            Set<String> issuers_for_trigger = new HashSet<String>();//Set to hold the issuers that the trigger will be created on

            //fetch the issuers that will have triggers created on them
            for(String s: ic_bins_to_set){
                PreparedStatement get_issuer = conn.prepareStatement(ABUScripts.fetch_issuers);
                get_issuer.setString(1,s);
                ResultSet rt = get_issuer.executeQuery();

                while(rt.next())
                {
                    issuers_for_trigger.add(rt.getString(1));
                }
            }

            //create the triggers
            System.out.println();
            for(String s:issuers_for_trigger){
                PreparedStatement drop_trigger = conn.prepareStatement(ABUScripts.drop_triggers_part_1 + s +"_A]"+ ABUScripts.drop_triggers_part_2
                        + "[dbo].[tr_at_update_mastercard_abu_" + s + "_A]" + ABUScripts.drop_triggers_part_3);

                drop_trigger.execute();

                System.out.println("Creating trigger " + "[dbo].[tr_at_update_mastercard_abu_" + s + "_A].......");
                PreparedStatement create_trigger = conn.prepareStatement(ABUScripts.TR_pc_cards_part_1 + s  +"_A]" +
                        ABUScripts.TR_pc_cards_part_2 + s + "_A]"+ ABUScripts.TR_pc_cards_part_3);
                create_trigger.executeUpdate();
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
    public void create_proc_to_auto_populate_abu_table()
    {
        try
        {
            System.out.println();
            PreparedStatement drop_SP_insert_new_records_in_ABU_table = conn.prepareStatement(ABUScripts.drop_SP_insert_new_records_in_ABU_table);
            drop_SP_insert_new_records_in_ABU_table.execute();

            String[] abu_bins= ic_bins_to_set.toArray(new String[0]);
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
            PreparedStatement create_SP_insert_new_records_in_ABU_table = conn.prepareStatement(ABUScripts.SP_insert_new_records_in_ABU_table_part_1 + bin_buffer
                    +ABUScripts.SP_insert_new_records_in_ABU_table_part_2);
            create_SP_insert_new_records_in_ABU_table.execute();

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

    public void insert_new_records_for_reason_code_N(String ICA_bins)
    {
        try
        {
            conn=JdbcManager.getConnection("postcard");
            String [] bin_list = ICA_bins.split(",");
            //PreparedStatement populate_pc_cards_abu_table_test_for_code_N = conn.prepareStatement(ABUScripts.populate_pc_cards_abu_table_test_for_code_N);
            PreparedStatement populate_pc_cards_abu_table_test_for_code_N = conn.prepareStatement(ABUScripts.populate_pc_cards_abu_table_test_for_code_N);
            for(String s: bin_list)
            {
                System.out.println("Copying records for BIN " + s + " from pc_cards to pc_cards_abu............");
                populate_pc_cards_abu_table_test_for_code_N.setString(1,s);
                System.out.println(populate_pc_cards_abu_table_test_for_code_N.executeUpdate() + " added");
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
    public void update_records_for_reason_code_C()
    {
        Map<String,String> issuer_pan = new HashMap<String, String>();//This HashMap will hold issuer_nr and pan present in
        try
        {
            conn = JdbcManager.getConnection("postcard");
            PreparedStatement update_records_for_reason_code_C = conn.prepareStatement(ABUScripts.fetch_records_from_pc_cards_abu);
            ResultSet rs = update_records_for_reason_code_C.executeQuery();

            while (rs.next())
            {
                issuer_pan.put(rs.getString(1), rs.getString(2));
            }
            rs.close();

            for (Map.Entry<String, String> entry : issuer_pan.entrySet())
            {
             update_records_for_reason_code_C = conn.prepareStatement("update pc_cards_" + entry.getKey() +"_A\n" + "set hold_rsp_code = 41 where pan in ('" +
                     entry.getValue() +"')");
             update_records_for_reason_code_C.executeUpdate();

            }
            conn.commit();


        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }


}

