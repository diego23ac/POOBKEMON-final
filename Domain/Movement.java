package Domain;

public abstract class Movement {
    protected String name;
    protected int power;
    protected int accuracy; // Corregido el typo "acurracy"
    protected int pp; // Puntos de poder
    protected int currentPP; // PP actuales
    protected double sideEffect;
    protected int priority;
    protected String typeMovement;
    protected Type moveType; // Tipo del movimiento
    protected String description;
    protected String category; // Physical, Special, Status

    public Movement(String name, int power, int accuracy, int pp, double sideEffect, int priority) {
        this.name = name;
        this.power = power;
        this.accuracy = accuracy;
        this.pp = pp;
        this.currentPP = pp; // Inicialmente tiene todos los PP
        this.sideEffect = sideEffect;
        this.priority = priority;
        this.typeMovement = "";
        this.description = "";
        this.category = "Physical"; // Por defecto
    }
    
    // Constructor con tipo
    public Movement(String name, int power, int accuracy, int pp, double sideEffect, int priority, Type moveType) {
        this(name, power, accuracy, pp, sideEffect, priority);
        this.moveType = moveType;
    }

    // ============ MÉTODOS ABSTRACTOS ============
    
    /**
     * Aplica el efecto específico del movimiento
     * @param user Pokémon que usa el movimiento
     * @param target Pokémon objetivo
     * @return true si el efecto se aplicó correctamente
     */
    public abstract boolean applySpecialEffect(Pokemon user, Pokemon target);
    
    /**
     * Verifica si el movimiento puede ser usado
     * @param user Pokémon que intenta usar el movimiento
     * @return true si puede usarse
     */
    public abstract boolean canBeUsed(Pokemon user);
    
    // ============ MÉTODOS COMUNES ============
    
    /**
     * Usa el movimiento (reduce PP)
     * @return true si se usó exitosamente
     */
    public boolean useMove() {
        if (currentPP > 0) {
            currentPP--;
            return true;
        }
        return false;
    }
    
    /**
     * Restaura todos los PP del movimiento
     */
    public void restorePP() {
        currentPP = pp;
    }
    
    /**
     * Restaura PP parcialmente
     * @param amount Cantidad de PP a restaurar
     */
    public void restorePP(int amount) {
        currentPP = Math.min(pp, currentPP + amount);
    }
    
    /**
     * Verifica si el movimiento tiene PP disponibles
     * @return true si currentPP > 0
     */
    public boolean hasPP() {
        return currentPP > 0;
    }
    
    /**
     * Calcula si el movimiento acierta
     * @param user Pokémon usuario
     * @param target Pokémon objetivo
     * @return true si el movimiento acierta
     */
    public boolean doesHit(Pokemon user, Pokemon target) {
        // TODO: Considerar modificadores de precisión y evasión
        java.util.Random random = new java.util.Random();
        int roll = random.nextInt(100) + 1;
        return roll <= accuracy;
    }
    
    /**
     * Verifica si el movimiento causa un efecto secundario
     * @return true si se activa el efecto secundario
     */
    public boolean triggersSideEffect() {
        if (sideEffect <= 0) return false;
        java.util.Random random = new java.util.Random();
        return random.nextDouble() < sideEffect;
    }

    // ============ GETTERS ============
    
    public String getName() {
        return name;
    }

    public int getPower() {
        return power;
    }

    public int getAcurracy() { // Manteniendo compatibilidad
        return accuracy;
    }
    
    public int getAccuracy() {
        return accuracy;
    }

    public int getPp() {
        return pp;
    }
    
    public int getCurrentPP() {
        return currentPP;
    }

    public double getSideEffect() {
        return sideEffect;
    }

    public int getPriority() {
        return priority;
    }
    
    public String getTypeMovement() {
        return typeMovement;
    }
    
    public Type getMoveType() {
        return moveType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getCategory() {
        return category;
    }
    
    // ============ SETTERS PROTEGIDOS ============
    
    protected void setDescription(String description) {
        this.description = description;
    }
    
    protected void setMoveType(Type moveType) {
        this.moveType = moveType;
    }
    
    protected void setCategory(String category) {
        this.category = category;
    }
    
    // ============ MÉTODOS DE INFORMACIÓN ============
    
    public void displayMoveInfo() {
        System.out.println("=== " + name.toUpperCase() + " ===");
        System.out.println("Categoría: " + category);
        System.out.println("Tipo: " + (moveType != null ? moveType.getFormattedName() : "Sin tipo"));
        System.out.println("Poder: " + (power > 0 ? power : "—"));
        System.out.println("Precisión: " + accuracy + "%");
        System.out.println("PP: " + currentPP + "/" + pp);
        System.out.println("Prioridad: " + priority);
        if (sideEffect > 0) {
            System.out.println("Efecto secundario: " + (int)(sideEffect * 100) + "% de probabilidad");
        }
        if (!description.isEmpty()) {
            System.out.println("Descripción: " + description);
        }
        System.out.println("========================");
    }
    
    @Override
    public String toString() {
        return name + " (" + category + ", Poder: " + power + ", PP: " + currentPP + "/" + pp + ")";
    }
}