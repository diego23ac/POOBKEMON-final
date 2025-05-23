package Presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel de selección de personaje para iniciar el juego
 */
public class CharacterSelection extends BasePanel {
    
    // Rutas de recursos (ajusta según tu estructura)
    private static final String BACKGROUND_PATH = "/Resources/MainFonds/characterSelectBg.png";
    private static final String MALE_CHARACTER_PATH = "/Resources/Characters/maleTrainer.png";
    private static final String FEMALE_CHARACTER_PATH = "/Resources/Characters/femaleTrainer.png";
    private static final String SELECTION_ARROW_PATH = "/Resources/UI/selectionArrow.png";
    
    private JLabel backgroundLabel;
    private JLabel titleLabel;
    private JLabel maleCharacterLabel;
    private JLabel femaleCharacterLabel;
    private JLabel selectionArrowLabel;
    private JTextField nameField;
    private JButton confirmButton;
    private JButton backButton;
    
    private boolean isMaleSelected = true;
    
    /**
     * Constructor
     */
    public CharacterSelection() {
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
        
        // Crear selección de personajes
        createCharacterSelection();
        
        // Crear campo de nombre
        createNameField();
        
        // Crear botones
        createButtons();
        
        // Ajustar componentes si hay un tamaño inicial
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
        backgroundLabel.setBackground(new Color(248, 248, 248));
        
        try {
            Image bgImage = loadImage(BACKGROUND_PATH);
            if (bgImage != null) {
                backgroundLabel.setIcon(new ImageIcon(bgImage));
            }
        } catch (Exception e) {
            System.err.println("Error loading character selection background: " + e.getMessage());
        }
        
        add(backgroundLabel);
    }
    
