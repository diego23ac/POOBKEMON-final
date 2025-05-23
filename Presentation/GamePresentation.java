package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel de presentación inicial del juego que muestra los GIFs de introducción
 */
public class GamePresentation extends BasePanel {
    
    private static final int FIRST_GIF_DURATION = 4400; // 4400 milisegundos
    private static final String FIRST_GIF_PATH = "/Resources/MainFonds/intro.gif";
    private static final String SECOND_GIF_PATH = "/Resources/MainFonds/introR.gif";
    
    private JLabel gifLabel;
    private Timer transitionTimer;
    private boolean canSkip = false;
    private boolean isShowingSecondGif = false;
    
    /**
     * Constructor de GamePresentation
     */
    public GamePresentation() {
        super();
        initializeComponents();
        setupEventListeners();
    }
    
    @Override
    protected void initializeComponents() {
        // Configurar el panel
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        
        // Crear el label para los GIFs centrado
        gifLabel = new JLabel();
        gifLabel.setHorizontalAlignment(JLabel.CENTER);
        gifLabel.setVerticalAlignment(JLabel.CENTER);
        
        // Mostrar el primer GIF
        showFirstGif();
        
        add(gifLabel, BorderLayout.CENTER);
        
        // Iniciar el timer para la transición automática
        startTransitionTimer();
    }
    
    /**
     * Muestra el primer GIF
     */
    private void showFirstGif() {
        try {
            // Obtener el tamaño de la ventana
            Dimension windowSize = POOBKemonEmeraldGUI.getInstance().getWindowSize();
            
            // Debug: verificar si el recurso existe
            java.net.URL resourceURL = getClass().getResource(FIRST_GIF_PATH);
            if (resourceURL == null) {
                System.err.println("Resource not found: " + FIRST_GIF_PATH);
                System.err.println("Current class loader: " + getClass().getClassLoader());
                // Intentar con diferentes rutas
                System.err.println("Trying alternative paths...");
                resourceURL = getClass().getResource("/Resources/MainFonds/intro.gif");
                if (resourceURL == null) {
                    resourceURL = getClass().getResource("Resources/MainFonds/intro.gif");
                }
                if (resourceURL == null) {
                    resourceURL = getClass().getResource("../Resources/MainFonds/intro.gif");
                }
            }
            
            ImageIcon gifIcon;
            if (resourceURL != null) {
                System.out.println("Loading GIF from: " + resourceURL);
                gifIcon = new ImageIcon(resourceURL);
            } else {
                throw new Exception("Could not find GIF resource");
            }
            
            // Escalar el GIF si es necesario
            Image scaledImage = gifIcon.getImage().getScaledInstance(
                windowSize.width, 
                windowSize.height, 
                Image.SCALE_DEFAULT
            );
            gifIcon = new ImageIcon(scaledImage);
            
            gifLabel.setIcon(gifIcon);
            
        } catch (Exception e) {
            System.err.println("Error loading first GIF: " + e.getMessage());
            e.printStackTrace();
            // In case of error, show a message
            gifLabel.setText("Error loading presentation");
            gifLabel.setForeground(Color.WHITE);
            setGameFont(gifLabel, 16f);
        }
    }
    
    /**
     * Muestra el segundo GIF
     */
    private void showSecondGif() {
        try {
            isShowingSecondGif = true;
            canSkip = true; // Permitir skip cuando se muestra el segundo GIF
            
            // Obtener el tamaño de la ventana
            Dimension windowSize = POOBKemonEmeraldGUI.getInstance().getWindowSize();
            
            // Cargar el segundo GIF
            ImageIcon gifIcon = new ImageIcon(getClass().getResource(SECOND_GIF_PATH));
            
            // Escalar el GIF si es necesario
            Image scaledImage = gifIcon.getImage().getScaledInstance(
                windowSize.width, 
                windowSize.height, 
                Image.SCALE_DEFAULT
            );
            gifIcon = new ImageIcon(scaledImage);
            
            gifLabel.setIcon(gifIcon);
            
        } catch (Exception e) {
            System.err.println("Error loading second GIF: " + e.getMessage());
            // If there's an error, go directly to menu
            goToGameMenu();
        }
    }
    
    /**
     * Inicia el timer para la transición automática entre GIFs
     */
    private void startTransitionTimer() {
        transitionTimer = new Timer(FIRST_GIF_DURATION, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transitionTimer.stop();
                showSecondGif();
            }
        });
        transitionTimer.setRepeats(false);
        transitionTimer.start();
    }
    
    /**
     * Configura los listeners de eventos para mouse y teclado
     */
    private void setupEventListeners() {
        // Listener para clics del mouse
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleSkipAction();
            }
        };
        
        // Listener para teclas
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleSkipAction();
            }
        };
        
        // Añadir los listeners
        addMouseListener(mouseAdapter);
        addKeyListener(keyAdapter);
        
        // Asegurar que el panel pueda recibir eventos de teclado
        setFocusable(true);
        requestFocusInWindow();
        
        // También añadir listeners al gifLabel por si acaso
        gifLabel.addMouseListener(mouseAdapter);
    }
    
    /**
     * Maneja la acción de skip (saltar la presentación)
     */
    private void handleSkipAction() {
        if (canSkip && isShowingSecondGif) {
            goToGameMenu();
        }
    }
    
    /**
     * Navega al menú del juego
     */
    private void goToGameMenu() {
        // Detener el timer si está activo
        if (transitionTimer != null && transitionTimer.isRunning()) {
            transitionTimer.stop();
        }
        
        // Cambiar al panel del menú
        POOBKemonEmeraldGUI.getInstance().switchToPanel("GAME_MENU");
    }
    
    /**
     * Método llamado cuando el panel se hace visible
     */
    @Override
    public void addNotify() {
        super.addNotify();
        // Asegurar que el panel tenga el foco cuando se muestra
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
        });
    }
    
    @Override
    protected void adjustComponentsToSize() {
        // Si necesitas ajustar algo cuando cambie el tamaño de la ventana
        if (gifLabel != null && gifLabel.getIcon() != null) {
            // Reescalar los GIFs si es necesario
            if (isShowingSecondGif) {
                showSecondGif();
            } else {
                showFirstGif();
            }
        }
    }
}