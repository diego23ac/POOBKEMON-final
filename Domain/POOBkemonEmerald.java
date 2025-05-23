package Domain;

import java.util.*;
import java.io.*;
import java.nio.file.*;

public class POOBkemonEmerald {
    private Character player1;
    private Character player2;
    private ArrayList<Pokemon> teamPlayer1;
    private ArrayList<Pokemon> teamPlayer2;
    private ArrayList<Item> bagPlayer1;
    private ArrayList<Item> bagPlayer2;
    private Battle currentBattle;
    private boolean gameStarted;
    
    // Rutas de archivos
    private static final String DATA_FOLDER = "data/";
    private static final String SAVES_FOLDER = "saves/";
    private static final String POKEMON_DATA_FILE = DATA_FOLDER + "pokemon.txt";
    private static final String MOVES_DATA_FILE = DATA_FOLDER + "moves.txt";
    private static final String ITEMS_DATA_FILE = DATA_FOLDER + "items.txt";

    public POOBkemonEmerald(Character player1, Character player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.teamPlayer1 = new ArrayList<Pokemon>();
        this.teamPlayer2 = new ArrayList<Pokemon>();
        this.bagPlayer1 = new ArrayList<Item>();
        this.bagPlayer2 = new ArrayList<Item>();
        this.currentBattle = null;
        this.gameStarted = false;
        
        // Crear carpetas si no existen
        createDirectories();
        // Cargar datos del juego
        loadGameData();
    }
    
    // ============ MÉTODOS DE CONTROL DEL JUEGO ============
    
    public void startGame() {
        if (gameStarted) {
            System.out.println("El juego ya ha comenzado.");
            return;
        }
        
        System.out.println("¡Bienvenidos al mundo POOBkemon!");
        System.out.println("Jugador 1: " + player1.getName());
        System.out.println("Jugador 2: " + player2.getName());
        
        // Verificar que ambos jugadores tengan al menos 1 Pokémon
        if (teamPlayer1.isEmpty() || teamPlayer2.isEmpty()) {
            System.out.println("Error: Ambos jugadores deben tener al menos un Pokémon para comenzar.");
            return;
        }
        
        gameStarted = true;
        System.out.println("¡El juego ha comenzado! Pueden crear batallas ahora.");
    }
    
    public void createNormalBattle() {
        if (!gameStarted) {
            System.out.println("Debe iniciar el juego primero.");
            return;
        }
        
        if (currentBattle != null && !currentBattle.isBattleEnded()) {
            System.out.println("Ya hay una batalla en curso. Termine la batalla actual antes de crear una nueva.");
            return;
        }
        
        // Verificar que ambos jugadores tengan Pokémon disponibles
        if (teamPlayer1.isEmpty() || teamPlayer2.isEmpty()) {
            System.out.println("Ambos jugadores necesitan Pokémon para batallar.");
            return;
        }
        
        // Crear batalla normal con el primer Pokémon de cada equipo
        Pokemon pokemon1 = getFirstAvailablePokemon(teamPlayer1);
        Pokemon pokemon2 = getFirstAvailablePokemon(teamPlayer2);
        
        if (pokemon1 == null || pokemon2 == null) {
            System.out.println("No hay Pokémon disponibles para la batalla.");
            return;
        }
        
        currentBattle = new NormalBattle(pokemon1, pokemon2, player1, player2);
        currentBattle.startBattle();
        System.out.println("Batalla normal creada exitosamente.");
    }
    
    public void createSurvivalBattle() {
        if (!gameStarted) {
            System.out.println("Debe iniciar el juego primero.");
            return;
        }
        
        if (currentBattle != null && !currentBattle.isBattleEnded()) {
            System.out.println("Ya hay una batalla en curso. Termine la batalla actual antes de crear una nueva.");
            return;
        }
        
        // Verificar que ambos jugadores sean Trainer (solo jugador vs jugador)
        if (!(player1 instanceof Trainer) || !(player2 instanceof Trainer)) {
            System.out.println("La batalla de supervivencia solo está disponible para jugador vs jugador.");
            return;
        }
        
        // Generar 6 Pokémon aleatorios para cada jugador
        ArrayList<Pokemon> randomTeam1 = generateRandomTeam(6);
        ArrayList<Pokemon> randomTeam2 = generateRandomTeam(6);
        
        if (randomTeam1.isEmpty() || randomTeam2.isEmpty()) {
            System.out.println("Error generando equipos aleatorios.");
            return;
        }
        
        // Reemplazar equipos temporalmente para la batalla de supervivencia
        ArrayList<Pokemon> originalTeam1 = new ArrayList<>(teamPlayer1);
        ArrayList<Pokemon> originalTeam2 = new ArrayList<>(teamPlayer2);
        
        teamPlayer1 = randomTeam1;
        teamPlayer2 = randomTeam2;
        
        // Crear batalla de supervivencia
        currentBattle = new SurvivalBattle(randomTeam1.get(0), randomTeam2.get(0), player1, player2);
        currentBattle.startBattle();
        System.out.println("Batalla de supervivencia creada exitosamente.");
    }
    
    public Battle getCurrentBattle() {
        return currentBattle;
    }
    
