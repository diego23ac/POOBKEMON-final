package Domain;

import java.awt.*;
import java.util.ArrayList;

public class Trainer extends Character {

    public Trainer(String name, Image image, String description) {
        super(name, image, description);
        // Los entrenadores humanos pueden tener configuraciones especiales
        setMaxItemCount(15); // Los humanos pueden cargar más objetos
    }

    @Override
    public void makeDecision(Battle battle) {
        // Los entrenadores humanos no toman decisiones automáticas
        // Este método se usa cuando necesitamos IA temporal o acciones por defecto
        System.out.println(name + " está pensando en su próximo movimiento...");
        System.out.println("Esperando decisión del jugador humano.");
    }

    @Override
    public Pokemon choosePokemon(ArrayList<Pokemon> availablePokemon) {
        // Para entrenadores humanos, esto sería controlado por la interfaz
        // Por ahora, devolver el primero disponible como placeholder
        if (!availablePokemon.isEmpty()) {
            return availablePokemon.get(0);
        }
        return null;
    }

    @Override
    public Movement chooseMove(Pokemon pokemon) {
        // Para entrenadores humanos, esto sería controlado por la interfaz
        // Por ahora, devolver el primer movimiento como placeholder
        if (pokemon != null && !pokemon.getMoves().isEmpty()) {
            return pokemon.getMoves().get(0);
        }
        return null;
    }

    @Override
    public Item chooseItem(ArrayList<Item> availableItems) {
        // Para entrenadores humanos, esto sería controlado por la interfaz
        // Por ahora, devolver el primer objeto como placeholder
        if (!availableItems.isEmpty()) {
            return availableItems.get(0);
        }
        return null;
    }
    
    // ============ MÉTODOS ESPECÍFICOS DE TRAINER ============
    
    public void displayBattleOptions(Battle battle) {
        System.out.println("=== OPCIONES DE " + name + " ===");
        System.out.println("1. Atacar");
        System.out.println("2. Usar objeto");
        System.out.println("3. Cambiar Pokémon");
        System.out.println("4. Ver estado del equipo");
        System.out.println("5. Ver objetos disponibles");
        System.out.println("==========================");
    }
    
    public boolean canMakeAdvancedStrategies() {
        // Los entrenadores humanos pueden hacer estrategias avanzadas
        return true;
    }
    
    public void celebrateVictory() {
        System.out.println("¡" + name + " ha ganado la batalla!");
        System.out.println("¡Excelente estrategia!");
    }
    
    public void handleDefeat() {
        System.out.println(name + " ha perdido la batalla.");
        System.out.println("¡No te rindas, entrena más y vuelve más fuerte!");
    }
}