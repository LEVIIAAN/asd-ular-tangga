import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.*;
import java.util.List;

public class PlayerPanel extends JPanel {
    private final Stack<Player> players;
    private Player current;
    private BufferedImage[] charIcons = new BufferedImage[5];

    public PlayerPanel(Stack<Player> players) {
        this.players = players;
        setOpaque(false);
        // Padding atas/bawah dikurangi sedikit (15 -> 10)
        setBorder(new EmptyBorder(10, 10, 10, 10));
        loadCharIcons();
    }

    private void loadCharIcons() {
        try {
            String[] files = {
                    "/assets/dolphin.png", "/assets/turtle.png",
                    "/assets/submarine.png", "/assets/shark.png",
                    "/assets/octopus.png"
            };
            for(int i=0; i<files.length; i++) {
                java.net.URL imgUrl = getClass().getResource(files[i]);
                if (imgUrl != null) charIcons[i] = ImageIO.read(imgUrl);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void setCurrent(Player p) {
        this.current = p;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        // [FIX] Hitungan tinggi presisi agar Layout Manager tidak memotong
        // Header (50) + (JumlahPemain * 50) + (Jarak * 4) + Padding(20)
        int count = players.size();
        int contentHeight = 50 + (count * 50) + (Math.max(0, count - 1) * 5) + 20;
        return new Dimension(340, contentHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        OceanTheme.drawRPGPanel(g2, 0, 0, getWidth(), getHeight());

        // Header
        g2.setFont(OceanTheme.FONT_TITLE.deriveFont(18f));
        g2.setColor(OceanTheme.BORDER_GOLD);
        String title = "DIVERS SQUAD";
        int titleW = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (getWidth() - titleW) / 2, 30); // Posisi Y naik dikit

        // Garis
        g2.setColor(new Color(255, 255, 255, 30));
        g2.drawLine(20, 40, getWidth()-20, 40);

        // --- KONFIGURASI SLOT COMPACT (50px) ---
        int startY = 50;
        int slotH = 50;  // Tinggi slot dikecilkan agar muat 5
        int gap = 5;     // Jarak antar slot

        List<Player> list = new ArrayList<>(players);
        Collections.reverse(list);

        for (Player p : list) {
            boolean isActive = (p == current);
            int x = 15;
            int w = getWidth() - 30;

            // Gambar Slot Latar
            OceanTheme.drawSlot(g2, x, startY, w, slotH);

            // Highlight Giliran
            if (isActive) {
                g2.setColor(new Color(255, 215, 0, 40));
                g2.fillRoundRect(x, startY, w, slotH, 10, 10);
                g2.setColor(OceanTheme.BORDER_GOLD);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(x, startY, w, slotH, 10, 10);
            }

            // Icon Character
            int iconSize = 36; // Icon sedikit lebih kecil
            int iconY = startY + (slotH - iconSize)/2;
            int type = p.getCharacterType();

            if (type >= 0 && type < 5 && charIcons[type] != null) {
                g2.drawImage(charIcons[type], x + 10, iconY, iconSize, iconSize, null);
            }

            // Teks Nama
            g2.setFont(OceanTheme.FONT_TEXT);
            g2.setColor(Color.WHITE);
            g2.drawString(p.getName(), x + 55, startY + 20);

            // Teks Skor
            g2.setFont(new Font("Verdana", Font.PLAIN, 10));
            g2.setColor(new Color(180, 180, 180));
            g2.drawString("Pos: " + p.getPosition() + " | Pearls: " + p.getScore(), x + 55, startY + 38);

            // Indikator Giliran
            if (isActive) {
                g2.setColor(OceanTheme.BUTTON_ORANGE);
                g2.setFont(new Font("Arial", Font.BOLD, 9));
                g2.drawString("YOUR TURN", x + w - 65, startY + 28);
            }

            startY += slotH + gap;
        }
    }
}