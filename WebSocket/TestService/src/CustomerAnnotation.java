
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket/chat1")
public class CustomerAnnotation {
	private String nickname;
    public Session session;

  

    public String getNickname() {
		return nickname;
	}


	@OnOpen
    public void start(Session session) {
    	ChatAnnotation.connections.add(this);
    	System.out.println("客户端连接");
    	this.session = session;
		
    }

    @OnClose
    public void end() {
    	System.out.println("客户端断开了");
    }

    @OnMessage
    public void incoming(String message) {
    	System.out.println(nickname);
    	Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
    	String msg =message+" "+time;
//    	System.out.println(nickname);
        broadcast(msg);
//        outcast(msg);
    }


	@OnError
    public void onError(Throwable t) throws Throwable {
        System.out.println(t.toString());
    }

    public void broadcast(String msg) {   
    	try {
			session.getBasicRemote().sendText(msg);		
			ChatAnnotation.session.getBasicRemote().sendText(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	

	
}
