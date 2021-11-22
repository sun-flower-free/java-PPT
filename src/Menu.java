import javax.swing.*;
import java.awt.event.*;

/**
 * 菜单栏
 */
public class Menu extends JMenuBar {

    private Event event = new Event();

    Menu(){
        //布局菜单栏
        this.add(createFileMenu());
        this.add(createEditMenu());
        this.add(createHelpMenu());
    }

    /**
     *定义“文件”菜单
     */
    private JMenu createFileMenu() {
        JMenu menu=new JMenu("文件(F)");
        menu.setMnemonic(KeyEvent.VK_F);    //设置快速访问符
        JMenuItem item=new JMenuItem("新建(N)",KeyEvent.VK_N);
        //item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));
        item.addActionListener(event);
        menu.add(item);
        item=new JMenuItem("打开(O)",KeyEvent.VK_O);
        //item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));
        item.addActionListener(event);
        menu.add(item);
        item=new JMenuItem("保存(S)",KeyEvent.VK_S);
        //item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
        item.addActionListener(event);
        menu.add(item);
        menu.addSeparator();
        item=new JMenuItem("退出(E)",KeyEvent.VK_E);
        //item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK));
        item.addActionListener(event);
        menu.add(item);
        return menu;
    }
    /**
     * 定义“编辑”菜单
     */
    private JMenu createEditMenu() {
        JMenu menu=new JMenu("编辑(E)");
        menu.setMnemonic(KeyEvent.VK_E);
        JMenuItem item=new JMenuItem("撤销(U)",KeyEvent.VK_U);
        item.addActionListener(event);
        //item.setEnabled(false);
        menu.add(item);
        menu.addSeparator();
        item=new JMenuItem("插入图片(I)",KeyEvent.VK_I);
        item.addActionListener(event);
        menu.add(item);
        item=new JMenuItem("保存为图片(P)",KeyEvent.VK_P);
        item.addActionListener(event);
        menu.add(item);
        menu.addSeparator();
        JCheckBoxMenuItem cbMenuItem=new JCheckBoxMenuItem("自动换行");
        cbMenuItem.addActionListener(event);
        //menu.add(cbMenuItem);
        return menu;
    }
    /**
     * 定义“帮助”菜单
     */
    private JMenu createHelpMenu() {
        JMenu menu=new JMenu("帮助(H)");
        menu.setMnemonic(KeyEvent.VK_H);
        JMenuItem item=new JMenuItem("帮助(H)",KeyEvent.VK_H);
        item.addActionListener(event);
        //item.setEnabled(false);
        menu.add(item);
        menu.addSeparator();
        item=new JMenuItem("About(A)",KeyEvent.VK_A);
        item.addActionListener(event);
        menu.add(item);
        return menu;
    }

    /**
     * 事件监听类
     */
    class Event implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JMenuItem instance){
                if (instance.getText().equals("新建(N)")) {
                    //new Window();
                    Window.addPage();
                }
                else if (instance.getText().equals("打开(O)")) {
                    System.out.print("File open: ");
                    Board.loadBoard();
                }
                else if (instance.getText().equals("保存(S)")) {
                    System.out.print("File save: ");
                    Board.saveBoard();
                }
                else if (instance.getText().equals("退出(E)")) {
                    int isSave = JOptionPane.showConfirmDialog(null,"是否保存当前文件？","保存",JOptionPane.YES_NO_OPTION);
                    if(isSave == 0){
                        System.out.print("File save and ");
                    }
                    System.out.println("File close");
                    Window.window.dispatchEvent(new WindowEvent(Window.window,WindowEvent.WINDOW_CLOSING) );
                }
                else if (instance.getText().equals("撤销(U)")) {
                    System.out.println("File revert");
                    Board.boards.get(Window.now).revert(false);
                }
                else if (instance.getText().equals("插入图片(I)")) {
                    System.out.println("File insert");
                    Board.boards.get(Window.now).loadImageToPanel();
                }
                else if (instance.getText().equals("保存为图片(P)")) {
                    System.out.print("File save as picture");
                    Board.boards.get(Window.now).savePanelAsImage();
                }
                else if (instance.getText().equals("帮助(H)")) {
                    JOptionPane.showInternalMessageDialog(null, Utils.getHelpMessageAll(), "帮助", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("Get help");
                }
                else if (instance.getText().equals("About(A)")) {
                    JOptionPane.showInternalMessageDialog(null, Utils.getAboutMe(), "About", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("It's me");
                }
            }
        }

    }

}
