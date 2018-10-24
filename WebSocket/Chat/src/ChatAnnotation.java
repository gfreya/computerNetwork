
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket/chat")
public class ChatAnnotation {

    private static final String GUEST_PREFIX = "Guest";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<ChatAnnotation> connections =
            new CopyOnWriteArraySet<ChatAnnotation>();

    private final String nickname;
    private Session session;

    public ChatAnnotation() {
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
    }
    
    public String getNickname(){
    	return nickname;
    }

    @OnOpen
    public void start(Session session) {
    	System.out.println("客户端连接了");
        this.session = session;
        connections.add(this);
        String message = String.format("* %s %s", nickname, "has joined.");
        broadcast(message);
    }

    @OnClose
    public void end() {
    	System.out.println("客户端断开了");
        connections.remove(this);
        String message = String.format("* %s %s",
                nickname, "has disconnected.");
        broadcast(message);
    }

    @OnMessage
    public void incoming(String message) { 
    	if(message.startsWith(GUEST_PREFIX)){
    		System.out.println("私聊模式");	
    		for(ChatAnnotation client : connections){
    			if(client.getNickname().equals(message)){
    				broadcast(message+"has disconnected.");
    				connections.remove(client);
    				System.out.println("DONE");
    			}
    		}
    		broadcast(nickname+"has disconnected.");
    		connections.remove(this);   		
    	}
    	else{
    	Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
    	String msg = nickname +": "+ message+" "+time;
        broadcast(msg);
        }
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
        System.out.println(t.toString());
    }
    
    private static void broadcast(String msg) {
        for (ChatAnnotation client : connections) {
            try {
                synchronized (client) {
                    client.session.getBasicRemote().sendText(msg);
                }
                
            } catch (IOException e) {
                System.out.println("Chat Error: Failed to send message to client"+e);
                connections.remove(client);
                try {
                    client.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                String message = String.format("* %s %s",
                        client.nickname, "has been disconnected.");
                broadcast(message);
            }
        }
    }
    
}
