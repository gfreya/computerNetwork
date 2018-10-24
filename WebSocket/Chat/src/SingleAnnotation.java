
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

@ServerEndpoint(value = "/websocket/chat1")
public class SingleAnnotation {

    private static final String GUEST_PREFIX = "Person";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<SingleAnnotation> connections =
            new CopyOnWriteArraySet<SingleAnnotation>();

    private final String nickname;
    private Session session;

    public SingleAnnotation() {
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
    }

    @OnOpen
    public void start(Session session) {
    	if(connections.size()<2){
    		System.out.println("�ͻ���������");
            this.session = session;
            connections.add(this);
            String message = String.format("* %s %s", nickname, "is online.");
            broadcast(message);
    	}else{
    		return;
    	}
    }

    @OnClose
    public void end() {
    	System.out.println("�ͻ��˶Ͽ���");
        connections.remove(this);
        String message = String.format("* %s %s",
                nickname, "has disconnected.");
        broadcast(message);
    }

    @OnMessage
    public void incoming(String message) {
    	Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
    	String msg = nickname +": "+ message+" "+time;
        broadcast(msg);
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
        System.out.println(t.toString());
    }

    private static void broadcast(String msg) {
        for (SingleAnnotation client : connections) {
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
