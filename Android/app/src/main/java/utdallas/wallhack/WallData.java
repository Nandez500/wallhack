package utdallas.wallhack;

import java.io.Serializable;

public class WallData implements Serializable {
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

    public String getType() {
        return type;
    }

    public double getAngleDeg() {
        return angleDeg;
    }

    public double getxPos() {
        return xPos;
    }

    public double getyPos() {
        return yPos;
    }

    public double getzPos() {
        return zPos;
    }
}
