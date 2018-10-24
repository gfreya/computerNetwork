package FtpClient;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

/**
 * codes for client
 * @author guoqy
 * created by pc 2017/3/1/
 */

public class ClientDemo {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12306);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        String respond = null;
        //String ip =null;
        int port1 = 0;
        InetAddress address = null;
        File downloatFile = new File(".");
        File tempFile = null;
        String download_path = downloatFile.getAbsolutePath();
        String save_path=null;
        Socket dataSocket = new Socket();
        String filename = null;
        //pasv upload
        ServerSocket ss = new ServerSocket();
        Random random = new Random(50);
    
        byte[] by = new byte[1024];
        int len=0;
        long file_size =0;
        System.out.println("link prepared");

        while (true) {
            System.out.println("Input UserName");
            Scanner scanner = new Scanner(System.in);
            String user = scanner.next();
            writer.println("USER " + user);
            writer.flush();
            respond = reader.readLine();
            System.out.println(respond);
            if (respond.trim().startsWith("331")) {
                break;
            }
        }


        while (true) {
            System.out.println("PASS : ");
            Scanner scanner = new Scanner(System.in);
            String pass = scanner.next();
            writer.println("PASS " + pass);
            writer.flush();
            respond = reader.readLine();
            System.out.println(respond);
            if (respond.trim().startsWith("230")) {
                System.out.println(respond.substring(4));
                break;
            } else {
                System.out.println("Login or password incorrect!");
                //soutprint();
            }
        }


        //System.out.println("3.");

        soutprint();


        while (true) {
            Scanner scanner = new Scanner(System.in);
            String str = scanner.next();
            switch (Integer.parseInt(str)) {
                case 1:
                    writer.println("LINK");
                    writer.flush();
                    respond = reader.readLine();
                    if (respond.startsWith("227")) {
                        String[] info = respond.substring(respond.indexOf("(") + 1, respond.indexOf(")")).split(",");
                        byte[] ip = new byte[4];
                        for (int i = 0; i < 4; i++) {
                            ip[i] = new Integer(info[i]).byteValue();
                        }
                        port1 = Integer.parseInt(info[4]) * 256 + Integer.parseInt(info[5]);
                        address = InetAddress.getByAddress(ip);
                        System.out.println("Link Start");
                        System.out.println("choose 2 to scan the file list");
                        System.out.println();
                        soutprint();
                    } else {
                        System.out.println("Link error");
                        soutprint();
                    }
                    break;
                case 2:

                    if (address != null && port1 != 0) {
                        dataSocket = new Socket(address, port1);
                        DataInputStream dis = new DataInputStream(dataSocket.getInputStream());
                        writer.println("List");
                        writer.flush();
                        respond = reader.readLine();
                        if (respond.trim().startsWith("150")) {
                            String s = null;
                            System.out.println(respond);
                            int i = 0;
                            while ((s = dis.readUTF()) != null) {
                                if (s.trim().startsWith("226")) {
                                    break;
                                }
                                System.out.println(i + " " + s);
                                System.out.println();
                                i++;
                            }
                            i--;
                            System.out.println("Files number " + i);
                            System.out.println("choose 3 to download files or 4 to upload files");
                            System.out.println();
                            soutprint();
                        }
                        dis.close();
                        dataSocket.close();
                    } else {
                        System.out.println("please start PASV first");
                        soutprint();
                    }
                    break;

                case 3:

                    if (address != null && port1 != 0) {
                        dataSocket = new Socket(address, port1);
                        DataInputStream dis = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
                        System.out.println("Input the name of the file that you want to downdload : ");
                        filename = scanner.next();
                        writer.println("DOWNLOAD ");
                        writer.flush();
                        writer.println(filename);
                        writer.flush();
                        respond = reader.readLine();
                        if(respond.trim().startsWith("150")){
                            System.out.println("File exist you can download");
                        }else {
                            System.out.println("File does not exist");
                            System.out.println();
                            soutprint();
                            break;
                        }
                        respond = reader.readLine();
                        file_size = Integer.parseInt(respond);
                        System.out.println("File size: "+respond);//check file size;

                        System.out.println("Input the path you want to save");
                        String path = scanner.next();
                        File file_temp = new File(path.trim());
                        if(path.trim().equals("")){
                            System.out.println("Input null");
                            System.out.println();
                            soutprint();
                            break;
                        }else if(!file_temp.exists() || !file_temp.isDirectory()){

                            file_temp = new File(path.trim());
                            download_path = file_temp.getAbsolutePath();
                            file_temp.mkdir();
                        }else {
                            download_path = file_temp.getAbsolutePath();
                            System.out.println("down path:"+download_path);
                        }

                        System.out.println("choose A to dwonload file");
                        System.out.println("choose B to download directory");
                        String flag_choose = scanner.next();
                        if(flag_choose.trim().equals("A")) {
                            writer.println("A");
                            writer.flush();
                            System.out.println("Save name");
                            String downLoad_filename = scanner.next();
                            File new_file = new File(download_path, downLoad_filename);
                            System.out.println("File has been created");

                            System.out.println("begin download file");
                            dataSocket = new Socket(address, port1);
                            dis = new DataInputStream(dataSocket.getInputStream());
                            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(new_file));
                            by = new byte[1024];
                            len = -1;
                            //System.out.println(len);
                            while ((len = dis.read(by)) != -1) {
                                System.out.println(len);
                                fos.write(by, 0, len);
                                fos.flush();
                            }

                            fos.close();            
                            soutprint();

                        }
                        else if(flag_choose.trim().equals("B")){
                            writer.println("B");
                            writer.flush();
                            System.out.println("start flag");
                            while (true){
                                respond = reader.readLine();
                                System.out.println("start flag TRUE"+respond);
                                if(respond.trim().equals("Directory")){
                                    //System.out.println("statr flag Directory");
                                    String new_file_name = reader.readLine();
                                    File new_file = new File(download_path,new_file_name);
                                    tempFile = new_file;
                                    new_file.mkdir();
                                    save_path = download_path;
                                    download_path = new_file.getAbsolutePath();

                                }
                                if(respond.trim().equals("OUT")){

                                    download_path = tempFile.getParentFile().getAbsolutePath();
                                }
                                else if (respond.trim().equals("File")){
                                    System.out.println("start flag File");
                                    System.out.println();
                                    String new_file_name = reader.readLine();
                                    File new_file = new File(download_path,new_file_name);
                                    dataSocket = new Socket(address, port1);
                                    dis = new DataInputStream(dataSocket.getInputStream());
                                    BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(new_file));
                                    by = new byte[1024];
                                    len = -1;
                                    while ((len = dis.read(by)) != -1) {
                                        System.out.println(len);
                                        fos.write(by, 0, len);
                                        fos.flush();
                                    }                                
                                    fos.close();


                                }
                                else if(respond.trim().equals("END")){
                                    soutprint();
                                    break;
                                }

                            }
                            System.out.println("end flag");
                            System.out.println();
                            soutprint();
                        }else {
                            System.out.println("wrong input");
                            System.out.println();
                            soutprint();
                            break;
                        }
                    }
                    soutprint();
                        break;

                case 4:
                    writer.println("UPLOAD");
                    writer.flush();
                    Socket s = new Socket(address, port1);
                    System.out.println("ok");
                    File Local_File = new File("C:\\Users\\guoqy\\Desktop\\Upload");
                    File[] fileArray = Local_File.listFiles();
                    int i=1;
                    for(File f:fileArray){
                        System.out.println(i+" "+f.getName());
                    }
                    System.out.println();
                    System.out.println("Input the file you want to upload");
                    filename = scanner.next();
                    File upload_file = new File(Local_File,filename);//upload file name
                    if(!upload_file.exists()){
                        writer.println("CLOSE");
                        writer.flush();
                        System.out.println("This file does not exist");
                        s.close();
                        System.out.println();
                        soutprint();

                        break;
                    }else {
                        writer.println(filename);
                        writer.flush();

                        System.out.println("start flag");
                        CopyFolder(upload_file,writer,reader,address,port1);
                        writer.println("END");
                        writer.flush();
                        System.out.println("END");
                        s.close();
                        soutprint();
                        break;
                        //dos.close();
                    }



                case 5:

                    System.out.println("Input the filename : ");
                    filename = scanner.next();
                    writer.println("SIZE ");
                    writer.flush();
                    writer.println(filename);
                    writer.flush();
                    respond = reader.readLine();
                    System.out.println(respond);
                    filename = null;//filename
                    soutprint();
                    break;

                case 6:
                    System.out.println("Input the  directory : ");
                    String dirname = scanner.next();
                    writer.println("CWD ");
                    writer.flush();
                    writer.println(dirname);
                    writer.flush();


                    respond = reader.readLine();
                    if (respond.startsWith("530")) {
                        System.out.println(respond);
                        soutprint();
                    } else if (respond.startsWith("250")) {
                        if (respond.charAt(4) == 'B') {
                            System.out.println("Broken client detected, missing argument to CWD. " + dirname
                                    + " is current directory");
                            soutprint();
                        } else {
                            System.out.println("CWD successful. " + dirname + " is current directory");
                            soutprint();
                        }
                    } else if (respond.startsWith("550")) {
                        System.out.println("CWD failed. " + dirname + ": directory not found.");
                        soutprint();
                    }
                    break;

                case 7:
                    System.out.println("Input RETURN to reutrn to original floder");
                    String returnfile = scanner.next();
                    if (!returnfile.trim().equals("RETURN")) {
                        System.out.println("Input Error¯¯");
                        System.out.println();
                        soutprint();
                        break;

                    }
                    writer.println(returnfile);
                    writer.flush();
                    respond = reader.readLine();
                    System.out.println(respond);
                    break;
                case 8:
                    break;

            }
        }




    }

    private static void soutprint() {
        System.out.println("1.Link to server");
        System.out.println("2.File list");
        System.out.println("3.Download File");
        System.out.println("4.Upload File");
        System.out.println("5.Check file Size");
        System.out.println("6.Change file directory");
        System.out.println("7.Return to original folder");
        System.out.println("8.Quit");
    }

    private static void CopyFolder(File file, PrintWriter writer, BufferedReader reader,InetAddress address,int port1) throws IOException {
        if(file.isDirectory()){
            System.out.println("Directory");//flag
            writer.println("Directory");
            writer.flush();
            String Sendname = file.getName();
            writer.println(Sendname);
            writer.flush();
            File[] fileArray = file.listFiles();
            for(File f:fileArray){
                CopyFolder(f,writer,reader,address,port1);
            }
        }else{
            writer.println("File");
            writer.flush();

            writer.println(file.getName());
            writer.flush();
            CopyFile(file,address,port1);
        }
    }



    private static void CopyFile(File file, InetAddress address,int port1) throws IOException {
        //long change = (int) (System.currentTimeMillis()%100);
        Socket dataSocket = new Socket(address,port1);
        DataOutputStream dos = new DataOutputStream(dataSocket.getOutputStream());
        BufferedInputStream dis = new BufferedInputStream(new FileInputStream(file));

        byte[] by = new byte[1024];
        int len= -1;
        while ((len = dis.read(by)) != -1) {
            dos.write(by, 0, len);




            dos.flush();          
        }
        dis.close();
        dataSocket.close();

    }



}
