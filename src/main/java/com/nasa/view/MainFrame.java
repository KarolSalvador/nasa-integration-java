package com.nasa.view;

import com.nasa.service.NasaApiClient;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URI;
import java.util.List;

public class MainFrame extends JFrame {
    private final NasaApiClient apiClient;

    private JComboBox<String> cbGalaxies;
    private JLabel lblTitle;
    private JTextArea taDetails;
    private JLabel lblImage;

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JComboBox<String> cbGalaxiesTop;
    private JPanel thumbnailPanel;
    private JScrollPane thumbScrollPane;
    private List<NasaApiClient.NasaImageResult> currentGalaxyResults;
    private int selectedThumbnailIndex = -1;
    private java.awt.image.BufferedImage cachedOriginalImage = null;

    private final Color COLOR_BG = new Color(10, 14, 20);
    private final Color COLOR_PANEL = new Color(18, 24, 33);
    private final Color COLOR_INPUT = new Color(28, 36, 48);
    private final Color COLOR_ACCENT = new Color(0, 122, 255);
    private final Color COLOR_HOVER = new Color(51, 153, 255);
    private final Color COLOR_TEXT = new Color(240, 246, 252);
    private final Color COLOR_MUTED = new Color(140, 155, 175);
    private final Color COLOR_BORDER = new Color(40, 52, 70);

    private final String[] GALAXY_OPTIONS = {
        "Andromeda Galaxy",
        "Milky Way",
        "Orion Nebula",
        "Sombrero Galaxy",
        "Whirlpool Galaxy",
        "Pillars of Creation",
        "Crab Nebula",
        "Ring Nebula",
        "Eagle Nebula",
        "Carina Nebula",
        "Triangulum Galaxy",
        "Centaurus A",
        "Pinwheel Galaxy",
        "Supernova Remnant",
        "Jupiter",
        "Saturn",
        "Mars Surface",
        "Solar Flare"
    };

    public MainFrame() {
        super("NASA Telemetry & Exploration Hub");
        apiClient = new NasaApiClient();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 850);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(COLOR_BG);

        // --- TELA 1: WELCOME ---
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

        JLabel lblWelcomeSub = new JLabel("Explore galáxias, nebulosas e astros do universo");
        lblWelcomeSub.setForeground(COLOR_MUTED);
        lblWelcomeSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblWelcomeSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerBox.add(lblWelcomeTitle);
        centerBox.add(Box.createVerticalStrut(5));
        centerBox.add(lblWelcomeSub);
        centerBox.add(Box.createVerticalStrut(25));

        JLabel lblInputLabel0 = new JLabel("SELECIONE O ASTRO OU GALÁXIA:");
        styleLabel(lblInputLabel0);
        lblInputLabel0.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerBox.add(lblInputLabel0);
        centerBox.add(Box.createVerticalStrut(5));

        JPanel panelGalaxyContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelGalaxyContainer.setBackground(COLOR_PANEL);
        cbGalaxies = new JComboBox<>(GALAXY_OPTIONS);
        styleComboBox(cbGalaxies);
        panelGalaxyContainer.add(cbGalaxies);
        panelGalaxyContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerBox.add(panelGalaxyContainer);

        centerBox.add(Box.createVerticalStrut(25));
        JButton btnStart = createStyledButton("INICIALIZAR TRANSMISSÃO");
        btnStart.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerBox.add(btnStart);

        welcomePanel.add(centerBox);
        cardPanel.add(welcomePanel, "WELCOME");

