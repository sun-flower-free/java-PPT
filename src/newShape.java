import java.awt.*;

/**
 * 新建绘图类，用于读取文件
 */
public class newShape extends Shape {

    public newShape(int x1, int y1, Color customColor, String op, int w) {
        super(x1, y1, customColor, op, w);
        System.out.println("new success eraser");
    }

    public newShape(int x1, int y1, Color customColor, String op, int w, int x2, int y2) {
        super(x1, y1, customColor, op, w);
        this.x2 = x2;
        this.y2 = y2;
        System.out.println("new success others");
    }

    public newShape(int x1, int y1, Color customColor, String op, int w, String con, Font f, int s) {
        super(x1, y1, customColor, op, w);
        this.content = con;
        this.font = f;
        this.size = s;
        System.out.println("new success text");
    }

    @Override
    public void draw(Graphics p) {

    }
}
