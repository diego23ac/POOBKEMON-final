package Presentation;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel de opciones del juego
 */
public class Options extends BasePanel {
    
    // Componentes de la interfaz
    private JLabel backgroundLabel;
    private JLabel titleLabel;
    
    // Controles de volumen
    private JSlider musicVolumeSlider;
    private JSlider sfxVolumeSlider;
    private JLabel musicVolumeLabel;
    private JLabel sfxVolumeLabel;
    
    // Controles de pantalla
    private JCheckBox fullscreenCheckbox;
    private JComboBox<String> resolutionCombo;
    
    // Controles de juego
    private JComboBox<String> difficultyCombo;
    private JCheckBox animationsCheckbox;
    private JComboBox<String> textSpeedCombo;
    
    // Botones
    private JButton saveButton;
    private JButton defaultsButton;
    private JButton backButton;
    
    /**
     * Constructor
     */
    public Options() {
        super();
        initializeComponents();
    }
    
    @Override
    protected void initializeComponents() {
        setLayout(null);
        setBackground(new Color(248, 248, 248));
        
        // Crear fondo
        createBackground();
        
        // Crear título
        createTitle();
        
        // Crear secciones de opciones
        createAudioSection();
        createVideoSection();
        createGameplaySection();
        
        // Crear botones
        createButtons();
        
        // Cargar configuración actual
        loadCurrentSettings();
        
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
        backgroundLabel.setBackground(new Color(240, 240, 240));
        add(backgroundLabel);
    }
    
