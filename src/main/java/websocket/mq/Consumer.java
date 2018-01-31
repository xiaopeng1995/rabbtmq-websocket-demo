package websocket.mq;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.configuration.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ Publisher
 */
public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    // rabbitmq
    protected Connection conn;
    protected Channel channel;

    // websocket
    protected String WEBSOCKET_TOPIC = "websocket.verify";

    /**
     * init config of sms
     *
     * @param config config file
     */
    public void init(AbstractConfiguration config) {
        try {
            logger.trace("Initializing RabbitMQ consumer resources ...");
            ConnectionFactory cf = new ConnectionFactory();
            cf.setUsername(config.getString("rabbitmq.userName", ConnectionFactory.DEFAULT_USER));
            cf.setPassword(config.getString("rabbitmq.password", ConnectionFactory.DEFAULT_PASS));
            cf.setVirtualHost(config.getString("rabbitmq.virtualHost", ConnectionFactory.DEFAULT_VHOST));
            cf.setAutomaticRecoveryEnabled(true);
            cf.setExceptionHandler(new RabbitMQExceptionHandler());
            this.conn = cf.newConnection(Address.parseAddresses(config.getString("rabbitmq.addresses")));
            this.channel = conn.createChannel();
            this.channel.exchangeDeclare(WEBSOCKET_TOPIC, "topic", true);
            this.channel.queueDeclare("websocket_queue", true, false, true, null);
            this.channel.queueBind("websocket_queue", WEBSOCKET_TOPIC, "#");
            this.channel.basicConsume("websocket_queue", true, new ConsumerHandler(this.channel));

        } catch (IOException | TimeoutException e) {
            logger.error("Failed to connect to RabbitMQ servers", e);
            throw new IllegalStateException("Init RabbitMQ communicator failed");
        }
    }

    public void destroy() {
        try {
            if (this.conn != null) {
                this.conn.close();
            }
        } catch (IOException e) {
            logger.warn("Communicator error: Exception closing the RabbitMQ connection, exiting uncleanly", e);
        }
    }
}
