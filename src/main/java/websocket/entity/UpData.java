package websocket.entity;

/**
 * 上行格式类型
 *
 * @author xiaopeng
 */
public class UpData {
    /**
     * 数据类型  1 Userinfo  2 MsgInfo
     */
    private static Integer type;

    /**
     * data
     */
    private static Object data;

    public static Integer getType() {
        return type;
    }

    public static void setType(Integer type) {
        UpData.type = type;
    }


    public static Object getData() {
        return data;
    }

    public static void setData(Object data) {
        UpData.data = data;
    }
}
