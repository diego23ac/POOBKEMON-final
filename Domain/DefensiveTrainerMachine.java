package Domain;
import java.awt.*;
import java.util.ArrayList;

public class DefensiveTrainerMachine extends Machine {

    public DefensiveTrainerMachine(String name, Image image, String description) {
        super(name, image, description);
        setAiType("Defensive");
        setDifficultyLevel(2); // Dificultad media
    }

    @Override
    protected int makeAIDecision(Battle battle) {
        Pokemon currentPokemon = battle.getCurrentPokemon();
        Pokemon opponentPokemon = battle.getOpponentPokemon();
        
        // Prioridades del entrenador defensivo:
        // 1. Usar objetos curativos si está muy dañado
        // 2. Cambiar Pokémon si no puede defenderse bien
        // 3. Usar movimientos defensivos/de estado
        // 4. Atacar solo si no hay mejor opción defensiva
        
        if (shouldUseHealingItem(currentPokemon)) {
            return 2; // Usar objeto
        } else if (shouldSwitchForDefense(battle)) {
            return 3; // Cambiar Pokémon
        } else {
            return 1; // Atacar (con enfoque defensivo)
        }
    }

    @Override
    protected void executeAIDecision(int decision, Battle battle) {
        displayAIThinking();
        
        switch (decision) {
            case 1: // Atacar con estrategia defensiva
                Movement defensiveMove = chooseDefensiveMove(battle.getCurrentPokemon(), battle.getOpponentPokemon());
                if (defensiveMove != null) {
                    battle.executeAttack(defensiveMove);
                } else {
                    // Si no hay movimiento defensivo, usar el más seguro
                    Movement safeMove = chooseSafestMove(battle.getCurrentPokemon());
                    if (safeMove != null) {
                        battle.executeAttack(safeMove);
                    }
                }
                break;
                
            case 2: // Usar objeto curativo
                Item healingItem = chooseHealingItem();
                if (healingItem != null) {
                    battle.useItem(healingItem);
                }
                break;
                
            case 3: // Cambiar a Pokémon más defensivo
                Pokemon defensivePokemon = chooseMostDefensivePokemon(battle.getOpponentPokemon());
                if (defensivePokemon != null) {
                    battle.switchPokemon(defensivePokemon);
                }
                break;
        }
    }

    @Override
    public Movement chooseMove(Pokemon pokemon) {
        if (pokemon == null || pokemon.getMoves().isEmpty()) {
            return null;
        }
        
        // Priorizar movimientos de estado y defensivos
        return chooseDefensiveMove(pokemon, null);
    }
    
    private Movement chooseDefensiveMove(Pokemon myPokemon, Pokemon opponentPokemon) {
        ArrayList<Movement> moves = myPokemon.getMoves();
        Movement bestMove = null;
        int highestPriority = -1;
        
        for (Movement move : moves) {
            int priority = evaluateDefensiveMoveValue(move, myPokemon, opponentPokemon);
            if (priority > highestPriority) {
                highestPriority = priority;
                bestMove = move;
            }
        }
        
        return bestMove != null ? bestMove : moves.get(0);
    }
    
    private int evaluateDefensiveMoveValue(Movement move, Pokemon myPokemon, Pokemon opponentPokemon) {
        // Sistema de puntuación para movimientos defensivos
        int value = 0;
        
        // Priorizar movimientos de estado (sin daño o poco daño)
        if (move instanceof StateMovement) {
            value += 50; // Alta prioridad para movimientos de estado
        }
        
        // Si el movimiento tiene poco poder, probablemente sea defensivo/estado
        if (move.getPower() <= 60) {
            value += 30;
        }
        
        // Considerar efectos secundarios defensivos
        if (move.getSideEffect() > 0.3) { // Si tiene alta probabilidad de efecto
            value += 20;
        }
        
        // Priorizar movimientos con alta precisión (más seguros)
        if (move.getAcurracy() >= 90) {
            value += 15;
        }
        
        // Bonificación si es un movimiento de tipo defensivo común
        String moveType = move.getClass().getSimpleName().toLowerCase();
        if (moveType.contains("protect") || moveType.contains("defend") || 
            moveType.contains("recover") || moveType.contains("heal")) {
            value += 40;
        }
        
        return value;
    }
    
