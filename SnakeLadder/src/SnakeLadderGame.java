import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.net.URL;

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

    // --- DATA & ASSETS ---
    private int selectedPlayerCount = 2; // Field Class (Penting agar tidak error lambda)
    private Image menuBackground;        // Variable untuk menyimpan gambar agar NO LAG

    // FONT KHUSUS
    private final Font MINECRAFT_TITLE_FONT = new Font("Impact", Font.BOLD, 52);
    private final Font WOOD_BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public SnakeLadderGame() {
        // 1. Init Sound
        sound = new SoundManager();

        // 2. PRE-LOAD IMAGE (RAHASIA AGAR TIDAK LAG)
        try {
            URL imgUrl = getClass().getResource("/assets/ocean_bg.png");
            if (imgUrl != null) {
                menuBackground = new ImageIcon(imgUrl).getImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Init UI & Music
        initMainFrame();
        sound.playBgm("bgm.wav");
    }

    private void initMainFrame() {
        setTitle("Ocean Adventures: Snake Ladders");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 850);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Tambahkan Halaman
        mainContainer.add(createMainMenu(), "MENU");
        mainContainer.add(createSettingsMenu(), "SETTINGS");

        add(mainContainer);
    }

    // ==========================================
    // PAGE 1: MAIN MENU
    // ==========================================
    private JPanel createMainMenu() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (menuBackground != null) {
                    int imgW = menuBackground.getWidth(this);
                    int imgH = menuBackground.getHeight(this);

                    // --- TEKNIK CROP / ZOOM ---
                    // Kita potong pinggiran gambar asli agar hanya MAP yang tampil.
                    // Angka ini adalah persentase (0.16 = 16%).
                    // Sesuaikan angka ini jika potongan dirasa kurang pas.

                    double cutTop    = 0.18; // Potong 18% Atas (Hilangkan Judul & Langit Biru)
                    double cutBottom = 0.05; // Potong 5% Bawah (Hilangkan Ombak bingkai)
                    double cutSide   = 0.04; // Potong 4% Kiri & Kanan (Hilangkan Tiang bingkai)

                    // Hitung koordinat sumber (Source Coordinates)
                    int sx1 = (int) (imgW * cutSide);
                    int sy1 = (int) (imgH * cutTop);
                    int sx2 = (int) (imgW * (1.0 - cutSide));
                    int sy2 = (int) (imgH * (1.0 - cutBottom));

                    // Gambar ulang: Ambil bagian tengah (Map), tarik hingga memenuhi layar
                    g.drawImage(menuBackground, 0, 0, getWidth(), getHeight(),
                            sx1, sy1, sx2, sy2, this);

                } else {
                    // Fallback jika gambar gagal load
                    g.setColor(new Color(0, 105, 148));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }

                // Overlay Hitam Transparan (Agar tulisan "OCEAN ADVENTURES" Emas lebih kontras)
                g.setColor(new Color(0, 0, 0, 60));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // --- JUDUL GAME (PROGRAMMATIC) ---
        // Karena background sudah bersih, judul ini akan terlihat lebih bagus
        JLabel titleLabel = new JLabel("OCEAN ADVENTURES", SwingConstants.CENTER);
        titleLabel.setFont(MINECRAFT_TITLE_FONT);
        titleLabel.setForeground(OceanTheme.PEARL_GOLD);
        applyShadow(titleLabel);

        JLabel subTitleLabel = new JLabel("SNAKE LADDERS", SwingConstants.CENTER);
        subTitleLabel.setFont(MINECRAFT_TITLE_FONT.deriveFont(36f));
        subTitleLabel.setForeground(Color.WHITE);
        applyShadow(subTitleLabel);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        panel.add(subTitleLabel, gbc);

        gbc.gridy = 2;
        panel.add(Box.createVerticalStrut(30), gbc);

        // --- TENGAH: SELECTOR & START ---
        JPanel middleRow = new JPanel(new GridBagLayout());
        middleRow.setOpaque(false);
        GridBagConstraints midGbc = new GridBagConstraints();
        midGbc.fill = GridBagConstraints.BOTH;
        midGbc.insets = new Insets(0, 10, 0, 10);

        JPanel selectorPanel = createWoodenSelector();
        midGbc.gridx = 0;
        midGbc.weightx = 0.3;
        middleRow.add(selectorPanel, midGbc);

        WoodenButton btnStart = new WoodenButton("START GAME");
        btnStart.setFont(WOOD_BUTTON_FONT.deriveFont(24f));
        btnStart.addActionListener(e -> showCharacterSelection(selectedPlayerCount));
        midGbc.gridx = 1;
        midGbc.weightx = 0.7;
        middleRow.add(btnStart, midGbc);

        JPanel wrapperMiddle = new JPanel(new BorderLayout());
        wrapperMiddle.setOpaque(false);
        wrapperMiddle.setBorder(new EmptyBorder(0, 100, 0, 100));
        wrapperMiddle.add(middleRow, BorderLayout.CENTER);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(wrapperMiddle, gbc);

        // --- BAWAH: OPTIONS & QUIT ---
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomRow.setOpaque(false);
        bottomRow.setBorder(new EmptyBorder(0, 100, 0, 100));

        WoodenButton btnOptions = new WoodenButton("OPTIONS");
        btnOptions.addActionListener(e -> cardLayout.show(mainContainer, "SETTINGS"));

        WoodenButton btnQuit = new WoodenButton("QUIT GAME");
        btnQuit.addActionListener(e -> System.exit(0));

        bottomRow.add(btnOptions);
        bottomRow.add(btnQuit);

        gbc.gridy = 4;
        gbc.ipady = 15;
        panel.add(bottomRow, gbc);

        return panel;
    }

    private JPanel createWoodenSelector() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new LineBorder(OceanTheme.BORDER_GOLD, 2, true));

        JLabel lblCount = new JLabel(String.valueOf(selectedPlayerCount), SwingConstants.CENTER);
        lblCount.setFont(MINECRAFT_TITLE_FONT.deriveFont(32f));
        lblCount.setForeground(Color.WHITE);
        lblCount.setOpaque(true);
        lblCount.setBackground(new Color(0,0,0,100));

        WoodenButton btnMin = new WoodenButton("-");
        btnMin.setPreferredSize(new Dimension(50, 60));
        btnMin.addActionListener(e -> {
            if (selectedPlayerCount > 2) {
                selectedPlayerCount--;
                lblCount.setText(String.valueOf(selectedPlayerCount));
            }
        });

        WoodenButton btnPlus = new WoodenButton("+");
        btnPlus.setPreferredSize(new Dimension(50, 60));
        btnPlus.addActionListener(e -> {
            if (selectedPlayerCount < 5) {
                selectedPlayerCount++;
                lblCount.setText(String.valueOf(selectedPlayerCount));
            }
        });

        p.add(btnMin, BorderLayout.WEST);
        p.add(lblCount, BorderLayout.CENTER);
        p.add(btnPlus, BorderLayout.EAST);
        return p;
    }

    // ==========================================
    // PAGE 2: CHARACTER SELECTION (REVISI)
    // ==========================================
    private void showCharacterSelection(int n) {

        JPanel selectionPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // --- LOGIKA CROP BACKGROUND (SAMA SEPERTI MAIN MENU) ---
                if (menuBackground != null) {
                    int imgW = menuBackground.getWidth(this);
                    int imgH = menuBackground.getHeight(this);

                    // Potong pinggiran agar hanya MAP yang tampil
                    double cutTop    = 0.18; // Potong 18% Atas (Hilangkan Judul lama)
                    double cutBottom = 0.05;
                    double cutSide   = 0.04;

                    int sx1 = (int) (imgW * cutSide);
                    int sy1 = (int) (imgH * cutTop);
                    int sx2 = (int) (imgW * (1.0 - cutSide));
                    int sy2 = (int) (imgH * (1.0 - cutBottom));

                    // Gambar bagian tengah (Map) memenuhi layar
                    g.drawImage(menuBackground, 0, 0, getWidth(), getHeight(),
                            sx1, sy1, sx2, sy2, this);
                } else {
                    g.setColor(OceanTheme.SIDEBAR_BG);
                    g.fillRect(0,0,getWidth(), getHeight());
                }

                // Overlay Gelap (Lebih gelap dari menu utama agar Input Field terbaca jelas)
                g.setColor(new Color(0, 0, 0, 180)); // Alpha 180 (Gelap)
                g.fillRect(0,0,getWidth(), getHeight());
            }
        };

        JLabel header = new JLabel("ASSEMBLE YOUR SQUAD", SwingConstants.CENTER);
        header.setFont(MINECRAFT_TITLE_FONT.deriveFont(42f));
        header.setForeground(OceanTheme.PEARL_GOLD);
        header.setBorder(new EmptyBorder(40, 0, 30, 0));
        selectionPanel.add(header, BorderLayout.NORTH);

        // Container Input
        JPanel inputsContainer = new JPanel();
        inputsContainer.setLayout(new BoxLayout(inputsContainer, BoxLayout.Y_AXIS));
        inputsContainer.setOpaque(false);

        // Pusatkan container secara vertikal
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        List<PlayerInputPanel> inputFields = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            PlayerInputPanel p = new PlayerInputPanel(i + 1);
            inputFields.add(p);
            inputsContainer.add(p);
            inputsContainer.add(Box.createVerticalStrut(15));
        }
        centerWrapper.add(inputsContainer);
        selectionPanel.add(centerWrapper, BorderLayout.CENTER);

        // Footer Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 40));
        footer.setOpaque(false);

        WoodenButton btnBack = new WoodenButton("BACK");
        btnBack.setPreferredSize(new Dimension(150, 50));
        btnBack.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));

        WoodenButton btnGo = new WoodenButton("LET'S DIVE!");
        btnGo.setPreferredSize(new Dimension(200, 50));

        // --- LOGIKA VALIDASI DUPLIKAT ---
        btnGo.addActionListener(e -> {
            Set<Integer> selectedChars = new HashSet<>();
            boolean hasDuplicate = false;

            for (PlayerInputPanel input : inputFields) {
                int selectedChar = input.getSelectedCharType();
                if (selectedChars.contains(selectedChar)) {
                    hasDuplicate = true;
                    break;
                }
                selectedChars.add(selectedChar);
            }

            if (hasDuplicate) {
                JOptionPane.showMessageDialog(this,
                        "Oops! Every diver must be unique.\nPlease choose different characters for each player.",
                        "Duplicate Character",
                        JOptionPane.WARNING_MESSAGE);
                return; // Stop
            }

            // Jika lolos validasi, mulai game
            List<Player> players = new ArrayList<>();
            Color[] pal = OceanTheme.PLAYER_COLORS;
            for (int i = 0; i < inputFields.size(); i++) {
                PlayerInputPanel input = inputFields.get(i);
                players.add(new Player(input.getNameField().getText(), pal[i % pal.length], input.getSelectedCharType()));
            }
            initGame(players);
        });

        footer.add(btnBack);
        footer.add(btnGo);
        selectionPanel.add(footer, BorderLayout.SOUTH);

        mainContainer.add(selectionPanel, "SELECTION");
        cardLayout.show(mainContainer, "SELECTION");
    }

    // ==========================================
    // PAGE 3: SETTINGS
    // ==========================================
    private JPanel createSettingsMenu() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (menuBackground != null) {
                    g.drawImage(menuBackground, 0, 0, getWidth(), getHeight(), this);
                }
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel title = new JLabel("OPTIONS");
        title.setFont(MINECRAFT_TITLE_FONT.deriveFont(40f));
        title.setForeground(OceanTheme.PEARL_GOLD);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(title, gbc);

        JPanel toggleContainer = new JPanel(new GridLayout(2, 1, 0, 20));
        toggleContainer.setOpaque(false);

        JCheckBox musicToggle = createWoodenCheckbox("Background Music");
        musicToggle.setSelected(!sound.isBgmMuted());
        musicToggle.addActionListener(e -> {
            if (musicToggle.isSelected()) sound.playBgm("bgm.wav");
            else sound.stopBgm();
        });
        toggleContainer.add(musicToggle);

        JCheckBox sfxToggle = createWoodenCheckbox("Sound Effects");
        sfxToggle.setSelected(!sound.isSfxMuted());
        sfxToggle.addActionListener(e -> sound.setSfxMuted(!sfxToggle.isSelected()));
        toggleContainer.add(sfxToggle);

        gbc.gridy = 1;
        panel.add(toggleContainer, gbc);

        WoodenButton btnBack = new WoodenButton("DONE");
        btnBack.setPreferredSize(new Dimension(200, 50));
        btnBack.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));

        gbc.gridy = 2;
        panel.add(btnBack, gbc);

        return panel;
    }

    private JCheckBox createWoodenCheckbox(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(WOOD_BUTTON_FONT.deriveFont(20f));
        cb.setForeground(Color.WHITE);
        cb.setOpaque(false);
        cb.setFocusPainted(false);
        return cb;
    }

    // ==========================================
    // CUSTOM COMPONENT: WOODEN BUTTON 🪵
    // ==========================================
    class WoodenButton extends JButton {
        private final Color WOOD_LIGHT = new Color(160, 82, 45);
        private final Color WOOD_MAIN  = new Color(139, 69, 19);
        private final Color WOOD_DARK  = new Color(80, 40, 10);
        private final Color BORDER     = new Color(218, 165, 32);
        private boolean isHovered = false;

        public WoodenButton(String text) {
            super(text);
            setFont(WOOD_BUTTON_FONT);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            Color baseColor = WOOD_MAIN;
            if (getModel().isPressed()) baseColor = WOOD_DARK;
            else if (isHovered) baseColor = WOOD_LIGHT;

            GradientPaint woodGradient = new GradientPaint(0, 0, baseColor.brighter(), 0, h, baseColor.darker());
            g2.setPaint(woodGradient);
            g2.fillRoundRect(2, 2, w - 4, h - 4, 15, 15);

            g2.setColor(BORDER);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(2, 2, w - 4, h - 4, 15, 15);

            g2.setColor(new Color(50, 30, 10));
            int pakuSize = 4;
            g2.fillOval(10, 10, pakuSize, pakuSize);
            g2.fillOval(w - 14, 10, pakuSize, pakuSize);
            g2.fillOval(10, h - 14, pakuSize, pakuSize);
            g2.fillOval(w - 14, h - 14, pakuSize, pakuSize);

            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int textX = (w - fm.stringWidth(text)) / 2;
            int textY = (h + fm.getAscent()) / 2 - 4;

            g2.setColor(new Color(0, 0, 0, 150));
            g2.drawString(text, textX + 2, textY + 2);

            if (isHovered) g2.setColor(Color.WHITE);
            else g2.setColor(new Color(255, 235, 205));
            g2.drawString(text, textX, textY);
            g2.dispose();
        }
    }

    private void applyShadow(JLabel label) {
        label.setUI(new javax.swing.plaf.basic.BasicLabelUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                g.setColor(new Color(0, 0, 0, 150));
                g.drawString(c instanceof JLabel ? ((JLabel)c).getText() : "", 3, c.getHeight() - 3);
                super.paint(g, c);
            }
        });
    }

    // ==========================================
    // GAME INITIALIZATION LOGIC
    // ==========================================
    private void initGame(List<Player> playersInput) {
        this.board = new GameBoard(6, 10);
        this.dice = new Dice();
        this.playerStack = new Stack<>();

        for (int i = playersInput.size() - 1; i >= 0; i--) {
            Player p = playersInput.get(i);
            p.setPosition(1);
            p.setScore(LeaderboardManager.getScore(p.getName()));
            playerStack.push(p);
            LeaderboardManager.addScore(p.getName(), 0);
        }

        JPanel gamePanelContainer = new JPanel(new BorderLayout());
        gamePanelContainer.setBackground(OceanTheme.WATER_CYAN);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(5, 10, 0, 10));

        JButton btnHome = new JButton("⌂");
        styleMiniButton(btnHome);
        btnHome.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Quit to Main Menu?", "Quit", JOptionPane.YES_NO_OPTION);
            if(c == JOptionPane.YES_OPTION) cardLayout.show(mainContainer, "MENU");
        });

        JButton btnRestart = new JButton("↻");
        styleMiniButton(btnRestart);
        btnRestart.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Restart Game?", "Restart", JOptionPane.YES_NO_OPTION);
            if(c == JOptionPane.YES_OPTION) {
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

        gamePanelContainer.add(topBar, BorderLayout.NORTH);
        gamePanelContainer.add(boardWrapper, BorderLayout.CENTER);
        gamePanelContainer.add(sidebar, BorderLayout.EAST);

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
        javax.swing.Timer t = new javax.swing.Timer(150, e -> {
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

        javax.swing.Timer stepTimer = new javax.swing.Timer(500, null);
        stepTimer.addActionListener(e -> {
            int current = currentPlayer.getPosition();
            if (current == targetPos) {
                ((javax.swing.Timer)e.getSource()).stop();
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
            sound.playSfx("bonus.wav");

            javax.swing.Timer t = new javax.swing.Timer(1200, e -> {
                refreshUI();
                ((javax.swing.Timer)e.getSource()).stop();
            });
            t.start();
            return;
        }

        if(board.isPrime(pos)) {
            controlPanel.setStatus("PRIME SPOT! SHORTCUT HINT!", OceanTheme.WATER_DEEP);
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

        WoodenButton btnMenu = new WoodenButton("MAIN MENU");
        btnMenu.setAlignmentX(CENTER_ALIGNMENT);
        btnMenu.addActionListener(e -> {
            d.dispose();
            cardLayout.show(mainContainer, "MENU");
        });
        p.add(btnMenu);
        d.add(p);
        d.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SnakeLadderGame().setVisible(true);
        });
    }

    // ==========================================
    // REVISI UI: INPUT PLAYER + PREVIEW ICON
    // ==========================================
    class PlayerInputPanel extends JPanel {
        private JTextField nameField;
        private JComboBox<String> charSelector;
        private JLabel iconPreview;

        // Path icon untuk preview (sesuai urutan di ComboBox)
        private final String[] iconPaths = {
                "/assets/dolphin.png", "/assets/turtle.png",
                "/assets/submarine.png", "/assets/shark.png",
                "/assets/octopus.png"
        };
        private final ImageIcon[] cachedIcons = new ImageIcon[5];

        public PlayerInputPanel(int num) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));
            setOpaque(false);

            // Background semi-transparan untuk panel input agar tulisan terbaca
            JPanel bgPanel = new JPanel();
            bgPanel.setPreferredSize(new Dimension(600, 60));
            bgPanel.setBackground(new Color(0, 0, 0, 100)); // Hitam transparan
            bgPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
            bgPanel.setBorder(new LineBorder(OceanTheme.BORDER_GOLD, 1));

            // Label Nama
            JLabel lblName = new JLabel("Diver " + num);
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblName.setForeground(OceanTheme.PEARL_GOLD);

            // Input Nama
            nameField = new JTextField("Player " + num);
            nameField.setPreferredSize(new Dimension(150, 35));
            nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            nameField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            // Selector
            String[] chars = { "🐬 Dolphin", "🐢 Turtle", "🚁 Submarine", "🦈 Shark", "🐙 Octopus" };
            charSelector = new JComboBox<>(chars);
            charSelector.setPreferredSize(new Dimension(160, 35));
            charSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            // Icon Preview
            iconPreview = new JLabel();
            iconPreview.setPreferredSize(new Dimension(40, 40));

            // Load icons untuk preview
            loadIcons();
            updatePreview(); // Set initial

            // Update icon saat pilihan berubah
            charSelector.addActionListener(e -> updatePreview());

            bgPanel.add(lblName);
            bgPanel.add(nameField);
            bgPanel.add(new JLabel("  Character: ")); // Spacer text
            bgPanel.getComponent(2).setForeground(Color.WHITE);
            bgPanel.add(charSelector);
            bgPanel.add(iconPreview);

            add(bgPanel);
        }

        private void loadIcons() {
            try {
                for (int i = 0; i < iconPaths.length; i++) {
                    URL url = getClass().getResource(iconPaths[i]);
                    if (url != null) {
                        Image img = ImageIO.read(url).getScaledInstance(35, 35, Image.SCALE_SMOOTH);
                        cachedIcons[i] = new ImageIcon(img);
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        private void updatePreview() {
            int idx = charSelector.getSelectedIndex();
            if (idx >= 0 && idx < cachedIcons.length && cachedIcons[idx] != null) {
                iconPreview.setIcon(cachedIcons[idx]);
            } else {
                iconPreview.setIcon(null);
            }
        }

        public JTextField getNameField() { return nameField; }
        public int getSelectedCharType() { return charSelector.getSelectedIndex(); }
    }
}