import java.awt.*;

/**
 基本图形的抽象类（橡皮擦类和多图形类都从该类继承）
 */
public abstract class Shape {

    protected int x1, y1;
    protected static Board.EventListener el = Board.boards.get(Window.now).event;
    protected Color selectedColor;
    protected String operation;
    protected int width;
    //用于设计其他图形
    public int x2, y2;
    //用于设计字体
    public String content;
    public Font font; // 字体
    public int size;

    public Shape(int x1, int y1) {
        Board board = Board.boards.get(Window.now);
        el = board.event;
        selectedColor = el.getSelectedColor();
        operation = el.getOperation();
        width = el.getWidth();
        this.x1 = x1;
        this.y1 = y1;
        //System.out.println("Shape linked to " + board.id);
    }

    public Shape(int x1, int y1, Color customColor) {
        this(x1, y1);
        selectedColor = customColor;
    }

    public Shape(int x1, int y1, Color customColor, String op, int w) {
        this(x1, y1, customColor);
        operation = op;
        width = w;
    }

    /**
     * 绘图方法
     * @param p 画笔工具
     */
    public abstract void draw(Graphics p);

    public void refresh() {
    }

}
