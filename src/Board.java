import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * 画板面板类，所有的绘图数据存储在这里，使用ArrayList维护
 */
public class Board extends JPanel {
    //维护看板数组，所有的看板都存储在这里
    public static final ArrayList<Board> boards = new ArrayList<>();
    //看板监听事件
    public EventListener event = new EventListener();
    // 点击点和落点的坐标
    private int x1, x2, y1, y2;
    // 当前选中的颜色
    public Color selectedColor;
    // 当前背景色
    public Color backgroundColor;
    // 当前使用的操作
    public String operation;
    // 当前线条粗细
    public int width;
    // 画笔
    private Graphics Pen;
    // 所有画过的图
    public Deque<Shape> history = new LinkedList<>();
    // 保存实时按键的栈
    private Deque<Integer> stack = new LinkedList<>();
    // 保存按鼠标时的历史状态（用于笔和橡皮擦的撤销）
    private Deque<Shape> previous = new LinkedList<>();
    // 面板序列号
    public int id;
    private BufferedImage image = null;

    /**
     * 基础构造类
     * @param i 当前面板id
     */
    Board(int i) {
        //添加编号，便于编译检查
        id = i;
        System.out.println("[\33[32;2m新建页面\33[;m] Get a Board");
        // 为画图区域添加背景色
        this.setBackground(Color.WHITE);
        // 为绘图区域添加鼠标/键盘监听
        //this.bindEvent();
        // 不打开文件
        image = null;
        // 得到窗口焦点
        //this.requestFocus();
        //System.out.println("[\33[32;2m新建页面\33[;m] Board构造函数 id：" + id);
    }

    /**
     * 读取构造类，用于新建文件
     * @param a 当前面板id
     * @param b 当前选择的前景色
     * @param c 当前选择的背景色
     * @param d 画笔宽度
     * @param e 当前执行操作
     * @param f 历史绘图记录
     */
    Board(int a, Color b, Color c, int d, String e, Deque<Shape> f){
        System.out.println("[\33[32;2m新建页面\33[;m] Get a Board");
        id = a;
        selectedColor = b;
        backgroundColor = c;
        width = d;
        operation = e;
        history = f;
        // 为画图区域添加背景色
        this.setBackground(backgroundColor);
        image = null;
        //System.out.println("[\33[32;2m新建页面\33[;m] Board构造函数 id：" + id);
    }

    public void bindEvent() {
        // 得到监听器实例
        // 添加鼠标/鼠标移动/键盘的监听器
        // （因为el实现了鼠标MouseListener、鼠标移动MouseMotionListener、键盘KeyListener的接口，所以可以这样写）
        this.addMouseListener(event);
        this.addMouseMotionListener(event);
        this.addKeyListener(event);
        // 将绘图区域的“笔”传给监听器，在监听器内进行绘制
        event.setPen(this.getGraphics());
        repaint();
        requestFocus();
        System.out.println("Add EventListener OK");
    }

    @Override
    public void paint(Graphics p) {
        // 该函数是窗口大小变化时自动调用的函数，其中的p默认是this.getGraphics()（也就是绘图区域的画笔）
        // 为父类重新绘制（即添加背景色）
        super.paint(p);
        // 如果读取了图片，则先把图片画上
        if (image != null) {
            p.drawImage(image, 0, 0, null);
        }
        // 遍历绘图历史，绘制该图形
        for (Shape item : history) {
            item.draw(p);
        }
    }

