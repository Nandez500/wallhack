package utdallas.wallhack;

import android.graphics.Color;

public class WallData {
    private int x;
    private int y;
    private boolean wood;
    private boolean metal;
    private boolean pvc;
    private boolean ac;

    private int color;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public WallData(){

        if (wood) {
            color = Color.BLACK;
        }
        else if(ac) {
            color = Color.RED;
        }
        else if(metal){
            color = Color.LTGRAY;
        }

    }
     public WallData(int x_, int y_, String mat){
         //material = mat;
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

    public boolean isWood() {
        return wood;
    }

    public void setWood(boolean wood) {
        this.wood = wood;
    }

    public boolean isMetal() {
        return metal;
    }

    public void setMetal(boolean metal) {
        this.metal = metal;
    }

    public boolean isPvc() {
        return pvc;
    }

    public void setPvc(boolean pvc) {
        this.pvc = pvc;
    }

    public boolean isAc() {
        return ac;
    }

    public void setAc(boolean ac) {
        this.ac = ac;
    }
}
