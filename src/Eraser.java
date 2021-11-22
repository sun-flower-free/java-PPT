import java.awt.*;

/**
 * 橡皮擦
 */
public class Eraser extends Shape {

    public Eraser(int x1, int y1) {
        super(x1, y1, el.getBackgroundColor());
    }

    public Eraser(int x1, int y1, Color customColor, String op, int w) {
        super(x1, y1, customColor, op, w);
        System.out.println("new success eraser");
    }

    @Override
    public void draw(Graphics p) {
        System.out.println("Eraser");
        p.setColor(this.selectedColor);
        // 以(x1,y1)为圆心，this.width*3为半径的白色实心圆
        p.fillOval(x1 - this.width * 3, y1 - this.width * 3, this.width * 6, this.width * 6);
    }

    @Override
    public void refresh() {
        // 刷新颜色
        this.selectedColor = el.getBackgroundColor();
    }
}
