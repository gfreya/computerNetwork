package HTTPServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.Socket;

/**
 * class ServerThread inherits from Thread
 * @author guoqy
 * created by pc 2017/3/10
 */

public class ServerThread extends Thread {
   private Socket socket=null;
   private BufferedReader reader=null;
   private PrintWriter writer=null;
   public ServerThread(Socket s){
	   socket=s;
   }
   public void run (){
	   try {
	    OutputStream out=socket.getOutputStream();
		reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
	   
		String firstlineofrequest=reader.readLine();
	    String uri=firstlineofrequest.split(" ")[1];
	    writer=new PrintWriter(out);
	    
	    File f=new File("D:/"+uri);
	    if(f.exists()){
	    FileInputStream file=new FileInputStream("D:/"+uri);
	    writer.println("HTTP/1.1 200 OK");
	    writer.flush();
	    if(uri.endsWith(".html")){
	    	writer.println("Content-Type:text/html");
	    	writer.flush();
	    }
	    else if(uri.endsWith(".jpg")){
	    	writer.println("Content-type:image/jpg");
	    	writer.flush();
	    }
	    else{
	    	writer.println("Content-type:application/octet-stream");
	    	writer.flush();
	    }
	    //读取剩下三行
	    for(int i=0;i<3;i++){
	    	reader.readLine();
	    }
	    
	  
	    //有文件
	    writer.println("Content-length:"+file.available());
	    writer.println();//根据http协议，先发一个空行
	    writer.flush();
	    try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    byte [] b=new byte[1024];
	    int len=0;
	    len=file.read(b);
	    while(len!=-1){
	    	out.write(b,0,len);
	    	System.out.println("1024");
	    	len=file.read(b);
	    	out.flush();
	    }
	 
	      
	      out.close();
	    }
	    else{
	    	writer.println("HTTP/1.1 404 Not Found");
	    	writer.println("Content-Type:text/plain");
	    	writer.println("Content-Length:20");
	    	writer.println();
	    	writer.println("Can Not Found File");
	    	writer.flush();
	    	
	    }
	    
	   } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   
   }
}
