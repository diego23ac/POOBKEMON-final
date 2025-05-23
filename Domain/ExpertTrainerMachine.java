package Domain;
import java.awt.*;
import java.util.ArrayList;

public class ExpertTrainerMachine extends Machine {

    public ExpertTrainerMachine(String name, Image image, String description) {
        super(name, image, description);
        setAiType("Expert");
        setDifficultyLevel(3); // Dificultad máxima
    }

    @Override
    protected int makeAIDecision(Battle battle) {
        Pokemon currentPokemon = battle.getCurrentPokemon();
        Pokemon opponentPokemon = battle.getOpponentPokemon();
        
        // El entrenador experto evalúa TODAS las opciones y elige la óptima
        
        // Evaluar todas las estrategias posibles
        double attackValue = evaluateAttackStrategy(currentPokemon, opponentPokemon);
        double itemValue = evaluateItemStrategy(currentPokemon, opponentPokemon);
        double switchValue = evaluateSwitchStrategy(currentPokemon, opponentPokemon);
        
        // Elegir la estrategia con mayor valor
        if (switchValue > attackValue && switchValue > itemValue && switchValue > 0) {
            return 3; // Cambiar Pokémon
        } else if (itemValue > attackValue && itemValue > 0) {
            return 2; // Usar objeto
        } else {
            return 1; // Atacar
        }
    }

    @Override
    protected void executeAIDecision(int decision, Battle battle) {
        displayAIThinking();
        System.out.println(name + " está calculando la estrategia óptima...");
        
        switch (decision) {
            case 1: // Ejecutar el mejor ataque posible
                Movement optimalMove = findOptimalMove(battle.getCurrentPokemon(), battle.getOpponentPokemon());
                if (optimalMove != null) {
                    System.out.println(name + " ejecuta una estrategia de ataque perfecta!");
                    battle.executeAttack(optimalMove);
                }
                break;
                
            case 2: // Usar objeto de manera óptima
                Item optimalItem = findOptimalItem(battle.getCurrentPokemon(), battle.getOpponentPokemon());
                if (optimalItem != null) {
                    System.out.println(name + " usa un objeto en el momento perfecto!");
                    battle.useItem(optimalItem);
                }
                break;
                
            case 3: // Cambiar al Pokémon óptimo
                Pokemon optimalPokemon = findOptimalPokemon(battle.getOpponentPokemon());
                if (optimalPokemon != null) {
                    System.out.println(name + " hace un cambio estratégico magistral!");
                    battle.switchPokemon(optimalPokemon);
                }
                break;
        }
    }

    @Override
    public Pokemon choosePokemon(ArrayList<Pokemon> availablePokemon) {
        // El experto siempre elige el Pokémon óptimo
        return findOptimalPokemon(null, availablePokemon);
    }

    @Override
    public Movement chooseMove(Pokemon pokemon) {
        if (pokemon == null || pokemon.getMoves().isEmpty()) {
            return null;
        }
        
        // Siempre el movimiento óptimo
        return findOptimalMove(pokemon, null);
    }
    
    private double evaluateAttackStrategy(Pokemon myPokemon, Pokemon opponentPokemon) {
        Movement bestMove = findOptimalMove(myPokemon, opponentPokemon);
        if (bestMove == null) return 0;
        
        double value = 0;
        
        // Valor base del ataque
       value += bestMove.getPower() / 10.0;
       
       // Efectividad de tipo
       double typeEffectiveness = calculateTypeEffectiveness(bestMove, myPokemon, opponentPokemon);
       value *= typeEffectiveness;
       
       // Factor de precisión
       value *= (bestMove.getAcurracy() / 100.0);
       
       // Considerar estadísticas del Pokémon
       if (bestMove instanceof PhysicalMovement) {
           value += myPokemon.getAttack() / 50.0;
       } else if (bestMove instanceof SpecialMovement) {
           value += myPokemon.getSpecialAttack() / 50.0;
       }
       
       // Bonificación si puede derrotar al oponente
       int estimatedDamage = estimateDamage(myPokemon, opponentPokemon, bestMove);
       if (estimatedDamage >= opponentPokemon.getPs()) {
           value += 50; // Gran bonificación por KO
       }
       
       // Penalización si nuestro Pokémon está en peligro
       double hpPercentage = (double) myPokemon.getPs() / getMaxHP(myPokemon);
       if (hpPercentage < 0.3) {
           value *= 0.7; // Reducir valor si estamos muy débiles
       }
       
       return value;
   }
   
