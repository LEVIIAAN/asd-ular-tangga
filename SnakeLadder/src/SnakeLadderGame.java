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
    private int selectedPlayerCount = 2;
    private Image menuBackground;

    // FONT KHUSUS
    private final Font MINECRAFT_TITLE_FONT = new Font("Impact", Font.BOLD, 52);
    private final Font WOOD_BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public SnakeLadderGame() {
        // 1. Init Sound
        sound = new SoundManager();

        // 2. Pre-load Background Image
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
    // PAGE 1: MAIN MENU (CROP BACKGROUND)
    // ==========================================
    private JPanel createMainMenu() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawCroppedBackground(g, getWidth(), getHeight());
                // Overlay Hitam Transparan
                g.setColor(new Color(0, 0, 0, 60));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // JUDUL
        JLabel titleLabel = new JLabel("OCEAN ADVENTURES", SwingConstants.CENTER);
        titleLabel.setFont(MINECRAFT_TITLE_FONT);
        titleLabel.setForeground(OceanTheme.PEARL_GOLD);
        applyShadow(titleLabel);

        JLabel subTitleLabel = new JLabel("SNAKE LADDERS", SwingConstants.CENTER);
        subTitleLabel.setFont(MINECRAFT_TITLE_FONT.deriveFont(36f));
        subTitleLabel.setForeground(Color.WHITE);
        applyShadow(subTitleLabel);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        panel.add(subTitleLabel, gbc);

        gbc.gridy = 2;
        panel.add(Box.createVerticalStrut(30), gbc);

        // SELECTOR & BUTTONS
        JPanel middleRow = new JPanel(new GridBagLayout());
        middleRow.setOpaque(false);
        GridBagConstraints midGbc = new GridBagConstraints();
        midGbc.fill = GridBagConstraints.BOTH;
        midGbc.insets = new Insets(0, 10, 0, 10);

        JPanel selectorPanel = createWoodenSelector();
        midGbc.gridx = 0; midGbc.weightx = 0.3;
        middleRow.add(selectorPanel, midGbc);

        WoodenButton btnStart = new WoodenButton("START GAME");
        btnStart.setFont(WOOD_BUTTON_FONT.deriveFont(24f));
        btnStart.addActionListener(e -> showCharacterSelection(selectedPlayerCount));
        midGbc.gridx = 1; midGbc.weightx = 0.7;
        middleRow.add(btnStart, midGbc);

        JPanel wrapperMiddle = new JPanel(new BorderLayout());
        wrapperMiddle.setOpaque(false);
        wrapperMiddle.setBorder(new EmptyBorder(0, 100, 0, 100));
        wrapperMiddle.add(middleRow, BorderLayout.CENTER);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(wrapperMiddle, gbc);

        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomRow.setOpaque(false);
        bottomRow.setBorder(new EmptyBorder(0, 100, 0, 100));

        WoodenButton btnOptions = new WoodenButton("OPTIONS");
        btnOptions.addActionListener(e -> cardLayout.show(mainContainer, "SETTINGS"));

        WoodenButton btnQuit = new WoodenButton("QUIT GAME");
        btnQuit.addActionListener(e -> System.exit(0));

        bottomRow.add(btnOptions);
        bottomRow.add(btnQuit);

        gbc.gridy = 4; gbc.ipady = 15;
        panel.add(bottomRow, gbc);

        return panel;
    }

    // Helper: Menggambar background yang dipotong (Zoomed Map)
    private void drawCroppedBackground(Graphics g, int w, int h) {
        if (menuBackground != null) {
            int imgW = menuBackground.getWidth(null);
            int imgH = menuBackground.getHeight(null);
            double cutTop = 0.18;
            double cutBottom = 0.05;
            double cutSide = 0.04;

            int sx1 = (int) (imgW * cutSide);
            int sy1 = (int) (imgH * cutTop);
            int sx2 = (int) (imgW * (1.0 - cutSide));
            int sy2 = (int) (imgH * (1.0 - cutBottom));

            g.drawImage(menuBackground, 0, 0, w, h, sx1, sy1, sx2, sy2, null);
        } else {
            g.setColor(new Color(0, 105, 148));
            g.fillRect(0, 0, w, h);
        }
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
    // PAGE 2: CHARACTER SELECTION (AVATAR UI)
    // ==========================================
    private void showCharacterSelection(int n) {
        JPanel selectionPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawCroppedBackground(g, getWidth(), getHeight());
                g.setColor(new Color(0, 0, 0, 180));
                g.fillRect(0,0,getWidth(), getHeight());
            }
        };

        JLabel header = new JLabel("ASSEMBLE YOUR SQUAD", SwingConstants.CENTER);
        header.setFont(MINECRAFT_TITLE_FONT.deriveFont(42f));
        header.setForeground(OceanTheme.PEARL_GOLD);
        header.setBorder(new EmptyBorder(40, 0, 30, 0));
        selectionPanel.add(header, BorderLayout.NORTH);

        JPanel inputsContainer = new JPanel();
        inputsContainer.setLayout(new BoxLayout(inputsContainer, BoxLayout.Y_AXIS));
        inputsContainer.setOpaque(false);

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

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 40));
        footer.setOpaque(false);

        WoodenButton btnBack = new WoodenButton("BACK");
        btnBack.setPreferredSize(new Dimension(150, 50));
        btnBack.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));

        WoodenButton btnGo = new WoodenButton("LET'S DIVE!");
        btnGo.setPreferredSize(new Dimension(200, 50));

        // VALIDASI DUPLIKAT
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
                        "Duplicate Character", JOptionPane.WARNING_MESSAGE);
                return;
            }

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
    // PAGE 3: SETTINGS (WITH VOLUME)
    // ==========================================
    private JPanel createSettingsMenu() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawCroppedBackground(g, getWidth(), getHeight());
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("OPTIONS");
        title.setFont(MINECRAFT_TITLE_FONT.deriveFont(40f));
        title.setForeground(OceanTheme.PEARL_GOLD);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        // BGM
        gbc.gridy = 1;
        JCheckBox musicToggle = createWoodenCheckbox("BGM (Music)");
        musicToggle.setSelected(!sound.isBgmMuted());
        musicToggle.addActionListener(e -> {
            if (musicToggle.isSelected()) sound.playBgm("bgm.wav");
            else sound.stopBgm();
        });
        panel.add(musicToggle, gbc);

        gbc.gridy = 2;
        JSlider bgmSlider = createWoodenSlider();
        bgmSlider.setValue((int)(sound.getBgmVolume() * 100));
        bgmSlider.addChangeListener(e -> {
            float vol = bgmSlider.getValue() / 100f;
            sound.setBgmVolume(vol);
        });
        panel.add(bgmSlider, gbc);

        // SFX
        gbc.gridy = 3;
        panel.add(Box.createVerticalStrut(10), gbc);

        gbc.gridy = 4;
        JCheckBox sfxToggle = createWoodenCheckbox("SFX (Effects)");
        sfxToggle.setSelected(!sound.isSfxMuted());
        sfxToggle.addActionListener(e -> sound.setSfxMuted(!sfxToggle.isSelected()));
        panel.add(sfxToggle, gbc);

        gbc.gridy = 5;
        JSlider sfxSlider = createWoodenSlider();
        sfxSlider.setValue((int)(sound.getSfxVolume() * 100));
        sfxSlider.addChangeListener(e -> {
            float vol = sfxSlider.getValue() / 100f;
            sound.setSfxVolume(vol);
        });
        panel.add(sfxSlider, gbc);

        // DONE BUTTON
        gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 20, 20, 20);
        WoodenButton btnBack = new WoodenButton("DONE");
        btnBack.setPreferredSize(new Dimension(200, 50));
        btnBack.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));
        panel.add(btnBack, gbc);

        return panel;
    }

    // ==========================================
    // GAMEPLAY LOGIC (WITH PRIME JUMP)
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

        // 1. Tombol Home (Kayu + Icon Putih)
        JButton btnHome = new BtnHomeWooden();
        btnHome.setToolTipText("Main Menu");
        btnHome.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Quit to Main Menu?", "Quit", JOptionPane.YES_NO_OPTION);
            if(c == JOptionPane.YES_OPTION) cardLayout.show(mainContainer, "MENU");
        });

        // 2. Tombol Restart (Kayu + Icon Putih)
        JButton btnRestart = new BtnRestartWooden();
        btnRestart.setToolTipText("Restart Game");
        btnRestart.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Restart Game?", "Restart", JOptionPane.YES_NO_OPTION);
            if(c == JOptionPane.YES_OPTION) {
                List<Player> currentList = new ArrayList<>(playerStack);
                Collections.reverse(currentList);
                initGame(currentList);
            }
        });

        JPanel rightCorner = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // Jarak antar tombol 10px
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

        // [LOGIKA 1] Simpan posisi AWAL sebelum bergerak
        int startPos = currentPlayer.getPosition();

        int target = startPos + roll;
        int maxPos = board.getTotalSquares();

        if (target >= maxPos) {
            target = maxPos;
            controlPanel.setPath("Final Dash to Treasure!");
        }

        final int finalTarget = target;
        // [LOGIKA 2] Kirim startPos ke checkEvents untuk verifikasi aturan Prima
        animateMove(finalTarget, () -> checkEvents(finalTarget, startPos));
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

    // [LOGIKA 3] Implementasi Aturan: Tangga hanya aktif jika prevPos adalah Prima
    private void checkEvents(int pos, int prevPos) {
        int pts = board.collectPoint(pos);
        if(pts > 0) {
            currentPlayer.addScore(pts);
            LeaderboardManager.addScore(currentPlayer.getName(), pts);
            sound.playSfx("coin.wav");
            controlPanel.setStatus("FOUND PEARLS: " + pts + "!", OceanTheme.CORAL_ORANGE);
        }

        if(board.getLinks().containsKey(pos)) {
            Link link = board.getLinks().get(pos);
            int next = link.getTo();
            boolean isLadder = next > pos;

            if (isLadder) {
                // HANYA NAIK JIKA POSISI SEBELUMNYA ADALAH PRIMA
                if (board.isPrime(prevPos)) {
                    controlPanel.setStatus("PRIME JUMP! RIDING THE CURRENT!", OceanTheme.PEARL_GOLD);
                    sound.playSfx("bonus.wav");

                    javax.swing.Timer t = new javax.swing.Timer(800, ev -> {
                        ((javax.swing.Timer)ev.getSource()).stop();
                        animateMove(next, () -> checkSpecial(next));
                    });
                    t.start();
                } else {
                    // Jika bukan prima, tangga tidak aktif
                    controlPanel.setStatus("NO PRIME BOOST. STAYING.", Color.GRAY);
                    checkSpecial(pos);
                }
            } else {
                // Ular selalu aktif (Turun)
                controlPanel.setStatus("WHIRLPOOL DOWN!", OceanTheme.CORAL_ORANGE);
                javax.swing.Timer t = new javax.swing.Timer(800, ev -> {
                    ((javax.swing.Timer)ev.getSource()).stop();
                    animateMove(next, () -> checkSpecial(next));
                });
                t.start();
            }
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
    // HELPERS & CUSTOM COMPONENTS
    // ==========================================
    private JSlider createWoodenSlider() {
        JSlider slider = new JSlider(0, 100);
        slider.setOpaque(false);
        slider.setForeground(OceanTheme.PEARL_GOLD);
        slider.setBackground(Color.WHITE);
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(25);
        return slider;
    }

    private JCheckBox createWoodenCheckbox(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(WOOD_BUTTON_FONT.deriveFont(20f));
        cb.setForeground(Color.WHITE);
        cb.setOpaque(false);
        cb.setFocusPainted(false);
        return cb;
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
            g2.fillOval(10, 10, 4, 4); g2.fillOval(w-14, 10, 4, 4);
            g2.fillOval(10, h-14, 4, 4); g2.fillOval(w-14, h-14, 4, 4);

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

    class HomeIcon implements Icon {
        private final int size = 28;
        private final Color ROOF_COLOR = new Color(139, 69, 19);
        private final Color BODY_COLOR = new Color(180, 100, 55);
        private final Color DOOR_COLOR = new Color(80, 40, 10);
        private final Color OUTLINE_GOLD = new Color(255, 215, 0);

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);

            g2.setColor(new Color(0, 0, 0, 70));
            g2.fillPolygon(new int[]{size/2, 2, size-2}, new int[]{3, size/2+1, size/2+1}, 3);
            g2.fillRect(5, size/2+1, size-10, size/2-2);

            GradientPaint bodyPaint = new GradientPaint(0, size/2, BODY_COLOR.brighter(), size, size, BODY_COLOR.darker());
            g2.setPaint(bodyPaint);
            g2.fillRect(4, size/2, size-8, size/2-2);
            g2.setColor(OUTLINE_GOLD);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(4, size/2, size-8, size/2-2);

            int[] roofX = {size/2, 0, size};
            int[] roofY = {0, size/2, size/2};
            Polygon roof = new Polygon(roofX, roofY, 3);
            g2.setColor(ROOF_COLOR); g2.fill(roof);
            g2.setColor(OUTLINE_GOLD); g2.setStroke(new BasicStroke(2f)); g2.draw(roof);

            int doorW = 8, doorH = 10, doorX = (size-doorW)/2, doorY = size-doorH-2;
            g2.setColor(DOOR_COLOR); g2.fillRect(doorX, doorY, doorW, doorH);
            g2.setColor(OUTLINE_GOLD.darker()); g2.setStroke(new BasicStroke(1f)); g2.drawRect(doorX, doorY, doorW, doorH);
            g2.setColor(OUTLINE_GOLD); g2.fillOval(doorX+doorW-3, doorY+doorH/2-1, 2, 2);
            g2.dispose();
        }
        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    // UI KARAKTER BARU (AVATAR STYLE)
    static class PlayerInputPanel extends JPanel {
        private JTextField nameField;
        private JComboBox<String> charSelector;
        private JLabel iconPreview;

        // Path icon
        private final String[] iconPaths = {
                "/assets/dolphin.png", "/assets/turtle.png",
                "/assets/submarine.png", "/assets/shark.png",
                "/assets/octopus.png"
        };
        private final String[] charNames = { "Dolphin", "Turtle", "Submarine", "Shark", "Octopus" };
        private final ImageIcon[] cachedIcons = new ImageIcon[5];
        private final ImageIcon[] smallIcons = new ImageIcon[5];

        public PlayerInputPanel(int num) {
            setLayout(new BorderLayout(15, 0));
            setOpaque(false);

            // Background Kartu Pemain (Hitam Transparan)
            setBackground(new Color(0, 0, 0, 80));
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(OceanTheme.BORDER_GOLD, 2, true), // Border Emas Luar
                    new EmptyBorder(10, 15, 10, 15) // Padding Dalam
            ));
            setMaximumSize(new Dimension(600, 85));

            loadIcons();

            // 1. ICON PREVIEW (KIRI)
            iconPreview = new JLabel();
            iconPreview.setPreferredSize(new Dimension(50, 50));
            iconPreview.setHorizontalAlignment(SwingConstants.CENTER);
            iconPreview.setBorder(new LineBorder(new Color(255, 255, 255, 50), 1));
            add(iconPreview, BorderLayout.WEST);

            // 2. TENGAH: NAMA & PILIHAN KARAKTER
            JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 8)); // Gap vertikal 8px
            centerPanel.setOpaque(false);

            // A. Baris Input Nama
            JPanel nameRow = new JPanel(new BorderLayout(10, 0));
            nameRow.setOpaque(false);

            JLabel lblName = new JLabel("Diver " + num + ":");
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblName.setForeground(OceanTheme.PEARL_GOLD); // Warna Emas

            nameField = new JTextField("Player " + num);
            nameField.setFont(new Font("Segoe UI", Font.BOLD, 14));

            // [FIX UTAMA DISINI]
            // Jangan transparan. Gunakan warna solid gelap agar teks tidak glitch.
            nameField.setOpaque(true);
            nameField.setBackground(new Color(30, 40, 50)); // Biru Gelap Solid
            nameField.setForeground(Color.WHITE);           // Teks Putih
            nameField.setCaretColor(Color.WHITE);           // Kursor Putih

            // Beri Border Emas Tipis pada kotak input
            nameField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(218, 165, 32), 1),
                    BorderFactory.createEmptyBorder(2, 8, 2, 8) // Padding teks di dalam kotak
            ));

            nameRow.add(lblName, BorderLayout.WEST);
            nameRow.add(nameField, BorderLayout.CENTER);

            // B. Baris Dropdown Karakter
            charSelector = new JComboBox<>(charNames);
            charSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            charSelector.setFocusable(false);

            // Custom Renderer untuk menampilkan Icon kecil di dropdown
            charSelector.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    int iconIdx = -1;
                    for(int i=0; i<charNames.length; i++) {
                        if(charNames[i].equals(value)) { iconIdx = i; break; }
                    }
                    if (iconIdx >= 0 && smallIcons[iconIdx] != null) {
                        label.setIcon(smallIcons[iconIdx]);
                        label.setIconTextGap(10);
                    }
                    return label;
                }
            });

            charSelector.addActionListener(e -> updatePreview());

            centerPanel.add(nameRow);
            centerPanel.add(charSelector);
            add(centerPanel, BorderLayout.CENTER);

            updatePreview();
        }

        private void loadIcons() {
            try {
                for (int i = 0; i < iconPaths.length; i++) {
                    URL url = getClass().getResource(iconPaths[i]);
                    if (url != null) {
                        BufferedImage original = ImageIO.read(url);
                        cachedIcons[i] = new ImageIcon(original.getScaledInstance(45, 45, Image.SCALE_SMOOTH));
                        smallIcons[i] = new ImageIcon(original.getScaledInstance(20, 20, Image.SCALE_SMOOTH));
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

        @Override
        protected void paintComponent(Graphics g) {
            // Menggambar background panel yang membulat
            g.setColor(getBackground());
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g);
        }
    }
    // ==========================================
    // CUSTOM MINI BUTTONS (TOP BAR) 🪵
    // ==========================================

    // Base Class: Membuat kotak kayu seragam
    abstract class MiniWoodenButton extends JButton {
        private final Color WOOD_LIGHT = new Color(160, 82, 45);
        private final Color WOOD_MAIN  = new Color(139, 69, 19);
        private final Color WOOD_DARK  = new Color(80, 40, 10);
        private final Color BORDER     = new Color(218, 165, 32);
        private boolean isHovered = false;

        public MiniWoodenButton() {
            setPreferredSize(new Dimension(45, 45)); // Ukuran kotak pas
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

            // 1. Background Kayu (Sama persis dengan tombol menu)
            Color baseColor = WOOD_MAIN;
            if (getModel().isPressed()) baseColor = WOOD_DARK;
            else if (isHovered) baseColor = WOOD_LIGHT;

            GradientPaint woodGradient = new GradientPaint(0, 0, baseColor.brighter(), 0, h, baseColor.darker());
            g2.setPaint(woodGradient);
            g2.fillRoundRect(2, 2, w - 4, h - 4, 12, 12);

            // 2. Border Emas
            g2.setColor(BORDER);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(2, 2, w - 4, h - 4, 12, 12);

            // 3. Paku di Sudut (Detail kecil)
            g2.setColor(new Color(60, 40, 10));
            g2.fillOval(5, 5, 3, 3); g2.fillOval(w-8, 5, 3, 3);
            g2.fillOval(5, h-8, 3, 3); g2.fillOval(w-8, h-8, 3, 3);

            // 4. Gambar Icon (Abstract)
            drawIcon(g2, w, h);

            g2.dispose();
        }

        protected abstract void drawIcon(Graphics2D g2, int w, int h);
    }

    // Tombol HOME: Menggambar Rumah Putih
    class BtnHomeWooden extends MiniWoodenButton {
        @Override
        protected void drawIcon(Graphics2D g2, int w, int h) {
            g2.setColor(Color.WHITE);
            int cx = w / 2;
            int cy = h / 2;

            // Atap Segitiga
            Polygon roof = new Polygon();
            roof.addPoint(cx, cy - 7);
            roof.addPoint(cx - 9, cy + 2);
            roof.addPoint(cx + 9, cy + 2);
            g2.fillPolygon(roof);

            // Badan Kotak
            g2.fillRect(cx - 6, cy + 2, 12, 8);

            // Pintu (Lubang kecil)
            g2.setColor(new Color(139, 69, 19)); // Warna kayu gelap
            g2.fillRect(cx - 2, cy + 6, 4, 4);
        }
    }

    // Tombol RESTART: Menggambar Panah Putar Putih
    class BtnRestartWooden extends MiniWoodenButton {
        @Override
        protected void drawIcon(Graphics2D g2, int w, int h) {
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = w / 2;
            int cy = h / 2;
            int r = 6;

            // Gambar Panah Melingkar
            g2.drawArc(cx - r, cy - r, r * 2, r * 2, 45, 270);

            // Ujung Panah
            // Koordinat manual agar tajam
            int tipX = cx + 3;
            int tipY = cy - 5;
            Polygon arrow = new Polygon();
            arrow.addPoint(tipX, tipY);
            arrow.addPoint(tipX - 4, tipY + 1);
            arrow.addPoint(tipX + 1, tipY + 5);
            g2.fillPolygon(arrow);
        }
    }
}