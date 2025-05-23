package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel del menú principal del juego
 */
public class GameMenu extends BasePanel {
    
    // Rutas de los recursos (ajusta estas rutas según tu estructura)
        private static final String BACKGROUND_GIF_PATH = "/Resources/MainFonds/fondoInicio.gif";
    private static final String TITLE_PNG_PATH = "/Resources/TextResources/logoPoobkemon.png";
    private static final String START_BUTTON_PATH = "/Resources/Buttoms/botonInicio.png";
    private static final String START_BUTTON_HOVER_PATH = "/Resources/Buttoms/botonInicio.png";
    private static final String OPTIONS_BUTTON_PATH = "/Resources/Buttoms/botonInicio.png";
    private static final String OPTIONS_BUTTON_HOVER_PATH = "/Resources/Buttoms/botonInicio.png";
    private static final String RETURN_BUTTON_PATH = "/Resources/Buttoms/botonInicio.png";
    private static final String RETURN_BUTTON_HOVER_PATH = "/Resources/Buttoms/botonInicio.png";
    
    private JLabel backgroundLabel;
    private JLabel titleLabel;
    private JButton startButton;
    private JButton optionsButton;
    private JButton returnButton;
    
    /**
     * Constructor de GameMenu
     */
    public GameMenu() {
        super();
        initializeComponents();
    }
    
    @Override
    protected void initializeComponents() {
        // Configurar el panel
        setLayout(null);
        setBackground(Color.BLACK);
        
        // IMPORTANTE: Crear los componentes en el orden correcto
        // Primero el fondo
        createBackground();
        
        // Luego el título
        createTitle();
        
        // Finalmente los botones
        createButtons();
        
        // Asegurar que el fondo esté en la capa más baja
        if (backgroundLabel != null) {
            setComponentZOrder(backgroundLabel, getComponentCount() - 1);
        }
        
        // Ajustar componentes al tamaño inicial
        if (currentSize != null) {
            adjustComponentsToSize();
        }
    }
    
    /**
     * Crea el fondo animado del menú
     */
    private void createBackground() {
        backgroundLabel = new JLabel();
        
        try {
            ImageIcon backgroundGif = new ImageIcon(getClass().getResource(BACKGROUND_GIF_PATH));
            backgroundLabel.setIcon(backgroundGif);
        } catch (Exception e) {
            System.err.println("Error loading background GIF: " + e.getMessage());
            // Fondo sólido como fallback
            backgroundLabel.setOpaque(true);
            backgroundLabel.setBackground(new Color(30, 30, 40));
        }
        
        add(backgroundLabel);
    }
    
