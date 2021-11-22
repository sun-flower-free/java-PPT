import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * 窗口类，维护界面，使用静态窗口维护
 */
public class Window {
    //当前操作页面序列号
    public static int now = 0;
    //事件监听
    public static Event event = null;
    //维护按钮序列
    public static ArrayList<JButton> pages = new ArrayList<>();
    //窗口主界面
    public static JFrame window = new JFrame("模拟PPT---  " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    //侧边滚动栏布局部分
    private static final JScrollPane side = new JScrollPane();
    private static final JPanel sideContainer = new JPanel();
    private static final JPanel visible = new JPanel();
    private static final JButton newPage = new JButton("新建页面");
    private static final JButton newPage2 = new JButton("新建页面");
    //页脚文字显示
    JLabel tip = new JLabel(" 当前无页面，请按新建或打开页面添加");
    //窗口大小，用于窗口布局
    Dimension d = window.getSize();

    Window(){
        Board.boards.add(new Board(0));
        Board.boards.get(0).bindEvent();
        event = new Event();
        drawUI();
        //init();
    }

    /**
     * 窗口UI设计类，所有的窗口设计都在这里
     */
    private void drawUI(){
        //获取当前系统界面格式并同步, Nimbus, Windows, Motif
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }catch(Exception e) {
            System.out.println(e);
        }
        newPage.addActionListener(event);
        newPage.setPreferredSize(new Dimension(160, 30));
        newPage2.addActionListener(event);
        newPage2.setPreferredSize(new Dimension(160, 30));
        //滚动区域界面布局
        side.setPreferredSize(new Dimension(180, d.height));
        sideContainer.setLayout (new GridBagLayout());
        // 添加占位符
        GridBagConstraints gbc = new GridBagConstraints ();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        visible.setPreferredSize(new Dimension(100, side.getHeight()-5-30));
        sideContainer.add(newPage, gbc);
        sideContainer.add(visible, gbc);
        //设置垂直和水平滚动条
        side.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        side.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        side.setViewportView(sideContainer);
        //布局整个窗口
        window.setMinimumSize(new Dimension(1100, 600));
        window.setSize(1000, 800);
        window.setLayout(new BorderLayout());
        Toolbar.tb = new Toolbar();
        Toolbar.tb.addToolbar();

        ImageIcon icon=new ImageIcon(Objects.requireNonNull(getClass().getResource("/icon.png")));  //xxx代表图片存放路径，2.png图片名称及格式
        window.setIconImage(icon.getImage());

        window.add(Toolbar.getInstance(), BorderLayout.NORTH);
        window.add(side, BorderLayout.WEST);
        window.add(tip, BorderLayout.SOUTH);
        window.setJMenuBar(new Menu());

        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * 新建页面函数
     * @param b 不定参数输入，用于构建界面，有两个输入方式：第一种是单击按钮新建界面，第二种是通过文件新建页面
     */
    public static void addPage(Board... b){
        GridBagConstraints gbc = new GridBagConstraints ();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        System.out.println("========================================");
        //添加新的页面
        JButton page = new JButton("第" + (pages.size()+1) + "张");
        int btn_higth = 100;
        page.setPreferredSize(new Dimension(160, btn_higth));
        //添加新的右侧绘图
        if (b.length == 0){
            Board.boards.add(new Board(Board.boards.size()));
        }
        else {
            Board.boards.add(b[0]);
        }
        Board.boards.get(Board.boards.size() - 1).repaint();
        Board.boards.get(Board.boards.size() - 1).setVisible(false);
        System.out.format("[\33[32;2m新建页面\33[;m] 当前总页数：" + Board.boards.size());
        //添加监听
        page.addActionListener(event);
        //加入按钮管理系统
        pages.add(page);
        if (Board.boards.size() < 10) {
            //先去掉之前的空占位符
            sideContainer.remove(visible);
            //加入侧边栏
            sideContainer.add(page, gbc);
            //添加新的占位符
            visible.setPreferredSize(new Dimension(100, side.getHeight() - btn_higth * pages.size() - 5 - 30));
            sideContainer.add(visible, gbc);
        } else {
            //在第十页以后在最下面添加新增按钮，方便操作
            //先去掉之前的空占位符
            sideContainer.remove(visible);
            sideContainer.remove(newPage2);
            //加入侧边栏
            sideContainer.add(page, gbc);
            //添加新的占位符
            visible.setPreferredSize(new Dimension(100, side.getHeight() - btn_higth * pages.size() - 5 - 30));
            sideContainer.add(visible, gbc);
            sideContainer.add(newPage2, gbc);
        }
        System.out.println("  visible length:" + visible.getHeight());
        //重绘
        window.validate();
    }

    /**
     * 事件监听类
     */
    class Event implements ActionListener, ChangeListener, MouseWheelListener, ItemListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JButton instance){
                if (instance.getText().equals("新建页面")){
                    addPage();
                }
                else if (instance.getText().matches("第[0-9]*张")) {
                    //获取当前画板序列号
                    int pre = now;
                    now = pages.indexOf(instance) + 1;
                    System.out.println("-----------------\33[33;2m选择第 " + now + " 张\33[m-------------------");
                    tip.setText("  第" + now + "张(序列号)");
                    window.add(Board.boards.get(now), BorderLayout.CENTER);
                    //刷新界面
                    Board.boards.get(pre).setVisible(false);
                    Board.boards.get(now).setVisible(true);
                    window.revalidate();//对面板中的组件重新布局并绘制
                    window.repaint();//对window本身进行重绘
                    window.setVisible(true);
                    //画板绑定事件监听
                    Board.boards.get(now).bindEvent();
                    //Board.boards.get(now).repaint();
                    System.out.println(Board.boards.get(now).history.size());
                }
            }
        }

        @Override
        public void stateChanged(ChangeEvent e) {

        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

        }

        @Override
        public void itemStateChanged(ItemEvent e) {

        }

    }

