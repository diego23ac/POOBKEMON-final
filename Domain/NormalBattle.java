package Domain;

import java.util.ArrayList;

public class NormalBattle extends Battle {
    private ArrayList<Pokemon> teamPlayer1;
    private ArrayList<Pokemon> teamPlayer2;
    private ArrayList<Item> bagPlayer1;
    private ArrayList<Item> bagPlayer2;
    private boolean canUseItems;
    private int maxPokemonPerTeam;
    private int maxItemsPerPlayer;

    public NormalBattle(Pokemon pokemonPlayer1, Pokemon pokemonPlayer2, Character player1, Character player2) {
        super(pokemonPlayer1, pokemonPlayer2, player1, player2);
        
        // Configuración específica de batalla normal
        this.canUseItems = true;
        this.maxPokemonPerTeam = 6;
        this.maxItemsPerPlayer = 3;
        
        // Inicializar equipos y bolsas (se llenarán desde POOBkemonEmerald)
        this.teamPlayer1 = new ArrayList<Pokemon>();
        this.teamPlayer2 = new ArrayList<Pokemon>();
        this.bagPlayer1 = new ArrayList<Item>();
        this.bagPlayer2 = new ArrayList<Item>();
        
        // Agregar los Pokémon iniciales a los equipos
        teamPlayer1.add(pokemonPlayer1);
        teamPlayer2.add(pokemonPlayer2);
    }

    @Override
    public void startBattle() {
        System.out.println("=== INICIANDO BATALLA NORMAL ===");
        System.out.println(player1.getName() + " vs " + player2.getName());
        System.out.println(player1.getName() + " envía a " + pokemonPlayer1.getName() + "!");
        System.out.println(player2.getName() + " envía a " + pokemonPlayer2.getName() + "!");
        System.out.println("¡Que comience la batalla!");
        
        displayBattleStatus();
        displayAvailableActions();
    }

    @Override
    public boolean checkWinCondition() {
        // En batalla normal, gana el jugador que deje sin Pokémon disponibles al oponente
        
        boolean player1HasAvailablePokemon = false;
        boolean player2HasAvailablePokemon = false;
        
        // Verificar si el jugador 1 tiene Pokémon disponibles
        for (Pokemon pokemon : teamPlayer1) {
            if (isPokemonAvailable(pokemon)) {
                player1HasAvailablePokemon = true;
                break;
            }
        }
        
        // Verificar si el jugador 2 tiene Pokémon disponibles
        for (Pokemon pokemon : teamPlayer2) {
            if (isPokemonAvailable(pokemon)) {
                player2HasAvailablePokemon = true;
                break;
            }
        }
        
        // Determinar ganador
        if (!player1HasAvailablePokemon && !player2HasAvailablePokemon) {
            // Empate (muy raro, pero posible)
            endBattle(null);
            return true;
        } else if (!player1HasAvailablePokemon) {
            // Gana jugador 2
            endBattle(player2);
            return true;
        } else if (!player2HasAvailablePokemon) {
            // Gana jugador 1
            endBattle(player1);
            return true;
        }
        
        // La batalla continúa
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
            handleFaintedPokemon();
        }
        
        // Cambiar turno si la batalla continúa
        if (!battleEnded) {
            switchTurn();
        }
    }

    @Override
    public void useItem(Item item) {
        if (!canUseItems) {
            System.out.println("No se pueden usar objetos en este momento.");
            return;
        }
        
        Character currentPlayer = getCurrentPlayer();
        ArrayList<Item> currentBag = (currentTurn == 1) ? bagPlayer1 : bagPlayer2;
        
        // Verificar si el jugador tiene el objeto
        if (!currentBag.contains(item)) {
            System.out.println(currentPlayer.getName() + " no tiene " + item.getClass().getSimpleName());
            return;
        }
        
        // Aplicar el efecto del objeto
        Pokemon targetPokemon = getCurrentPokemon();
        applyItemEffect(item, targetPokemon);
        
        // Remover el objeto de la bolsa (se consume)
        currentBag.remove(item);
        
        // En batalla normal, usar un objeto consume el turno
        if (!battleEnded) {
            switchTurn();
        }
    }

    @Override
    public void switchPokemon(Pokemon newPokemon) {
        Character currentPlayer = getCurrentPlayer();
        ArrayList<Pokemon> currentTeam = (currentTurn == 1) ? teamPlayer1 : teamPlayer2;
        Pokemon currentPokemon = getCurrentPokemon();
        
        // Verificar que el nuevo Pokémon esté en el equipo
        if (!currentTeam.contains(newPokemon)) {
            System.out.println("Ese Pokémon no está en tu equipo.");
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
    
    // ============ MÉTODOS ESPECÍFICOS DE BATALLA NORMAL ============
    
    private void handleFaintedPokemon() {
        Character playerWithFaintedPokemon = getOpponentPlayer();
        ArrayList<Pokemon> teamWithFaintedPokemon = (currentTurn == 1) ? teamPlayer2 : teamPlayer1;
        
        System.out.println(getOpponentPokemon().getName() + " se ha debilitado!");
        
        // Verificar si hay más Pokémon disponibles
        Pokemon nextPokemon = getNextAvailablePokemon(teamWithFaintedPokemon);
        
        if (nextPokemon != null) {
            // Forzar cambio de Pokémon
            int opponentPlayerNumber = (currentTurn == 1) ? 2 : 1;
            updateActivePokemon(nextPokemon, opponentPlayerNumber);
            System.out.println(playerWithFaintedPokemon.getName() + " debe enviar a su siguiente Pokémon.");
        }
        // Si no hay más Pokémon, checkWinCondition() detectará la victoria
    }
    
    private Pokemon getNextAvailablePokemon(ArrayList<Pokemon> team) {
        for (Pokemon pokemon : team) {
            if (isPokemonAvailable(pokemon)) {
                return pokemon;
            }
        }
        return null;
    }
    
    public void addPokemonToTeam(Pokemon pokemon, int playerNumber) {
        if (playerNumber == 1 && teamPlayer1.size() < maxPokemonPerTeam) {
            teamPlayer1.add(pokemon);
        } else if (playerNumber == 2 && teamPlayer2.size() < maxPokemonPerTeam) {
            teamPlayer2.add(pokemon);
        } else {
            System.out.println("No se puede agregar más Pokémon al equipo.");
        }
    }
    
    public void addItemToBag(Item item, int playerNumber) {
        if (playerNumber == 1 && bagPlayer1.size() < maxItemsPerPlayer) {
            bagPlayer1.add(item);
        } else if (playerNumber == 2 && bagPlayer2.size() < maxItemsPerPlayer) {
            bagPlayer2.add(item);
        } else {
            System.out.println("No se pueden agregar más objetos a la bolsa.");
        }
    }
    
    // ============ GETTERS ESPECÍFICOS ============
    
    public ArrayList<Pokemon> getTeamPlayer1() {
        return new ArrayList<>(teamPlayer1);
    }
    
    public ArrayList<Pokemon> getTeamPlayer2() {
        return new ArrayList<>(teamPlayer2);
    }
    
    public ArrayList<Item> getBagPlayer1() {
        return new ArrayList<>(bagPlayer1);
    }
    
    public ArrayList<Item> getBagPlayer2() {
        return new ArrayList<>(bagPlayer2);
    }
    
    public boolean canUseItems() {
        return canUseItems;
    }
}