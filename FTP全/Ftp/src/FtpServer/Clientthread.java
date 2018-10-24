package FtpServer;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;

/**
 * class Clientthread inherits from Thread
 * @author guoqy
 * created by pc 2017/3/1
 */

public class Clientthread extends Thread{
    private Socket socket;
    File Loacl_File = new File("C:\\Users\\guoqy\\Desktop");
    String Local_Path = Loacl_File.getAbsolutePath();
    byte[] by = new byte[1024];
    int len = -1;
    long file_size =0;
    String respond =null;
    int port1 = 0;
    InetAddress address = null;
    public Clientthread(Socket s) {
        this.socket = s;
    }
    Random random = new Random(50);
    @Override
    public void run() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            ServerSocket ss = new ServerSocket();


            while(true){

                String msg = reader.readLine();

                if (msg.trim().startsWith("USER")) {
                    String name = msg.substring(4).trim();
                    if (name.trim().equals("guoqy")) {
                        writer.println("331 Password required for");
                        writer.flush();
                    } else {
                        writer.println("501 Syntax error");
                        writer.flush();
                    }
                }

                else if(msg.trim().toUpperCase().startsWith("PASS")){
                    String password = msg.substring(4).trim();
                    if(password.trim().startsWith("20154825")){
                        writer.println("230 Login");
                        writer.flush();
                    }else {
                        writer.println("530 Login or password incorrect");
                        writer.flush();
                    }
                }

                else if(msg.toUpperCase().startsWith("LINK")){//get link with client
                    int port_high;
                    int port_low;
                    while (true) {
                        port_high = random.nextInt(20);
                        port_low = random.nextInt(1000);
                        try {
                            ss = new ServerSocket(256 * port_high + port_low);
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                        InetAddress inetAddress = null;
                    try {
                        inetAddress = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    writer.println("227 Entering Passive Mode (" + inetAddress.getHostAddress().replace(".", ",") + ","
                            + port_high + "," + port_low + ")");
                    writer.flush();
                }

            else if(msg.toUpperCase().startsWith("LIST")){
                    Socket s = ss.accept();
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                    if (Loacl_File.isDirectory()) {
                        File[] files = Loacl_File.listFiles();
                        writer.println("150 Opening data channel for directory list");
                        writer.flush();
                        dos.writeUTF(Local_Path);
                        dos.flush();
                        for (final File file_temp : files) {
                            String infor = new String();
                            infor += file_temp.getName() + " ";
                            infor += "\nSize: "+file_temp.length() + " ";

                            Date date = new Date(file_temp.lastModified());
                            infor += "\n"+date;
                            infor += "\n"+file_temp.getAbsolutePath();
                            dos.writeUTF(infor);
                            dos.flush();
                        }
                    } else {
                        dos.writeUTF(Local_Path + " is not a directory");
                        dos.flush();
                    }
                    dos.writeUTF("226 Transfer OK");
                    dos.flush();
                    dos.close();
                    s.close();



                }



            else if(msg.trim().toUpperCase().startsWith("SIZE")) {

                    String Filename = reader.readLine();//搜索文件�?
                    File file = new File(Loacl_File,Filename);
                    if (file.exists()) {
                        writer.println("File size: "+file.length());
                        writer.flush();
                    } else {
                        writer.println("550 SIZE failed. " + Local_Path + ": file not found.");
                        writer.flush();
                    }
                }

             else if(msg.trim().toUpperCase().startsWith("CWD")) {
                    String dirname = reader.readLine();
                    File file_temp = new File(Loacl_File,dirname.trim());
                    if (dirname.trim().equals("")) {
                        writer.println("250 Broken client detected, missing argument to CWD. " + Local_Path
                                + " is current directory.");
                        writer.flush();
                    } else if (!file_temp.exists() || !file_temp.isDirectory()) {
                        writer.println("550 CWD failed. " + Local_Path + ": directory not found.");
                        writer.flush();
                    } else {
                        Loacl_File = file_temp;
                        Local_Path = file_temp.getAbsolutePath();
                        writer.println("250 CWD successful. " + Local_Path + " is current directory");
                        writer.flush();
                    }
                }

             else if (msg.trim().toUpperCase().startsWith("RETURN")){
                    Loacl_File = new File("C:\\Users\\guoqy\\Desktop");
                    Local_Path = Loacl_File.getAbsolutePath();
                    writer.println("Location now:"+Local_Path);
                    writer.flush();
                }

            else if(msg.trim().toUpperCase().startsWith("DOWNLOAD")){
                    Socket s = ss.accept();
                    DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
                    String fileName = reader.readLine();
                    File file = new File(Loacl_File,fileName);
                    if (file.exists()) {
                        writer.println("150 Opening data channel for file transfer");
                        writer.flush();

                        writer.println(file.length());
                        writer.flush();

                        String flag = reader.readLine();
                        System.out.println(flag);


                        if(flag.trim().equals("A")) {
                            System.out.println("OK");
                            if (file.isFile()) {
                                System.out.println("start1");
                                CopyFile(file,dos, ss);

                                dos.close();

                                System.out.println("end");

                            }
                        }
                        else if(flag.trim().equals("B")){
                            System.out.println("start flag");
                            CopyFolder(file,writer,reader,s,dos,ss);
                            writer.println("END");
                            writer.flush();
                            dos.close();

                        }



                    } else {
                        writer.println("550 RETR failed. " + Local_Path + ": directory not found");
                        writer.flush();
                    }
                    s.close();

                }

             else if(msg.trim().toUpperCase().equals("UPLOAD")){
                    Socket s = ss.accept();
                   


                    String respond = reader.readLine();
                    if(respond.trim().equals("CLOSE")){
                        s.close();
                    }else {
                    	File Upload_File = new File("C:\\Users\\guoqy\\Desktop", respond);
                    	//防止名字重复
                        Upload_File.mkdir();
                        String Local_Path2 = Upload_File.getAbsolutePath();
                        System.out.println("start flag");
                        while (true) {
                            respond = reader.readLine();
                            System.out.println("start flag TRUE   " + respond);
                            if (respond.trim().equals("Directory")) {
                                String new_file_name = reader.readLine();
                                File new_file = new File(Local_Path2, new_file_name);
                                new_file.mkdir();//响应服务器创建相应的文件�?                        

                            } else if (respond.trim().equals("File")) {
                                System.out.println("start flag File");
                                System.out.println();
                                String new_file_name = reader.readLine();
                                File new_file = new File(Local_Path2, new_file_name);
                                while (new_file.exists()) {
                                    int i = 1;
                                    new_file = new File(Local_Path2, new_file_name + i);
                                    i++;
                                }
                                new_file.createNewFile();

                                Socket dataSocket = ss.accept();
                                DataInputStream dis = new DataInputStream(dataSocket.getInputStream());
                                BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(new_file));
                                by = new byte[1024];
                                len = -1;                             
                                while ((len = dis.read(by)) != -1) {
                                    System.out.println(len);
                                    fos.write(by, 0, len);
                                    fos.flush();
                                }
                                System.out.println("download over");
                                dis.close();
                                dataSocket.close();
                                fos.close();


                            } else if (respond.trim().equals("END")) {
                                s.close();
                                System.out.println();

                                break;
                            }
                        }

                    }



                }





            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void CopyFolder(File file, PrintWriter writer, BufferedReader reader, Socket s, DataOutputStream dos,ServerSocket ss) throws IOException {
        if(file.isDirectory()){
            writer.println("Directory");
            writer.flush();
            String Sendname = file.getName();//传�?�文件名�?
            writer.println(Sendname);
            writer.flush();
            File[] fileArray = file.listFiles();
            for(File f:fileArray){
                CopyFolder(f,writer,reader,s,dos,ss);


            }
            writer.println("OUT");
            writer.flush();

        }else if(file.isFile()){
            writer.println("File");
            writer.flush();

            writer.println(file.getName());//传�?�文件名�?
            writer.flush();
            CopyFile(file,dos,ss);
        }else{

        }
    }

    private void CopyFile(File file, DataOutputStream dos, ServerSocket ss) throws IOException {
        Socket dataSocket = ss.accept();
        dos = new DataOutputStream(dataSocket.getOutputStream());
        BufferedInputStream dis = new BufferedInputStream(new FileInputStream(file));
        System.out.println("start");
        byte[] by = new byte[1024];
        int len= -1;
        while ((len = dis.read(by)) != -1) {

            System.out.println(len);
            System.out.println(new String(by,0,len));
            dos.write(by, 0, len);
            dos.flush();

        }
        dis.close();
        dataSocket.close();
    }

}
