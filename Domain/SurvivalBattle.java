package Domain;

import java.util.ArrayList;

public class SurvivalBattle extends Battle {
    private ArrayList<Pokemon> teamPlayer1;
    private ArrayList<Pokemon> teamPlayer2;
    private int pokemonDefeatedPlayer1;
    private int pokemonDefeatedPlayer2;
    private static final int TEAM_SIZE = 6;
    private boolean itemsAllowed;

    public SurvivalBattle(Pokemon pokemonPlayer1, Pokemon pokemonPlayer2, Character player1, Character player2) {
        super(pokemonPlayer1, pokemonPlayer2, player1, player2);
        
        // Configuración específica de batalla de supervivencia
        this.itemsAllowed = false; // No se permiten objetos en supervivencia
        this.pokemonDefeatedPlayer1 = 0;
        this.pokemonDefeatedPlayer2 = 0;
        
        // Los equipos se asignarán desde POOBkemonEmerald con 6 Pokémon aleatorios
        this.teamPlayer1 = new ArrayList<Pokemon>();
        this.teamPlayer2 = new ArrayList<Pokemon>();
        
        // Agregar los Pokémon iniciales
        teamPlayer1.add(pokemonPlayer1);
        teamPlayer2.add(pokemonPlayer2);
    }

    @Override
    public void startBattle() {
        System.out.println("=== INICIANDO BATALLA DE SUPERVIVENCIA ===");
        System.out.println("¡Solo jugador vs jugador!");
        System.out.println("6 Pokémon aleatorios por jugador - ¡Sin objetos!");
        System.out.println(player1.getName() + " vs " + player2.getName());
        System.out.println(player1.getName() + " envía a " + pokemonPlayer1.getName() + "!");
        System.out.println(player2.getName() + " envía a " + pokemonPlayer2.getName() + "!");
        System.out.println("¡Que comience la batalla de supervivencia!");
        
        displayBattleStatus();
        displaySurvivalStatus();
    }

    @Override
    public boolean checkWinCondition() {
        // En supervivencia, gana quien derrote todos los Pokémon del oponente
        
        boolean player1HasAvailablePokemon = false;
        boolean player2HasAvailablePokemon = false;
        
        // Contar Pokémon disponibles
        for (Pokemon pokemon : teamPlayer1) {
            if (isPokemonAvailable(pokemon)) {
                player1HasAvailablePokemon = true;
                break;
            }
        }
        
        for (Pokemon pokemon : teamPlayer2) {
            if (isPokemonAvailable(pokemon)) {
                player2HasAvailablePokemon = true;
                break;
            }
        }
        
        // Determinar ganador
        if (!player1HasAvailablePokemon && !player2HasAvailablePokemon) {
            // Empate (muy raro)
            endBattle(null);
            return true;
        } else if (!player1HasAvailablePokemon) {
            // Gana jugador 2
            System.out.println(player2.getName() + " ha derrotado todos los Pokémon de " + player1.getName() + "!");
            endBattle(player2);
            return true;
        } else if (!player2HasAvailablePokemon) {
            // Gana jugador 1
            System.out.println(player1.getName() + " ha derrotado todos los Pokémon de " + player2.getName() + "!");
            endBattle(player1);
            return true;
        }
        
        return false;
    }

    @Override
    public void executeAttack(Movement move) {
        Pokemon attacker = getCurrentPokemon();
        Pokemon defender = getOpponentPokemon();
        
        // Verificar si el ataque es válido
        if (!canUseMove(attacker, move)) {
            System.out.println(attacker.getName() + " no puede usar " + move.getName());
            return;
        }
        
        System.out.println(attacker.getName() + " usa " + move.getName() + "!");
        
        // Aplicar el daño
        applyDamage(attacker, defender, move);
        
        // Verificar si el Pokémon defensor se debilitó
        if (!isPokemonAvailable(defender)) {
            handleFaintedPokemonSurvival();
        }
        
        // Cambiar turno si la batalla continúa
        if (!battleEnded) {
            switchTurn();
        }
    }

    @Override
    public void useItem(Item item) {
        // En batalla de supervivencia NO se permiten objetos
        System.out.println("¡No se pueden usar objetos en la batalla de supervivencia!");
        System.out.println("Solo puedes atacar o cambiar de Pokémon.");
    }