    /**
     * 保存为图片
     */
    public void savePanelAsImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("saved.png"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            // String.split的参数是正则表达式，为了匹配纯文本的.需要把参数用方括号括起来
            String[] tmp = file.getName().split("[.]");
            if (tmp.length <= 1) {
                JOptionPane.showMessageDialog(null, "保存文件没有拓展名，保存失败...");
                return;
            }
            String extension = tmp[tmp.length - 1];
            // HELP:文档内说imageio支持jpg，但本地测试保存无反应
            if (!extension.equals("png") && !extension.equals("jpg")) {
                JOptionPane.showMessageDialog(null, "拓展名非法（允许使用：png/jpg），保存失败...");
                return;
            }
            Dimension imageSize = this.getSize();
            BufferedImage image = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            this.paint(graphics);
            graphics.dispose();
            try {
                ImageIO.write(image, extension, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从图片打开
     */
    public void loadImageToPanel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(".png", "*.png", "*.jpg"));
        int result = fileChooser.showOpenDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        try {
            // 读取该张图片
            image = ImageIO.read(new File(filePath));
            // 清除所有历史
            clear(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存当前文件
     */
    public static void saveBoard() {
        if(boards.size() <= 1){
            JOptionPane.showMessageDialog(null, "还未创建文件，按新建或者打开创建文件");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("saved.zppt"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            // String.split的参数是正则表达式，为了匹配纯文本的.需要把参数用方括号括起来
            String[] tmp = file.getName().split("[.]");
            if (tmp.length <= 1) {
                JOptionPane.showMessageDialog(null, "保存文件无拓展名，保存失败...");
                return;
            }
            String extension = tmp[tmp.length - 1];
            // HELP:文档内说imageio支持jpg，但本地测试保存无反应
            if (!extension.equals("zppt")) {
                JOptionPane.showMessageDialog(null, "无效拓展名，保存失败...");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(file);       //定一个文件输出流，用来写文件
                ObjectOutputStream oos = new ObjectOutputStream(fos);                //用文件输出流构造对象输出流

                oos.writeInt(boards.size());
                for (var board : boards) {
                    oos.writeInt(board.id);
                    oos.writeObject(board.selectedColor);
                    oos.writeObject(board.backgroundColor);
                    oos.writeInt(board.width);
                    oos.writeObject(board.operation);
                    oos.writeInt(board.history.size());
                    for (var h : board.history){
                        oos.writeInt(h.x1);
                        oos.writeInt(h.y1);
                        oos.writeObject(h.selectedColor);
                        oos.writeObject(h.operation);
                        oos.writeInt(h.width);
                        //System.out.println(h.operation);
                        switch (h.operation) {
                            case "橡皮擦":
                                break;
                            case "文本":
                                oos.writeObject(h.content);
                                //oos.writeObject(h.font);
                                oos.writeInt(h.size);
                                break;
                            default:
                                oos.writeInt(h.x2);
                                oos.writeInt(h.y2);
                        }
                    }
                }
                oos.close();
                fos.close();
                System.out.println("save success");
            } catch (IOException e) {
                System.out.println("save failed");
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取文件
     */
    public static void loadBoard() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(".zppt", "*.zppt"));
        int result = fileChooser.showOpenDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        try {
            FileInputStream fis = new FileInputStream(filePath);      //构造文件输入流
            ObjectInputStream ois = new ObjectInputStream(fis);               //用文件输出流初始化对象输入流
            int num = ois.readInt();
            System.out.print(num);
            for(int i = 0; i < num; ++i){
                int a = ois.readInt(); // id
                a = Board.boards.size();
                Color b = (Color) ois.readObject(); // selectedColor
                Color c = (Color) ois.readObject(); // backgroundColor
                int d = ois.readInt(); // width
                String e = (String) ois.readObject(); // operation
                int f = ois.readInt(); // num of history
                System.out.println(" " + a + " " + f);
                Deque<Shape> g = new LinkedList<>();
                for (int j = 0; j < f; ++j){
                    //System.out.println(j);
                    int x1 = ois.readInt(); // x1
                    int y1 = ois.readInt(); // y1
                    Color cc = (Color) ois.readObject(); // customColor
                    String dd = (String) ois.readObject(); // operation
                    int ee = ois.readInt(); // width
                    //System.out.print(dd + " ");
                    Shape h;
                    switch (dd) {
                        case "橡皮擦":
                            h = new Eraser(x1, y1, cc, dd, ee);
                            break;
                        case "文本":
                            String con = (String) ois.readObject();
                            //Font font = (Font) ois.readObject();
                            Font font = Toolbar.getInstance().getSelectedFont();
                            int size = ois.readInt();
                            h = new TextShape(x1, y1, cc, dd, ee, con, font, size);
                            break;
                        default:
                            int x2 = ois.readInt();
                            int y2 = ois.readInt();
                            newShape tmp = new newShape(x1, y1, cc, dd, ee, x2, y2);
                            MultiShape tmp2 = new MultiShape(tmp);
                            h = (MultiShape) tmp2;
                            //System.out.println("(" + x1 + "," + y1 + ") (" + x2 + "," + y2 + ")");
                    }
                    //h = new newShape(aa, bb, cc, dd, ee);
                    g.add(h);
                }
                Board board = new Board(a, b, c, d, e, g);
                board.event.setPen(board.getGraphics());
                Window.addPage(board);
                System.out.format("[\33[32;2m添加页面\33[;m] 当前总页数：" + Board.boards.size());
            }
            System.out.println("[\33[32;2m添加页面\33[;m] load success");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("load failed");
            e.printStackTrace();
        }

    }

    public void clearFile() {
        // 移除内部的图片缓存
        this.image = null;
    }

    /**
     * 撤销
     * @param fixed 判定撤销方式：锁定撤销(true)时，起点不会被删除，在动态拖拽操作中使用；
     *              非锁定撤销时(false)，起点和边同时被删除，在手动调用的撤销操作中使用
     */
    public void revert(boolean fixed) {
        Shape toCompare = fixed ? previous.peek() : previous.poll();
        Shape tmp;
        while ((tmp = history.peekLast()) != null) {
            if (!tmp.equals(toCompare)) {
                history.pollLast();
            } else {
                break;
            }
        }
        //System.out.println("delete something");
        repaint();
    }

    /**
     * 清除所有状态并重新绘制
     * @param clearFile 判断是否清除
     */
    public void clear(boolean clearFile) {
        history.clear();
        previous.clear();
        if (clearFile) {
            // 清除之前打开的文件
            clearFile();
        }
        repaint();
    }

    /**
     * 设置背景色
     */
    public void setBackgroundColor() {
        setBackground(backgroundColor);
        for (var item : history) {
            if (item instanceof Eraser) {
                // 刷新历史橡皮到当前背景颜色
                item.refresh();
            }
        }
        // 重新绘制
        repaint();
    }

    @Override
    public void repaint(){
        super.repaint();
        System.out.println("[内部事件] \33[31;2mRepaint on board: " + id + "\33[m");
    }

    /**
     * 面板的事件监听类
     */
    public class EventListener extends MouseInputAdapter implements KeyListener {

        EventListener() {
            System.out.println("[\33[32;2m新建页面\33[;m] Get EventListener");
            // 默认画笔为黑色，背景色为白色，选中操作为铅笔
            selectedColor = Color.BLACK;
            backgroundColor = Color.WHITE;
            operation = "铅笔";
            width = 1;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // 保存该个时刻的最新状态
            previous.push(history.peekLast());
            // 按下鼠标时调用的函数
            x1 = e.getX();
            y1 = e.getY();
            x2 = e.getX();
            y2 = e.getY();
            // 原地画点，为了和mouseDragged协作实现动态拖拽的效果
            if (operation.equals("橡皮擦")) {
                System.out.println("[\33[35;2m事件检测\33[m] Eraser using Pressed in " + id);
                addEraser();
            } else if (operation.equals("文本")) {
                System.out.println("[\33[35;2m事件检测\33[m] Text using Pressed in " + id);
                addText();
            } else {
                System.out.println("[\33[35;2m事件检测\33[m] Something using Pressed in " + id);
                addShape();
            }
            requestFocus();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            x2 = e.getX();
            y2 = e.getY();
            switch (operation) {
                case "铅笔":
                    addShape();
                    x1 = x2;
                    y1 = y2;
                    break;
                case "橡皮擦":
                    addEraser();
                    x1 = x2;
                    y1 = y2;
                    break;
                case "文本":
                    revert(true);
                    addText();
                    break;
                default:
                    revert(true);
                    addShape();
                    break;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            x2 = e.getX();
            y2 = e.getY();
            switch (operation) {
                case "铅笔":
                    System.out.println("[\33[35;2m事件检测\33[m] Pencil using Released in " + id);
                    addShape();
                    break;
                case "橡皮擦":
                    System.out.println("[\33[35;2m事件检测\33[m] Eraser using Released in " + id);
                    addEraser();
                    break;
                case "文本":
                    System.out.println("[\33[35;2m事件检测\33[m] Text using Released in " + id);
                    revert(true);
                    addText();
                    break;
                default:
                    System.out.println("[\33[35;2m事件检测\33[m] Something using Released in " + id);
                    revert(true);
                    addShape();
                    break;
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // 有大于一个按键并且上一个按键为Ctrl
            if (stack.size() >= 1 && stack.peek() == 17) {
                switch (e.getKeyCode()) {
                    case 90 -> revert(false); // Ctrl+Z -> 撤销
                    case 83 -> saveBoard(); // Ctrl+S -> 保存文件
                    case 79 -> loadBoard(); // Ctrl+O -> 打开文件
                    case 81 -> clear(true); // Ctrl+Q -> 清空历史
                    case 72 -> showHelpMessage();// Ctrl+H -> 弹出帮助信息
                }
            }
            // 将按键码压栈
            stack.push(e.getKeyCode());
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // 松开按键则弹栈
            stack.pop();
        }

        public Color getSelectedColor() {
            return selectedColor;
        }

        public String getOperation() {
            return operation;
        }

        public Color getBackgroundColor() {
            return backgroundColor;
        }

        public int getWidth() {
            return width;
        }

        public void setPen(Graphics pen) {
            Pen = pen;
        }

        public Deque<Shape> getHistory() {
            return history;
        }

        private void addShape() {
            // 添加新图
            Shape tmp = new MultiShape(x1, y1, x2, y2);
            // 加入历史
            history.add(tmp);
            // 用pen将tmp画在图上
            tmp.draw(Pen);
        }

        private void addEraser() {
            // 添加新图
            Shape tmp = new Eraser(x1, y1);
            // 加入历史
            history.add(tmp);
            // 用pen将tmp画在图上
            tmp.draw(Pen);
        }

        private void addText() {
            // 添加新文本
            Shape tmp = new TextShape(x2, y2);
            // 加入历史
            history.add(tmp);
            // 用pen将tmp画在图上
            tmp.draw(Pen);
        }

        private void showHelpMessage() {
            JOptionPane.showInternalMessageDialog(null, Utils.getHelpMessage(), "帮助", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 画图类
     */
    public static class MultiShape extends Shape {

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
            //System.out.println("drawing");
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
                    System.out.println("[\33[36;2m绘画检测\33[m] Rect on " + Window.now);
                    break;
                case "圆":
                    this.swap();
                    p.drawOval(x1, y1, x2 - x1, y2 - y1);
                    System.out.println("[\33[36;2m绘画检测\33[m] Oval on " + Window.now);
                    break;
            }
        }
    }

}