    private Movement chooseSafestMove(Pokemon pokemon) {
        ArrayList<Movement> moves = pokemon.getMoves();
        Movement safestMove = moves.get(0);
        
        for (Movement move : moves) {
            // Elegir el movimiento más preciso con poder moderado
            if (move.getAcurracy() > safestMove.getAcurracy() || 
               (move.getAcurracy() == safestMove.getAcurracy() && 
                move.getPower() > safestMove.getPower() && move.getPower() <= 80)) {
                safestMove = move;
            }
        }
        
        return safestMove;
    }
    
    private boolean shouldUseHealingItem(Pokemon pokemon) {
        // Usar objeto curativo si HP está por debajo del 40%
        double hpPercentage = (double) pokemon.getPs() / pokemon.getPs(); // TODO: Necesitamos maxHP
        return hpPercentage < 0.4 && hasHealingItems();
    }
    
    private boolean shouldSwitchForDefense(Battle battle) {
        Pokemon current = battle.getCurrentPokemon();
        Pokemon opponent = battle.getOpponentPokemon();
        
        // Cambiar si el Pokémon actual es muy vulnerable al oponente
        if (isVulnerableToOpponent(current, opponent)) {
            return getAvailablePokemon().size() > 1;
        }
        
        return false;
    }
    
    private boolean isVulnerableToOpponent(Pokemon myPokemon, Pokemon opponentPokemon) {
        // Verificar si nuestro Pokémon es vulnerable por tipos
        Type myPrimaryType = myPokemon.getPrimaryType();
        Type mySecondaryType = myPokemon.getSecondaryType();
        Type opponentPrimaryType = opponentPokemon.getPrimaryType();
        
        // Si el oponente tiene ventaja de tipo, somos vulnerables
        double effectiveness = opponentPrimaryType.calculateMultiplier(myPrimaryType);
        if (mySecondaryType != null) {
            effectiveness *= opponentPrimaryType.calculateMultiplier(mySecondaryType);
        }
        
        return effectiveness > 1.5; // Si recibimos más del 150% de daño
    }
    
    private Pokemon chooseMostDefensivePokemon(Pokemon opponentPokemon) {
        ArrayList<Pokemon> available = getAvailablePokemon();
        Pokemon bestDefender = null;
        int highestDefenseValue = -1;
        
        for (Pokemon pokemon : available) {
            int defenseValue = evaluateDefensiveValue(pokemon, opponentPokemon);
            if (defenseValue > highestDefenseValue) {
                highestDefenseValue = defenseValue;
                bestDefender = pokemon;
            }
        }
        
        return bestDefender;
    }
    
    private int evaluateDefensiveValue(Pokemon pokemon, Pokemon opponent) {
        int value = 0;
        
        // Valor base por estadísticas defensivas
        value += pokemon.getDefence() * 2;
        value += pokemon.getSpecialDefence() * 2;
        value += pokemon.getPs(); // HP también cuenta para defensa
        
        // Bonificación si resiste los ataques del oponente
        Type opponentType = opponent.getPrimaryType();
        double resistance = opponentType.calculateMultiplier(pokemon.getPrimaryType());
        if (pokemon.getSecondaryType() != null) {
            resistance *= opponentType.calculateMultiplier(pokemon.getSecondaryType());
        }
        
        if (resistance < 1.0) {
            value += (int)((1.0 - resistance) * 100); // Bonificación por resistencia
        }
        
        return value;
    }
    
    private Item chooseHealingItem() {
        for (Item item : getAvailableItems()) {
            if (item instanceof Potion) {
                return item;
            }
        }
        return null;
    }
    
    private boolean hasHealingItems() {
        return chooseHealingItem() != null;
    }
}