package utdallas.wallhack;

import android.graphics.Color;

public class WallData {
    public final String type;
    public final double angleDeg;
    public final double xPos;
    public final double yPos;
    public final double zPos;

    public WallData(String t, double ang, double x, double y, double z) {
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
