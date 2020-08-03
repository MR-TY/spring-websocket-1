package org.springagg.websocket;

import org.springagg.consts.Constants;
import org.springagg.redis.RedisTemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Component
public class TextMessageHandler extends TextWebSocketHandler {

    @Autowired
    private RedisTemplateUtils redisTemplateValue;

    private static final Map<String, WebSocketSession> users;


    static {
        users = new HashMap<String, WebSocketSession>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        /*
         * 链接成功后会触发此方法，可在此处对离线消息什么的进行处理
         */
        users.put( session.getId(), session );
        String username = (String) session.getAttributes().get( Constants.DEFAULT_WEBSOCKET_USERNAME );
        System.out.println( username + " connect success ..." );
        session.sendMessage( new TextMessage( username + " 链接成功!!" ) );
        if (redisTemplateValue.hasKey( username )) {
            session.sendMessage( new TextMessage( (String) redisTemplateValue.get( username ) ) );
            redisTemplateValue.remove( username );
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage( session, message );
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        /*
         * 前端 websocket.send() 会触发此方法
         */
        System.out.println( "message -> " + message.getPayload() );
        super.handleTextMessage( session, message );
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        System.err.println( exception.getMessage() );
        System.out.println( "websocket connection closed......" );
        users.remove( session.getId() );
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println( "websocket connection closed......" );
        users.remove( session.getId() );
    }

    public void sendMessageToUser(String info) {
        redisTemplateValue.set( "admin", info);
        redisTemplateValue.set( "tangyu169", info);
        TextMessage message = new TextMessage( info );
        Iterator<Map.Entry<String, WebSocketSession>> it = userIterator();
        while (it.hasNext()) {
            WebSocketSession session = it.next().getValue();
            String loginName = (String) session.getAttributes().get( Constants.DEFAULT_WEBSOCKET_USERNAME );
            if (redisTemplateValue.hasKey( loginName )) {
                try {
                    if (session.isOpen()) {
                        session.sendMessage( message );
                    }
                    redisTemplateValue.remove( loginName );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Iterator<Map.Entry<String, WebSocketSession>> userIterator() {
        Set<Map.Entry<String, WebSocketSession>> entrys = users.entrySet();
        return entrys.iterator();
    }
}
