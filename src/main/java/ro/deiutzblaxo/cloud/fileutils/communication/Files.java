package ro.deiutzblaxo.cloud.fileutils.communication;

import java.io.*;

public abstract class Files {

    public static void sendFile(DataOutputStream dataOutputStream , File file,int batch_size) throws IOException {
        {
            int bytes = 0;
            // Open the File where he located in your pc
            FileInputStream fileInputStream
                    = new FileInputStream(file);

            // Here we send the File to Server
            dataOutputStream.writeLong(file.length());
            // Here we  break file into chunks
            byte[] buffer = new byte[batch_size];
            while ((bytes = fileInputStream.read(buffer))
                    != -1) {
                // Send the file to Server Socket
                dataOutputStream.write(buffer, 0, bytes);
                dataOutputStream.flush();
            }
            // close the file here
            fileInputStream.close();
        }


    }
    public static void receiveFile(DataInputStream dataInputStream,int batch_size, File newFile) throws Exception{
        int bytes = 0;
        FileOutputStream fileOutputStream
                = new FileOutputStream(newFile);

        long size
                = dataInputStream.readLong(); // read file size
        byte[] buffer = new byte[batch_size];
        while (size > 0
                && (bytes = dataInputStream.read(
                buffer, 0,
                (int)Math.min(buffer.length, size)))
                != -1) {
            // Here we write the file using write method
            fileOutputStream.write(buffer, 0, bytes);
            size -= bytes; // read upto file size
        }
        // Here we received file
        System.out.println("File is Received");
        fileOutputStream.close();
    }

}
