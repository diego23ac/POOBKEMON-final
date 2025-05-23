package Presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Panel de selección de modo de juego y tipo de batalla
 */
public class GameMode extends BasePanel {
    
    // Componentes de la interfaz
    private JLabel backgroundLabel;
    private JLabel titleLabel;
    
    // Paneles para cada modo
    private JPanel normalModePanel;
    private JPanel survivalModePanel;
    
    // Botones de modo de juego
    private JButton normalModeButton;
    private JButton survivalModeButton;
    
    // Botones de tipo de batalla (Normal Mode)
    private JButton playerVsPlayerButton;
    private JButton playerVsMachineButton;
    private JButton machineVsMachineButton;
    
    // Botón de supervivencia (Survival Mode)
    private JButton survivalStartButton;
    
    // Botón de retorno
    private JButton backButton;
    
    // Estado actual
    private String selectedMode = null;
    private String selectedBattleType = null;
    
    /**
     * Constructor
     */
    public GameMode() {
        super();
        initializeComponents();
    }
    
    @Override
    protected void initializeComponents() {
        setLayout(null);
        setBackground(Color.BLACK);
        
        // Crear fondo
        createBackground();
        
        // Crear título
        createTitle();
        
        // Crear selección de modo
        createModeSelection();
        
        // Crear paneles de opciones de batalla
        createBattleOptions();
        
        // Crear botón de retorno
        createBackButton();
        
        // Mostrar solo la selección de modo inicialmente
        showModeSelection();
        
        if (currentSize != null) {
            adjustComponentsToSize();
        }
    }
    
    /**
     * Crea el fondo del panel
     */
    private void createBackground() {
        backgroundLabel = new JLabel();
        backgroundLabel.setOpaque(true);
        backgroundLabel.setBackground(new Color(30, 30, 40));
        add(backgroundLabel);
    }
    
