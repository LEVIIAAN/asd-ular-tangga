import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {
    private final DiceFacePanel dicePanel;
    private final JLabel statusLabel;
    private final JTextArea pathArea;
    private final OceanTheme.Button rollBtn;

    public ControlPanel(ActionListener onRoll) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(new EmptyBorder(15, 20, 15, 20)); // Padding vertikal dikurangi (20->15)

        // TITLE
        JLabel title = new JLabel("ACTION LOG");
        title.setFont(OceanTheme.FONT_TITLE.deriveFont(18f));
        title.setForeground(OceanTheme.BORDER_GOLD);
        title.setAlignmentX(CENTER_ALIGNMENT);
        add(title);

        add(Box.createVerticalStrut(10)); // Jarak dikurangi

        // DADU FULL
        dicePanel = new DiceFacePanel();
        dicePanel.setPreferredSize(new Dimension(80, 80));
        dicePanel.setMaximumSize(new Dimension(80, 80));
        dicePanel.setAlignmentX(CENTER_ALIGNMENT);
        add(dicePanel);

        add(Box.createVerticalStrut(10)); // Jarak dikurangi

        // BUTTON
        rollBtn = new OceanTheme.Button("ROLL DICE");
        rollBtn.setAlignmentX(CENTER_ALIGNMENT);
        rollBtn.setMaximumSize(new Dimension(180, 45)); // Tinggi tombol dikurangi dikit
        rollBtn.addActionListener(onRoll);
        add(rollBtn);

        add(Box.createVerticalStrut(10));

        // STATUS
        statusLabel = new JLabel("WAITING...", SwingConstants.CENTER);
        statusLabel.setFont(OceanTheme.FONT_TEXT);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(statusLabel);

        add(Box.createVerticalStrut(10));

        // LOG AREA
        pathArea = new JTextArea(3, 20);
        pathArea.setEditable(false);
        pathArea.setLineWrap(true);
        pathArea.setWrapStyleWord(true);
        pathArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        pathArea.setBackground(new Color(0, 0, 0, 60));
        pathArea.setForeground(Color.WHITE);
        pathArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(OceanTheme.BORDER_GOLD, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        pathArea.setMaximumSize(new Dimension(300, 60)); // Batasi tinggi log
        add(pathArea);
    }

    public void updateDice(int val) { dicePanel.setValue(val); }
    public void setStatus(String t, Color c) {
        statusLabel.setText(t);
        if (c.equals(OceanTheme.CORAL_ORANGE)) statusLabel.setForeground(Color.RED);
        else statusLabel.setForeground(OceanTheme.PEARL_GOLD);
    }
    public void setPath(String t) { pathArea.setText(t); }
    public void toggleBtn(boolean b) { rollBtn.setEnabled(b); }

    @Override
    protected void paintComponent(Graphics g) {
        OceanTheme.drawRPGPanel((Graphics2D)g, 0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }
}