package Presentation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;

/**
 * Clase abstracta base para todos los JPanels del juego.
 * Proporciona métodos útiles para el manejo de imágenes y GIFs.
 */
public abstract class BasePanel extends JPanel {
    
    protected Dimension currentSize;
    private static Font gameFont;
    private static final String FONT_PATH = "/Resources/TextResources/PressStart2P.ttf";
    private static final float DEFAULT_FONT_SIZE = 12f;
    
    // Inicialización estática de la fuente
    static {
        loadGameFont();
    }
    
    /**
     * Constructor de la clase BasePanel
     */
    public BasePanel() {
        setLayout(null); // Por defecto null layout para posicionamiento absoluto
        setBackground(Color.BLACK);
    }
    
    /**
     * Carga la fuente personalizada del juego
     */
    private static void loadGameFont() {
        try {
            InputStream fontStream = BasePanel.class.getResourceAsStream(FONT_PATH);
            if (fontStream != null) {
                gameFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                // Registrar la fuente en el entorno gráfico
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(gameFont);
                fontStream.close();
                System.out.println("Game font loaded successfully: PressStart2P");
            } else {
                System.err.println("Font file not found: " + FONT_PATH);
                // Fallback a una fuente monoespaciada
                gameFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
            }
        } catch (Exception e) {
            System.err.println("Error loading game font: " + e.getMessage());
            e.printStackTrace();
            // Fallback a una fuente monoespaciada
            gameFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        }
    }
    
    /**
     * Obtiene la fuente del juego con el tamaño especificado
     * @param size El tamaño de la fuente
     * @return La fuente del juego con el tamaño especificado
     */
    public static Font getGameFont(float size) {
        return gameFont.deriveFont(size);
    }
    
    /**
     * Obtiene la fuente del juego con el tamaño por defecto
     * @return La fuente del juego
     */
    public static Font getGameFont() {
        return getGameFont(DEFAULT_FONT_SIZE);
    }
    
    /**
     * Configura la fuente del juego para un componente
     * @param component El componente al que se le aplicará la fuente
     * @param size El tamaño de la fuente
     */
    protected void setGameFont(JComponent component, float size) {
        component.setFont(getGameFont(size));
    }
    
    /**
     * Configura la fuente del juego con tamaño por defecto para un componente
     * @param component El componente al que se le aplicará la fuente
     */
    protected void setGameFont(JComponent component) {
        setGameFont(component, DEFAULT_FONT_SIZE);
    }

    /**
     * Método abstracto que debe ser implementado por las subclases
     * Se llama cuando el panel necesita ser inicializado
     */
    protected abstract void initializeComponents();
    
    /**
     * Método llamado cuando la ventana cambia de tamaño
     * @param newSize El nuevo tamaño de la ventana
     */
    public void onResize(Dimension newSize) {
        this.currentSize = newSize;
        adjustComponentsToSize();
    }
    
    /**
     * Método que las subclases pueden sobrescribir para ajustar componentes al cambiar el tamaño
     */
    protected void adjustComponentsToSize() {
        // Las subclases pueden implementar este método
    }
    