        // --- TELA 2: ACTIVE ---
        JPanel activePanel = new JPanel(new BorderLayout(15, 15));
        activePanel.setBackground(COLOR_BG);
        activePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(COLOR_PANEL);
        topPanel.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));

        JLabel lblTopParamLabel = new JLabel("ASTRO:");
        lblTopParamLabel.setForeground(COLOR_MUTED);
        lblTopParamLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));

        cbGalaxiesTop = new JComboBox<>(GALAXY_OPTIONS);
        styleComboBox(cbGalaxiesTop);
        cbGalaxiesTop.addActionListener(e -> cbGalaxies.setSelectedIndex(cbGalaxiesTop.getSelectedIndex()));
        cbGalaxies.addActionListener(e -> cbGalaxiesTop.setSelectedIndex(cbGalaxies.getSelectedIndex()));

        JButton btnSearchTop = createStyledButton("ATUALIZAR");

        topPanel.add(lblTopParamLabel);
        topPanel.add(cbGalaxiesTop);
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
        lblImage.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (cachedOriginalImage != null && currentGalaxyResults != null && !currentGalaxyResults.isEmpty()) {
                    openImageModal();
                }
            }
        });

        JPanel imageContainer = new JPanel(new BorderLayout());
        imageContainer.setBackground(COLOR_PANEL);
        imageContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        imageContainer.add(lblImage, BorderLayout.CENTER);
        centerContent.add(imageContainer, BorderLayout.CENTER);

        thumbnailPanel = new JPanel();
        thumbnailPanel.setLayout(new BoxLayout(thumbnailPanel, BoxLayout.Y_AXIS));
        thumbnailPanel.setBackground(COLOR_PANEL);

        thumbScrollPane = new JScrollPane(thumbnailPanel);
        thumbScrollPane.setPreferredSize(new Dimension(200, 0));
        thumbScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            "Registros da Missão",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11),
            COLOR_MUTED
        ));
        thumbScrollPane.getViewport().setBackground(COLOR_PANEL);
        thumbScrollPane.setVisible(false);
        centerContent.add(thumbScrollPane, BorderLayout.EAST);

        imageContainer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (cachedOriginalImage != null) {
                    int w = imageContainer.getWidth() - 20;
                    int h = imageContainer.getHeight() - 20;
                    if (w > 100 && h > 100) {
                        lblImage.setIcon(getScaledImageIcon(cachedOriginalImage, w, h));
                    }
                }
            }
        });

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

    private void styleComboBox(JComboBox<String> cb) {
        cb.setBackground(COLOR_INPUT);
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cb.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        cb.setOpaque(true);

        // Força o editor interno a ter fundo escuro e texto branco no Windows
        if (cb.getEditor() != null && cb.getEditor().getEditorComponent() != null) {
            Component editorComp = cb.getEditor().getEditorComponent();
            editorComp.setBackground(COLOR_INPUT);
            editorComp.setForeground(Color.WHITE);
        }

        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                // Força o fundo da lista suspensa a ficar escuro
                list.setBackground(COLOR_INPUT);
                list.setForeground(Color.WHITE);

                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                
                if (isSelected) {
                    c.setBackground(COLOR_ACCENT);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(COLOR_INPUT);
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
    }

    private void executeSearch() {
        String queryStr = (String) cbGalaxies.getSelectedItem();
        lblTitle.setText("Buscando nos arquivos estelares da NASA...");
        lblImage.setIcon(null);
        lblImage.setText("Baixando pacotes visuais...");
        taDetails.setText("");
        cachedOriginalImage = null;
        thumbScrollPane.setVisible(false);
        selectedThumbnailIndex = 0;

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            String titleText = "";
            String detailsText = "";
            java.awt.image.BufferedImage loadedImg = null;
            List<NasaApiClient.NasaImageResult> resultsList = null;
            String errorMsg = null;

            @Override
            protected Void doInBackground() {
                try {
                    resultsList = apiClient.searchSpaceObjects(queryStr);
                    if (!resultsList.isEmpty()) {
                        NasaApiClient.NasaImageResult first = resultsList.get(0);
                        titleText = first.getTitle();
                        detailsText = first.getDescription();
                        loadedImg = javax.imageio.ImageIO.read(URI.create(first.getImageUrl()).toURL());
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
                    if (loadedImg != null) {
                        cachedOriginalImage = loadedImg;
                        int maxWidth = lblImage.getParent() != null ? lblImage.getParent().getWidth() - 20 : 800;
                        int maxHeight = lblImage.getParent() != null ? lblImage.getParent().getHeight() - 20 : 500;
                        if (maxWidth < 200) maxWidth = 800;
                        if (maxHeight < 200) maxHeight = 500;
                        lblImage.setIcon(getScaledImageIcon(loadedImg, maxWidth, maxHeight));
                        lblImage.setText("");
                        if (resultsList != null) {
                            currentGalaxyResults = resultsList;
                            populateThumbnailGallery(resultsList);
                        }
                    } else {
                        lblImage.setText("Nenhum dado visual retornado.");
                    }
                }
            }
        };
        worker.execute();
    }

    private void populateThumbnailGallery(List<NasaApiClient.NasaImageResult> results) {
        thumbnailPanel.removeAll();
        for (int i = 0; i < results.size(); i++) {
            final int index = i;
            NasaApiClient.NasaImageResult res = results.get(i);

            JButton thumbBtn = new JButton("Registro #" + (i + 1)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    if (selectedThumbnailIndex == index) {
                        g2.setColor(COLOR_ACCENT);
                    } else {
                        g2.setColor(getModel().isRollover() ? COLOR_HOVER : COLOR_INPUT);
                    }

                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            thumbBtn.setForeground(Color.WHITE);
            thumbBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            thumbBtn.setFocusPainted(false);
            thumbBtn.setBorder(new EmptyBorder(8, 10, 8, 10));
            thumbBtn.setContentAreaFilled(false);
            thumbBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            thumbBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            thumbBtn.setMaximumSize(new Dimension(180, 35));

            thumbBtn.addActionListener(e -> {
                selectedThumbnailIndex = index;
                populateThumbnailGallery(currentGalaxyResults);
                loadSelectedImage(res);
            });

            thumbnailPanel.add(thumbBtn);
            thumbnailPanel.add(Box.createVerticalStrut(5));
        }
        thumbScrollPane.setVisible(true);
        thumbnailPanel.revalidate();
        thumbnailPanel.repaint();
    }

    private void loadSelectedImage(NasaApiClient.NasaImageResult res) {
        lblTitle.setText(res.getTitle());
        taDetails.setText(res.getDescription());
        lblImage.setIcon(null);
        lblImage.setText("Carregando registro selecionado...");

        SwingWorker<java.awt.image.BufferedImage, Void> imgWorker = new SwingWorker<>() {
            @Override
            protected java.awt.image.BufferedImage doInBackground() throws Exception {
                return javax.imageio.ImageIO.read(URI.create(res.getImageUrl()).toURL());
            }

            @Override
            protected void done() {
                try {
                    java.awt.image.BufferedImage img = get();
                    if (img != null) {
                        cachedOriginalImage = img;
                        int maxWidth = lblImage.getParent().getWidth() - 20;
                        int maxHeight = lblImage.getParent().getHeight() - 20;
                        lblImage.setIcon(getScaledImageIcon(img, Math.max(maxWidth, 200), Math.max(maxHeight, 200)));
                        lblImage.setText("");
                    }
                } catch (Exception ex) {
                    lblImage.setText("Falha ao carregar imagem.");
                }
            }
        };
        imgWorker.execute();
    }

    private void openImageModal() {
        JDialog modal = new JDialog(this, "Visualizador Ampliado - Alta Resolução", true);
        modal.setSize(1000, 700);
        modal.setLocationRelativeTo(this);
        modal.getContentPane().setBackground(COLOR_BG);

        JLabel lblModalImg = new JLabel("", JLabel.CENTER);
        if (cachedOriginalImage != null) {
            lblModalImg.setIcon(new ImageIcon(cachedOriginalImage));
        }

        JScrollPane modalScroll = new JScrollPane(lblModalImg);
        modalScroll.getViewport().setBackground(COLOR_BG);
        modal.add(modalScroll, BorderLayout.CENTER);

        JButton btnClose = createStyledButton("FECHAR VISUALIZADOR");
        btnClose.addActionListener(e -> modal.dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(COLOR_PANEL);
        bottomPanel.add(btnClose);
        modal.add(bottomPanel, BorderLayout.SOUTH);

        modal.setVisible(true);
    }

    private ImageIcon getScaledImageIcon(java.awt.image.BufferedImage img, int maxWidth, int maxHeight) {
        int originalWidth = img.getWidth();
        int originalHeight = img.getHeight();
        double scale = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);
        if (scale > 1.0) scale = 1.0;

        int targetWidth = (int) (originalWidth * scale);
        int targetHeight = (int) (originalHeight * scale);
        Image scaledImg = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}