   private double evaluateItemStrategy(Pokemon myPokemon, Pokemon opponentPokemon) {
       Item bestItem = findOptimalItem(myPokemon, opponentPokemon);
       if (bestItem == null) return 0;
       
       double value = 0;
       double hpPercentage = (double) myPokemon.getPs() / getMaxHP(myPokemon);
       
       if (bestItem instanceof Potion) {
           // Valor aumenta cuando HP está bajo
           if (hpPercentage < 0.4) {
               value = (0.4 - hpPercentage) * 100; // Más valor cuanto menos HP
               
               // Bonificación extra si podemos sobrevivir otro ataque después de curar
               int estimatedDamage = estimateOpponentDamage(opponentPokemon, myPokemon);
               int healAmount = getHealingAmount(bestItem);
               if (myPokemon.getPs() + healAmount > estimatedDamage) {
                   value += 25; // Bonificación por supervivencia
               }
           }
       } else if (bestItem instanceof Revive) {
           // Usar Revive solo si es estratégicamente ventajoso
           Pokemon bestFaintedPokemon = findBestFaintedPokemon(opponentPokemon);
           if (bestFaintedPokemon != null) {
               double reviveValue = calculateMatchupValue(bestFaintedPokemon, opponentPokemon);
               if (reviveValue > calculateMatchupValue(myPokemon, opponentPokemon)) {
                   value = reviveValue * 30; // Valor basado en mejora del matchup
               }
           }
       }
       
       return value;
   }
   
   private double evaluateSwitchStrategy(Pokemon currentPokemon, Pokemon opponentPokemon) {
       Pokemon bestSwitch = findOptimalPokemon(opponentPokemon);
       if (bestSwitch == null || bestSwitch == currentPokemon) return 0;
       
       double currentMatchup = calculateMatchupValue(currentPokemon, opponentPokemon);
       double newMatchup = calculateMatchupValue(bestSwitch, opponentPokemon);
       
       // Solo cambiar si hay mejora significativa
       double improvement = newMatchup - currentMatchup;
       if (improvement > 0.5) {
           return improvement * 40; // Valor basado en la mejora
       }
       
       return 0;
   }
   
   private Movement findOptimalMove(Pokemon myPokemon, Pokemon opponentPokemon) {
       if (myPokemon.getMoves().isEmpty()) return null;
       
       Movement bestMove = null;
       double bestValue = -1;
       
       for (Movement move : myPokemon.getMoves()) {
           double value = evaluateMoveComprehensively(move, myPokemon, opponentPokemon);
           if (value > bestValue) {
               bestValue = value;
               bestMove = move;
           }
       }
       
       return bestMove;
   }
   
   private double evaluateMoveComprehensively(Movement move, Pokemon myPokemon, Pokemon opponentPokemon) {
       double value = 0;
       
       // Poder base
       value += move.getPower();
       
       // Precisión
       value *= (move.getAcurracy() / 100.0);
       
       // Efectividad de tipo
       if (opponentPokemon != null) {
           double typeEffectiveness = calculateTypeEffectiveness(move, myPokemon, opponentPokemon);
           value *= typeEffectiveness;
       }
       
       // Compatibilidad con estadísticas
       if (move instanceof PhysicalMovement) {
           value += myPokemon.getAttack() * 0.5;
       } else if (move instanceof SpecialMovement) {
           value += myPokemon.getSpecialAttack() * 0.5;
       } else if (move instanceof StateMovement) {
           // Los movimientos de estado tienen valor situacional
           value += evaluateStateMovement(move, myPokemon, opponentPokemon);
       }
       
       // Efectos secundarios
       if (move.getSideEffect() > 0) {
           value += move.getSideEffect() * 20; // Valor por probabilidad de efecto
       }
       
       // Prioridad del movimiento
       value += move.getPriority() * 10;
       
       return value;
   }
   
   private Pokemon findOptimalPokemon(Pokemon opponentPokemon) {
       return findOptimalPokemon(opponentPokemon, getAvailablePokemon());
   }
   
