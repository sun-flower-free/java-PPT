import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;

/**
 * 工具栏
 */
public class Toolbar extends JPanel {
    // 单例模式
    public static Toolbar tb;
    // 文本输入文本框
    private JTextField jtf1 = new JTextField("Input Content Here", 20);
    // “前景色”单选框
    JRadioButton fore;
    // 字体选择器
    JComboBox<String> fontChooser = new JComboBox<>();
    // 字号选择器
    JComboBox<Integer> sizeChooser = new JComboBox<>();
    // 事件监听类
    EventListener el = new EventListener();

    public Toolbar(int j) {
        //System.out.println("New ToolBar " + j);
        //addToolbar(j);
    }

    public Toolbar(){
        //addToolbar();
    }

    /**
     获取实例的静态方法
     */
    public static Toolbar getInstance() {
        System.out.println("Get a ToolBar");
        if (tb == null) {
            tb = new Toolbar();
        }
        return tb;
    }

    public void addToolbar(){
        JPanel northPanel = new JPanel();
        JPanel southPanel = new JPanel();
        this.setLayout(new BorderLayout());

        //System.out.println("ToolBar linked to " + Window.now);
        String[] shapeArray = { "铅笔", "直线", "矩形", "圆", "文本", "橡皮擦", "帮助", "插入", "清空" };
        // 添加所有的按钮并添加按钮点击事件监听
        for (String item : shapeArray) {
            JButton tmp = new JButton(item);
            tmp.addActionListener(el);
            northPanel.add(tmp);
        }
        // 添加颜色列表
        northPanel.add(new Colorlist());
        // 用于判断设置前景色还是设置背景色
        ButtonGroup bg = new ButtonGroup();
        fore = new JRadioButton("前景色", true);
        JRadioButton back = new JRadioButton("背景色");
        // 加入到同一个按钮组
        fore.setBackground(Color.WHITE);
        back.setBackground(Color.WHITE);
        bg.add(fore);
        bg.add(back);
        // 添加单选框
        northPanel.add(fore);
        northPanel.add(back);
        // 添加线条粗细调整到toolbar的第二行
        southPanel.add(new Linewidth());
        // 添加字体选择器到toolbar的第二行
        setComboBox(Utils.getSystemFonts());
        southPanel.add(fontChooser);
        // 添加字号选择器到toolbar的第二行
        for (int i = 9; i <= 72; i++) {
            sizeChooser.addItem(Integer.valueOf(i));
        }
        sizeChooser.setSelectedIndex(7);
        //sizeChooser.setBackground(Color.WHITE);
        southPanel.add(sizeChooser);
        // 添加文本框到toolbar的第二行
        southPanel.add(jtf1);
        // Toolbar布局
        northPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        southPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        northPanel.setBackground(Color.WHITE);
        southPanel.setBackground(Color.WHITE);

        this.add(northPanel, BorderLayout.NORTH);
        this.add(southPanel, BorderLayout.SOUTH);
        this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
    }

    /**
     获取文本
      */
    public String getTextString() {
        return this.jtf1.getText();
    }

    /**
     当前选中的是否是前景色
     */
    public boolean isForebackgroundSelected() {
        return this.fore.isSelected();
    }

    public void setComboBox(List<String> list) {
        for (var item : list) {
            this.fontChooser.addItem(item);
        }
    }

    public Font getSelectedFont() {
        return Utils.map.get(this.fontChooser.getSelectedItem());
    }

    public int getSelectedSize() {
        return (Integer) this.sizeChooser.getSelectedItem();
    }

    public class EventListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton instance = (JButton) e.getSource();
            // 点击的是颜色（因为颜色按钮没有文字）
            if ("".equals(e.getActionCommand())) {
                if (isForebackgroundSelected()) {
                    // 设置前景色
                    Board.boards.get(Window.now).selectedColor = instance.getBackground();
                } else {
                    // 设置背景色
                    Board.boards.get(Window.now).backgroundColor = instance.getBackground();
                    // 刷新画板
                    Board.boards.get(Window.now).setBackgroundColor();
                }
            } else {
                // 选择帮助操作时输出帮助信息并return
                if (instance.getText().equals("帮助")) {
                    showHelpMessage();
                    Board.boards.get(Window.now).requestFocus();
                    return;
                } else if (instance.getText().equals("插入")) {
                    System.out.println("[\33[34;2m面板按钮检测\33[;m] " + Board.boards.get(Window.now).operation + " id: " + Window.now);
                    Board.boards.get(Window.now).loadImageToPanel();
                    Board.boards.get(Window.now).validate();
                    Board.boards.get(Window.now).requestFocus();
                    return;
                } else if (instance.getText().equals("清空")) {
                    System.out.println("[\33[34;2m面板按钮检测\33[;m] " + Board.boards.get(Window.now).operation + " id: " + Window.now);
                    Board.boards.get(Window.now).clear(true);
                    Board.boards.get(Window.now).requestFocus();
                    return;
                }
                // 否则将操作赋值给参数
                Board.boards.get(Window.now).operation = instance.getText();
                System.out.println("[\33[34;2m面板按钮检测\33[;m] " + Board.boards.get(Window.now).operation + " id: " + Window.now);
            }
            // 将焦点还给绘图区域（没有焦点没有办法响应键盘事件）
            Board.boards.get(Window.now).requestFocus();
        }

        private void showHelpMessage() {
            JOptionPane.showInternalMessageDialog(null, Utils.getHelpMessage(), "帮助", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
