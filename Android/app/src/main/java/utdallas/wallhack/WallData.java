package utdallas.wallhack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;

public class WallData implements Serializable {

    private final String type;
    private final float angleDeg;
    private float xPos;

    public void setyPos(float yPos) {
        this.yPos = yPos;
    }

    private float yPos;
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

    @Override
    public String toString() {
        return "WallData{" +
                "type='" + type + '\'' +
                ", xPos=" + xPos +
                ", yPos=" + yPos +
                ", texture=" + texture +
                '}';
    }

    public WallData(Context context, String t, float ang, float x, float y, float z) {
        type = t;
        angleDeg = ang;
        xPos = x;
        yPos = y;
        zPos = z;

        switch (type){
            case "Wood":
                //color = Color.BLACK;
                texture = BitmapFactory.decodeResource(context.getResources(),R.drawable.wood_texture);
                break;
            case "Metal":
                //color =
                texture = BitmapFactory.decodeResource(context.getResources(),R.drawable.metal_texture);
                break;
            case "Wire/PVC":
                //color = Color.RED;
                texture = BitmapFactory.decodeResource(context.getResources(),R.drawable.pipe);
                break;
            case "ac":
                //color = Color.YELLOW;
                texture = BitmapFactory.decodeResource(context.getResources(),R.drawable.lightning);
                break;
            default:
                texture = BitmapFactory.decodeResource(context.getResources(),R.drawable.unknown);
        }
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

    public void setxPos(float x){
        this.xPos = x;
    }

    public float getyPos() {
        return yPos;
    }

    public float getzPos() {
        return zPos;
    }
}
