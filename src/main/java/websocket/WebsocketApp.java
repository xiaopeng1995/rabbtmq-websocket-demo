package websocket;

import com.bazaarvoice.dropwizard.webjars.WebJarBundle;
import websocket.api.BroadcasterResource;
import websocket.mq.Consumer;
import websocket.ws.BroadcastServlet;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xiaopeng on 2018/1/31.
 */
public class WebsocketApp extends Application<Configuration> {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketApp.class);

    public static void main(String[] args) throws Exception {
        logger.info("start websocket server ..");
        new WebsocketApp().run(ArrayUtils.subarray(args, args.length - 2, args.length));
        //测试定时推送
        int i = 0;
        //启动rabbitmq
        logger.info("start rabbimq server ..");
        rabbitmqServer(args);

        //测试定时推送
        while (true) {
            i++;
            Thread.sleep(1000);
            //BroadcastSocket.broadcast("{\"count\":"+i+"}");
        }
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        bootstrap.addBundle(new WebJarBundle());
        bootstrap.addBundle(new AssetsBundle("/web", "/web", "index.html", "web"));
    }

    @Override
    public void run(Configuration conf, Environment env) throws Exception {
        env.jersey().register(new BroadcasterResource(env.getObjectMapper()));
        env.getApplicationContext().getServletHandler().addServletWithMapping(
                BroadcastServlet.class, "/ws/*"
        );
    }

    /**
     * rabbitmqServer
     * @param args 配置文件地址
     * @throws ConfigurationException 配置异常
     */
    private static void rabbitmqServer(String[] args) throws ConfigurationException {
        //rabbitmq 服务器启动
        PropertiesConfiguration rabbitConfigmq;
        //win linux 路径处理
        if (args.length >= 3) {
            rabbitConfigmq = new PropertiesConfiguration(args[0]);
        } else {
            rabbitConfigmq = new PropertiesConfiguration("config/rabbitmq.properties");
        }
        // comsumer
        logger.debug("Initializing consumer ...");
        Consumer consumer = new Consumer();
        consumer.init(rabbitConfigmq);
        logger.info("rabbitmq up and running.");
    }
}
