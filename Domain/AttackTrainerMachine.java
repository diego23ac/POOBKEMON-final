package Domain;
import java.awt.*;
import java.util.ArrayList;

public class AttackTrainerMachine extends Machine {

    public AttackTrainerMachine(String name, Image image, String description) {
        super(name, image, description);
        setAiType("Aggressive");
        setDifficultyLevel(2); // Dificultad media
    }

    @Override
    protected int makeAIDecision(Battle battle) {
        Pokemon currentPokemon = battle.getCurrentPokemon();
        Pokemon opponentPokemon = battle.getOpponentPokemon();
        
        // Prioridades del entrenador ofensivo:
        // 1. Cambiar si puede conseguir mejor ventaja ofensiva
        // 2. Atacar con el movimiento más efectivo
        // 3. Usar objetos solo si es crítico
        
        if (shouldSwitchForOffense(battle)) {
            return 3; // Cambiar Pokémon
        } else if (shouldUseOffensiveItem(currentPokemon)) {
            return 2; // Usar objeto
        } else {
            return 1; // Atacar (opción principal)
        }
    }

    @Override
    protected void executeAIDecision(int decision, Battle battle) {
        displayAIThinking();
        
        switch (decision) {
            case 1: // Atacar con máxima potencia
                Movement powerfulMove = chooseMostPowerfulMove(battle.getCurrentPokemon(), battle.getOpponentPokemon());
                if (powerfulMove != null) {
                    battle.executeAttack(powerfulMove);
                }
                break;
                
            case 2: // Usar objeto ofensivo
                Item offensiveItem = chooseOffensiveItem();
                if (offensiveItem != null) {
                    battle.useItem(offensiveItem);
                }
                break;
                
            case 3: // Cambiar a Pokémon más ofensivo
                Pokemon attackPokemon = chooseMostOffensivePokemon(battle.getOpponentPokemon());
                if (attackPokemon != null) {
                    battle.switchPokemon(attackPokemon);
                }
                break;
        }
    }

    @Override
    public Movement chooseMove(Pokemon pokemon) {
        if (pokemon == null || pokemon.getMoves().isEmpty()) {
            return null;
        }
        
        // Siempre elegir el movimiento más poderoso
        return chooseMostPowerfulMove(pokemon, null);
    }
    
    private Movement chooseMostPowerfulMove(Pokemon myPokemon, Pokemon opponentPokemon) {
        ArrayList<Movement> moves = myPokemon.getMoves();
        Movement bestMove = null;
        int highestValue = -1;
        
        for (Movement move : moves) {
            int attackValue = evaluateOffensiveMoveValue(move, myPokemon, opponentPokemon);
            if (attackValue > highestValue) {
                highestValue = attackValue;
                bestMove = move;
            }
        }
        
        return bestMove != null ? bestMove : moves.get(0);
    }
    
    private int evaluateOffensiveMoveValue(Movement move, Pokemon myPokemon, Pokemon opponentPokemon) {
        int value = 0;
        
        // Valor base por poder del movimiento
        value += move.getPower() * 2;
        
        // Bonificación por precisión (ataques certeros son mejores)
        value += move.getAcurracy() / 10;
        
        // Priorizar movimientos físicos y especiales sobre estado
        if (move instanceof PhysicalMovement) {
            value += 30 + myPokemon.getAttack() / 10; // Bonificación por ataque físico
        } else if (move instanceof SpecialMovement) {
            value += 30 + myPokemon.getSpecialAttack() / 10; // Bonificación por ataque especial
        } else if (move instanceof StateMovement) {
            value -= 20; // Penalización para movimientos de estado
        }
        
        // Bonificación por efectividad de tipo si conocemos al oponente
        if (opponentPokemon != null && move instanceof SpecialMovement) {
            SpecialMovement specialMove = (SpecialMovement) move;
            // TODO: Necesitamos acceso al tipo del movimiento desde SpecialMovement
            // Type moveType = specialMove.getType();
            // double effectiveness = moveType.calculateMultiplier(opponentPokemon.getPrimaryType());
            // value += (int)(effectiveness * 20);
        }
        
        // Priorizar movimientos con baja probabilidad de efectos secundarios
        // (queremos daño directo)
        if (move.getSideEffect() < 0.2) {
            value += 15;
        }
        
        return value;
    }
    
