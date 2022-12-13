package kh.springboot.websocket.endpoint;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import kh.springboot.websocket.config.WebSocketConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@Service
@ServerEndpoint(value = "/chat", configurator = WebSocketConfig.class)
public class ChatEndpoint {

    //for문이 도는동안 clients 수가 달라지면 발생하는 오류를 방지
//    private static Set<Session> clients = Collections.synchronizedSet(new HashSet<>());
    private static Set<Session> clients = new HashSet<>();
    //접속자의 HttpSession 객체를 저장할 멤버필드
    private HttpSession hSession;


    @OnOpen
    public void onConnetion(Session client, EndpointConfig config) { //OnOpen은 연결된 client의 정보를 가지고 있음.
        clients.add(client);
        this.hSession = (HttpSession) config.getUserProperties().get("hSession");//session 꺼내기
        System.out.println("웹 소켓 연결 > " + (String) this.hSession.getAttribute("IP"));
        System.out.println(this.hSession.getAttribute("loginID"));
    }

    @OnMessage
    public void onMessage(String msg) { //throw Exception을 하면 for문이 돌다가 어떤 사람은 받고 나머지 사람은 못 받는 상황이 발생할 수 있음.
        JsonObject json = new JsonObject();

        msg = msg.replace("<", "&lt;");
        msg = msg.replace("&lt;img", "<img");
        synchronized (clients) {
            Set<Session> clientsCopy = new HashSet<>(clients);
            for (Session client : clientsCopy) {

                try {
                    String sender = (String) this.hSession.getAttribute("loginID");
                    if (sender == null || sender == "") {
                        sender = "익명의 바보";
                    }

                    json.addProperty("ip", (String) this.hSession.getAttribute("IP"));
                    json.addProperty("sender", sender);
                    json.addProperty("msg", msg);

                    System.out.println((String) this.hSession.getAttribute("IP") + " > " + msg);

                    client.getBasicRemote().sendText(json.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @OnClose
    public void onClose(Session client) {
        clients.remove(client);
    }

    @OnError
    public void onError(Session client, Throwable throwable) {
        clients.remove(client);

    }
}
