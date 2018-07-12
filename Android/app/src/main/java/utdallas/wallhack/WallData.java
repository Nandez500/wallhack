package utdallas.wallhack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;

public class WallData implements Serializable {

    private final String type;
    private final float angleDeg;
    private final float xPos;
    private final float yPos;
    private final float zPos;
    private int color;
    private Bitmap texture;

    public Bitmap getTexture(){
        return texture;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public WallData(Context context,String t, float ang, float x, float y, float z) {
        type = t;
        angleDeg = ang;
        xPos = x;
        yPos = y;
        zPos = z;

        switch (type){
            case "wood":
                //color = Color.BLACK;
                texture = BitmapFactory.decodeResource(context.getResources(),R.drawable.wood_texture);
                break;
            case "metal":
                //color =
                texture = BitmapFactory.decodeResource(context.getResources(),R.drawable.metal_texture);
                break;
            case "wire/pvc":
                //color = Color.RED;
                texture = BitmapFactory.decodeResource(context.getResources(),R.drawable.pipe);
                break;
            case "ac":
                //color = Color.YELLOW;
                texture = BitmapFactory.decodeResource(context.getResources(),R.drawable.lightning);
                break;
        }
    }

    public String toString() {
        return String.format("type: %s\nangleDeg: %f\nx: %f\ny: %f\nz: %f", type, angleDeg, xPos, yPos, zPos);
    }

    public String getType() {
        return type;
    }

    public float getAngleDeg() {
        return angleDeg;
    }

    public float getxPos() {
        return xPos;
    }

    public float getyPos() {
        return yPos;
    }

    public float getzPos() {
        return zPos;
    }
}
