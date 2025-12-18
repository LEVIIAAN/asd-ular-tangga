import javax.swing.*;
import java.awt.*;

public class DiceFacePanel extends JPanel {
    private int value = 1;

    public DiceFacePanel() {
        setOpaque(false);
    }

    public void setValue(int v) { this.value = v; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 10;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        // 1. Gambar Dadu (Mutiara Putih/Kebiruan)
        GradientPaint pearl = new GradientPaint(x, y, Color.WHITE, x+size, y+size, OceanTheme.BUBBLE_BLUE);
        g2.setPaint(pearl);
        g2.fillRoundRect(x, y, size, size, 25, 25);

        // 2. Border Biru Laut
        g2.setColor(OceanTheme.WATER_DEEP);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x, y, size, size, 25, 25);

        // 3. Pips (Titik Dadu - Warna Biru Gelap)
        drawPips(g2, x, y, size);
    }

    private void drawPips(Graphics2D g2, int x, int y, int size) {
        int r = size / 7;
        int cx = x + size/2, cy = y + size/2;
        int l = x + size/4, rX = x + 3*size/4;
        int t = y + size/4, b = y + 3*size/4;

        g2.setColor(OceanTheme.WATER_DEEP); // Warna Titik

        if(value%2!=0) fillPip(g2, cx-r/2, cy-r/2, r);
        if(value>1) { fillPip(g2, l-r/2, t-r/2, r); fillPip(g2, rX-r/2, b-r/2, r); }
        if(value>3) { fillPip(g2, l-r/2, b-r/2, r); fillPip(g2, rX-r/2, t-r/2, r); }
        if(value==6) { fillPip(g2, l-r/2, cy-r/2, r); fillPip(g2, rX-r/2, cy-r/2, r); }
    }

    private void fillPip(Graphics2D g2, int px, int py, int pr) {
        g2.fillOval(px, py, pr, pr);
    }
}