    /**
     * Crea el título
     */
    private void createTitle() {
        titleLabel = createGameLabel("OPTIONS", 0, 30, 800, 40, 28f);
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel);
    }
    
    /**
     * Crea la sección de audio
     */
    private void createAudioSection() {
        // Título de sección
        JLabel audioTitle = createGameLabel("AUDIO", 100, 120, 200, 30, 18f);
        audioTitle.setForeground(Color.BLACK);
        add(audioTitle);
        
        // Volumen de música
        JLabel musicLabel = createGameLabel("Music Volume:", 120, 160, 200, 25, 12f);
        musicLabel.setForeground(Color.BLACK);
        add(musicLabel);
        
        musicVolumeSlider = new JSlider(0, 100, 70);
        musicVolumeSlider.setBackground(new Color(240, 240, 240));
        musicVolumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateMusicVolumeLabel();
            }
        });
        add(musicVolumeSlider);
        
        musicVolumeLabel = createGameLabel("70%", 520, 160, 60, 25, 12f);
        musicVolumeLabel.setForeground(Color.BLACK);
        add(musicVolumeLabel);
        
        // Volumen de efectos
        JLabel sfxLabel = createGameLabel("SFX Volume:", 120, 200, 200, 25, 12f);
        sfxLabel.setForeground(Color.BLACK);
        add(sfxLabel);
        
        sfxVolumeSlider = new JSlider(0, 100, 80);
        sfxVolumeSlider.setBackground(new Color(240, 240, 240));
        sfxVolumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateSfxVolumeLabel();
            }
        });
        add(sfxVolumeSlider);
        
        sfxVolumeLabel = createGameLabel("80%", 520, 200, 60, 25, 12f);
        sfxVolumeLabel.setForeground(Color.BLACK);
        add(sfxVolumeLabel);
    }
    
    /**
     * Crea la sección de video
     */
    private void createVideoSection() {
        // Título de sección
        JLabel videoTitle = createGameLabel("VIDEO", 100, 260, 200, 30, 18f);
        videoTitle.setForeground(Color.BLACK);
        add(videoTitle);
        
        // Pantalla completa
        fullscreenCheckbox = new JCheckBox("Fullscreen");
        setGameFont(fullscreenCheckbox, 12f);
        fullscreenCheckbox.setBackground(new Color(240, 240, 240));
        fullscreenCheckbox.setForeground(Color.BLACK);
        add(fullscreenCheckbox);
        
        // Resolución
        JLabel resolutionLabel = createGameLabel("Resolution:", 120, 340, 150, 25, 12f);
        resolutionLabel.setForeground(Color.BLACK);
        add(resolutionLabel);
        
        String[] resolutions = {"1920x1080", "1600x900", "1366x768", "1280x720", "1024x768"};
        resolutionCombo = new JComboBox<>(resolutions);
        setGameFont(resolutionCombo, 12f);
        add(resolutionCombo);
    }
    
    /**
     * Crea la sección de gameplay
     */
    private void createGameplaySection() {
        // Título de sección
        JLabel gameplayTitle = createGameLabel("GAMEPLAY", 100, 400, 200, 30, 18f);
        gameplayTitle.setForeground(Color.BLACK);
        add(gameplayTitle);
        
        // Dificultad
        JLabel difficultyLabel = createGameLabel("Difficulty:", 120, 440, 150, 25, 12f);
        difficultyLabel.setForeground(Color.BLACK);
        add(difficultyLabel);
        
        String[] difficulties = {"Easy", "Normal", "Hard"};
        difficultyCombo = new JComboBox<>(difficulties);
        difficultyCombo.setSelectedIndex(1); // Normal por defecto
        setGameFont(difficultyCombo, 12f);
        add(difficultyCombo);
        
        // Animaciones de batalla
        animationsCheckbox = new JCheckBox("Battle Animations");
        animationsCheckbox.setSelected(true);
        setGameFont(animationsCheckbox, 12f);
        animationsCheckbox.setBackground(new Color(240, 240, 240));
        animationsCheckbox.setForeground(Color.BLACK);
        add(animationsCheckbox);
        
        // Velocidad de texto
        JLabel textSpeedLabel = createGameLabel("Text Speed:", 120, 520, 150, 25, 12f);
        textSpeedLabel.setForeground(Color.BLACK);
        add(textSpeedLabel);
        
        String[] speeds = {"Slow", "Normal", "Fast"};
        textSpeedCombo = new JComboBox<>(speeds);
        textSpeedCombo.setSelectedIndex(1); // Normal por defecto
        setGameFont(textSpeedCombo, 12f);
        add(textSpeedCombo);
    }
    
    /**
     * Crea los botones de control
     */
    private void createButtons() {
        // Botón Guardar
        saveButton = createGameButton("SAVE", 0, 0, 120, 45, 12f);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();
            }
        });
        
        // Botón Valores por defecto
        defaultsButton = createGameButton("DEFAULTS", 0, 0, 120, 45, 12f);
        defaultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetToDefaults();
            }
        });
        
        // Botón Volver
        backButton = createGameButton("BACK", 0, 0, 120, 45, 12f);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });
        
        add(saveButton);
        add(defaultsButton);
        add(backButton);
    }
    
    /**
     * Actualiza la etiqueta del volumen de música
     */
    private void updateMusicVolumeLabel() {
        musicVolumeLabel.setText(musicVolumeSlider.getValue() + "%");
    }
    
    /**
     * Actualiza la etiqueta del volumen de efectos
     */
    private void updateSfxVolumeLabel() {
        sfxVolumeLabel.setText(sfxVolumeSlider.getValue() + "%");
    }
    
    /**
     * Guarda la configuración
     */
    private void saveSettings() {
        // TODO: Implementar guardado real de configuración
        System.out.println("Saving settings...");
        System.out.println("Music Volume: " + musicVolumeSlider.getValue());
        System.out.println("SFX Volume: " + sfxVolumeSlider.getValue());
        System.out.println("Fullscreen: " + fullscreenCheckbox.isSelected());
        System.out.println("Resolution: " + resolutionCombo.getSelectedItem());
        System.out.println("Difficulty: " + difficultyCombo.getSelectedItem());
        System.out.println("Battle Animations: " + animationsCheckbox.isSelected());
        System.out.println("Text Speed: " + textSpeedCombo.getSelectedItem());
        
        JOptionPane.showMessageDialog(this,
            "Settings saved successfully!",
            "Settings Saved",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Restaura los valores por defecto
     */
    private void resetToDefaults() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Reset all settings to default values?",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            musicVolumeSlider.setValue(70);
            sfxVolumeSlider.setValue(80);
            fullscreenCheckbox.setSelected(false);
            resolutionCombo.setSelectedIndex(0);
            difficultyCombo.setSelectedIndex(1);
            animationsCheckbox.setSelected(true);
            textSpeedCombo.setSelectedIndex(1);
        }
    }
    
    /**
     * Vuelve al menú principal
     */
    private void goBack() {
        POOBKemonEmeraldGUI.getInstance().switchToPanel("GAME_MENU");
    }
    
    /**
     * Carga la configuración actual
     */
    private void loadCurrentSettings() {
        // TODO: Cargar configuración real desde archivo o preferencias
        // Por ahora usa valores por defecto
    }
    
    @Override
    protected void adjustComponentsToSize() {
        if (currentSize == null) return;
        
        int centerX = currentSize.width / 2;
        int startX = centerX - 300;
        
        // Fondo
        if (backgroundLabel != null) {
            backgroundLabel.setBounds(0, 0, currentSize.width, currentSize.height);
        }
        
        // Título
        if (titleLabel != null) {
            titleLabel.setBounds(0, 30, currentSize.width, 40);
        }
        
        // Audio
        if (musicVolumeSlider != null) {
            musicVolumeSlider.setBounds(startX + 200, 160, 200, 30);
        }
        if (sfxVolumeSlider != null) {
            sfxVolumeSlider.setBounds(startX + 200, 200, 200, 30);
        }
        
        // Video
        if (fullscreenCheckbox != null) {
            fullscreenCheckbox.setBounds(startX + 20, 300, 200, 30);
        }
        if (resolutionCombo != null) {
            resolutionCombo.setBounds(startX + 200, 340, 200, 30);
        }
        
        // Gameplay
        if (difficultyCombo != null) {
            difficultyCombo.setBounds(startX + 200, 440, 200, 30);
        }
        if (animationsCheckbox != null) {
            animationsCheckbox.setBounds(startX + 20, 480, 250, 30);
        }
        if (textSpeedCombo != null) {
            textSpeedCombo.setBounds(startX + 200, 520, 200, 30);
        }
        
        // Botones
        int buttonY = currentSize.height - 100;
        if (saveButton != null) {
            saveButton.setBounds(centerX - 200, buttonY, 120, 45);
        }
        if (defaultsButton != null) {
            defaultsButton.setBounds(centerX - 60, buttonY, 120, 45);
        }
        if (backButton != null) {
            backButton.setBounds(centerX + 80, buttonY, 120, 45);
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
}