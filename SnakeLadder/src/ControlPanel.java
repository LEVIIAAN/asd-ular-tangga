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
        // Padding diperkecil
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // JUDUL
        JLabel title = new JLabel("ACTION LOG");
        title.setFont(OceanTheme.FONT_TITLE.deriveFont(20f)); // Font kecil
        title.setForeground(OceanTheme.BORDER_GOLD);
        title.setAlignmentX(CENTER_ALIGNMENT);
        add(title);

        add(Box.createVerticalStrut(15));

        // DADU (Diperkecil)
        JPanel diceContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        diceContainer.setOpaque(false);
        dicePanel = new DiceFacePanel();
        dicePanel.setPreferredSize(new Dimension(70, 70)); // Ukuran dadu 70x70
        diceContainer.add(dicePanel);
        add(diceContainer);

        add(Box.createVerticalStrut(10));

        // TOMBOL
        rollBtn = new OceanTheme.Button("ROLL DICE");
        rollBtn.setAlignmentX(CENTER_ALIGNMENT);
        rollBtn.setMaximumSize(new Dimension(160, 45)); // Tombol lebih ramping
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

        /// --- LOG AREA (Perbaikan Tampilan) ---
        // Label Judul Kecil untuk kotak hitam
        JLabel logLabel = new JLabel("ADVENTURE LOG");
        logLabel.setFont(new Font("Verdana", Font.BOLD, 10));
        logLabel.setForeground(new Color(180, 180, 180)); // Abu terang
        logLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(logLabel);

        add(Box.createVerticalStrut(5));

        pathArea = new JTextArea(3, 20);
        pathArea.setEditable(false);
        pathArea.setLineWrap(true);
        pathArea.setWrapStyleWord(true);
        pathArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

        // Ubah warna background agar tidak hitam pekat, tapi biru gelap transparan
        pathArea.setBackground(new Color(0, 0, 0, 80));
        pathArea.setForeground(Color.WHITE); // Teks Putih

        JScrollPane scroll = new JScrollPane(pathArea);
        scroll.setBorder(BorderFactory.createLineBorder(OceanTheme.BORDER_GOLD, 1)); // Border Emas
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll);
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