package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase principal de la GUI del juego POOBKemon Emerald.
 * Maneja el JFrame principal y la navegación entre diferentes JPanels.
 */
public class POOBKemonEmeraldGUI extends JFrame {
    
    private static POOBKemonEmeraldGUI instance;
    private BasePanel currentPanel;
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private Map<String, BasePanel> panels; // Registro de todos los paneles
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem saveItem;
    private JMenuItem openItem;
    private JMenuItem exitItem;
    
    /**
     * Constructor privado para implementar el patrón Singleton
     */
    private POOBKemonEmeraldGUI() {
        panels = new HashMap<>();
        initializeFrame();
    }
    
    /**
     * Obtiene la instancia única de POOBKemonEmeraldGUI
     * @return La instancia de POOBKemonEmeraldGUI
     */
    public static POOBKemonEmeraldGUI getInstance() {
        if (instance == null) {
            instance = new POOBKemonEmeraldGUI();
        }
        return instance;
    }
    
    /**
     * Inicializa el JFrame con las configuraciones necesarias
     */
    private void initializeFrame() {
        setTitle("POOBKemon Emerald");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Configurar pantalla completa
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false); // Puedes cambiar a true si quieres quitar la barra de título
        
        // Obtener dimensiones de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setLocationRelativeTo(null);
        
        // Crear la barra de menú
        createMenuBar();
        
        // Configurar el layout principal
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(Color.BLACK);
        
        add(mainContainer);
        
        // Listener para manejar cambios de tamaño
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (currentPanel != null) {
                    currentPanel.onResize(getSize());
                }
            }
        });
    }
    
    /**
     * Crea la barra de menú con las opciones de archivo
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // Configurar la fuente del menú
        Font menuFont = BasePanel.getGameFont(10f);
        
        // Crear el menú File
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F'); // Alt + F para abrir el menú
        fileMenu.setFont(menuFont);
        
        // Crear item Save
        saveItem = new JMenuItem("Save Game");
        saveItem.setMnemonic('S');
        saveItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S")); // Ctrl + S
        saveItem.setFont(menuFont);
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGame();
            }
        });
        
        // Crear item Open
        openItem = new JMenuItem("Open Game");
        openItem.setMnemonic('O');
        openItem.setAccelerator(KeyStroke.getKeyStroke("ctrl O")); // Ctrl + O
        openItem.setFont(menuFont);
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openGame();
            }
        });
        
        // Crear item Exit
        exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('X');
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q")); // Ctrl + Q
        exitItem.setFont(menuFont);
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitGame();
            }
        });
        
        // Añadir items al menú
        fileMenu.add(saveItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator(); // Línea separadora
        fileMenu.add(exitItem);
        
        // Añadir menú a la barra
        menuBar.add(fileMenu);
        
        // Establecer la barra de menú en el JFrame
        setJMenuBar(menuBar);
    }
    
    /**
     * Maneja la acción de guardar el juego
     */
    private void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Game");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".pkmn");
            }
            
            @Override
            public String getDescription() {
                return "POOBKemon Save Files (*.pkmn)";
            }
        });
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Asegurar que tenga la extensión correcta
            if (!selectedFile.getName().toLowerCase().endsWith(".pkmn")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".pkmn");
            }
            
            // TODO: Implementar la lógica real de guardado aquí
            JOptionPane.showMessageDialog(this, 
                "Game saved successfully to:\n" + selectedFile.getAbsolutePath(),
                "Save Successful", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Maneja la acción de abrir un juego guardado
     */
    private void openGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Game");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".pkmn");
            }
            
            @Override
            public String getDescription() {
                return "POOBKemon Save Files (*.pkmn)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // TODO: Implementar la lógica real de carga aquí
            JOptionPane.showMessageDialog(this, 
                "Game loaded successfully from:\n" + selectedFile.getAbsolutePath(),
                "Load Successful", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Por ejemplo, podrías cambiar al panel del juego
            // switchToPanel("GAME_PANEL");
        }
    }
    
    /**
     * Maneja la acción de salir del juego
     */
    private void exitGame() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit?\nAny unsaved progress will be lost.",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * Cambia al panel especificado
     * @param panelName El nombre del panel al que se quiere cambiar
     */
    public void switchToPanel(String panelName) {
        BasePanel panel = panels.get(panelName);
        if (panel != null) {
            currentPanel = panel;
            cardLayout.show(mainContainer, panelName);
        } else {
            System.err.println("Panel not found: " + panelName);
        }
    }
    
    /**
     * Añade un nuevo panel al contenedor principal
     * @param panel El panel a añadir (debe extender BasePanel)
     * @param name El nombre identificador del panel
     */
    public void addPanel(BasePanel panel, String name) {
        panels.put(name, panel);
        mainContainer.add(panel, name);
        panel.onResize(getSize());
    }
    
    /**
     * Remueve un panel del contenedor principal
     * @param name El nombre del panel a remover
     */
    public void removePanel(String name) {
        BasePanel panel = panels.remove(name);
        if (panel != null) {
            mainContainer.remove(panel);
        }
    }
    
    /**
     * Obtiene las dimensiones actuales de la ventana
     * @return Las dimensiones de la ventana
     */
    public Dimension getWindowSize() {
        return getSize();
    }
    
    /**
     * Método principal para iniciar la aplicación
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Starting POOBKemon Emerald GUI...");
            
            POOBKemonEmeraldGUI gui = POOBKemonEmeraldGUI.getInstance();
            
            // Añadir los paneles iniciales
            System.out.println("Adding GamePresentation panel...");
            gui.addPanel(new GamePresentation(), "GAME_PRESENTATION");
            
            System.out.println("Adding GameMenu panel...");
            gui.addPanel(new GameMenu(), "GAME_MENU");
            
            System.out.println("Adding CharacterSelection panel...");
            gui.addPanel(new CharacterSelection(), "CHARACTER_SELECTION");
            
            System.out.println("Adding Options panel...");
            gui.addPanel(new Options(), "OPTIONS");
            
            // Iniciar con la presentación
            System.out.println("Switching to GAME_PRESENTATION...");
            gui.switchToPanel("GAME_PRESENTATION");
            
            gui.setVisible(true);
            System.out.println("GUI is now visible");
        });
    }
}