package websocket.entity;

/**
 * 上行用户信息
 * @author xiaopeng
 * @date 2018/1/31
 */
public class Userinfo {
    private static String id;

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        Userinfo.id = id;
    }
}
