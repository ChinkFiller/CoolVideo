package com.test.http_spring.Controller;





import com.test.http_spring.mapper.usersMapper;
import com.test.http_spring.utils.ToolsFunction;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



//这个时服务器主动推动程序，目前没有写好，后期会写的
@ServerEndpoint(value = "/pusher/{userid}")
@Component
public class PusherController{
    public static final Map<String, Session> SESSION_POOL = new HashMap<>();

    @Autowired
    usersMapper usersmaper;

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userid") String userId) {
        try {
            System.out.println(usersmaper.getOneUserByToken(userId));
            SESSION_POOL.put(userId,session);
        } catch (Exception e) {
            ToolsFunction.errorLog("[Pusher]"+e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session,@PathParam(value = "userid") String userid) {
        try {
            SESSION_POOL.remove(userid);
        } catch (Exception e) {
            ToolsFunction.errorLog("[Pusher]"+e.getMessage());
        }
    }

    @OnMessage
    public void onMessage(String message, @PathParam(value = "userid") String userId) {
        System.out.println("【WebSocket消息】收到客户端消息：" + message);
    }
    public boolean sendMessage(String msg,String token){
        try {
            SESSION_POOL.get(token).getBasicRemote().sendText(msg);
            return true;
        }catch (IOException e){
            return false;
        }
    }
}
