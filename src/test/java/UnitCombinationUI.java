import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UnitCombinationUI extends JFrame {

    private JComboBox<String> generator1ComboBox;
    private JComboBox<String> generator2ComboBox;
    private JComboBox<String> generator3ComboBox;
    private JLabel resultLabel;

    public UnitCombinationUI() {
        // 设置界面的标题
        super("机组组合界面");

        // 设置界面大小和位置
        setSize(500, 200);
        setLocationRelativeTo(null);

        // 设置布局为网格布局
        setLayout(new GridLayout(3, 2, 10, 10));

        // 添加机组1的下拉框
        add(new JLabel("机组1："));
        generator1ComboBox = new JComboBox<>(new String[] {"机组A", "机组B", "机组C"});
        add(generator1ComboBox);

        // 添加机组2的下拉框
        add(new JLabel("机组2："));
        generator2ComboBox = new JComboBox<>(new String[] {"机组A", "机组B", "机组C"});
        add(generator2ComboBox);

        // 添加机组3的下拉框
        add(new JLabel("机组3："));
        generator3ComboBox = new JComboBox<>(new String[] {"机组A", "机组B", "机组C"});
        add(generator3ComboBox);

        // 添加结果显示的标签
        add(new JLabel("机组组合结果："));
        resultLabel = new JLabel("");
        add(resultLabel);

        // 添加计算按钮
        JButton calculateButton = new JButton("计算");
        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 获取选中的机组
                String generator1 = (String) generator1ComboBox.getSelectedItem();
                String generator2 = (String) generator2ComboBox.getSelectedItem();
                String generator3 = (String) generator3ComboBox.getSelectedItem();

                // 计算机组组合结果
                String result = calculateUnitCombination(generator1, generator2, generator3);

                // 更新结果显示标签
                resultLabel.setText(result);
            }
        });
        add(calculateButton);

        // 设置界面可见
        setVisible(true);
    }

    private String calculateUnitCombination(String generator1, String generator2, String generator3) {
        // TODO: 根据选中的机组计算机组组合结果，并返回字符串类型的结果
        // 这里只是一个示例，具体的计算方法需要根据实际情况进行修改
        return generator1 + " + " + generator2 + " + " + generator3;
    }

    public static void main(String[] args) {
        // 创建机组组合界面对象
        UnitCombinationUI unitCombinationUI = new UnitCombinationUI();
    }
}
