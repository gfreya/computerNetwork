package HTTPServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * codes for server
 * @author guoqy
 * created by pc 2017/3/10
 */
public class HTTPServer {
       public static void main(String[] args) {
    	   ServerSocket serverSocket=null;
		  try {
			 serverSocket=new ServerSocket(8888);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  while(true){
			  try {
				Socket socket=serverSocket.accept();
				new ServerThread(socket).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
		  }
	}
}