    public void endCurrentBattle() {
        if (currentBattle == null) {
            System.out.println("No hay batalla activa.");
            return;
        }
        
        if (!currentBattle.isBattleEnded()) {
            System.out.println("La batalla aún no ha terminado.");
            return;
        }
        
        Character winner = currentBattle.getWinner();
        if (winner != null) {
            System.out.println("¡" + winner.getName() + " ha ganado la batalla!");
        } else {
            System.out.println("La batalla ha terminado sin ganador.");
        }
        
        currentBattle = null;
        System.out.println("Batalla finalizada.");
    }
    
    // ============ MÉTODOS DE DELEGACIÓN A BATALLA ============
    
    public void executeBattleTurn(String actionType, Object actionData) {
        if (currentBattle == null || currentBattle.isBattleEnded()) {
            System.out.println("No hay batalla activa.");
            return;
        }
        
        switch (actionType.toLowerCase()) {
            case "attack":
                if (actionData instanceof Movement) {
                    currentBattle.executeAttack((Movement) actionData);
                } else {
                    System.out.println("Datos de ataque inválidos.");
                }
                break;
            case "item":
                if (actionData instanceof Item) {
                    currentBattle.useItem((Item) actionData);
                } else {
                    System.out.println("Datos de objeto inválidos.");
                }
                break;
            case "switch":
                if (actionData instanceof Pokemon) {
                    currentBattle.switchPokemon((Pokemon) actionData);
                } else {
                    System.out.println("Datos de Pokémon inválidos.");
                }
                break;
            default:
                System.out.println("Acción no reconocida: " + actionType);
        }
        
        // Verificar si la batalla ha terminado después del turno
        if (currentBattle.checkWinCondition()) {
            endCurrentBattle();
        }
    }
    
    public void selectPokemon(int playerNumber, Pokemon pokemon) {
        if (currentBattle == null) {
            System.out.println("No hay batalla activa.");
            return;
        }
        
        ArrayList<Pokemon> team = (playerNumber == 1) ? teamPlayer1 : teamPlayer2;
        
        if (!team.contains(pokemon)) {
            System.out.println("El Pokémon seleccionado no pertenece al equipo del jugador.");
            return;
        }
        
        if (pokemon.getPs() <= 0) {
            System.out.println("No puedes seleccionar un Pokémon debilitado.");
            return;
        }
        
        executeBattleTurn("switch", pokemon);
    }
    
    public void selectItem(int playerNumber, Item item) {
        if (currentBattle == null) {
            System.out.println("No hay batalla activa.");
            return;
        }
        
        ArrayList<Item> bag = (playerNumber == 1) ? bagPlayer1 : bagPlayer2;
        
        if (!bag.contains(item)) {
            System.out.println("El objeto seleccionado no está disponible.");
            return;
        }
        
        executeBattleTurn("item", item);
    }
    
    // ============ MÉTODOS AUXILIARES ============
    
    private Pokemon getFirstAvailablePokemon(ArrayList<Pokemon> team) {
        for (Pokemon pokemon : team) {
            if (pokemon.getPs() > 0) {
                return pokemon;
            }
        }
        return null;
    }
    
    private ArrayList<Pokemon> generateRandomTeam(int size) {
        ArrayList<Pokemon> randomTeam = new ArrayList<>();
        // TODO: Implementar generación de Pokémon aleatorios desde los datos cargados
        // Por ahora retorna lista vacía hasta que tengamos los datos de Pokémon
        return randomTeam;
    }
    
    // Getters para acceso controlado
    public boolean isGameStarted() { return gameStarted; }
    public Character getPlayer1() { return player1; }
    public Character getPlayer2() { return player2; }
    public ArrayList<Pokemon> getTeamPlayer1() { return new ArrayList<>(teamPlayer1); }
    public ArrayList<Pokemon> getTeamPlayer2() { return new ArrayList<>(teamPlayer2); }
    public ArrayList<Item> getBagPlayer1() { return new ArrayList<>(bagPlayer1); }
    public ArrayList<Item> getBagPlayer2() { return new ArrayList<>(bagPlayer2); }
    
    // ============ RESTO DE MÉTODOS EXISTENTES ============
    // (Todos los métodos de archivos que ya teníamos permanecen igual)
    
    private void createDirectories() {
        try {
            Files.createDirectories(Paths.get(DATA_FOLDER));
            Files.createDirectories(Paths.get(SAVES_FOLDER));
        } catch (IOException e) {
            System.err.println("Error creando directorios: " + e.getMessage());
        }
    }
    
    private void loadGameData() {
        try {
            loadPokemonData();
            loadMovesData();
            loadItemsData();
            System.out.println("Datos del juego cargados correctamente.");
        } catch (IOException e) {
            System.err.println("Error cargando datos del juego: " + e.getMessage());
        }
    }
    
    private void loadPokemonData() throws IOException {
        List<String> lines = readFile(POKEMON_DATA_FILE);
        for (String line : lines) {
            if (!line.trim().isEmpty() && !line.startsWith("#")) {
                parsePokemonLine(line);
            }
        }
    }
    
    private void loadMovesData() throws IOException {
        List<String> lines = readFile(MOVES_DATA_FILE);
        for (String line : lines) {
            if (!line.trim().isEmpty() && !line.startsWith("#")) {
                parseMoveeLine(line);
            }
        }
    }
    
    private void loadItemsData() throws IOException {
        List<String> lines = readFile(ITEMS_DATA_FILE);
        for (String line : lines) {
            if (!line.trim().isEmpty() && !line.startsWith("#")) {
                parseItemLine(line);
            }
        }
    }
    
    // [Resto de métodos de archivos permanecen igual...]
    
    public static void main(String[] args) throws Exception {
        
    }
}