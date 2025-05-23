package Domain;
import java.awt.*;
import java.util.ArrayList;

public class ChangeTrainerMachine extends Machine {

    public ChangeTrainerMachine(String name, Image image, String description) {
        super(name, image, description);
        setAiType("Adaptive");
        setDifficultyLevel(2); // Dificultad media
    }

    @Override
    protected int makeAIDecision(Battle battle) {
        Pokemon currentPokemon = battle.getCurrentPokemon();
        Pokemon opponentPokemon = battle.getOpponentPokemon();
        
        // Prioridades del entrenador adaptativo:
        // 1. SIEMPRE evaluar si hay mejor Pokémon para el matchup actual
        // 2. Cambiar inmediatamente si encuentra mejor opción
        // 3. Solo atacar si ya tiene el mejor Pokémon activo
        
        Pokemon betterPokemon = findBestCounterPokemon(opponentPokemon);
        
        if (betterPokemon != null && betterPokemon != currentPokemon) {
            return 3; // Cambiar Pokémon (prioridad máxima)
        } else if (shouldUseAdaptiveItem(currentPokemon, opponentPokemon)) {
            return 2; // Usar objeto
        } else {
            return 1; // Atacar con el mejor matchup disponible
        }
    }

    @Override
    protected void executeAIDecision(int decision, Battle battle) {
        displayAIThinking();
        System.out.println(name + " está analizando la efectividad de tipos...");
        
        switch (decision) {
            case 1: // Atacar con movimiento más efectivo
                Movement effectiveMove = chooseMostEffectiveMove(battle.getCurrentPokemon(), battle.getOpponentPokemon());
                if (effectiveMove != null) {
                    battle.executeAttack(effectiveMove);
                }
                break;
                
            case 2: // Usar objeto estratégico
                Item strategicItem = chooseStrategicItem(battle.getCurrentPokemon(), battle.getOpponentPokemon());
                if (strategicItem != null) {
                    battle.useItem(strategicItem);
                }
                break;
                
            case 3: // Cambiar al mejor counter
                Pokemon counterPokemon = findBestCounterPokemon(battle.getOpponentPokemon());
                if (counterPokemon != null) {
                    System.out.println(name + " cambia para obtener ventaja de tipo!");
                    battle.switchPokemon(counterPokemon);
                }
                break;
        }
    }

    @Override
    public Pokemon choosePokemon(ArrayList<Pokemon> availablePokemon) {
        // Siempre elegir basado en el oponente actual si está disponible
        return availablePokemon.isEmpty() ? null : availablePokemon.get(0);
    }

    @Override
    public Movement chooseMove(Pokemon pokemon) {
        if (pokemon == null || pokemon.getMoves().isEmpty()) {
            return null;
        }
        
        // Elegir movimiento más efectivo contra el tipo del oponente
        return chooseMostEffectiveMove(pokemon, null);
    }
    
    private Pokemon findBestCounterPokemon(Pokemon opponentPokemon) {
        ArrayList<Pokemon> available = getAvailablePokemon();
        Pokemon bestCounter = null;
        double bestEffectiveness = -1.0;
        
        for (Pokemon pokemon : available) {
            double effectiveness = calculateMatchupValue(pokemon, opponentPokemon);
            if (effectiveness > bestEffectiveness) {
                bestEffectiveness = effectiveness;
                bestCounter = pokemon;
            }
        }
        
        // Solo cambiar si hay una mejora significativa (al menos 1.5x efectividad)
        return bestEffectiveness >= 1.5 ? bestCounter : null;
    }
    
    private double calculateMatchupValue(Pokemon myPokemon, Pokemon opponentPokemon) {
        double value = 1.0;
        
        // Calcular efectividad ofensiva (qué tan bien atacamos al oponente)
        Type myPrimaryType = myPokemon.getPrimaryType();
        Type opponentPrimaryType = opponentPokemon.getPrimaryType();
        Type opponentSecondaryType = opponentPokemon.getSecondaryType();
        
        double offensiveEffectiveness = myPrimaryType.calculateMultiplier(opponentPrimaryType);
        if (opponentSecondaryType != null) {
            offensiveEffectiveness *= myPrimaryType.calculateMultiplier(opponentSecondaryType);
        }
        
        // Calcular efectividad defensiva (qué tan bien resistimos al oponente)
        Type mySecondaryType = myPokemon.getSecondaryType();
        
        double defensiveEffectiveness = opponentPrimaryType.calculateMultiplier(myPrimaryType);
        if (mySecondaryType != null) {
            defensiveEffectiveness *= opponentPrimaryType.calculateMultiplier(mySecondaryType);
        }
        
        // Combinar ambas efectividades
        // Mayor peso a la efectividad ofensiva, pero también considerar defensa
        value = (offensiveEffectiveness * 2.0) / (defensiveEffectiveness + 0.5);
        
        // Bonificación adicional por estadísticas favorables
        if (offensiveEffectiveness > 1.0) {
            // Si tenemos ventaja de tipo, considerar nuestro ataque
            value += (myPokemon.getAttack() + myPokemon.getSpecialAttack()) / 200.0;
        }
        
        if (defensiveEffectiveness < 1.0) {
            // Si resistimos al oponente, considerar nuestra defensa
            value += (myPokemon.getDefence() + myPokemon.getSpecialDefence()) / 200.0;
        }
        
        return value;
    }
    
