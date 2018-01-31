package websocket;

import com.rabbitmq.client.*;
import websocket.mq.RabbitMQExceptionHandler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import websocket.util.JsonUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * Created by xiaopeng on 2018/1/31.
 */
public class TestSend {
    private static final Logger logger = LoggerFactory.getLogger(TestSend.class);
    // rabbitmq
    protected Connection conn;
    protected Channel channel;
    //websocket
    protected String WEBSOCKET_TOPIC = "websocket.verify";

    private void init() {
        try {
            logger.trace("Initializing RabbitMQ publisher resources ...");
            ConnectionFactory cf = new ConnectionFactory();
            cf.setUsername("guest");
            cf.setPassword("guest");
            cf.setVirtualHost("/");
            cf.setAutomaticRecoveryEnabled(true);
            cf.setExceptionHandler(new RabbitMQExceptionHandler());
            this.conn = cf.newConnection(Address.parseAddresses("127.0.0.1"));
            this.channel = conn.createChannel();
            this.channel.exchangeDeclare(WEBSOCKET_TOPIC, "topic", true);
        } catch (IOException | TimeoutException e) {
            logger.error("Failed to connect to RabbitMQ servers", e);
            throw new IllegalStateException("Init RabbitMQ communicator failed");
        }
    }

    @Test
    public void sendMsgOnly() throws Exception{
        init();
        AMQP.BasicProperties properties = MessageProperties.BASIC;
        Map<String, String> map = new ConcurrentHashMap<>();
        //	{"userId":"1001"}
        map.put("userId", "1001");
        map.put("data", "{\"count\":5}");
        this.channel.basicPublish(WEBSOCKET_TOPIC, "only", properties, JsonUtils.Mapper.writeValueAsBytes(map));
    }

    @Test
    public void sendMsgAll() throws Exception{
        init();
        AMQP.BasicProperties properties = MessageProperties.BASIC;
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("data", "{\"count\":66}");
        this.channel.basicPublish(WEBSOCKET_TOPIC, "all", properties, JsonUtils.Mapper.writeValueAsBytes(map));
    }
}