/*
 * 废弃方法
 */
//    void init() {
//        JFrame window = new JFrame();
//        window.setSize(800, 600);
//        JPanel myPanel1 = new JPanel();//面板1
//        JPanel myPanel2 =new JPanel();//面板2
//        JButton button1 = new JButton("开始");//按钮1
//        JButton button2 = new JButton("开始");//按钮2
//        JButton button3 = new JButton("开始");//按钮3
//        JSplitPane jSplitPane =new JSplitPane();//设定为左右拆分布局
//        jSplitPane.setOneTouchExpandable(true);//让分割线显示出箭头
//        jSplitPane.setContinuousLayout(true);//操作箭头，重绘图形
//        //jSplitPane.setPreferredSize(new Dimension (100,200));
//        jSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);//设置分割线方向
//        myPanel1.setSize(400, 400);
//        myPanel2.setSize(200, 400);
//        jSplitPane.setLeftComponent(myPanel1);//布局中添加组件 ，面板1
//        jSplitPane.setRightComponent(myPanel2);//添加面板2
//        jSplitPane.setDividerSize(1);//设置分割线的宽度
//        //jSplitPane.setDividerLocation(100);//设置分割线位于中央
//        jSplitPane.setDividerLocation(400);//设定分割线的距离左边的位置
//        window.setContentPane(jSplitPane);
//        //pack();
//        myPanel1.add(button1);
//        myPanel2.add(button3);
//        myPanel2.add(button2);
//        //window.add(myPanel1);
//        myPanel1.setBorder(BorderFactory.createLineBorder(Color.green));
//        //window.add(myPanel2);
//        myPanel2.setBorder(BorderFactory.createLineBorder(Color.red));
//        window.setVisible(true);
//        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    }
//
//    private void drawUI_0(){
//        JFrame window = new JFrame("Hello Fuck");
//        window.setLayout(new BorderLayout());
//        window.setSize(900, 600);
//        Board.boards.add(new Board(0));//0
//        Board.boards.add(new Board(1));//1
//        Board.boards.add(new Board(2));//2
//        Board.boards.add(new Board(3));//3
//        window.add(Board.boards.get(2), BorderLayout.CENTER);
//        window.add(Toolbar.getInstance(), BorderLayout.NORTH);
//        Board.boards.get(now).requestFocus();
//        window.setVisible(true);
//        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        Board.boards.get(now).bindEvent();
//    }
}






