package com.nasa.view;

import com.nasa.model.ApodResponse;
import com.nasa.model.EpicResponse;
import com.nasa.service.NasaApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;
import java.time.LocalDate;

public class MainFrame extends JFrame {
    private final NasaApiClient apiClient;
    
    private JButton btnApodTab;
    private JButton btnEpicTab;
    private int selectedModule = 0; // 0 = APOD, 1 = EPIC

    private JTextField tfDate;
    private JLabel lblTitle;
    private JTextArea taDetails;
    private JLabel lblImage;
    
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // Paleta Dark Sci-Fi Moderna
    private final Color COLOR_BG = new Color(10, 14, 20);       
    private final Color COLOR_PANEL = new Color(18, 24, 33);    
    private final Color COLOR_INPUT = new Color(28, 36, 48);    
    private final Color COLOR_ACCENT = new Color(0, 122, 255);  
    private final Color COLOR_HOVER = new Color(51, 153, 255);  
    private final Color COLOR_TEXT = new Color(240, 246, 252);  
    private final Color COLOR_MUTED = new Color(140, 155, 175); 
    private final Color COLOR_BORDER = new Color(40, 52, 70);   

    public MainFrame() {
        super("NASA Telemetry & Exploration Hub");
        apiClient = new NasaApiClient();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(COLOR_BG);

        // --- TELA 1: ESTADO INICIAL (Centralizado) ---
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(COLOR_BG);
        
        JPanel centerBox = new JPanel();
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));
        centerBox.setBackground(COLOR_PANEL);
        centerBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            new EmptyBorder(35, 45, 35, 45)
        ));

        JLabel lblWelcomeTitle = new JLabel("NASA EXPLORATION HUB");
        lblWelcomeTitle.setForeground(COLOR_TEXT);
        lblWelcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblWelcomeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblWelcomeSub = new JLabel("Escolha o módulo de telemetria e informe a data");
        lblWelcomeSub.setForeground(COLOR_MUTED);
        lblWelcomeSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblWelcomeSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerBox.add(lblWelcomeTitle);
        centerBox.add(Box.createVerticalStrut(5));
        centerBox.add(lblWelcomeSub);
        centerBox.add(Box.createVerticalStrut(25));

        // Seletores de Módulo Padronizados
        JPanel moduleBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        moduleBox.setBackground(COLOR_PANEL);
        
        btnApodTab = createTabButton("APOD (Astronomia)", 0);
        btnEpicTab = createTabButton("EPIC (Terra Global)", 1);
        updateModuleSelectionUI();

        moduleBox.add(btnApodTab);
        moduleBox.add(btnEpicTab);
        moduleBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblModLabel = new JLabel("MÓDULO DE DADOS:");
        styleLabel(lblModLabel);
        lblModLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerBox.add(lblModLabel);
        centerBox.add(Box.createVerticalStrut(5));
        centerBox.add(moduleBox);
        centerBox.add(Box.createVerticalStrut(20));

        // Campo de Data Direto
        JLabel lblDate = new JLabel("DATA (Formato: AAAA-MM-DD):");
        styleLabel(lblDate);
        lblDate.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        datePanel.setBackground(COLOR_PANEL);
        
        tfDate = new JTextField(LocalDate.now().minusDays(1).toString(), 14);
        styleTextField(tfDate);

        datePanel.add(tfDate);
        datePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerBox.add(lblDate);
        centerBox.add(Box.createVerticalStrut(5));
        centerBox.add(datePanel);
        centerBox.add(Box.createVerticalStrut(25));

        JButton btnStart = createStyledButton("INICIALIZAR TRANSMISSÃO");
        btnStart.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerBox.add(btnStart);

        welcomePanel.add(centerBox);
        cardPanel.add(welcomePanel, "WELCOME");

        // --- TELA 2: ESTADO ATIVO (Painel Completo) ---
        JPanel activePanel = new JPanel(new BorderLayout(15, 15));
        activePanel.setBackground(COLOR_BG);
        activePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(COLOR_PANEL);
        topPanel.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));

        JButton btnTopApod = createTabButton("APOD", 0);
        JButton btnTopEpic = createTabButton("EPIC", 1);
        
        btnTopApod.addActionListener(e -> { selectedModule = 0; updateModuleSelectionUI(); });
        btnTopEpic.addActionListener(e -> { selectedModule = 1; updateModuleSelectionUI(); });

        JTextField tfDateTop = new JTextField(tfDate.getText(), 12);
        styleTextField(tfDateTop);
        tfDateTop.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { tfDate.setText(tfDateTop.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { tfDate.setText(tfDateTop.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { tfDate.setText(tfDateTop.getText()); }
        });

        JButton btnSearchTop = createStyledButton("ATUALIZAR");

        topPanel.add(new JLabel("MÓDULO:") {{ setForeground(COLOR_MUTED); setFont(new Font("Segoe UI", Font.BOLD, 11)); }});
        topPanel.add(btnTopApod);
        topPanel.add(btnTopEpic);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(new JLabel("DATA:") {{ setForeground(COLOR_MUTED); setFont(new Font("Segoe UI", Font.BOLD, 11)); }});
        topPanel.add(tfDateTop);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(btnSearchTop);

        activePanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerContent = new JPanel(new BorderLayout(10, 10));
        centerContent.setBackground(COLOR_BG);

        lblTitle = new JLabel("TELEMETRIA ESPACIAL", JLabel.CENTER);
        lblTitle.setForeground(COLOR_TEXT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        centerContent.add(lblTitle, BorderLayout.NORTH);

        lblImage = new JLabel("Aguardando transmissão de dados visuais...", JLabel.CENTER);
        lblImage.setForeground(COLOR_MUTED);
        lblImage.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        
        JPanel imageContainer = new JPanel(new BorderLayout());
        imageContainer.setBackground(COLOR_PANEL);
        imageContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        imageContainer.add(lblImage, BorderLayout.CENTER);
        centerContent.add(imageContainer, BorderLayout.CENTER);

        activePanel.add(centerContent, BorderLayout.CENTER);

        taDetails = new JTextArea(3, 40);
        taDetails.setBackground(COLOR_INPUT);
        taDetails.setForeground(COLOR_TEXT);
        taDetails.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taDetails.setLineWrap(true);
        taDetails.setWrapStyleWord(true);
        taDetails.setEditable(false);
        taDetails.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scrollPane = new JScrollPane(taDetails);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        activePanel.add(scrollPane, BorderLayout.SOUTH);

        cardPanel.add(activePanel, "ACTIVE");
        add(cardPanel, BorderLayout.CENTER);

        btnStart.addActionListener(e -> {
            cardLayout.show(cardPanel, "ACTIVE");
            executeSearch();
        });

        btnSearchTop.addActionListener(e -> executeSearch());
    }

    private JButton createTabButton(String text, int moduleIndex) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (selectedModule == moduleIndex) {
                    g2.setColor(COLOR_ACCENT);
                } else {
                    g2.setColor(COLOR_INPUT);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addActionListener(e -> {
            selectedModule = moduleIndex;
            updateModuleSelectionUI();
        });
        return btn;
    }

    private void updateModuleSelectionUI() {
        if (btnApodTab != null && btnEpicTab != null) {
            btnApodTab.repaint();
            btnEpicTab.repaint();
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? COLOR_HOVER : COLOR_ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void styleLabel(JLabel label) {
        label.setForeground(COLOR_MUTED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
    }

    private void styleTextField(JTextField tf) {
        tf.setBackground(COLOR_INPUT);
        tf.setForeground(COLOR_TEXT);
        tf.setCaretColor(Color.WHITE);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void executeSearch() {
        String dateStr = tfDate.getText().trim();
        lblTitle.setText("Sincronizando com a frota orbital da NASA...");
        lblImage.setIcon(null);
        lblImage.setText("Baixando pacotes visuais...");
        taDetails.setText("");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            String titleText = "";
            String detailsText = "";
            ImageIcon imgIcon = null;
            String errorMsg = null;

            @Override
            protected Void doInBackground() {
                try {
                    if (selectedModule == 0) {
                        ApodResponse apod = apiClient.fetchApod(dateStr);
                        titleText = (apod.getTitle() != null ? apod.getTitle() : "APOD") + " (" + apod.getDate() + ")";
                        detailsText = apod.getExplanation();
                        if (apod.getUrl() != null && !apod.getUrl().isEmpty()) {
                            imgIcon = resizeImage(URI.create(apod.getUrl()), 580, 340);
                        }
                    } else {
                        EpicResponse epic = apiClient.fetchEpic(dateStr);
                        titleText = "Terra Global (DSCOVR) - " + epic.getDate();
                        detailsText = epic.getCaption();
                        String imgUrl = epic.getImageUrl(dateStr);
                        if (imgUrl != null && !imgUrl.isEmpty()) {
                            imgIcon = resizeImage(URI.create(imgUrl), 360, 360);
                        }
                    }
                } catch (Exception ex) {
                    errorMsg = ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (errorMsg != null) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Erro de Telemetria: " + errorMsg, "Falha", JOptionPane.ERROR_MESSAGE);
                    lblTitle.setText("FALHA NA TRANSMISSÃO");
                    lblImage.setText("Sinal perdido.");
                } else {
                    lblTitle.setText(titleText);
                    taDetails.setText(detailsText);
                    if (imgIcon != null) {
                        lblImage.setIcon(imgIcon);
                        lblImage.setText("");
                    } else {
                        lblImage.setText("Nenhum dado visual retornado para esta data.");
                    }
                }
            }
        };
        worker.execute();
    }

    private ImageIcon resizeImage(URI uri, int targetWidth, int targetHeight) {
        try {
            // Lê a imagem via InputStream para evitar bloqueios de URL do Java Swing
            java.awt.image.BufferedImage originalImage = javax.imageio.ImageIO.read(uri.toURL());
            if (originalImage != null) {
                Image resizedImg = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                return new ImageIcon(resizedImg);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem da URL: " + uri + " | Motivo: " + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MainFrame().setVisible(true);
        });
    }
}