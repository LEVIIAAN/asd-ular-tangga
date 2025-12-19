import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage; // [FIX 1] Import ini sebelumnya hilang
import java.io.File;                 // [FIX 2] Penting untuk membaca file
import javax.imageio.ImageIO;        // [FIX 3] Penting untuk memuat gambar
import java.util.*;
import java.util.List;

public class PlayerPanel extends JPanel {
    private final Stack<Player> players;
    private Player current;

    // Cache gambar
    private BufferedImage[] charIcons = new BufferedImage[5];

    public PlayerPanel(Stack<Player> players) {
        this.players = players;
        setOpaque(false);
        setBorder(new EmptyBorder(25, 15, 25, 15));

        loadCharIcons(); // Load gambar saat panel dibuat
    }

    // Method untuk load gambar dari folder assets
    private void loadCharIcons() {
        try {
            // Karena folder assets ada di dalam src, kita pakai tanda '/' di depan
            String[] files = {
                    "SnakeLadder/src/assets/dolphin.png",
                    "SnakeLadder/src/assets/turtle.png",
                    "SnakeLadder/src/assets/submarine.png",
                    "SnakeLadder/src/assets/shark.png",
                    "SnakeLadder/src/assets/octopus.png"
            };

            // Loop untuk 5 karakter
            for(int i=0; i<5; i++) {
                // PENTING: Gunakan getResource karena folder ada di dalam src
                java.net.URL imgUrl = getClass().getResource(files[i]);

                if (imgUrl != null) {
                    charIcons[i] = ImageIO.read(imgUrl);
                } else {
                    System.out.println("❌ Gagal menemukan gambar: " + files[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrent(Player p) {
        this.current = p;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        // Tinggi dinamis agar pas
        int h = 60 + (players.size() * 75) + 20;
        return new Dimension(360, h);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gambar Background Panel
        OceanTheme.drawRPGPanel(g2, 0, 0, getWidth(), getHeight());

        // Header Text
        g2.setFont(OceanTheme.FONT_TITLE.deriveFont(18f));
        g2.setColor(OceanTheme.BORDER_GOLD);
        String title = "DIVERS SQUAD";
        int titleW = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (getWidth() - titleW) / 2, 40);

        // Garis Pembatas Header
        g2.setColor(new Color(255, 255, 255, 30));
        g2.drawLine(20, 50, getWidth()-20, 50);

        // Konfigurasi List Pemain
        int startY = 65;
        int slotH = 65;
        int gap = 10;

        List<Player> list = new ArrayList<>(players);
        Collections.reverse(list); // Balik urutan agar Player 1 di atas (opsional)

        for (Player p : list) {
            boolean isActive = (p == current);
            int x = 20;
            int w = getWidth() - 40;

            // Gambar Slot Latar Belakang
            OceanTheme.drawSlot(g2, x, startY, w, slotH);

            // Highlight jika giliran pemain ini
            if (isActive) {
                g2.setColor(new Color(255, 215, 0, 40));
                g2.fillRoundRect(x, startY, w, slotH, 10, 10);
                g2.setColor(OceanTheme.BORDER_GOLD);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(x, startY, w, slotH, 10, 10);
            }

            // --- BAGIAN ICON (FIX LOGIC) ---
            int iconSize = 40;
            int iconY = startY + (slotH - iconSize)/2;
            int type = p.getCharacterType();

            // Cek apakah gambar tersedia
            if (type >= 0 && type < 5 && charIcons[type] != null) {
                // Gambar PNG
                g2.drawImage(charIcons[type], x + 12, iconY, iconSize, iconSize, null);

                // Penanda warna kecil (lingkaran) di pojok kanan bawah icon
                g2.setColor(p.getColor());
                g2.fillOval(x + 12 + iconSize - 10, iconY + iconSize - 10, 10, 10);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1));
                g2.drawOval(x + 12 + iconSize - 10, iconY + iconSize - 10, 10, 10);
            } else {
                // FALLBACK: Jika gambar tidak ada, gunakan lingkaran warna biasa
                g2.setColor(p.getColor());
                g2.fillOval(x + 12, iconY, iconSize, iconSize);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(x + 12, iconY, iconSize, iconSize);
            }

            // --- TEKS NAMA & SKOR ---
            g2.setFont(OceanTheme.FONT_TEXT);
            g2.setColor(Color.WHITE);
            g2.drawString(p.getName(), x + 60, startY + 25);

            g2.setFont(new Font("Verdana", Font.PLAIN, 10));
            g2.setColor(new Color(180, 180, 180));
            g2.drawString("Pos: " + p.getPosition() + " | Pearls: " + p.getScore(), x + 60, startY + 45);

            // Tanda "YOUR TURN"
            if (isActive) {
                g2.setColor(OceanTheme.BUTTON_ORANGE);
                g2.setFont(new Font("Arial", Font.BOLD, 9));
                g2.drawString("YOUR TURN", x + w - 65, startY + 35);
            }

            startY += slotH + gap;
        }
    }
}