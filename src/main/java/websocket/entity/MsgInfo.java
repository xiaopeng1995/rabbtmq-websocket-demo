package websocket.entity;

/**
 * 上行消息信息
 * @author xiaopeng
 */
public class MsgInfo {
    private static String data;

    public static String getData() {
        return data;
    }

    public static void setData(String data) {
        MsgInfo.data = data;
    }
}