    private Movement chooseMostEffectiveMove(Pokemon myPokemon, Pokemon opponentPokemon) {
        if (myPokemon.getMoves().isEmpty()) {
            return null;
        }
        
        if (opponentPokemon == null) {
            // Si no conocemos al oponente, elegir el movimiento más poderoso
            Movement strongestMove = myPokemon.getMoves().get(0);
            for (Movement move : myPokemon.getMoves()) {
                if (move.getPower() > strongestMove.getPower()) {
                    strongestMove = move;
                }
            }
            return strongestMove;
        }
        
        Movement bestMove = null;
        double bestEffectiveness = -1.0;
        
        for (Movement move : myPokemon.getMoves()) {
            double effectiveness = evaluateMoveEffectiveness(move, myPokemon, opponentPokemon);
            if (effectiveness > bestEffectiveness) {
                bestEffectiveness = effectiveness;
                bestMove = move;
            }
        }
        
        return bestMove != null ? bestMove : myPokemon.getMoves().get(0);
    }
    
    private double evaluateMoveEffectiveness(Movement move, Pokemon myPokemon, Pokemon opponentPokemon) {
        double effectiveness = 1.0;
        
        // Factor base: poder del movimiento
        effectiveness += move.getPower() / 100.0;
        
        // Factor de precisión
        effectiveness *= move.getAcurracy() / 100.0;
        
        // Efectividad de tipo (si podemos determinarla)
        if (move instanceof SpecialMovement) {
            // TODO: Necesitamos acceso al tipo del movimiento
            // SpecialMovement specialMove = (SpecialMovement) move;
            // Type moveType = specialMove.getType();
            // double typeEffectiveness = moveType.calculateMultiplier(opponentPokemon.getPrimaryType());
            // if (opponentPokemon.getSecondaryType() != null) {
            //     typeEffectiveness *= moveType.calculateMultiplier(opponentPokemon.getSecondaryType());
            // }
            // effectiveness *= typeEffectiveness;
        }
        
        // Compatibilidad con las estadísticas del Pokémon
        if (move instanceof PhysicalMovement) {
            effectiveness += myPokemon.getAttack() / 200.0;
        } else if (move instanceof SpecialMovement) {
            effectiveness += myPokemon.getSpecialAttack() / 200.0;
        }
        
        return effectiveness;
    }
    
    private boolean shouldUseAdaptiveItem(Pokemon myPokemon, Pokemon opponentPokemon) {
        // Usar objetos estratégicamente basado en el matchup
        double matchupValue = calculateMatchupValue(myPokemon, opponentPokemon);
        
        // Si estamos en desventaja y tenemos pocos HP, usar objeto curativo
        if (matchupValue < 0.8) {
            double hpPercentage = (double) myPokemon.getPs() / myPokemon.getPs(); // TODO: maxHP
            return hpPercentage < 0.6 && hasHealingItems();
        }
        
        return false;
    }
    
    private Item chooseStrategicItem(Pokemon myPokemon, Pokemon opponentPokemon) {
        // Elegir objeto basado en la situación estratégica
        for (Item item : getAvailableItems()) {
            if (item instanceof Potion) {
                return item;
            } else if (item instanceof Revive && needsRevive()) {
                return item;
            }
        }
        return null;
    }
    
    private boolean hasHealingItems() {
        for (Item item : getAvailableItems()) {
            if (item instanceof Potion) {
                return true;
            }
        }
        return false;
    }
    
    private boolean needsRevive() {
        // Verificar si tenemos Pokémon debilitados que podrían ser útiles
        for (Pokemon pokemon : getTeam()) {
            if (pokemon.getPs() <= 0) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void displayAIThinking() {
        super.displayAIThinking();
        System.out.println("Analizando ventajas de tipo y buscando el mejor matchup...");
    }
}