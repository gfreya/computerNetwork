package HTTPClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * codes for client
 * @author guoqy
 * created by pc 2017/3/10
 */

public class HTTPClient {
   public static void main(String[] args) {
	   try {
		Socket socket=new Socket(InetAddress.getLocalHost(),8888);
		PrintWriter writer=new PrintWriter(socket.getOutputStream());
		InputStream inputStream=socket.getInputStream();
		BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Please input the filename：");
			//客户端发送GET请求
			String filename=in.readLine();
		   writer.println("GET /"+filename+" HTTP/1.1");
		   writer.println("Host:local host");
		   writer.println("connection :keep-alive");
		   writer.println("");
		   writer.flush();
		   
		   //接受文件
		   String first=reader.readLine();//"HTTP/1.1 200 OK"
		   System.out.println(first);
		   String second=reader.readLine();//"Content-Type:"
		   System.out.println(second);
		   String third=reader.readLine();//"Content-length:"
		   System.out.println(third);
		   String forth=reader.readLine();//根据http协议，先发一个空行
		   System.out.println(forth);
		   if(first.endsWith("OK")){
		   //读取数据
		   byte []b=new byte[1024];
		   System.out.println("Data Transfer...");
		   OutputStream out=new FileOutputStream("C:\\Users\\guoqy\\Desktop"+"/"+filename);
		   int len =inputStream.read(b);
		   //System.out.println(b);
		   while(len!=-1){
			   out.write(b,0,len);
			   System.out.println("1024");
			   len=inputStream.read(b);
		   }
		   System.out.println("Data Transfer Done");
		   out.close();
		   inputStream.close();
		   }
		   else{
			   StringBuffer result=new StringBuffer();
			   String line=null;
			   while((line=reader.readLine())!=null){
				   result.append(line);
			   }
			   System.out.print(result);
		   }
		
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
	
}

