import javax.swing.*;
import java.awt.*;

public class OceanTheme {
    // --- WARNA LAMA (TETAP DIPERTAHANKAN) ---
    public static final Color WATER_DEEP   = new Color(0, 76, 153);
    public static final Color WATER_CYAN   = new Color(0, 183, 235);
    public static final Color CORAL_ORANGE = new Color(255, 127, 80);
    public static final Color SAND_BEIGE   = new Color(245, 222, 179);
    public static final Color BUBBLE_BLUE  = new Color(173, 216, 230);
    public static final Color PEARL_GOLD   = new Color(255, 215, 0);

    // --- WARNA RPG (UI BARU) ---
    // Background Sidebar sekarang Biru Laut Medium, bukan Cream
    public static final Color SIDEBAR_BG      = new Color(0, 105, 148);
    public static final Color PANEL_BG_DARK   = new Color(30, 40, 50);   // Panel lebih gelap
    public static final Color WOOD_DARK       = new Color(61, 46, 36);
    public static final Color SLOT_BG         = new Color(20, 25, 30);
    public static final Color BORDER_GOLD     = new Color(218, 165, 32);
    public static final Color BUTTON_ORANGE   = new Color(255, 140, 0);

    // Warna Pemain
    public static final Color[] PLAYER_COLORS = {
            new Color(255, 99, 71),   // Red
            new Color(30, 144, 255),  // Blue
            new Color(50, 205, 50),   // Green
            new Color(255, 215, 0),   // Gold
            new Color(138, 43, 226)   // Purple
    };

    // [UBAH] Font diperkecil sedikit agar elegan
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22); // Turun dari 28
    public static final Font FONT_TEXT  = new Font("Verdana", Font.BOLD, 11);  // Turun dari 12

    // --- HELPER GRAPHICS (SAMA SEPERTI SEBELUMNYA) ---
    public static void drawRPGPanel(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(WOOD_DARK);
        g2.fillRoundRect(x, y, w, h, 20, 20);
        GradientPaint gp = new GradientPaint(x, y, new Color(0,0,0,100), x, y+h, new Color(0,0,0,0));
        g2.setPaint(gp);
        g2.fillRoundRect(x, y, w, h, 20, 20);
        g2.setColor(BORDER_GOLD);
        g2.setStroke(new BasicStroke(3)); // Border sedikit lebih tipis
        g2.drawRoundRect(x+2, y+2, w-4, h-4, 20, 20);
        drawCornerBolt(g2, x + 10, y + 10);
        drawCornerBolt(g2, x + w - 10, y + 10);
        drawCornerBolt(g2, x + 10, y + h - 10);
        drawCornerBolt(g2, x + w - 10, y + h - 10);
    }

    private static void drawCornerBolt(Graphics2D g2, int x, int y) {
        g2.setColor(PEARL_GOLD);
        g2.fillOval(x - 3, y - 3, 6, 6);
    }

    public static void drawSlot(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(SLOT_BG);
        g2.fillRoundRect(x, y, w, h, 10, 10);
        g2.setColor(new Color(0,0,0,150));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, w, h, 10, 10);
    }

    public static void drawCurrent(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.setColor(new Color(255, 255, 255, 150));
        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{10}, 0));
        g2.drawLine(x1, y1, x2, y2);
    }

    public static void drawTentacle(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.setColor(new Color(128, 0, 128, 180));
        g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x1, y1, x2, y2);
    }

    // --- BUTTON CLASS ---
    public static class Button extends JButton {
        public Button(String text) {
            super(text);
            setFont(FONT_TITLE.deriveFont(16f)); // Font tombol diperkecil
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, BUTTON_ORANGE.brighter(), 0, h, BUTTON_ORANGE.darker());
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w, h, 15, 15);
            g2.setColor(new Color(139, 69, 19));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, w-2, h-2, 15, 15);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.fillRoundRect(5, 5, w-10, h/2 - 5, 10, 10);
            super.paintComponent(g);
        }
    }
}