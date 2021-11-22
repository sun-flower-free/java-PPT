import java.awt.*;

/**
 * 绘画图形类，现未用到
 */
public class MultiShape extends Shape {

    public MultiShape(int x1, int y1, int x2, int y2) {
        super(x1, y1);
        this.x2 = x2;
        this.y2 = y2;
    }

    public MultiShape(int x1, int y1, Color customColor, String op, int w, int x2, int y2) {
        super(x1, y1, customColor, op, w);
        this.x2 = x2;
        this.y2 = y2;
        System.out.println("new success others");
    }

    public MultiShape(newShape n){
        super(n.x1, n.y1, n.selectedColor, n.operation, n.width);
        this.x2 = n.x2;
        this.y2 = n.y2;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D p = (Graphics2D) g;
        p.setColor(this.selectedColor);
        p.setStroke(new BasicStroke(this.width));
        this.innerDraw(p);
    }

    private void swap() {
        // 保证x1<=x2,y1<=y2，在绘制矩形、圆时需要
        if (x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        if (y1 > y2) {
            int tmp = y1;
            y1 = y2;
            y2 = tmp;
        }
    }

    private void innerDraw(Graphics p) {
        switch (this.operation) {
            case "铅笔":
                p.drawLine(x1, y1, x2, y2);
                System.out.println("[\33[36;2m绘画检测\33[m] Pencil on " + Window.now);
                break;
            case "直线":
                //和铅笔本质上相同
                p.drawLine(x1, y1, x2, y2);
                System.out.println("[\33[36;2m绘画检测\33[m] Line on " + Window.now);
                break;
            case "矩形":
                this.swap();
                p.drawRect(x1, y1, x2 - x1, y2 - y1);
                //System.out.println("Rect linked to " + Window.now);
                break;
            case "圆":
                this.swap();
                p.drawOval(x1, y1, x2 - x1, y2 - y1);
                //System.out.println("Oval linked to " + Window.now);
                break;
        }
    }
}