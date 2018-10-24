package FtpServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * codes for server
 * @author guoqy
 * created by pc 2017/3/1/
 */

public class ServerDemo {
    public static void main(String[] args) throws IOException{
        ServerSocket server = new ServerSocket(12306);
        while (true){
            Socket s = server.accept();
            Clientthread clientthread = new Clientthread(s);
            clientthread.start();
        }
    }
}
