package Domain;

public abstract class Battle {
    protected Pokemon pokemonPlayer1;
    protected Pokemon pokemonPlayer2;
    protected Character player1;
    protected Character player2;
    protected Character winner;
    protected boolean battleEnded;
    protected int currentTurn; // 1 para player1, 2 para player2
    
    public Battle(Pokemon pokemonPlayer1, Pokemon pokemonPlayer2, Character player1, Character player2) {
        this.pokemonPlayer1 = pokemonPlayer1;
        this.pokemonPlayer2 = pokemonPlayer2;
        this.player1 = player1;
        this.player2 = player2;
        this.battleEnded = false;
        this.currentTurn = 1; // Comienza player1
    }
    
    // Métodos abstractos que cada tipo de batalla debe implementar
    public abstract void startBattle();
    public abstract boolean checkWinCondition();
    public abstract void executeAttack(Movement move);
    public abstract void useItem(Item item);
    public abstract void switchPokemon(Pokemon newPokemon);
    
    // Métodos comunes para todas las batallas
    public void switchTurn() {
        currentTurn = currentTurn == 1 ? 2 : 1;
    }
    
    public Character getCurrentPlayer() {
        return currentTurn == 1 ? player1 : player2;
    }
    
    public Pokemon getCurrentPokemon() {
        return currentTurn == 1 ? pokemonPlayer1 : pokemonPlayer2;
    }
    
    public Pokemon getOpponentPokemon() {
        return currentTurn == 1 ? pokemonPlayer2 : pokemonPlayer1;
    }
    
    // Getters y Setters
    public Character getWinner() { return winner; }
    public boolean isBattleEnded() { return battleEnded; }
    public int getCurrentTurn() { return currentTurn; }
}