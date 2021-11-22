import java.awt.*;

/**
 * 绘制文字类
 */
public class TextShape extends Shape {

    public TextShape(int x1, int y1) {
        super(x1, y1);
        Toolbar toolbar = Toolbar.tb;
        this.content = toolbar.getTextString();
        this.font = toolbar.getSelectedFont();
        this.size = toolbar.getSelectedSize();
    }

    public TextShape(int x1, int y1, Color customColor, String op, int w, String con, Font f, int s) {
        super(x1, y1, customColor, op, w);
        this.content = con;
        this.font = f;
        this.size = s;
        System.out.println("new success text");
    }

    @Override
    public void draw(Graphics g) {
        System.out.println("texting");
        Graphics2D p = (Graphics2D) g;
        p.setColor(this.selectedColor);
        p.setFont(this.font.deriveFont((float) size));
        p.drawString(this.content, x1, y1);
    }
}