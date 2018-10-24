
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.catalina.ant.SessionsTask;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;

@ServerEndpoint(value = "/websocket/chat")
public class ChatAnnotation {
     static  CopyOnWriteArraySet<CustomerAnnotation> connections =
            new CopyOnWriteArraySet<CustomerAnnotation>();
     
	private static String nickname;
    public static Session session;

    @OnOpen
    public void start(Session session) {
    	System.out.println("客户端连接了");
    	System.out.println(connections.size());
        ChatAnnotation.session = session;
         localtalk("connected");
    }

    @OnClose
    public void end() {
    	System.out.println("客户端断开了");
    }

    @OnMessage
    public void incoming(String message) { 	
    	if(message.startsWith("101")){
    		this.nickname = message.substring(8);
    	}
    	Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
    	String msg = nickname +": "+ message+" "+time;
        broadcast("102"+msg);
        localtalk(msg);
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
        System.out.println(t.toString());
    }

    private void broadcast(String msg) {
        for (CustomerAnnotation client : connections) {
//       	System.out.println("!!!"+client.getNickname());
            try {
				client.session.getBasicRemote().sendText(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         
        }
    }
    
    public  void localtalk(String msg) {
    	try {
			ChatAnnotation.session.getBasicRemote().sendText(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
