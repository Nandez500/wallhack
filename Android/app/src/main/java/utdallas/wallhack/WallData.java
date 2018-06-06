package utdallas.wallhack;

import android.graphics.Color;

public class WallData {
    private int x;
    private int y;
    private String material;
    private int color;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public WallData(String [] params){
        material = params[0];
        x = Integer.parseInt(params[1]);
        y = Integer.parseInt(params[2]);

        if (material == "wood") {
            color = Color.BLACK;
        }
        else if(material == "wire/pvc") {
            color = Color.RED;
        }
        else if(material == "metal"){
            color = Color.LTGRAY;

        }

    }
     public WallData(int x_, int y_, String mat){
         material = mat;
         x = x_;
         y = y_;

         if (mat == "wood") {
             color = Color.BLACK;
         }
         else if(mat == "wire/pvc") {
             color = Color.RED;
         }
         else if(mat == "metal"){
             color = Color.LTGRAY;

         }
     }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }
}