    @Override
    public void switchPokemon(Pokemon newPokemon) {
        Character currentPlayer = getCurrentPlayer();
        ArrayList<Pokemon> currentTeam = (currentTurn == 1) ? teamPlayer1 : teamPlayer2;
        Pokemon currentPokemon = getCurrentPokemon();
        
        // Verificar que el nuevo Pokémon esté en el equipo
        if (!currentTeam.contains(newPokemon)) {
            System.out.println("Ese Pokémon no está en tu equipo de supervivencia.");
            return;
        }
        
        // Verificar que el nuevo Pokémon esté disponible
        if (!isPokemonAvailable(newPokemon)) {
            System.out.println(newPokemon.getName() + " no puede pelear.");
            return;
        }
        
        // Verificar que no sea el mismo Pokémon
        if (currentPokemon == newPokemon) {
            System.out.println(newPokemon.getName() + " ya está en batalla.");
            return;
        }
        
        // Realizar el cambio
        updateActivePokemon(newPokemon, currentTurn);
        
        // Cambiar Pokémon consume el turno
        if (!battleEnded) {
            switchTurn();
        }
    }
    
    // ============ MÉTODOS ESPECÍFICOS DE SUPERVIVENCIA ============
    
    private void handleFaintedPokemonSurvival() {
        Character playerWithFaintedPokemon = getOpponentPlayer();
        ArrayList<Pokemon> teamWithFaintedPokemon = (currentTurn == 1) ? teamPlayer2 : teamPlayer1;
        
        // Incrementar contador de Pokémon derrotados
        if (currentTurn == 1) {
            pokemonDefeatedPlayer2++;
        } else {
            pokemonDefeatedPlayer1++;
        }
        
        System.out.println(getOpponentPokemon().getName() + " se ha debilitado!");
        System.out.println("Pokémon derrotados de " + playerWithFaintedPokemon.getName() + ": " + 
                          (currentTurn == 1 ? pokemonDefeatedPlayer2 : pokemonDefeatedPlayer1) + "/" + TEAM_SIZE);
        
        // Verificar si hay más Pokémon disponibles
        Pokemon nextPokemon = getNextAvailablePokemon(teamWithFaintedPokemon);
        
        if (nextPokemon != null) {
            // Forzar cambio de Pokémon
            int opponentPlayerNumber = (currentTurn == 1) ? 2 : 1;
            updateActivePokemon(nextPokemon, opponentPlayerNumber);
            System.out.println(playerWithFaintedPokemon.getName() + " debe enviar a su siguiente Pokémon.");
        } else {
            System.out.println("¡" + playerWithFaintedPokemon.getName() + " no tiene más Pokémon disponibles!");
        }
        
        displaySurvivalStatus();
    }
    
    private Pokemon getNextAvailablePokemon(ArrayList<Pokemon> team) {
        for (Pokemon pokemon : team) {
            if (isPokemonAvailable(pokemon)) {
                return pokemon;
            }
        }
        return null;
    }
    
    public void setTeams(ArrayList<Pokemon> team1, ArrayList<Pokemon> team2) {
        if (team1.size() == TEAM_SIZE && team2.size() == TEAM_SIZE) {
            this.teamPlayer1 = new ArrayList<>(team1);
            this.teamPlayer2 = new ArrayList<>(team2);
            
            // Actualizar Pokémon activos
            this.pokemonPlayer1 = team1.get(0);
            this.pokemonPlayer2 = team2.get(0);
        } else {
            System.out.println("Error: Los equipos de supervivencia deben tener exactamente " + TEAM_SIZE + " Pokémon.");
        }
    }
    
    private void displaySurvivalStatus() {
        System.out.println("=== ESTADO DE SUPERVIVENCIA ===");
        System.out.println(player1.getName() + " - Pokémon derrotados: " + pokemonDefeatedPlayer1 + "/" + TEAM_SIZE);
        System.out.println(player2.getName() + " - Pokémon derrotados: " + pokemonDefeatedPlayer2 + "/" + TEAM_SIZE);
        System.out.println("===============================");
    }
    
    @Override
    public void displayAvailableActions() {
        System.out.println("=== ACCIONES DISPONIBLES (SUPERVIVENCIA) ===");
        System.out.println("1. Atacar");
        System.out.println("2. Cambiar Pokémon");
        System.out.println("¡No se permiten objetos!");
        System.out.println("=========================================");
    }
    
    // ============ GETTERS ESPECÍFICOS ============
    
    public ArrayList<Pokemon> getTeamPlayer1() {
        return new ArrayList<>(teamPlayer1);
    }
    
    public ArrayList<Pokemon> getTeamPlayer2() {
        return new ArrayList<>(teamPlayer2);
    }
    
    public int getPokemonDefeatedPlayer1() {
        return pokemonDefeatedPlayer1;
    }
    
    public int getPokemonDefeatedPlayer2() {
        return pokemonDefeatedPlayer2;
    }
    
    public boolean areItemsAllowed() {
        return itemsAllowed;
    }
}