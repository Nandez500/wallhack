package utdallas.wallhack;

public interface Constants {
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_TARGET= 1;
    public static final int MESSAGE_WRITE = 2;
    //public static final int MESSAGE_WRITE = 3;
    //public static final int MESSAGE_DEVICE_NAME = 4;
    //public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public class WallTarget {
        public final String type;
        public final double angleDeg;
        public final double xPos;
        public final double yPos;
        public final double zPos;

        public WallTarget(String t, double ang, double x, double y, double z) {
            type = t;
            angleDeg = ang;
            xPos = x;
            yPos = y;
            zPos = z;
        }

        public String toString() {
            return String.format("type: %s\nangleDeg: %f\nx: %f\ny: %f\nz: %f", type, angleDeg, xPos, yPos, zPos);
        }
    }
}