    /**
     * Crea el título de la pantalla
     */
    private void createTitle() {
        titleLabel = createGameLabel("CHOOSE YOUR CHARACTER", 0, 50, 800, 40, 24f);
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel);
    }
    
    /**
     * Crea la selección de personajes
     */
    private void createCharacterSelection() {
        // Personaje masculino
        maleCharacterLabel = new JLabel();
        try {
            Image maleImage = loadImage(MALE_CHARACTER_PATH);
            if (maleImage != null) {
                Image scaled = maleImage.getScaledInstance(120, 160, Image.SCALE_SMOOTH);
                maleCharacterLabel.setIcon(new ImageIcon(scaled));
            } else {
                maleCharacterLabel.setText("MALE");
                setGameFont(maleCharacterLabel, 14f);
            }
        } catch (Exception e) {
            maleCharacterLabel.setText("MALE");
            setGameFont(maleCharacterLabel, 14f);
        }
        
        maleCharacterLabel.setHorizontalAlignment(JLabel.CENTER);
        maleCharacterLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        maleCharacterLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectMale();
            }
        });
        
        // Personaje femenino
        femaleCharacterLabel = new JLabel();
        try {
            Image femaleImage = loadImage(FEMALE_CHARACTER_PATH);
            if (femaleImage != null) {
                Image scaled = femaleImage.getScaledInstance(120, 160, Image.SCALE_SMOOTH);
                femaleCharacterLabel.setIcon(new ImageIcon(scaled));
            } else {
                femaleCharacterLabel.setText("FEMALE");
                setGameFont(femaleCharacterLabel, 14f);
            }
        } catch (Exception e) {
            femaleCharacterLabel.setText("FEMALE");
            setGameFont(femaleCharacterLabel, 14f);
        }
        
        femaleCharacterLabel.setHorizontalAlignment(JLabel.CENTER);
        femaleCharacterLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        femaleCharacterLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectFemale();
            }
        });
        
        // Flecha de selección
        selectionArrowLabel = new JLabel("▼");
        setGameFont(selectionArrowLabel, 24f);
        selectionArrowLabel.setForeground(Color.RED);
        selectionArrowLabel.setHorizontalAlignment(JLabel.CENTER);
        
        add(maleCharacterLabel);
        add(femaleCharacterLabel);
        add(selectionArrowLabel);
    }
    
    /**
     * Crea el campo de entrada de nombre
     */
    private void createNameField() {
        // Label
        JLabel nameLabel = createGameLabel("ENTER YOUR NAME:", 0, 350, 800, 30, 16f);
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        add(nameLabel);
        
        // Campo de texto
        nameField = new JTextField();
        setGameFont(nameField, 18f);
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setDocument(new JTextFieldLimit(10)); // Límite de 10 caracteres
        add(nameField);
    }
    
    /**
     * Crea los botones de confirmación y retorno
     */
    private void createButtons() {
        // Botón Confirmar
        confirmButton = createGameButton("CONFIRM", 0, 0, 150, 50, 14f);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmSelection();
            }
        });
        
        // Botón Volver
        backButton = createGameButton("BACK", 0, 0, 150, 50, 14f);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });
        
        add(confirmButton);
        add(backButton);
    }
    
    /**
     * Selecciona el personaje masculino
     */
    private void selectMale() {
        isMaleSelected = true;
        updateSelectionArrow();
    }
    
    /**
     * Selecciona el personaje femenino
     */
    private void selectFemale() {
        isMaleSelected = false;
        updateSelectionArrow();
    }
    
    /**
     * Actualiza la posición de la flecha de selección
     */
    private void updateSelectionArrow() {
        if (currentSize != null) {
            int arrowY = currentSize.height / 3 - 50;
            if (isMaleSelected) {
                selectionArrowLabel.setBounds(currentSize.width / 2 - 150, arrowY, 100, 30);
            } else {
                selectionArrowLabel.setBounds(currentSize.width / 2 + 50, arrowY, 100, 30);
            }
        }
    }
    
    /**
     * Confirma la selección y procede al juego
     */
    private void confirmSelection() {
        String playerName = nameField.getText().trim();
        
        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter your name!", 
                "Name Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // TODO: Guardar la selección del jugador y nombre
        System.out.println("Player selected: " + (isMaleSelected ? "Male" : "Female"));
        System.out.println("Player name: " + playerName);
        
        // TODO: Navegar al juego principal
        // POOBKemonEmeraldGUI.getInstance().switchToPanel("GAME_WORLD");
        
        JOptionPane.showMessageDialog(this,
            "Welcome " + playerName + "!\nGame will start soon...",
            "Welcome",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Vuelve al menú principal
     */
    private void goBack() {
        POOBKemonEmeraldGUI.getInstance().switchToPanel("GAME_MENU");
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
        
        // Personajes
        int characterY = currentSize.height / 3;
        if (maleCharacterLabel != null) {
            maleCharacterLabel.setBounds(currentSize.width / 2 - 200, characterY, 120, 160);
        }
        if (femaleCharacterLabel != null) {
            femaleCharacterLabel.setBounds(currentSize.width / 2 + 80, characterY, 120, 160);
        }
        
        // Actualizar flecha
        updateSelectionArrow();
        
        // Campo de nombre
        if (nameField != null) {
            nameField.setBounds(currentSize.width / 2 - 150, 400, 300, 40);
        }
        
        // Botones
        if (confirmButton != null) {
            confirmButton.setBounds(currentSize.width / 2 - 160, 500, 150, 50);
        }
        if (backButton != null) {
            backButton.setBounds(currentSize.width / 2 + 10, 500, 150, 50);
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
            nameField.requestFocusInWindow();
        });
    }
    
    /**
     * Clase interna para limitar caracteres en el campo de texto
     */
    private class JTextFieldLimit extends javax.swing.text.PlainDocument {
        private int limit;
        
        JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }
        
        @Override
        public void insertString(int offset, String str, javax.swing.text.AttributeSet attr) 
                throws javax.swing.text.BadLocationException {
            if (str == null) return;
            
            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str.toUpperCase(), attr);
            }
        }
    }
}