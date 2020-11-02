package postilion.realtime.mastercardabu;

import java.io.IOException;
import java.util.Scanner;

/**
 *This program is use to deploy the Mastercard ABU Application
 * This program was written using JAVA 1.6
 * @author Vin-Anuonye Chukwuemeka
 * @version 1.0
 * @throws java.io.IOException,java.lang.NullPointerException
 * @since 23-10-2020
 */

public class MastercardAbuUtil {


    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final AbuTables abuTables = new AbuTables();
        final AbuFiles createABUFolder = new AbuFiles();

        System.out.println("1. Deploy Mastercard ABU:");
        System.out.println("2. Populate pc_cards_abu table with new records for reason code N:");
        System.out.println("3. Modify pc_cards_abu records for reason code C:");
        System.out.println();
        System.out.println("Options 2 and 3 used only be used on the test environment");
        System.out.println();

        try
        {
            int choice = scanner.nextInt();

            switch (choice)
            {
                case 1:
                {
                    createABUFolder.createAbuFolderAndCopyConfigFiles();

                    abuTables.createAbuTable();
                    abuTables.checkPcCardsView();
                    abuTables.checkPcCardAccountsView();
                    abuTables.copyRecordsToAbuTable();
                    abuTables.createTriggersOnIcaIssuerTables();
                    abuTables.createProcedureToAutoPopulateAbuTable();
                    break;

                }

                case 2:
                {
                    System.out.println("Enter ICA BINs separated by commas");
                    Scanner sn = new Scanner(System.in);
                    String bin_list = sn.nextLine();
                    abuTables.insertNewRecordsForReasonCodeN(bin_list);
                    break;
                }

                case 3:
                    System.out.println("updating hold response code to 41....");
                    abuTables.updateRecordsForReasonCodeC();
                    break;

                case 4:
                {
                    abuTables.insertNewRecordForReasonCodeR();
                }

                default:
                    break;
            }


        }



        catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
        catch (NullPointerException e){
            System.out.println("A null pointer error occurred");
            e.printStackTrace();
        }



    }
}