    private boolean shouldSwitchForOffense(Battle battle) {
        Pokemon current = battle.getCurrentPokemon();
        Pokemon opponent = battle.getOpponentPokemon();
        
        // Cambiar si podemos conseguir mejor ventaja ofensiva
        Pokemon betterAttacker = findBetterAttacker(current, opponent);
        return betterAttacker != null && betterAttacker != current;
    }
    
    private Pokemon findBetterAttacker(Pokemon currentPokemon, Pokemon opponentPokemon) {
        ArrayList<Pokemon> available = getAvailablePokemon();
        Pokemon bestAttacker = currentPokemon;
        int highestAttackValue = evaluateOffensiveValue(currentPokemon, opponentPokemon);
        
        for (Pokemon pokemon : available) {
            if (pokemon != currentPokemon) {
                int attackValue = evaluateOffensiveValue(pokemon, opponentPokemon);
                if (attackValue > highestAttackValue) {
                    highestAttackValue = attackValue;
                    bestAttacker = pokemon;
                }
            }
        }
        
        return bestAttacker != currentPokemon ? bestAttacker : null;
    }
    
    private int evaluateOffensiveValue(Pokemon pokemon, Pokemon opponent) {
        int value = 0;
        
        // Valor base por estadísticas ofensivas
        value += pokemon.getAttack() * 3;
        value += pokemon.getSpecialAttack() * 3;
        value += pokemon.getSpeed() * 2; // La velocidad ayuda a atacar primero
        
        // Bonificación si tiene ventaja de tipo contra el oponente
        Type myType = pokemon.getPrimaryType();
        double effectiveness = myType.calculateMultiplier(opponent.getPrimaryType());
        if (opponent.getSecondaryType() != null) {
            effectiveness *= myType.calculateMultiplier(opponent.getSecondaryType());
        }
        
        if (effectiveness > 1.0) {
            value += (int)((effectiveness - 1.0) * 100); // Bonificación por efectividad
        }
        
        // Evaluar potencial de los movimientos
        for (Movement move : pokemon.getMoves()) {
            if (move.getPower() > 80) {
                value += 20; // Bonificación por movimientos poderosos
            }
        }
        
        return value;
    }
    
    private Pokemon chooseMostOffensivePokemon(Pokemon opponentPokemon) {
        ArrayList<Pokemon> available = getAvailablePokemon();
        Pokemon bestAttacker = null;
        int highestOffenseValue = -1;
        
        for (Pokemon pokemon : available) {
            int offenseValue = evaluateOffensiveValue(pokemon, opponentPokemon);
            if (offenseValue > highestOffenseValue) {
                highestOffenseValue = offenseValue;
                bestAttacker = pokemon;
            }
        }
        
        return bestAttacker;
    }
    
    private boolean shouldUseOffensiveItem(Pokemon pokemon) {
        // Solo usar objetos si HP está muy bajo (menos del 25%)
        double hpPercentage = (double) pokemon.getPs() / pokemon.getPs(); // TODO: Necesitamos maxHP
        return hpPercentage < 0.25 && hasOffensiveItems();
    }
    
    private Item chooseOffensiveItem() {
        // Priorizar objetos curativos para mantener la ofensiva
        for (Item item : getAvailableItems()) {
            if (item instanceof Potion) {
                return item;
            }
        }
        return null;
    }
    
    private boolean hasOffensiveItems() {
        return chooseOffensiveItem() != null;
    }
}