   private Pokemon findOptimalPokemon(Pokemon opponentPokemon, ArrayList<Pokemon> candidates) {
       if (candidates.isEmpty()) return null;
       
       Pokemon bestPokemon = null;
       double bestValue = -1;
       
       for (Pokemon pokemon : candidates) {
           double value = evaluatePokemonComprehensively(pokemon, opponentPokemon);
           if (value > bestValue) {
               bestValue = value;
               bestPokemon = pokemon;
           }
       }
       
       return bestPokemon;
   }
   
   private double evaluatePokemonComprehensively(Pokemon pokemon, Pokemon opponent) {
       double value = 0;
       
       // Estadísticas base
       value += pokemon.getAttack() * 0.3;
       value += pokemon.getSpecialAttack() * 0.3;
       value += pokemon.getDefence() * 0.2;
       value += pokemon.getSpecialDefence() * 0.2;
       value += pokemon.getSpeed() * 0.25;
       value += pokemon.getPs() * 0.4;
       
       if (opponent != null) {
           // Ventaja de matchup
           double matchupValue = calculateMatchupValue(pokemon, opponent);
           value *= matchupValue;
           
           // Considerar si podemos ganar en un turno
           Movement bestMove = findOptimalMove(pokemon, opponent);
           if (bestMove != null) {
               int estimatedDamage = estimateDamage(pokemon, opponent, bestMove);
               if (estimatedDamage >= opponent.getPs()) {
                   value += 100; // Gran bonificación por KO potencial
               }
           }
       }
       
       // Evaluar calidad del moveset
       value += evaluateMoveset(pokemon);
       
       return value;
   }
   
   private Item findOptimalItem(Pokemon myPokemon, Pokemon opponentPokemon) {
       Item bestItem = null;
       double bestValue = -1;
       
       for (Item item : getAvailableItems()) {
           double value = evaluateItemComprehensively(item, myPokemon, opponentPokemon);
           if (value > bestValue) {
               bestValue = value;
               bestItem = item;
           }
       }
       
       return bestItem;
   }
   
   private double evaluateItemComprehensively(Item item, Pokemon myPokemon, Pokemon opponentPokemon) {
       double value = 0;
       
       if (item instanceof Potion) {
           double hpPercentage = (double) myPokemon.getPs() / getMaxHP(myPokemon);
           
           // Más valor cuando HP está más bajo
           value = Math.max(0, (0.6 - hpPercentage) * 100);
           
           // Considerar si nos ayuda a sobrevivir
           int estimatedDamage = estimateOpponentDamage(opponentPokemon, myPokemon);
           int healAmount = getHealingAmount(item);
           
           if (myPokemon.getPs() + healAmount > estimatedDamage) {
               value += 30; // Bonificación por supervivencia
           }
           
       } else if (item instanceof Revive) {
           Pokemon bestFainted = findBestFaintedPokemon(opponentPokemon);
           if (bestFainted != null) {
               double faintedValue = evaluatePokemonComprehensively(bestFainted, opponentPokemon);
               double currentValue = evaluatePokemonComprehensively(myPokemon, opponentPokemon);
               
               if (faintedValue > currentValue * 1.2) {
                   value = (faintedValue - currentValue) * 0.5;
               }
           }
       }
       
       return value;
   }
   
   // ============ MÉTODOS AUXILIARES ============
   
   private double calculateTypeEffectiveness(Movement move, Pokemon attacker, Pokemon defender) {
       // TODO: Implementar cuando tengamos acceso al tipo del movimiento
       // Por ahora, usar efectividad base del tipo del atacante
       if (defender != null) {
           Type attackerType = attacker.getPrimaryType();
           double effectiveness = attackerType.calculateMultiplier(defender.getPrimaryType());
           if (defender.getSecondaryType() != null) {
               effectiveness *= attackerType.calculateMultiplier(defender.getSecondaryType());
           }
           return effectiveness;
       }
       return 1.0;
   }
   
