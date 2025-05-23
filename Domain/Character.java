package Domain;
import java.awt.*;
import java.util.*;

public abstract class Character {
    protected String name;
    protected Image image;
    protected String description;
    protected ArrayList<Item> items;
    protected ArrayList<Pokemon> team;
    protected int maxTeamSize;
    protected int maxItemCount;

    public Character(String name, Image image, String description) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.items = new ArrayList<Item>();
        this.team = new ArrayList<Pokemon>();
        this.maxTeamSize = 6; // Máximo estándar de Pokémon
        this.maxItemCount = 10; // Máximo de objetos por defecto
    }

    // ============ MÉTODOS ABSTRACTOS ============
    // Cada tipo de personaje implementará estos según su comportamiento
    
    public abstract void makeDecision(Battle battle);
    public abstract Pokemon choosePokemon(ArrayList<Pokemon> availablePokemon);
    public abstract Movement chooseMove(Pokemon pokemon);
    public abstract Item chooseItem(ArrayList<Item> availableItems);
    
    // ============ MÉTODOS DE GESTIÓN DE EQUIPO ============
    
    public boolean addPokemon(Pokemon pokemon) {
        if (team.size() < maxTeamSize && pokemon != null) {
            team.add(pokemon);
            System.out.println(name + " ha agregado a " + pokemon.getName() + " a su equipo.");
            return true;
        } else if (team.size() >= maxTeamSize) {
            System.out.println("El equipo de " + name + " está lleno.");
            return false;
        }
        return false;
    }
    
    public boolean removePokemon(Pokemon pokemon) {
        if (team.contains(pokemon)) {
            team.remove(pokemon);
            System.out.println(name + " ha removido a " + pokemon.getName() + " de su equipo.");
            return true;
        }
        System.out.println(pokemon.getName() + " no está en el equipo de " + name + ".");
        return false;
    }
    
    public Pokemon getFirstAvailablePokemon() {
        for (Pokemon pokemon : team) {
            if (pokemon.getPs() > 0) {
                return pokemon;
            }
        }
        return null;
    }
    
    public ArrayList<Pokemon> getAvailablePokemon() {
        ArrayList<Pokemon> available = new ArrayList<>();
        for (Pokemon pokemon : team) {
            if (pokemon.getPs() > 0) {
                available.add(pokemon);
            }
        }
        return available;
    }
    
    public boolean hasAvailablePokemon() {
        return !getAvailablePokemon().isEmpty();
    }
    
    // ============ MÉTODOS DE GESTIÓN DE OBJETOS ============
    
    public boolean addItem(Item item) {
        if (items.size() < maxItemCount && item != null) {
            items.add(item);
            System.out.println(name + " ha obtenido " + item.getClass().getSimpleName() + ".");
            return true;
        } else if (items.size() >= maxItemCount) {
            System.out.println("La bolsa de " + name + " está llena.");
            return false;
        }
        return false;
    }
    
    public boolean removeItem(Item item) {
        if (items.contains(item)) {
            items.remove(item);
            return true;
        }
        return false;
    }
    
    public boolean useItem(Item item, Pokemon targetPokemon) {
        if (items.contains(item)) {
            // TODO: Implementar cuando tengamos el método en Item
            // item.applyEffect(targetPokemon);
            items.remove(item);
            System.out.println(name + " usa " + item.getClass().getSimpleName() + " en " + targetPokemon.getName());
            return true;
        }
        System.out.println(name + " no tiene ese objeto.");
        return false;
    }
    
    public ArrayList<Item> getAvailableItems() {
        return new ArrayList<>(items);
    }
    
    // ============ MÉTODOS DE INFORMACIÓN ============
    
    public void displayTeamStatus() {
        System.out.println("=== EQUIPO DE " + name.toUpperCase() + " ===");
        if (team.isEmpty()) {
            System.out.println("No tiene Pokémon en su equipo.");
        } else {
            for (int i = 0; i < team.size(); i++) {
                Pokemon pokemon = team.get(i);
                String status = (pokemon.getPs() > 0) ? "Disponible" : "Debilitado";
                System.out.println((i + 1) + ". " + pokemon.getName() + " (HP: " + pokemon.getPs() + ") - " + status);
            }
        }
        System.out.println("=====================================");
    }
    
    public void displayItems() {
        System.out.println("=== OBJETOS DE " + name.toUpperCase() + " ===");
        if (items.isEmpty()) {
            System.out.println("No tiene objetos.");
        } else {
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                System.out.println((i + 1) + ". " + item.getClass().getSimpleName());
            }
        }
        System.out.println("=====================================");
    }
    
    // ============ GETTERS Y SETTERS ============
    
    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }
    
    public ArrayList<Pokemon> getTeam() {
        return new ArrayList<>(team);
    }
    
    public ArrayList<Item> getItems() {
        return new ArrayList<>(items);
    }
    
    public int getTeamSize() {
        return team.size();
    }
    
    public int getItemCount() {
        return items.size();
    }
    
    public int getMaxTeamSize() {
        return maxTeamSize;
    }
    
    public int getMaxItemCount() {
        return maxItemCount;
    }
    
    protected void setMaxTeamSize(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }
    
    protected void setMaxItemCount(int maxItemCount) {
        this.maxItemCount = maxItemCount;
    }
}