    /**
     * Carga una imagen desde la ruta especificada
     * @param path La ruta de la imagen
     * @return La imagen cargada o null si hay error
     */
    protected Image loadImage(String path) {
        try {
            URL resource = getClass().getResource(path);
            if (resource != null) {
                return ImageIO.read(resource);
            } else {
                File file = new File(path);
                if (file.exists()) {
                    return ImageIO.read(file);
                }
            }
        } catch (IOException e) {
            System.err.println("Error cargando imagen: " + path);
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Escala una imagen a las dimensiones especificadas
     * @param image La imagen a escalar
     * @param width El ancho deseado
     * @param height La altura deseada
     * @return La imagen escalada
     */
    protected Image scaleImage(Image image, int width, int height) {
        if (image == null) return null;
        return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
    
    /**
     * Carga y escala una imagen en un solo paso
     * @param path La ruta de la imagen
     * @param width El ancho deseado
     * @param height La altura deseada
     * @return La imagen cargada y escalada
     */
    protected Image loadAndScaleImage(String path, int width, int height) {
        Image image = loadImage(path);
        return scaleImage(image, width, height);
    }
    
    /**
     * Crea un JLabel con una imagen
     * @param imagePath La ruta de la imagen
     * @param x Posición X
     * @param y Posición Y
     * @param width Ancho del label
     * @param height Altura del label
     * @return El JLabel creado
     */
    protected JLabel createImageLabel(String imagePath, int x, int y, int width, int height) {
        JLabel label = new JLabel();
        label.setBounds(x, y, width, height);
        
        Image image = loadAndScaleImage(imagePath, width, height);
        if (image != null) {
            label.setIcon(new ImageIcon(image));
        }
        
        return label;
    }
    
    /**
     * Crea un JLabel con un GIF animado
     * @param gifPath La ruta del GIF
     * @param x Posición X
     * @param y Posición Y
     * @param width Ancho del label
     * @param height Altura del label
     * @return El JLabel creado con el GIF
     */
    protected JLabel createGifLabel(String gifPath, int x, int y, int width, int height) {
        JLabel label = new JLabel();
        label.setBounds(x, y, width, height);
        
        try {
            URL resource = getClass().getResource(gifPath);
            ImageIcon gifIcon;
            
            if (resource != null) {
                gifIcon = new ImageIcon(resource);
            } else {
                gifIcon = new ImageIcon(gifPath);
            }
            
            // Escalar el GIF si es necesario
            if (gifIcon.getIconWidth() != width || gifIcon.getIconHeight() != height) {
                Image scaledImage = gifIcon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
                gifIcon = new ImageIcon(scaledImage);
            }
            
            label.setIcon(gifIcon);
        } catch (Exception e) {
            System.err.println("Error cargando GIF: " + gifPath);
            e.printStackTrace();
        }
        
        return label;
    }
    
    /**
     * Carga todos los frames de un GIF como imágenes separadas
     * @param gifPath La ruta del GIF
     * @return Lista de imágenes representando cada frame
     */
    protected List<BufferedImage> loadGifFrames(String gifPath) {
        List<BufferedImage> frames = new ArrayList<>();
        
        try {
            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            URL resource = getClass().getResource(gifPath);
            ImageInputStream stream;
            
            if (resource != null) {
                stream = ImageIO.createImageInputStream(resource.openStream());
            } else {
                stream = ImageIO.createImageInputStream(new File(gifPath));
            }
            
            reader.setInput(stream);
            
            int numFrames = reader.getNumImages(true);
            for (int i = 0; i < numFrames; i++) {
                frames.add(reader.read(i));
            }
            
            stream.close();
        } catch (IOException e) {
            System.err.println("Error cargando frames del GIF: " + gifPath);
            e.printStackTrace();
        }
        
        return frames;
    }
    
    /**
     * Crea un fondo que se ajusta al tamaño del panel
     * @param imagePath La ruta de la imagen de fondo
     */
    protected void setScaledBackground(String imagePath) {
        JLabel backgroundLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image image = loadImage(imagePath);
                if (image != null && currentSize != null) {
                    g.drawImage(image, 0, 0, currentSize.width, currentSize.height, this);
                }
            }
        };
        
        if (currentSize != null) {
            backgroundLabel.setBounds(0, 0, currentSize.width, currentSize.height);
        }
        
        // Añadir como primera capa (fondo)
        add(backgroundLabel);
        setComponentZOrder(backgroundLabel, getComponentCount() - 1);
    }
    
    /**
     * Calcula una posición relativa basada en porcentajes
     * @param percentage El porcentaje (0-100)
     * @param total El valor total
     * @return La posición calculada
     */
    protected int getRelativePosition(double percentage, int total) {
        return (int) (total * percentage / 100.0);
    }
    
    /**
     * Crea un botón estilizado con la fuente del juego
     * @param text El texto del botón
     * @param x Posición X
     * @param y Posición Y
     * @param width Ancho del botón
     * @param height Altura del botón
     * @param fontSize Tamaño de la fuente
     * @return El botón creado
     */
    protected JButton createGameButton(String text, int x, int y, int width, int height, float fontSize) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        setGameFont(button, fontSize);
        
        // Estilo típico de Pokémon
        button.setBackground(new Color(248, 248, 248));
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 200, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(248, 248, 248));
            }
        });
        
        return button;
    }
    
    /**
     * Crea un label estilizado con la fuente del juego
     * @param text El texto del label
     * @param x Posición X
     * @param y Posición Y
     * @param width Ancho del label
     * @param height Altura del label
     * @param fontSize Tamaño de la fuente
     * @return El label creado
     */
    protected JLabel createGameLabel(String text, int x, int y, int width, int height, float fontSize) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        setGameFont(label, fontSize);
        label.setForeground(Color.WHITE);
        return label;
    }
    
    /**
     * Obtiene las dimensiones relativas basadas en el tamaño actual
     * @param widthPercentage Porcentaje del ancho (0-100)
     * @param heightPercentage Porcentaje de la altura (0-100)
     * @return Las dimensiones calculadas
     */
    protected Dimension getRelativeDimension(double widthPercentage, double heightPercentage) {
        if (currentSize == null) return new Dimension(100, 100);
        
        int width = getRelativePosition(widthPercentage, currentSize.width);
        int height = getRelativePosition(heightPercentage, currentSize.height);
        
        return new Dimension(width, height);
    }
}