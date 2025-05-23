package Domain;

public abstract class Item {
    protected String name;
    protected String description;
    protected String effectType;
    protected int effectValue;
    protected boolean consumable;

    public Item(String name) {
        this.name = name;
        this.description = "";
        this.effectType = "";
        this.effectValue = 0;
        this.consumable = true; // La mayoría de objetos se consumen al usarse
    }
    
    public Item(String name, String description, String effectType, int effectValue) {
        this.name = name;
        this.description = description;
        this.effectType = effectType;
        this.effectValue = effectValue;
        this.consumable = true;
    }

    // ============ MÉTODOS ABSTRACTOS ============
    
    /**
     * Aplica el efecto del objeto al Pokémon objetivo
     * @param targetPokemon Pokémon que recibe el efecto
     * @return true si se aplicó correctamente
     */
    public abstract boolean applyEffect(Pokemon targetPokemon);
    
    /**
     * Usa el objeto en un Pokémon
     * @param targetPokemon Pokémon objetivo
     * @return true si se usó correctamente
     */
    public abstract boolean use(Pokemon targetPokemon);
    
    // ============ MÉTODOS COMUNES ============
    
    /**
     * Verifica si el objeto puede ser usado en el Pokémon dado
     * @param targetPokemon Pokémon objetivo
     * @return true si puede usarse
     */
    public boolean canUseOn(Pokemon targetPokemon) {
        if (targetPokemon == null) {
            return false;
        }
        
        // Verificaciones básicas que implementarán las subclases
        return true;
    }
    
    /**
     * Obtiene una descripción del efecto del objeto
     * @return Descripción del efecto
     */
    public String getEffectDescription() {
        return description.isEmpty() ? "Objeto sin descripción." : description;
    }
    
    // ============ GETTERS Y SETTERS ============
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getEffectType() {
        return effectType;
    }
    
    public int getEffectValue() {
        return effectValue;
    }
    
    public boolean isConsumable() {
        return consumable;
    }
    
    protected void setDescription(String description) {
        this.description = description;
    }
    
    protected void setEffectType(String effectType) {
        this.effectType = effectType;
    }
    
    protected void setEffectValue(int effectValue) {
        this.effectValue = effectValue;
    }
    
    protected void setConsumable(boolean consumable) {
        this.consumable = consumable;
    }
    
    @Override
    public String toString() {
        return name + ": " + getEffectDescription();
    }
}