    /**
     * Crea el título principal
     */
    private void createTitle() {
        titleLabel = createGameLabel("SELECT GAME MODE", 0, 50, 800, 40, 24f);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel);
    }
    
    /**
     * Crea los botones de selección de modo
     */
    private void createModeSelection() {
        // Botón Modo Normal
        normalModeButton = createStyledButton("NORMAL MODE", 
            "Classic Pokemon battles with various battle types");
        normalModeButton.addActionListener(e -> selectNormalMode());
        
        // Botón Modo Supervivencia
        survivalModeButton = createStyledButton("SURVIVAL MODE", 
            "Test your skills in endless battles (Player vs Player only)");
        survivalModeButton.addActionListener(e -> selectSurvivalMode());
        
        add(normalModeButton);
        add(survivalModeButton);
    }
    
    /**
     * Crea las opciones de batalla para cada modo
     */
    private void createBattleOptions() {
        // Panel para modo normal
        normalModePanel = new JPanel();
        normalModePanel.setLayout(null);
        normalModePanel.setOpaque(false);
        
        JLabel normalTitle = createGameLabel("SELECT BATTLE TYPE", 0, 0, 600, 30, 18f);
        normalTitle.setForeground(Color.WHITE);
        normalTitle.setHorizontalAlignment(JLabel.CENTER);
        normalModePanel.add(normalTitle);
        
        // Botones de tipo de batalla
        playerVsPlayerButton = createBattleTypeButton("PLAYER VS PLAYER", 
            "Battle against another human player", 0, 60);
        playerVsPlayerButton.addActionListener(e -> selectBattleType("PVP"));
        
        playerVsMachineButton = createBattleTypeButton("PLAYER VS MACHINE", 
            "Battle against the computer AI", 0, 150);
        playerVsMachineButton.addActionListener(e -> selectBattleType("PVM"));
        
        machineVsMachineButton = createBattleTypeButton("MACHINE VS MACHINE", 
            "Watch two AI battle each other", 0, 240);
        machineVsMachineButton.addActionListener(e -> selectBattleType("MVM"));
        
        normalModePanel.add(playerVsPlayerButton);
        normalModePanel.add(playerVsMachineButton);
        normalModePanel.add(machineVsMachineButton);
        
        // Panel para modo supervivencia
        survivalModePanel = new JPanel();
        survivalModePanel.setLayout(null);
        survivalModePanel.setOpaque(false);
        
        JLabel survivalTitle = createGameLabel("SURVIVAL MODE", 0, 0, 600, 30, 18f);
        survivalTitle.setForeground(Color.WHITE);
        survivalTitle.setHorizontalAlignment(JLabel.CENTER);
        survivalModePanel.add(survivalTitle);
        
        JLabel survivalDesc = createGameLabel(
            "Challenge yourself in endless Player vs Player battles!", 
            0, 50, 600, 60, 12f);
        survivalDesc.setForeground(Color.YELLOW);
        survivalDesc.setHorizontalAlignment(JLabel.CENTER);
        survivalModePanel.add(survivalDesc);
        
        survivalStartButton = createGameButton("START SURVIVAL", 200, 150, 200, 60, 16f);
        survivalStartButton.addActionListener(e -> startSurvivalMode());
        survivalModePanel.add(survivalStartButton);
        
        // Añadir paneles pero ocultarlos inicialmente
        normalModePanel.setVisible(false);
        survivalModePanel.setVisible(false);
        
        add(normalModePanel);
        add(survivalModePanel);
    }
    
    /**
     * Crea un botón estilizado para los modos
     */
    private JButton createStyledButton(String title, String description) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        
        // Panel interno para el contenido
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Título del modo
        JLabel titleLabel = new JLabel(title);
        setGameFont(titleLabel, 18f);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Descripción
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        setGameFont(descLabel, 10f);
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(descLabel);
        
        button.add(contentPanel, BorderLayout.CENTER);
        
        // Estilo del botón
        button.setBackground(new Color(50, 50, 70));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efectos hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(70, 70, 90));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.YELLOW, 2),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(50, 50, 70));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 2),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });
        
        return button;
    }
    
    /**
     * Crea un botón de tipo de batalla
     */
    private JButton createBattleTypeButton(String text, String description, int x, int y) {
        JButton button = createGameButton(text, x, y, 400, 70, 14f);
        
        // Añadir descripción como tooltip
        button.setToolTipText(description);
        
        return button;
    }
    
    /**
     * Crea el botón de retorno
     */
    private void createBackButton() {
        backButton = createGameButton("BACK", 50, 50, 100, 40, 12f);
        backButton.addActionListener(e -> handleBack());
        add(backButton);
    }
    
    /**
     * Muestra la selección de modo inicial
     */
    private void showModeSelection() {
        titleLabel.setText("SELECT GAME MODE");
        normalModeButton.setVisible(true);
        survivalModeButton.setVisible(true);
        normalModePanel.setVisible(false);
        survivalModePanel.setVisible(false);
        selectedMode = null;
        selectedBattleType = null;
    }
    
    /**
     * Selecciona el modo normal
     */
    private void selectNormalMode() {
        selectedMode = "NORMAL";
        titleLabel.setText("NORMAL MODE");
        normalModeButton.setVisible(false);
        survivalModeButton.setVisible(false);
        normalModePanel.setVisible(true);
        
        // Ajustar posición del panel
        if (currentSize != null) {
            normalModePanel.setBounds(
                (currentSize.width - 600) / 2,
                200,
                600,
                400
            );
        }
    }
    
    /**
     * Selecciona el modo supervivencia
     */
    private void selectSurvivalMode() {
        selectedMode = "SURVIVAL";
        titleLabel.setText("SURVIVAL MODE");
        normalModeButton.setVisible(false);
        survivalModeButton.setVisible(false);
        survivalModePanel.setVisible(true);
        
        // Ajustar posición del panel
        if (currentSize != null) {
            survivalModePanel.setBounds(
                (currentSize.width - 600) / 2,
                200,
                600,
                300
            );
        }
    }
    
    /**
     * Selecciona el tipo de batalla
     */
    private void selectBattleType(String battleType) {
        selectedBattleType = battleType;
        
        // TODO: Guardar la configuración de batalla
        System.out.println("Selected Mode: " + selectedMode);
        System.out.println("Selected Battle Type: " + battleType);
        
        // Navegar a la selección de personaje
        POOBKemonEmeraldGUI.getInstance().switchToPanel("CHARACTER_SELECTION");
    }
    
    /**
     * Inicia el modo supervivencia
     */
    private void startSurvivalMode() {
        selectedBattleType = "PVP"; // Supervivencia siempre es PVP
        
        // TODO: Guardar la configuración de batalla
        System.out.println("Selected Mode: " + selectedMode);
        System.out.println("Selected Battle Type: " + selectedBattleType);
        
        // Navegar a la selección de personaje
        POOBKemonEmeraldGUI.getInstance().switchToPanel("CHARACTER_SELECTION");
    }
    
    /**
     * Maneja el botón de retorno
     */
    private void handleBack() {
        if (normalModePanel.isVisible() || survivalModePanel.isVisible()) {
            // Volver a la selección de modo
            showModeSelection();
        } else {
            // Volver al menú principal
            POOBKemonEmeraldGUI.getInstance().switchToPanel("GAME_MENU");
        }
    }
    
    @Override
    protected void adjustComponentsToSize() {
        if (currentSize == null) return;
        
        // Fondo
        if (backgroundLabel != null) {
            backgroundLabel.setBounds(0, 0, currentSize.width, currentSize.height);
        }
        
        // Título
        if (titleLabel != null) {
            titleLabel.setBounds(0, 50, currentSize.width, 40);
        }
        
        // Botones de modo
        int buttonWidth = 400;
        int buttonHeight = 120;
        int centerX = currentSize.width / 2;
        int startY = 200;
        
        if (normalModeButton != null) {
            normalModeButton.setBounds(
                centerX - buttonWidth - 50,
                startY,
                buttonWidth,
                buttonHeight
            );
        }
        
        if (survivalModeButton != null) {
            survivalModeButton.setBounds(
                centerX + 50,
                startY,
                buttonWidth,
                buttonHeight
            );
        }
        
        // Paneles de opciones
        if (normalModePanel != null && normalModePanel.isVisible()) {
            normalModePanel.setBounds(
                (currentSize.width - 600) / 2,
                200,
                600,
                400
            );
        }
        
        if (survivalModePanel != null && survivalModePanel.isVisible()) {
            survivalModePanel.setBounds(
                (currentSize.width - 600) / 2,
                200,
                600,
                300
            );
        }
        
        // Botón de retorno
        if (backButton != null) {
            backButton.setBounds(50, currentSize.height - 100, 100, 40);
        }
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> {
            if (currentSize == null) {
                currentSize = POOBKemonEmeraldGUI.getInstance().getWindowSize();
            }
            adjustComponentsToSize();
        });
    }
    
    /**
     * Obtiene el modo seleccionado
     * @return El modo seleccionado (NORMAL o SURVIVAL)
     */
    public String getSelectedMode() {
        return selectedMode;
    }
    
    /**
     * Obtiene el tipo de batalla seleccionado
     * @return El tipo de batalla (PVP, PVM, MVM)
     */
    public String getSelectedBattleType() {
        return selectedBattleType;
    }
}