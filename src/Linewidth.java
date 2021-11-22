import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 调整线宽
 */
public class Linewidth extends JPanel {
    private static final long serialVersionUID = 15100151;

    EventListener el = new EventListener();

    public Linewidth() {
        addLinewidth();
    }

    public Linewidth(int i) {
        //el = Board.boards.get(i).event;
        addLinewidth();
    }

    public void addLinewidth(){
        this.add(new JLabel("线条粗细"));
        JSlider slider = new JSlider(1, 9, 1);
        slider.setMajorTickSpacing(4);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        //System.out.println("Linewidth linked to " + el.num);
        slider.addChangeListener(el);
        slider.setBackground(Color.WHITE);
        this.add(slider);
        this.setBackground(Color.WHITE);
    }

    public class EventListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider jslider = (JSlider) e.getSource();
            Board.boards.get(Window.now).width = jslider.getValue();
            // 将焦点还给绘图区域（没有焦点没有办法响应键盘事件）
            requestFocus();
        }
    }
}
