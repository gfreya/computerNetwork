

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;


public class ClientDemo {
    public static void main(String[] args) throws IOException {
    	 Socket socket = new Socket("ftp.NEU.edu.cn", 21);
         BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
         String respond = null;
         int port1 = 0;
         InetAddress address = null;
         File downloatFile = new File(".");
         File tempFile = null;
         String download_path = downloatFile.getAbsolutePath();
         String save_path=null;
         Socket dataSocket = new Socket();
         String filename = null;
         ServerSocket ss = new ServerSocket();
         Random random = new Random(50);
         byte[] by = new byte[1024];
         int len=0;
         long file_size =0;
         System.out.println("link prepared");

        while (true) {
            System.out.println("Please Input UserName");
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
            if (respond.trim().startsWith("331")) {
                break;
            } else {
                System.out.println("Login or password incorrect!");
                   }
        }

        while(true){
        	System.out.println("PORT");
        	writer.println("PORT 172,28,212,81,11,120");
        	writer.flush();
        	respond = reader.readLine();
        	System.out.println(respond);
        	if(respond.trim().startsWith("200")){
        		break;
       	    }else if(respond.trim().startsWith("500")){
       	    	break;
       	    }
        	
        }
    
        while(true){
        	System.out.println("QUIT");
        	writer.println("QUIT");
        	writer.flush();
        	break;
        	
        }
    }

    
}

