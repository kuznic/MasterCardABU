package postilion.realtime.mastercardabu;


import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This utility creates the mastercardabu folder and copies
 * content of the configuration folder into the mastercardabu folder
 * @author Vin-Anuonye Chukwuemeka
 * @throws IOException
 * @version 1.0
 */

public class CreateAbuFolder
{
    UnZipZippedFolders unZipper = new UnZipZippedFolders();

    public void create() throws IOException
    {
        //File abuFolder = new File("mastercardabu");// create a new File object for the mastercard abu folder
        File abuFolder = new File("C:\\mastercardabu");// create a new File object for the mastercard abu folder

        //check if mastercardabu folder exists and deletes the folder

        if(!abuFolder.exists())
        {
            System.out.println("Creating c:\\mastercardabu folder......");
            if(abuFolder.mkdirs()){
                System.out.println("Folder mastercardabu has been successfully created!");
            }
        }
        else
        {
            System.out.println("mastercardabu folder already exists in c drive, moving on............");
        }

        String abuFolderPath = abuFolder.getAbsolutePath();

        System.out.println("Enter path to folder where Mastercard ABU setup files has been extracted to: ");
        Scanner scanner = new Scanner(System.in);

        String abuConfigFilesFolder = scanner.nextLine();//location where configuration files currently exist

        File check_if_path_exists = new File(abuConfigFilesFolder);
        if(!check_if_path_exists.exists())
        {
            throw new IOException("Path provided does not exist!");

        }

        File abuConfigFolderFileObject = new File(abuConfigFilesFolder);//create a file object for the config folder
        String[] configFiles = abuConfigFolderFileObject.list();//read the names of the content of th folder into an array

        ArrayList<String> zippedList = new ArrayList<String>(2);//Array to hold names of zipped files



        //create empty file copies of the config files in mastercardabu folder
        if (configFiles != null) {
            for(String  filename:configFiles){
                File aConfigFile = new File(abuFolderPath+ "/" + filename);
                System.out.println("copying file  " + aConfigFile.getName() + "........");

                //store names of zipped files in zippedList array
                if(filename.matches(".*zip.*"))
                {
                    zippedList.add(filename);
                }

                if(aConfigFile.createNewFile())
                {
                    System.out.println(aConfigFile.getName() + "  copied");
                    System.out.println();
                }

            }
        }

        File[] configFileArray = abuConfigFolderFileObject.listFiles();//read the config files into an array

        //copy config files across to the mastercardabu folder
        if (configFileArray != null) {

            for(File file: configFileArray){
                File aFile = new File(abuFolderPath + "/" + file.getName());
                RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r");
                RandomAccessFile outFile = new RandomAccessFile(aFile, "rw");
                FileChannel inChannel = randomAccessFile.getChannel();
                MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
                byte[] data = new byte[50000];

                while(buffer.hasRemaining()){
                    int remaining = data.length;
                    if(buffer.remaining() < remaining){
                        remaining=buffer.remaining();
                    }
                    buffer.get(data,0,remaining);

                    outFile.write(data);
                }
                randomAccessFile.close();
                outFile.close();

            }
        }
        //Unzip zipped folders

        for(String zippedFile:zippedList)
        {
            String zipFilePath = abuFolderPath+ "\\" +zippedFile;
            try {
                unZipper.unzip(zipFilePath, abuFolderPath);
            } catch (Exception ex) {
                // some errors occurred
                ex.printStackTrace();
            }
        }

        //delete zipped files after unzipping
        unZipper.deleteFile(abuFolderPath,zippedList);



    }

}



