package utdallas.wallhack;

import android.graphics.Color;

public class WallData {
    private int x;
    private int y;
    private String mat;

    private int color;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

     public WallData(int x_, int y_, String mat_){
         mat = mat_;
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
         else if(mat == "ac"){
             color = Color.YELLOW;
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

    public String getMat() {
        return mat;
    }

    public void setMat(String mat) {
        this.mat = mat;
    }
}