    /**
     * Crea el título del juego
     */
    private void createTitle() {
        titleLabel = new JLabel();
        
        try {
            Image titleImage = loadImage(TITLE_PNG_PATH);
            if (titleImage != null) {
                // Escalar la imagen manteniendo la proporción
                int originalWidth = titleImage.getWidth(null);
                int originalHeight = titleImage.getHeight(null);
                int newWidth = 700; // Ancho deseado más grande
                int newHeight = (int) ((double) originalHeight / originalWidth * newWidth);
                
                Image scaledImage = titleImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                titleLabel.setIcon(new ImageIcon(scaledImage));
                System.out.println("Title image loaded successfully");
            } else {
                System.out.println("Title image is null");
            }
        } catch (Exception e) {
            System.err.println("Error loading title image: " + e.getMessage());
            e.printStackTrace();
            // Texto como fallback
            titleLabel.setText("POOBKEMON EMERALD");
            setGameFont(titleLabel, 32f);
            titleLabel.setForeground(Color.WHITE);
        }
        
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel);
    }
    
    /**
     * Crea los botones del menú
     */
    private void createButtons() {
        // Crear botón Start con texto personalizado
        startButton = createImageButton(START_BUTTON_PATH, START_BUTTON_HOVER_PATH, "START");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleStartButton();
            }
        });
        
        // Crear botón Options con texto personalizado
        optionsButton = createImageButton(OPTIONS_BUTTON_PATH, OPTIONS_BUTTON_HOVER_PATH, "OPTIONS");
        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleOptionsButton();
            }
        });
        
        // Crear botón Return con texto personalizado
        returnButton = createImageButton(RETURN_BUTTON_PATH, RETURN_BUTTON_HOVER_PATH, "RETURN");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleReturnButton();
            }
        });
        
        add(startButton);
        add(optionsButton);
        add(returnButton);
    }
    
    /**
     * Crea un botón con imágenes para estados normal y hover
     * @param normalPath Ruta de la imagen normal
     * @param hoverPath Ruta de la imagen hover
     * @param text Texto a mostrar en el botón
     * @return El botón creado
     */
    private JButton createImageButton(String normalPath, String hoverPath, String text) {
        JButton button = new JButton();
        
        // Configurar el texto del botón
        button.setText(text);
        setGameFont(button, 14f);
        button.setForeground(Color.WHITE);
        
        // Configurar apariencia del botón
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.CENTER);
        
        try {
            // Cargar imágenes
            Image normalImage = loadImage(normalPath);
            Image hoverImage = loadImage(hoverPath);
            
            if (normalImage != null) {
                // Tamaño estándar para los botones
                int buttonWidth = 250;
                int buttonHeight = 60;
                
                // Escalar imágenes
                Image scaledNormal = normalImage.getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
                ImageIcon normalIcon = new ImageIcon(scaledNormal);
                
                button.setIcon(normalIcon);
                button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
                
                // Configurar el botón
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setOpaque(false);
                
                // Añadir efecto hover si existe la imagen hover
                if (hoverImage != null && !normalPath.equals(hoverPath)) {
                    Image scaledHover = hoverImage.getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
                    ImageIcon hoverIcon = new ImageIcon(scaledHover);
                    
                    button.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            button.setIcon(hoverIcon);
                            button.setForeground(Color.YELLOW); // Cambiar color del texto en hover
                        }
                        
                        @Override
                        public void mouseExited(MouseEvent e) {
                            button.setIcon(normalIcon);
                            button.setForeground(Color.WHITE);
                        }
                    });
                }
            } else {
                // Fallback a botón de texto sin imagen
                button.setBackground(new Color(50, 50, 70));
                button.setOpaque(true);
                button.setBorderPainted(true);
                button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                button.setPreferredSize(new Dimension(250, 60));
            }
        } catch (Exception e) {
            System.err.println("Error creating image button: " + e.getMessage());
            // Configurar como botón simple en caso de error
            button.setBackground(new Color(50, 50, 70));
            button.setOpaque(true);
            button.setBorderPainted(true);
        }
        
        return button;
    }
    
    /**
     * Maneja el evento del botón Start
     */
    private void handleStartButton() {
        System.out.println("Start button clicked");
        // Navegar a la selección de modo de juego
        POOBKemonEmeraldGUI.getInstance().switchToPanel("GAME_MODE");
    }
    
    /**
     * Maneja el evento del botón Options
     */
    private void handleOptionsButton() {
        System.out.println("Options button clicked");
        // Navegar al panel de opciones
        POOBKemonEmeraldGUI.getInstance().switchToPanel("OPTIONS");
    }
    
    /**
     * Maneja el evento del botón Return
     */
    private void handleReturnButton() {
        System.out.println("Return button clicked");
        // Volver a la presentación
        POOBKemonEmeraldGUI.getInstance().switchToPanel("GAME_PRESENTATION");
    }
    
    @Override
    protected void adjustComponentsToSize() {
        if (currentSize == null) return;
        
        // Ajustar fondo
        if (backgroundLabel != null) {
            backgroundLabel.setBounds(0, 0, currentSize.width, currentSize.height);
            
            // Reescalar el GIF del fondo si es necesario
            if (backgroundLabel.getIcon() instanceof ImageIcon) {
                ImageIcon icon = (ImageIcon) backgroundLabel.getIcon();
                Image img = icon.getImage();
                Image scaledImg = img.getScaledInstance(currentSize.width, currentSize.height, Image.SCALE_DEFAULT);
                backgroundLabel.setIcon(new ImageIcon(scaledImg));
            }
        }
        
        // Ajustar título (centrado en la parte superior)
        if (titleLabel != null) {
            int titleWidth = 700;
            int titleHeight = 200;
            int titleX = (currentSize.width - titleWidth) / 2;
            int titleY = currentSize.height / 8; // Más arriba
            titleLabel.setBounds(titleX, titleY, titleWidth, titleHeight);
        }
        
        // Ajustar botones debajo del título
        int buttonWidth = 250;
        int buttonHeight = 60;
        int buttonX = (currentSize.width - buttonWidth) / 2;
        int buttonSpacing = 80; // Espacio entre botones
        
        // Posicionar botones debajo del logo
        int startY = currentSize.height / 8 + 250; // Debajo del título
        
        if (startButton != null) {
            startButton.setBounds(buttonX, startY, buttonWidth, buttonHeight);
        }
        
        if (optionsButton != null) {
            optionsButton.setBounds(buttonX, startY + buttonSpacing, buttonWidth, buttonHeight);
        }
        
        if (returnButton != null) {
            returnButton.setBounds(buttonX, startY + (buttonSpacing * 2), buttonWidth, buttonHeight);
        }
        
        // Asegurar que el fondo esté en la capa más baja
        if (backgroundLabel != null) {
            setComponentZOrder(backgroundLabel, getComponentCount() - 1);
        }
    }
}