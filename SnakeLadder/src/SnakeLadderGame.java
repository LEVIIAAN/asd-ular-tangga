import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SnakeLadderGame extends JFrame {
    // --- LAYOUT MANAGER ---
    private CardLayout cardLayout;
    private JPanel mainContainer;

    // --- GAME COMPONENTS ---
    private GameBoard board;
    private Stack<Player> playerStack;
    private Dice dice;
    private SoundManager sound;
    private BoardPanel boardPanel;
    private ControlPanel controlPanel;
    private PlayerPanel playerPanel;
    private Player currentPlayer;

    // --- DATA SEMENTARA ---
    private int selectedPlayerCount = 2; // Default

    public SnakeLadderGame() {
        initMainFrame();
        sound = new SoundManager();
        sound.playBgm("bgm.wav"); // Mulai musik di menu
    }

    private void initMainFrame() {
        setTitle("Ocean Adventure: Treasure Hunt");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 850); // Ukuran default lebih besar
        setLocationRelativeTo(null);

        // Setup CardLayout (Untuk pindah-pindah halaman)
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // TAMBAHKAN HALAMAN-HALAMAN
        mainContainer.add(createMainMenu(), "MENU");
        mainContainer.add(createSettingsMenu(), "SETTINGS");
        // Halaman Selection & Game akan di-generate dinamis saat dibutuhkan,
        // tapi kita siapkan container-nya.

        add(mainContainer);
    }

    // ==========================================
    // PAGE 1: HOME PAGE (MAIN MENU)
    // ==========================================
    private JPanel createMainMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(OceanTheme.WATER_CYAN);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));

        // 1. Title
        JLabel title = new JLabel("OCEAN ADVENTURE");
        title.setFont(OceanTheme.FONT_TITLE.deriveFont(48f));
        title.setForeground(OceanTheme.WATER_DEEP);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitle = new JLabel("TREASURE HUNT");
        subTitle.setFont(OceanTheme.FONT_TITLE.deriveFont(24f));
        subTitle.setForeground(OceanTheme.BUTTON_ORANGE);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 2. Player Count Selector
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        selectorPanel.setOpaque(false);
        selectorPanel.setMaximumSize(new Dimension(300, 80));

        JLabel lblCount = new JLabel("2");
        lblCount.setFont(new Font("Arial", Font.BOLD, 60));
        lblCount.setForeground(Color.WHITE);

        OceanTheme.Button btnMin = new OceanTheme.Button("-");
        btnMin.setPreferredSize(new Dimension(60, 60));
        btnMin.addActionListener(e -> {
            if (selectedPlayerCount > 2) {
                selectedPlayerCount--;
                lblCount.setText(String.valueOf(selectedPlayerCount));
            }
        });

        OceanTheme.Button btnPlus = new OceanTheme.Button("+");
        btnPlus.setPreferredSize(new Dimension(60, 60));
        btnPlus.addActionListener(e -> {
            if (selectedPlayerCount < 5) {
                selectedPlayerCount++;
                lblCount.setText(String.valueOf(selectedPlayerCount));
            }
        });

        selectorPanel.add(btnMin);
        selectorPanel.add(lblCount);
        selectorPanel.add(btnPlus);

        JLabel lblInfo = new JLabel("PLAYERS");
        lblInfo.setFont(OceanTheme.FONT_TEXT.deriveFont(16f));
        lblInfo.setForeground(OceanTheme.WATER_DEEP);
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. Buttons
        OceanTheme.Button btnStart = new OceanTheme.Button("START GAME");
        btnStart.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnStart.setPreferredSize(new Dimension(200, 60));
        btnStart.addActionListener(e -> showCharacterSelection(selectedPlayerCount));

        OceanTheme.Button btnSettings = new OceanTheme.Button("SETTINGS");
        btnSettings.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSettings.setPreferredSize(new Dimension(200, 50));
        // Ubah warna tombol settings agar beda dikit (opsional, via override paint kalau mau)
        btnSettings.addActionListener(e -> cardLayout.show(mainContainer, "SETTINGS"));

        // Layout Assembly
        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(subTitle);
        panel.add(Box.createVerticalStrut(60));
        panel.add(selectorPanel);
        panel.add(lblInfo);
        panel.add(Box.createVerticalStrut(40));
        panel.add(btnStart);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btnSettings);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // ==========================================
    // PAGE 2: CHARACTER SELECTION
    // ==========================================
    private void showCharacterSelection(int n) {
        // Kita buat panel baru setiap kali dipanggil agar fresh
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BorderLayout());
        selectionPanel.setBackground(OceanTheme.SIDEBAR_BG);

        // Header
        JLabel header = new JLabel("ASSEMBLE YOUR SQUAD", SwingConstants.CENTER);
        header.setFont(OceanTheme.FONT_TITLE.deriveFont(32f));
        header.setForeground(OceanTheme.PEARL_GOLD);
        header.setBorder(new EmptyBorder(30, 0, 30, 0));
        selectionPanel.add(header, BorderLayout.NORTH);

        // Input List (Center)
        JPanel inputsContainer = new JPanel();
        inputsContainer.setLayout(new BoxLayout(inputsContainer, BoxLayout.Y_AXIS));
        inputsContainer.setOpaque(false);
        inputsContainer.setBorder(new EmptyBorder(0, 100, 0, 100));

        List<PlayerInputPanel> inputFields = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            PlayerInputPanel p = new PlayerInputPanel(i + 1);
            inputFields.add(p);
            inputsContainer.add(p);
            inputsContainer.add(Box.createVerticalStrut(15));
        }

        // Scroll pane in case screen is small
        JScrollPane scroll = new JScrollPane(inputsContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        selectionPanel.add(scroll, BorderLayout.CENTER);

        // Footer Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        footer.setOpaque(false);

        OceanTheme.Button btnBack = new OceanTheme.Button("BACK");
        btnBack.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));

        OceanTheme.Button btnGo = new OceanTheme.Button("LET'S DIVE!");
        btnGo.addActionListener(e -> {
            // Collect Data
            List<Player> players = new ArrayList<>();
            Color[] pal = OceanTheme.PLAYER_COLORS;

            for (int i = 0; i < inputFields.size(); i++) {
                PlayerInputPanel input = inputFields.get(i);
                players.add(new Player(
                        input.getNameField().getText(),
                        pal[i % pal.length],
                        input.getSelectedCharType()
                ));
            }
            initGame(players); // Masuk ke Game
        });

        footer.add(btnBack);
        footer.add(btnGo);
        selectionPanel.add(footer, BorderLayout.SOUTH);

        // Tambahkan ke card layout dan tampilkan
        mainContainer.add(selectionPanel, "SELECTION");
        cardLayout.show(mainContainer, "SELECTION");
    }

    // ==========================================
    // PAGE 3: SETTINGS PAGE
    // ==========================================
    private JPanel createSettingsMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(OceanTheme.WATER_DEEP);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));

        JLabel title = new JLabel("SETTINGS");
        title.setFont(OceanTheme.FONT_TITLE.deriveFont(40f));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Toggle Music (Dummy Implementation visual)
        JCheckBox musicToggle = new JCheckBox("Background Music");
        musicToggle.setFont(OceanTheme.FONT_TITLE.deriveFont(20f));
        musicToggle.setForeground(Color.WHITE);
        musicToggle.setOpaque(false);
        musicToggle.setSelected(true);
        musicToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        musicToggle.addActionListener(e -> {
            if (musicToggle.isSelected()) sound.playBgm("bgm.wav");
            else sound.stopBgm();
        });

        OceanTheme.Button btnBack = new OceanTheme.Button("BACK TO MENU");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));

        panel.add(title);
        panel.add(Box.createVerticalStrut(50));
        panel.add(musicToggle);
        panel.add(Box.createVerticalStrut(50));
        panel.add(btnBack);

        return panel;
    }

    // ==========================================
    // PAGE 4: GAMEPLAY (The Actual Game)
    // ==========================================
    private void initGame(List<Player> playersInput) {
        // 1. Setup Logic
        this.board = new GameBoard(6, 10);
        this.dice = new Dice();
        this.playerStack = new Stack<>();

        // Reset & Push Players
        for (int i = playersInput.size() - 1; i >= 0; i--) {
            Player p = playersInput.get(i);
            p.setPosition(1);
            p.setScore(LeaderboardManager.getScore(p.getName()));
            playerStack.push(p);
            LeaderboardManager.addScore(p.getName(), 0);
        }

        // 2. Setup UI Components for Game
        JPanel gamePanelContainer = new JPanel(new BorderLayout());
        gamePanelContainer.setBackground(OceanTheme.WATER_CYAN);

        // Top Bar (Restart / Home)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(5, 10, 0, 10));

        JButton btnHome = new JButton("⌂"); // Home Icon
        styleMiniButton(btnHome);
        btnHome.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Quit to Main Menu?", "Quit", JOptionPane.YES_NO_OPTION);
            if(c == JOptionPane.YES_OPTION) cardLayout.show(mainContainer, "MENU");
        });

        JButton btnRestart = new JButton("↻"); // Restart Icon
        styleMiniButton(btnRestart);
        btnRestart.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Restart Game?", "Restart", JOptionPane.YES_NO_OPTION);
            if(c == JOptionPane.YES_OPTION) {
                // Restart dengan pemain yang sama
                List<Player> currentList = new ArrayList<>(playerStack);
                Collections.reverse(currentList);
                initGame(currentList);
            }
        });

        JPanel rightCorner = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightCorner.setOpaque(false);
        rightCorner.add(btnHome);
        rightCorner.add(btnRestart);
        topBar.add(rightCorner, BorderLayout.EAST);

        // Board & Sidebar
        boardPanel = new BoardPanel(board, playerStack);
        JPanel boardWrapper = new JPanel(new BorderLayout());
        boardWrapper.setBackground(OceanTheme.WATER_DEEP);
        boardWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(OceanTheme.BUBBLE_BLUE, 4),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        boardWrapper.add(boardPanel, BorderLayout.CENTER);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(OceanTheme.SIDEBAR_BG);
        sidebar.setBorder(new EmptyBorder(15, 15, 15, 15));

        playerPanel = new PlayerPanel(playerStack);
        sidebar.add(playerPanel);
        sidebar.add(Box.createVerticalStrut(15));
        controlPanel = new ControlPanel(e -> playTurn());
        sidebar.add(controlPanel);

        // Assembly
        gamePanelContainer.add(topBar, BorderLayout.NORTH);
        gamePanelContainer.add(boardWrapper, BorderLayout.CENTER);
        gamePanelContainer.add(sidebar, BorderLayout.EAST);

        // Show Game
        mainContainer.add(gamePanelContainer, "GAME");
        cardLayout.show(mainContainer, "GAME");

        refreshUI();
    }

    private void styleMiniButton(JButton btn) {
        btn.setFont(new Font("Segoe UI Symbol", Font.BOLD, 20));
        btn.setForeground(Color.WHITE);
        btn.setBackground(OceanTheme.BUTTON_ORANGE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    // ==========================================
    // GAME LOGIC METHODS (Sama seperti sebelumnya)
    // ==========================================
    private void refreshUI() {
        if(playerStack.isEmpty()) return;
        currentPlayer = playerStack.peek();
        playerPanel.setCurrent(currentPlayer);
        controlPanel.setStatus("DIVER: " + currentPlayer.getName().toUpperCase(), OceanTheme.WATER_DEEP);
        controlPanel.toggleBtn(true);
        controlPanel.setPath("");
        boardPanel.clearPath();
        boardPanel.repaint();
        playerPanel.repaint();
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
                playerStack.pop();
                playerStack.add(0, currentPlayer);
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
            cardLayout.show(mainContainer, "MENU"); // Kembali ke Menu Utama
        });

        p.add(btnMenu);
        d.add(p);
        d.setVisible(true);
    }

    // MAIN ENTRY POINT
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SnakeLadderGame().setVisible(true);
        });
    }

    // --- HELPER CLASS FOR INPUT ---
    static class PlayerInputPanel extends JPanel {
        private JTextField nameField;
        private JComboBox<String> charSelector;

        public PlayerInputPanel(int num) {
            setLayout(new GridLayout(1, 2, 10, 0));
            setOpaque(false);
            setMaximumSize(new Dimension(500, 40));

            nameField = new JTextField("Diver " + num);
            nameField.setFont(new Font("Arial", Font.PLAIN, 14));

            String[] chars = {
                    "🐬 Dolphin",
                    "🐢 Turtle",
                    "🚁 Submarine",
                    "🦈 Shark",
                    "🐙 Octopus"
            };
            charSelector = new JComboBox<>(chars);
            charSelector.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

            add(nameField);
            add(charSelector);
        }

        public JTextField getNameField() { return nameField; }
        public int getSelectedCharType() { return charSelector.getSelectedIndex(); }
    }
}