package websocket.mq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import websocket.util.JsonUtils;
import websocket.ws.BroadcastSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;


/**
 * Consumer Handler
 */
public class ConsumerHandler extends DefaultConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerHandler.class);


    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    public ConsumerHandler(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            logger.info("收到消息："+new String(body));
            String verifyType = envelope.getRoutingKey();
            Map map = JsonUtils.Mapper.readValue(new String(body), Map.class);
            if("only".equals(verifyType))
            {
                BroadcastSocket.broadcast(map.get("data").toString(),map.get("userId").toString());
            }
            if ("all".equals(verifyType))
            {
                BroadcastSocket.broadcast(map.get("data").toString());
            }

        } catch (Exception e) {
            logger.warn("发送 msg 发生错误:", e);
        }
    }
}