   private double calculateMatchupValue(Pokemon myPokemon, Pokemon opponentPokemon) {
       if (opponentPokemon == null) return 1.0;
       
       // Efectividad ofensiva
       Type myType = myPokemon.getPrimaryType();
       double offensive = myType.calculateMultiplier(opponentPokemon.getPrimaryType());
       if (opponentPokemon.getSecondaryType() != null) {
           offensive *= myType.calculateMultiplier(opponentPokemon.getSecondaryType());
       }
       
       // Efectividad defensiva
       Type opponentType = opponentPokemon.getPrimaryType();
       double defensive = opponentType.calculateMultiplier(myPokemon.getPrimaryType());
       if (myPokemon.getSecondaryType() != null) {
           defensive *= opponentType.calculateMultiplier(myPokemon.getSecondaryType());
       }
       
       // Combinar con peso hacia la ofensiva
       return (offensive * 2.0) / (defensive + 0.5);
   }
   
   private int estimateDamage(Pokemon attacker, Pokemon defender, Movement move) {
       // Estimación básica de daño (simplificada)
       int baseDamage = move.getPower();
       
       if (move instanceof PhysicalMovement) {
           baseDamage = (baseDamage * attacker.getAttack()) / defender.getDefence();
       } else if (move instanceof SpecialMovement) {
           baseDamage = (baseDamage * attacker.getSpecialAttack()) / defender.getSpecialDefence();
       }
       
       // Aplicar efectividad de tipo
       double typeEffectiveness = calculateTypeEffectiveness(move, attacker, defender);
       baseDamage = (int) (baseDamage * typeEffectiveness);
       
       return Math.max(1, baseDamage / 5); // Ajuste para ser más realista
   }
   
   private int estimateOpponentDamage(Pokemon opponent, Pokemon myPokemon) {
       if (opponent.getMoves().isEmpty()) return 20; // Estimación por defecto
       
       Movement strongestMove = opponent.getMoves().get(0);
       for (Movement move : opponent.getMoves()) {
           if (move.getPower() > strongestMove.getPower()) {
               strongestMove = move;
           }
       }
       
       return estimateDamage(opponent, myPokemon, strongestMove);
   }
   
   private int getMaxHP(Pokemon pokemon) {
       // TODO: Necesitamos un método para obtener HP máximo
       // Por ahora, asumir que PS actual es el máximo si está sano
       return pokemon.getPs();
   }
   
   private int getHealingAmount(Item item) {
       if (item instanceof Potion) {
           return 20; // Poción básica
       } else if (item instanceof SuperPotion) {
           return 50; // Super poción
       } else if (item instanceof HyperPotion) {
           return 200; // Hiper poción
       }
       return 0;
   }
   
   private Pokemon findBestFaintedPokemon(Pokemon opponentPokemon) {
       Pokemon bestFainted = null;
       double bestMatchup = -1;
       
       for (Pokemon pokemon : getTeam()) {
           if (pokemon.getPs() <= 0) {
               double matchup = calculateMatchupValue(pokemon, opponentPokemon);
               if (matchup > bestMatchup) {
                   bestMatchup = matchup;
                   bestFainted = pokemon;
               }
           }
       }
       
       return bestFainted;
   }
   
   private double evaluateStateMovement(Movement move, Pokemon myPokemon, Pokemon opponentPokemon) {
       // Los movimientos de estado tienen valor situacional
       double value = 0;
       
       // Valor base por la probabilidad del efecto
       value += move.getSideEffect() * 30;
       
       // Bonificación si el oponente es más fuerte (necesitamos debuffear)
       if (opponentPokemon != null) {
           int opponentTotal = opponentPokemon.getAttack() + opponentPokemon.getSpecialAttack();
           int myTotal = myPokemon.getAttack() + myPokemon.getSpecialAttack();
           
           if (opponentTotal > myTotal) {
               value += 20; // Valor extra si necesitamos nivelar el campo
           }
       }
       
       return value;
   }
   
   private double evaluateMoveset(Pokemon pokemon) {
       double value = 0;
       
       for (Movement move : pokemon.getMoves()) {
           value += move.getPower() * 0.1;
           value += move.getAcurracy() * 0.05;
           
           if (move instanceof StateMovement) {
               value += 5; // Versatilidad
           }
       }
       
       return value;
   }
   
   @Override
   public void displayAIThinking() {
       super.displayAIThinking();
       System.out.println("Procesando miles de posibilidades estratégicas...");
       System.out.println("Evaluando ventajas de tipo, estadísticas y movimientos...");
       System.out.println("Seleccionando la jugada óptima...");
   }
}