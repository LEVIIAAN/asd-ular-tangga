import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SnakeLadderGame extends JFrame {
    private final GameBoard board;
    private final Stack<Player> playerStack;
    private final Dice dice;
    private final SoundManager sound;

    private BoardPanel boardPanel;
    private ControlPanel controlPanel;
    private PlayerPanel playerPanel;
    private Player currentPlayer;

    // [UBAH] Konstruktor sekarang menerima List<Player> yang sudah jadi
    public SnakeLadderGame(List<Player> playersInput) {
        this.board = new GameBoard(6, 10);
        this.dice = new Dice();
        this.sound = new SoundManager();
        this.playerStack = new Stack<>();

        // [PENTING] Loop terbalik agar urutan giliran benar
        for (int i = playersInput.size()-1; i >= 0; i--) {
            Player p = playersInput.get(i);

            // Reset posisi & ambil skor lama, TAPI jangan buat object baru (new Player)
            p.setPosition(1);
            p.setScore(LeaderboardManager.getScore(p.getName()));

            playerStack.push(p);
            LeaderboardManager.addScore(p.getName(), 0);
        }

        initUI();
        sound.playBgm("bgm.wav");
    }

    private void initUI() {
        setTitle("Ocean Adventure: Treasure Hunt");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(OceanTheme.WATER_CYAN);

        // --- [MULAI KODE BARU: TOMBOL RESTART] ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(5, 10, 0, 10));

        JButton btnRestart = new JButton("↻");
        btnRestart.setFont(new Font("Segoe UI Symbol", Font.BOLD, 24));
        btnRestart.setForeground(Color.WHITE);
        btnRestart.setBackground(OceanTheme.BUTTON_ORANGE);
        btnRestart.setFocusPainted(false);
        btnRestart.setBorder(BorderFactory.createEmptyBorder(2, 15, 2, 15));

        // Aksi Restart: Tutup game -> Buka baru dengan list pemain yang sama
        btnRestart.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to restart?", "Restart", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                sound.stopBgm();
                dispose();
                List<Player> currentList = new ArrayList<>(playerStack);
                Collections.reverse(currentList);
                new SnakeLadderGame(currentList).setVisible(true);
            }
        });

        JPanel rightCorner = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightCorner.setOpaque(false);
        rightCorner.add(btnRestart);
        topBar.add(rightCorner, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // --- BOARD PANEL ---
        boardPanel = new BoardPanel(board, playerStack);
        JPanel boardWrapper = new JPanel(new BorderLayout());
        boardWrapper.setBackground(OceanTheme.WATER_DEEP);
        boardWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(OceanTheme.BUBBLE_BLUE, 4),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        boardWrapper.add(boardPanel, BorderLayout.CENTER);
        add(boardWrapper, BorderLayout.CENTER);

        // --- RIGHT PANEL ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(OceanTheme.SIDEBAR_BG);
        rightPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        playerPanel = new PlayerPanel(playerStack);
        rightPanel.add(playerPanel);
        rightPanel.add(Box.createVerticalStrut(15));

        controlPanel = new ControlPanel(e -> playTurn());
        rightPanel.add(controlPanel);

        add(rightPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        refreshUI();
    }

    private void refreshUI() {
        currentPlayer = playerStack.peek();
        playerPanel.setCurrent(currentPlayer);
        controlPanel.setStatus("DIVER: " + currentPlayer.getName().toUpperCase(), OceanTheme.WATER_DEEP);
        controlPanel.toggleBtn(true);
        controlPanel.setPath("");
        boardPanel.clearPath();
    }

    private void playTurn() {
        controlPanel.toggleBtn(false);
        controlPanel.setStatus("ROLLING...", Color.GRAY);
        sound.playSfx("roll.wav");

        final int[] count = {0};
        javax.swing.Timer t = new javax.swing.Timer(80, e -> {
            if (count[0]++ < 10) {
                controlPanel.updateDice(new Random().nextInt(6)+1);
            } else {
                ((javax.swing.Timer)e.getSource()).stop();
                finalizeTurn();
            }
        });
        t.start();
    }

    private void finalizeTurn() {
        int roll = dice.rollMain();
        controlPanel.updateDice(roll);
        int currentPos = currentPlayer.getPosition();
        int target = currentPos + roll;
        int maxPos = board.getTotalSquares();

        if (target >= maxPos) {
            target = maxPos;
            controlPanel.setPath("Final Dash to Treasure!");
        }

        final int finalTarget = target;
        animateMove(finalTarget, () -> checkEvents(finalTarget));
    }

    private void animateMove(int targetPos, Runnable onComplete) {
        int startPos = currentPlayer.getPosition();
        if (startPos == targetPos) {
            onComplete.run();
            return;
        }

        int direction = Integer.compare(targetPos, startPos);
        javax.swing.Timer stepTimer = new javax.swing.Timer(250, null);
        stepTimer.addActionListener(e -> {
            int current = currentPlayer.getPosition();
            if (current == targetPos) {
                stepTimer.stop();
                onComplete.run();
                return;
            }
            currentPlayer.setPosition(current + direction);
            boardPanel.repaint();
        });
        stepTimer.setInitialDelay(0);
        stepTimer.start();
    }

    private void checkEvents(int pos) {
        int pts = board.collectPoint(pos);
        if(pts > 0) {
            currentPlayer.addScore(pts);
            LeaderboardManager.addScore(currentPlayer.getName(), pts);
            sound.playSfx("coin.wav");
            controlPanel.setStatus("FOUND PEARLS: " + pts + "!", OceanTheme.CORAL_ORANGE);
        }

        if(board.getLinks().containsKey(pos)) {
            int next = board.getLinks().get(pos).getTo();
            boolean isUp = next > pos;
            String msg = isUp ? "RIDING THE CURRENT!" : "WHIRLPOOL DOWN!";
            Color c = isUp ? OceanTheme.WATER_DEEP : OceanTheme.CORAL_ORANGE;
            controlPanel.setStatus(msg, c);

            javax.swing.Timer t = new javax.swing.Timer(800, ev -> {
                ((javax.swing.Timer)ev.getSource()).stop();
                animateMove(next, () -> checkSpecial(next));
            });
            t.start();
        } else {
            checkSpecial(pos);
        }
    }

    private void checkSpecial(int pos) {
        if(board.hasStar(pos)) {
            controlPanel.setStatus("MAGIC BUBBLE: BONUS TURN!", OceanTheme.PEARL_GOLD);
            javax.swing.Timer t = new javax.swing.Timer(1200, e -> {
                playerStack.pop(); // Keluarkan pemain sekarang dari atas tumpukan
                playerStack.add(0, currentPlayer); // Masukkan ke paling bawah antrian
                refreshUI();
                ((javax.swing.Timer)e.getSource()).stop();
            });
            t.start();
            return;
        }

        if(board.isPrime(pos)) {
            controlPanel.setStatus("PRIME SPOT! SHORTCUT!", OceanTheme.WATER_DEEP);
            List<Integer> path = board.findShortestPath(pos, board.getTotalSquares());
            boardPanel.updatePath(path);
        }

        if(currentPlayer.hasWon(board.getTotalSquares())) {
            sound.playSfx("win.wav");
            LeaderboardManager.addWin(currentPlayer.getName());
            showLeaderboard();
            return;
        }

        javax.swing.Timer t = new javax.swing.Timer(1200, e -> {
            playerStack.pop();
            playerStack.add(0, currentPlayer);
            refreshUI();
            ((javax.swing.Timer)e.getSource()).stop();
        });
        t.start();
    }

    private void showLeaderboard() {
        JDialog d = new JDialog(this, "Ocean Treasure Found!", true);
        d.setSize(500, 600);
        d.setLocationRelativeTo(this);
        d.setUndecorated(true);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(OceanTheme.WATER_CYAN);
        p.setBorder(new LineBorder(OceanTheme.WATER_DEEP, 5));

        JLabel lbl = new JLabel("🏆 TOP DIVERS 🏆");
        lbl.setFont(OceanTheme.FONT_TITLE);
        lbl.setForeground(Color.WHITE);
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        p.add(Box.createVerticalStrut(20));
        p.add(lbl);

        List<Map.Entry<String, Integer>> top3 = LeaderboardManager.getTop3Scores();

        int rank = 1;
        for (Map.Entry<String, Integer> entry : top3) {
            JLabel text = new JLabel(rank + ". " + entry.getKey() + " - " + entry.getValue() + " Pearls");
            text.setFont(OceanTheme.FONT_TEXT.deriveFont(20f));
            text.setForeground(OceanTheme.WATER_DEEP);
            text.setAlignmentX(CENTER_ALIGNMENT);
            p.add(Box.createVerticalStrut(10));
            p.add(text);
            rank++;
        }

        p.add(Box.createVerticalStrut(40));
        OceanTheme.Button btnMenu = new OceanTheme.Button("MAIN MENU");
        btnMenu.setAlignmentX(CENTER_ALIGNMENT);
        btnMenu.addActionListener(e -> {
            d.dispose();
            dispose();
            showMainMenu();
        });

        p.add(btnMenu);
        d.add(p);
        d.setVisible(true);
    }

    // --- MAIN MENU & ENTRY POINT ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SnakeLadderGame::showMainMenu);
    }

    private static void showMainMenu() {
        JFrame frame = new JFrame("Ocean Adventure");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(OceanTheme.WATER_CYAN);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("OCEAN ADVENTURE");
        title.setFont(OceanTheme.FONT_TITLE);
        title.setForeground(OceanTheme.WATER_DEEP);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JPanel selector = new JPanel(new FlowLayout());
        selector.setOpaque(false);
        JLabel numLabel = new JLabel("2");
        numLabel.setFont(new Font("Arial", Font.BOLD, 50));
        numLabel.setForeground(Color.WHITE);

        OceanTheme.Button btnMin = new OceanTheme.Button("-");
        btnMin.addActionListener(e -> {
            int n = Integer.parseInt(numLabel.getText());
            if(n>2) numLabel.setText(""+(n-1));
        });
        OceanTheme.Button btnPlus = new OceanTheme.Button("+");
        btnPlus.addActionListener(e -> {
            int n = Integer.parseInt(numLabel.getText());
            if(n<4) numLabel.setText(""+(n+1));
        });

        selector.add(btnMin); selector.add(numLabel); selector.add(btnPlus);

        OceanTheme.Button btnStart = new OceanTheme.Button("START DIVE");
        btnStart.setAlignmentX(CENTER_ALIGNMENT);
        btnStart.setPreferredSize(new Dimension(200, 60));

        // [BARU] Menuju ke Menu Pemilihan Karakter
        btnStart.addActionListener(e -> {
            frame.dispose();
            showCharacterSelection(2); // Ganti logika ini sesuai UI Anda
        });

        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(50));
        mainPanel.add(selector);
        mainPanel.add(Box.createVerticalStrut(50));
        mainPanel.add(btnStart);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // [BARU] MENU POP-UP UNTUK MEMILIH KARAKTER + NAMA
    private static void showCharacterSelection(int n) {
        JDialog d = new JDialog((Frame)null, "Assemble Your Squad", true);
        d.setSize(550, 500);
        d.setLocationRelativeTo(null);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(OceanTheme.SIDEBAR_BG); // Pastikan warna ini ada di OceanTheme

        java.util.List<PlayerInputPanel> inputs = new ArrayList<>();
        for(int i=0; i<n; i++) {
            PlayerInputPanel panel = new PlayerInputPanel(i+1);
            inputs.add(panel);
            p.add(panel);
            p.add(Box.createVerticalStrut(10));
        }

        OceanTheme.Button btn = new OceanTheme.Button("LET'S DIVE!");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> {
            java.util.List<Player> players = new ArrayList<>();
            Color[] pal = OceanTheme.PLAYER_COLORS;

            for(int i=0; i<inputs.size(); i++) {
                PlayerInputPanel input = inputs.get(i);
                // MEMBUAT PLAYER DENGAN TIPE KARAKTER YANG DIPILIH
                players.add(new Player(
                        input.getNameField().getText(),
                        pal[i % pal.length],
                        input.getSelectedCharType() // Mengambil int 0/1/2
                ));
            }
            d.dispose();
            new SnakeLadderGame(players).setVisible(true);
        });

        p.add(Box.createVerticalStrut(20));
        p.add(btn);
        d.add(p);
        d.setVisible(true);
    }

    // Helper Panel untuk Input per Player
    static class PlayerInputPanel extends JPanel {
        private JTextField nameField;
        private JComboBox<String> charSelector;

        public PlayerInputPanel(int num) {
            setLayout(new GridLayout(1, 2, 10, 0));
            setOpaque(false);

            nameField = new JTextField("Diver " + num);
            String[] chars = {"🐬 Dolphin", "🐢 Turtle", "🚁 Submarine"};
            charSelector = new JComboBox<>(chars);

            add(nameField);
            add(charSelector);
        }

        public JTextField getNameField() { return nameField; }
        public int getSelectedCharType() { return charSelector.getSelectedIndex(); }
    }
}