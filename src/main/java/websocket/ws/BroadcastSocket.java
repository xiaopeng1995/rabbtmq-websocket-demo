package websocket.ws;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import websocket.util.JsonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class BroadcastSocket extends WebSocketAdapter {
    /**
     * 用户ID 用于绑定session
     */
    private static final String USERID = "userId";

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(BroadcastSocket.class);

    /**
     * 所有session包含未绑定
     */
    private static Set<Session> sessions = new CopyOnWriteArraySet<>();

    /**
     * 绑定用户信息sessions 可接入redis
     */
    private static Map<String, Session> msessions = new HashMap<>();

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        sessions.add(session);
        log.info("Socket Connected: {}", Integer.toHexString(session.hashCode()));
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        sessions.remove(getSession());
        super.onWebSocketClose(statusCode, reason);
        log.info("Socket Closed: [{}] {}", statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        log.error("Websocket error", cause);
    }

    @Override
    public void onWebSocketText(String message) {
        log.info("Got text {} from {}", message, Integer.toHexString(getSession().hashCode()));
        try {
            Map upData = JsonUtils.Mapper.readValue(message, Map.class);
            String userId = upData.get(USERID).toString();
            msessions.put(userId, getSession());
            log.info("bind user :{} Session Success!", userId);
            broadcast("bind user :" + userId + " Session Success!", userId);
        } catch (IOException e) {
            log.error("bad msg :{} filter ", message);
        }
    }

    /**
     * 群体推送消息
     *
     * @param msg 消息内容
     */
    public static void broadcast(String msg) {

        sessions.forEach(session -> {
            try {
                session.getRemote().sendString(msg);
                log.info("send msg:{} to:{} ", msg, session.hashCode());
            } catch (IOException e) {
                log.error("Problem broadcasting message", e);
            }
        });
    }

    /**
     * 单个推送
     *
     * @param msg    消息
     * @param userId 用户id
     */
    public static void broadcast(String msg, String userId) {
        Session session = msessions.get(userId);
        try {
            if (session == null) {
                log.error("not find user bind session !");
                return;
            }
            session.getRemote().sendString(msg);
            log.info("send msg:{} to:{} ", msg, Integer.toHexString(session.hashCode()));
        } catch (IOException e) {
            log.error("user：{} push msg {} fail！", userId, msg);
        }
    }
}
