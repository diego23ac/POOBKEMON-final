package Domain;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public abstract class Machine extends Character {
    protected Random random;
    protected String aiType;
    protected int difficultyLevel; // 1 = Fácil, 2 = Medio, 3 = Difícil

    public Machine(String name, Image image, String description) {
        super(name, image, description);
        this.random = new Random();
        this.aiType = "Basic";
        this.difficultyLevel = 1;
        
        // Las máquinas tienen limitaciones diferentes
        setMaxItemCount(5); // Menos objetos que los humanos
    }

    @Override
    public void makeDecision(Battle battle) {
        System.out.println(name + " está analizando la situación...");
        
        // Lógica básica de decisión de IA
        Pokemon currentPokemon = battle.getCurrentPokemon();
        
        if (currentPokemon.getPs() <= 0) {
            // Cambiar Pokémon si el actual está debilitado
            Pokemon newPokemon = choosePokemon(getAvailablePokemon());
            if (newPokemon != null) {
                battle.switchPokemon(newPokemon);
            }
        } else {
            // Decidir entre atacar, usar objeto o cambiar Pokémon
            int decision = makeAIDecision(battle);
            executeAIDecision(decision, battle);
        }
    }
    
    protected abstract int makeAIDecision(Battle battle);
    protected abstract void executeAIDecision(int decision, Battle battle);

    @Override
    public Pokemon choosePokemon(ArrayList<Pokemon> availablePokemon) {
        if (availablePokemon.isEmpty()) {
            return null;
        }
        
        // Estrategia básica: elegir el Pokémon con más HP
        Pokemon bestPokemon = availablePokemon.get(0);
        for (Pokemon pokemon : availablePokemon) {
            if (pokemon.getPs() > bestPokemon.getPs()) {
                bestPokemon = pokemon;
            }
        }
        return bestPokemon;
    }

    @Override
    public Movement chooseMove(Pokemon pokemon) {
        if (pokemon == null || pokemon.getMoves().isEmpty()) {
            return null;
        }
        
        // Estrategia básica: elegir el movimiento con más poder
        Movement bestMove = pokemon.getMoves().get(0);
        for (Movement move : pokemon.getMoves()) {
            if (move.getPower() > bestMove.getPower()) {
                bestMove = move;
            }
        }
        return bestMove;
    }

    @Override
    public Item chooseItem(ArrayList<Item> availableItems) {
        if (availableItems.isEmpty()) {
            return null;
        }
        
        // Estrategia básica: usar el primer objeto disponible
        return availableItems.get(0);
    }
    
    // ============ MÉTODOS ESPECÍFICOS DE MACHINE ============
    
    protected boolean shouldUseItem(Battle battle) {
        Pokemon currentPokemon = battle.getCurrentPokemon();
        
        // Usar objeto si el HP está bajo (menos del 30%)
        if (currentPokemon.getPs() < (currentPokemon.getPs() * 0.3)) {
            return !getAvailableItems().isEmpty();
        }
        return false;
    }
    
    protected boolean shouldSwitchPokemon(Battle battle) {
        Pokemon currentPokemon = battle.getCurrentPokemon();
        Pokemon opponentPokemon = battle.getOpponentPokemon();
        
        // Cambiar si el Pokémon actual está muy débil
        if (currentPokemon.getPs() < (currentPokemon.getPs() * 0.2)) {
            return !getAvailablePokemon().isEmpty();
        }
        
        return false;
    }
    
    public void displayAIThinking() {
        System.out.println(name + " (" + aiType + ") está calculando...");
    }
    
    // ============ GETTERS Y SETTERS ============
    
    public String getAiType() {
        return aiType;
    }
    
    protected void setAiType(String aiType) {
        this.aiType = aiType;
    }
    
    public int getDifficultyLevel() {
        return difficultyLevel;
    }
    
    protected void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = Math.max(1, Math.min(3, difficultyLevel));
    }
}