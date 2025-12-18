import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class BoardPanel extends JPanel {
    private static final int TOTAL_NODES = 60;
    private BufferedImage mapImage;
    private final Point2D.Double[] manualNodes = new Point2D.Double[TOTAL_NODES];
    private final GameBoard board;
    private final Stack<Player> players;
    private List<Integer> highlightPath = new ArrayList<>();
    private BufferedImage[] charIcons = new BufferedImage[3];

    public BoardPanel(GameBoard board, Stack<Player> players) {
        this.board = board;
        this.players = players;
        setPreferredSize(new Dimension(600, 680));

        // Load Gambar (Sama seperti sebelumnya)
        try {
            File absFile = new File("C:\\Users\\Farhan Fitran\\Documents\\Coding Projects\\ASD\\Final Project\\SnakeLadder\\assets\\ocean_bg.png");
            File relFile = new File("assets/ocean_bg.png");
            File oldFile = new File("assets/bgfixhd.jpg");

            if (absFile.exists()) mapImage = ImageIO.read(absFile);
            else if (relFile.exists()) mapImage = ImageIO.read(relFile);
            else if (oldFile.exists()) mapImage = ImageIO.read(oldFile);
        } catch (Exception e) { e.printStackTrace(); }
        loadCharIcons();
        initPreciseOceanNodes();
    }

    // ... (METHOD initPreciseOceanNodes SAMA PERSIS SEPERTI SEBELUMNYA, TIDAK DIUBAH) ...
    private void initPreciseOceanNodes() {
        manualNodes[0] = new Point2D.Double(0.143, 0.820); // 1
        manualNodes[1] = new Point2D.Double(0.210, 0.821); // 2
        manualNodes[2] = new Point2D.Double(0.283, 0.806); // 3
        manualNodes[3] = new Point2D.Double(0.405, 0.836); // 4
        manualNodes[4] = new Point2D.Double(0.485, 0.860); // 5
        manualNodes[5] = new Point2D.Double(0.558, 0.866); // 6
        manualNodes[6] = new Point2D.Double(0.628, 0.863); // 7
        manualNodes[7] = new Point2D.Double(0.693, 0.850); // 8
        manualNodes[8] = new Point2D.Double(0.753, 0.827); // 9
        manualNodes[9] = new Point2D.Double(0.803, 0.793); // 10
        manualNodes[10] = new Point2D.Double(0.772, 0.731); // 11
        manualNodes[11] = new Point2D.Double(0.573, 0.737); // 12
        manualNodes[12] = new Point2D.Double(0.487, 0.759); // 13
        manualNodes[13] = new Point2D.Double(0.433, 0.717); // 14
        manualNodes[14] = new Point2D.Double(0.422, 0.661); // 15
        manualNodes[15] = new Point2D.Double(0.343, 0.659); // 16
        manualNodes[16] = new Point2D.Double(0.258, 0.677); // 17
        manualNodes[17] = new Point2D.Double(0.190, 0.640); // 18
        manualNodes[18] = new Point2D.Double(0.170, 0.581); // 19
        manualNodes[19] = new Point2D.Double(0.095, 0.521); // 20
        manualNodes[20] = new Point2D.Double(0.085, 0.467); // 21
        manualNodes[21] = new Point2D.Double(0.185, 0.457); // 22
        manualNodes[22] = new Point2D.Double(0.267, 0.434); // 23
        manualNodes[23] = new Point2D.Double(0.345, 0.451); // 24
        manualNodes[24] = new Point2D.Double(0.417, 0.491); // 25
        manualNodes[25] = new Point2D.Double(0.440, 0.551); // 26
        manualNodes[26] = new Point2D.Double(0.493, 0.591); // 27
        manualNodes[27] = new Point2D.Double(0.570, 0.573); // 28
        manualNodes[28] = new Point2D.Double(0.642, 0.587); // 29
        manualNodes[29] = new Point2D.Double(0.740, 0.646); // 30
        manualNodes[30] = new Point2D.Double(0.818, 0.647); // 31
        manualNodes[31] = new Point2D.Double(0.888, 0.629); // 32
        manualNodes[32] = new Point2D.Double(0.922, 0.570); // 33
        manualNodes[33] = new Point2D.Double(0.890, 0.523); // 34
        manualNodes[34] = new Point2D.Double(0.863, 0.474); // 35
        manualNodes[35] = new Point2D.Double(0.852, 0.420); // 36
        manualNodes[36] = new Point2D.Double(0.775, 0.404); // 37
        manualNodes[37] = new Point2D.Double(0.698, 0.413); // 38
        manualNodes[38] = new Point2D.Double(0.640, 0.469); // 39
        manualNodes[39] = new Point2D.Double(0.542, 0.469); // 40
        manualNodes[40] = new Point2D.Double(0.463, 0.441); // 41
        manualNodes[41] = new Point2D.Double(0.417, 0.403); // 42
        manualNodes[42] = new Point2D.Double(0.392, 0.354); // 43
        manualNodes[43] = new Point2D.Double(0.383, 0.304); // 44
        manualNodes[44] = new Point2D.Double(0.428, 0.284); // 45
        manualNodes[45] = new Point2D.Double(0.508, 0.273); // 46
        manualNodes[46] = new Point2D.Double(0.628, 0.274); // 47
        manualNodes[47] = new Point2D.Double(0.740, 0.319); // 48
        manualNodes[48] = new Point2D.Double(0.815, 0.303); // 49
        manualNodes[49] = new Point2D.Double(0.865, 0.273); // 50
        manualNodes[50] = new Point2D.Double(0.862, 0.216); // 51
        manualNodes[51] = new Point2D.Double(0.827, 0.173); // 52
        manualNodes[52] = new Point2D.Double(0.758, 0.154); // 53
        manualNodes[53] = new Point2D.Double(0.672, 0.143); // 54
        manualNodes[54] = new Point2D.Double(0.600, 0.153); // 55
        manualNodes[55] = new Point2D.Double(0.545, 0.199); // 56
        manualNodes[56] = new Point2D.Double(0.442, 0.207); // 57
        manualNodes[57] = new Point2D.Double(0.372, 0.174); // 58
        manualNodes[58] = new Point2D.Double(0.293, 0.180); // 59
        manualNodes[59] = new Point2D.Double(0.230, 0.209); // 60
    }

    public void updatePath(List<Integer> path) {
        this.highlightPath = path;
        repaint();
    }

    public void clearPath() {
        this.highlightPath.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Draw Map
        if (mapImage != null) g2.drawImage(mapImage, 0, 0, getWidth(), getHeight(), null);
        else {
            g2.setColor(OceanTheme.WATER_CYAN);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. Draw Links & Scores
        drawLinks(g2);
        drawScores(g2);

        // 3. Highlight Path
        if (!highlightPath.isEmpty()) {
            g2.setColor(new Color(255, 215, 0, 150));
            g2.setStroke(new BasicStroke(6));
            for (int i = 0; i < highlightPath.size() - 1; i++) {
                Point p1 = getNodePos(highlightPath.get(i));
                Point p2 = getNodePos(highlightPath.get(i+1));
                if (p1 != null && p2 != null) g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        // 4. Draw Players (UPDATED)
        int[] offsetCount = new int[TOTAL_NODES + 1];
        for (Player p : players) {
            int posIndex = p.getPosition();
            Point pos = getNodePos(posIndex);

            if (pos != null) {
                int shiftX = (offsetCount[posIndex] * 10) - 5;
                int shiftY = (offsetCount[posIndex] * 10) - 5;

                // [UBAH] Mengirim tipe karakter ke method draw
                drawDiver(g2, pos.x + shiftX, pos.y + shiftY, p.getColor(), p.getCharacterType());
                offsetCount[posIndex]++;
            }
        }
    }

    // [REVISI 1] BUBBLES LEBIH KECIL (75%)
    private void drawScores(Graphics2D g2) {
        Map<Integer, Integer> points = board.getPointsMap();
        g2.setFont(new Font("Segoe UI", Font.BOLD, 9)); // Font diperkecil

        for (Map.Entry<Integer, Integer> entry : points.entrySet()) {
            int pos = entry.getKey();
            int value = entry.getValue();
            Point p = getNodePos(pos);

            if (p != null) {
                int size = 21; // [UBAH] Ukuran dikecilkan (28 -> 21)
                int x = p.x + 8;
                int y = p.y - 12;

                GradientPaint bubbleEffect = new GradientPaint(
                        x, y, new Color(135, 206, 250, 200),
                        x+size, y+size, new Color(0, 105, 148, 200)
                );
                g2.setPaint(bubbleEffect);
                g2.fillOval(x, y, size, size);

                // Shine
                g2.setColor(new Color(255, 255, 255, 180));
                g2.fillOval(x + 4, y + 4, 4, 4);
                g2.setColor(new Color(255, 255, 255, 100));
                g2.setStroke(new BasicStroke(1));
                g2.drawOval(x, y, size, size);

                // Text
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                String txt = "+" + value;
                int txtW = fm.stringWidth(txt);
                g2.drawString(txt, x + (size - txtW)/2, y + (size/2) + 4);
            }
        }
    }

    // [REVISI 2] GAMBAR KARAKTER SESUAI PILIHAN
    private void drawDiver(Graphics2D g2, int x, int y, Color c, int type) {
        int size = 40; // Ukuran Avatar
        int offset = size / 2;

        // Cek apakah gambar tersedia
        if (type >= 0 && type < charIcons.length && charIcons[type] != null) {
            // GAMBAR BAYANGAN
            g2.setColor(new Color(0,0,0,60));
            g2.fillOval(x - offset + 3, y - offset + 5, size, size-5);

            // GAMBAR KARAKTER (PNG)
            g2.drawImage(charIcons[type], x - offset, y - offset, size, size, null);

            // INDIKATOR WARNA PEMAIN (Lingkaran kecil di pojok kanan atas)
            g2.setColor(c);
            g2.fillOval(x + offset - 12, y - offset, 12, 12);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1));
            g2.drawOval(x + offset - 12, y - offset, 12, 12);
        } else {
            // FALLBACK (Jika gambar tidak ada, pakai lingkaran warna biasa)
            g2.setColor(c);
            g2.fillOval(x - 12, y - 12, 24, 24);
            g2.setColor(Color.WHITE);
            g2.drawOval(x - 12, y - 12, 24, 24);
        }
    }

    private void drawLinks(Graphics2D g2) {
        for (Link link : board.getLinks().values()) {
            Point p1 = getNodePos(link.getFrom());
            Point p2 = getNodePos(link.getTo());
            if (p1 != null && p2 != null) {
                // Glow
                g2.setColor(new Color(0, 255, 255, 50));
                g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                // Dashed Line
                g2.setColor(new Color(255, 255, 255, 180));
                g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{8, 8}, 0));
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    private Point getNodePos(int nodeIndex) {
        if (nodeIndex < 1 || nodeIndex > TOTAL_NODES) return null;
        Point2D.Double p = manualNodes[nodeIndex - 1];
        if (p == null) return new Point(0,0);
        return new Point((int)(p.x * getWidth()), (int)(p.y * getHeight()));
    }

    private void loadCharIcons() {
        try {
            String[] files = {"assets/dolphin.png", "assets/turtle.png", "assets/submarine.png"};
            for(int i=0; i<3; i++) {
                File f = new File(files[i]);
                if(f.exists()) charIcons[i] = ImageIO.read(f);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}