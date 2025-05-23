package Domain;
import java.awt.*;
import java.util.*;

/**
 * Pokemon class
 * 
 * @author Corso Diego
 * @author Duran Roger
 * @version 1.0
 */
public class Pokemon {
    private String name;
    private Image image;
    private String description;
    private int ps; // HP actual
    private int maxPs; // HP máximo
    private int attack;
    private int defence;
    private int speed;
    private int specialAttack;
    private int specialDefence;
    private Type primaryType;
    private Type secondaryType;
    private ArrayList<Movement> moves;
    private HashMap<String, Integer> stats;
    private HashMap<String, Integer> statModifiers; // Para modificadores temporales (+1, -1, etc.)
    private ArrayList<String> statusEffects; // Para estados como paralizado, quemado, etc.
    private int level;

    /**
     * Pokemon constructor
     * @param name name of Pokémon
     * @param description description of Pokémon
     * @param ps life points of Pokémon
     * @param attack attack points of Pokémon
     * @param defence defence points of Pokémon
     * @param speed speed points of Pokémon
     * @param specialAttack special attack points of Pokémon
     * @param specialDefence special defence points of Pokémon
     * @param primaryType First natural type of Pokémon
     * @param secondaryType Second natural type of Pokémon
     */
    public Pokemon(String name, String description, int ps, int attack, int defence, int speed, int specialAttack, int specialDefence, Type primaryType, Type secondaryType) {
        this.name = name;
        this.description = description;
        this.ps = ps;
        this.maxPs = ps; // HP máximo es igual al inicial
        this.attack = attack;
        this.defence = defence;
        this.speed = speed;
        this.specialAttack = specialAttack;
        this.specialDefence = specialDefence;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
        this.moves = new ArrayList<Movement>();
        this.stats = saveStats(ps, attack, defence, speed, specialAttack, specialDefence);
        this.statModifiers = new HashMap<String, Integer>();
        this.statusEffects = new ArrayList<String>();
        this.level = 50; // Nivel por defecto
        
        initializeStatModifiers();
    }
    
    /**
     * Constructor con nivel personalizado
     */
    public Pokemon(String name, String description, int ps, int attack, int defence, int speed, int specialAttack, int specialDefence, Type primaryType, Type secondaryType, int level) {
        this(name, description, ps, attack, defence, speed, specialAttack, specialDefence, primaryType, secondaryType);
        this.level = level;
        
        // Ajustar estadísticas según el nivel
        adjustStatsForLevel();
    }

    /**
     * Create a backup of stats
     * @param ps
     * @param attack
     * @param defence
     * @param speed
     * @param specialAttack
     * @param specialDefence
     * @return HashMap with stats of Pokémon
     */
    private HashMap<String, Integer> saveStats(int ps, int attack, int defence, int speed, int specialAttack, int specialDefence) {
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("ps", ps);
        stats.put("attack", attack);
        stats.put("defence", defence);
        stats.put("speed", speed);
        stats.put("specialAttack", specialAttack);
        stats.put("specialDefence", specialDefence);
        return stats;
    }
    
    private void initializeStatModifiers() {
        statModifiers.put("attack", 0);
        statModifiers.put("defence", 0);
        statModifiers.put("specialAttack", 0);
        statModifiers.put("specialDefence", 0);
        statModifiers.put("speed", 0);
        statModifiers.put("accuracy", 0);
        statModifiers.put("evasion", 0);
    }
    
    private void adjustStatsForLevel() {
        // Ajuste simple basado en nivel (nivel 50 como base)
        double levelMultiplier = level / 50.0;
        
        this.ps = (int) (this.ps * levelMultiplier);
        this.maxPs = this.ps;
        this.attack = (int) (this.attack * levelMultiplier);
        this.defence = (int) (this.defence * levelMultiplier);
        this.speed = (int) (this.speed * levelMultiplier);
        this.specialAttack = (int) (this.specialAttack * levelMultiplier);
        this.specialDefence = (int) (this.specialDefence * levelMultiplier);
        
        // Actualizar backup de stats
        this.stats = saveStats(maxPs, attack, defence, speed, specialAttack, specialDefence);
    }

    // ============ MÉTODOS DE COMBATE ============
    
    /**
     * Calcula el daño que este Pokémon causaría con un movimiento específico
     * @param move Movimiento a usar
     * @param defender Pokémon defensor
     * @return Cantidad de daño a causar
     */
    public int calculateDamage(Movement move, Pokemon defender) {
        if (move == null || defender == null) {
            return 0;
        }
        
        // Verificar si el movimiento causa daño
        if (move.getPower() <= 0) {
            return 0; // Movimientos de estado no causan daño directo
        }
        
        // Fórmula básica de daño de Pokémon (simplificada)
        double damage = 0;
        
        // Determinar ataque y defensa a usar
        int attackStat;
        int defenseStat;
        
        if (move instanceof PhysicalMovement) {
            attackStat = getModifiedAttack();
            defenseStat = defender.getModifiedDefence();
        } else if (move instanceof SpecialMovement) {
            attackStat = getModifiedSpecialAttack();
            defenseStat = defender.getModifiedSpecialDefence();
        } else {
            // StateMovement - no causa daño directo
            return 0;
        }
        
        // Fórmula de daño base
        damage = ((2.0 * level + 10) / 250.0) * (attackStat / (double) defenseStat) * move.getPower() + 2;
        
        // Aplicar efectividad de tipo
        double typeEffectiveness = calculateTypeEffectiveness(move, defender);
        damage *= typeEffectiveness;
        
        // STAB (Same Type Attack Bonus) - bonificación si el tipo del movimiento coincide con el del Pokémon
        if (isSTAB(move)) {
            damage *= 1.5;
        }
        
        // Factor aleatorio (entre 85% y 100%)
        Random random = new Random();
        double randomFactor = 0.85 + (random.nextDouble() * 0.15);
        damage *= randomFactor;
        
        // Efectos de estado que afectan el daño
        if (hasStatusEffect("burn") && move instanceof PhysicalMovement) {
            damage *= 0.5; // Quemadura reduce ataques físicos
        }
        
        return Math.max(1, (int) damage); // Mínimo 1 de daño
    }
    
    /**
     * Recibe daño y actualiza HP
     * @param damage Cantidad de daño a recibir
     */
    public void takeDamage(int damage) {
        int finalDamage = Math.max(0, damage);
        this.ps = Math.max(0, this.ps - finalDamage);
        
        System.out.println(name + " recibe " + finalDamage + " puntos de daño!");
        
        if (this.ps <= 0) {
            System.out.println(name + " se ha debilitado!");
            // Limpiar efectos de estado al debilitarse
            statusEffects.clear();
        }
    }
    
    /**
     * Restaura HP
     * @param healAmount Cantidad de HP a restaurar
     */
    public void heal(int healAmount) {
        int oldHp = this.ps;
        this.ps = Math.min(maxPs, this.ps + healAmount);
        int actualHeal = this.ps - oldHp;
        
        if (actualHeal > 0) {
            System.out.println(name + " recupera " + actualHeal + " puntos de HP!");
        } else {
            System.out.println(name + " ya tiene el HP al máximo!");
        }
    }
    
    /**
     * Revive al Pokémon con HP parcial
     * @param hpPercentage Porcentaje de HP a restaurar (0.0 a 1.0)
     */
    public void revive(double hpPercentage) {
        if (ps > 0) {
            System.out.println(name + " no necesita ser revivido!");
            return;
        }
        
        hpPercentage = Math.max(0.0, Math.min(1.0, hpPercentage));
        this.ps = (int) (maxPs * hpPercentage);
        
        // Limpiar efectos de estado al revivir
        statusEffects.clear();
        
        System.out.println(name + " ha sido revivido con " + ps + " HP!");
    }
    
    // ============ MÉTODOS DE EFECTIVIDAD Y TIPOS ============
    
    private double calculateTypeEffectiveness(Movement move, Pokemon defender) {
        // Para movimientos especiales, podemos obtener el tipo
        if (move instanceof SpecialMovement) {
            // TODO: Cuando implementemos el getter de tipo en SpecialMovement
            // Type moveType = ((SpecialMovement) move).getType();
            // return calculateTypeMatchup(moveType, defender);
        }
        
        // Por ahora, usar el tipo primario del atacante como aproximación
        return calculateTypeMatchup(this.primaryType, defender);
    }
    
    private double calculateTypeMatchup(Type attackType, Pokemon defender) {
        double effectiveness = attackType.calculateMultiplier(defender.getPrimaryType());
        
        if (defender.getSecondaryType() != null) {
            effectiveness *= attackType.calculateMultiplier(defender.getSecondaryType());
        }
        
        return effectiveness;
    }
    
    private boolean isSTAB(Movement move) {
        // STAB: Same Type Attack Bonus
        // TODO: Implementar cuando podamos obtener el tipo del movimiento
        // Por ahora, asumir que los movimientos físicos corresponden al tipo primario
        return true; // Simplificación temporal
    }
    
    // ============ MÉTODOS DE ESTADÍSTICAS MODIFICADAS ============
    
    public int getModifiedAttack() {
        return applyStatModifier(attack, statModifiers.get("attack"));
    }
    
    public int getModifiedDefence() {
        return applyStatModifier(defence, statModifiers.get("defence"));
    }
    
    public int getModifiedSpecialAttack() {
        return applyStatModifier(specialAttack, statModifiers.get("specialAttack"));
    }
    
    public int getModifiedSpecialDefence() {
        return applyStatModifier(specialDefence, statModifiers.get("specialDefence"));
    }
    
    public int getModifiedSpeed() {
        return applyStatModifier(speed, statModifiers.get("speed"));
    }
    
    private int applyStatModifier(int baseStat, int modifier) {
        // Modificadores van de -6 a +6
        modifier = Math.max(-6, Math.min(6, modifier));
        
        double multiplier;
        if (modifier >= 0) {
            multiplier = (2.0 + modifier) / 2.0;
        } else {
            multiplier = 2.0 / (2.0 - modifier);
        }
        
        return (int) (baseStat * multiplier);
    }
    
    // ============ MÉTODOS DE MODIFICADORES Y ESTADOS ============
    
    public void modifyStat(String stat, int change) {
        if (statModifiers.containsKey(stat)) {
            int currentMod = statModifiers.get(stat);
            int newMod = Math.max(-6, Math.min(6, currentMod + change));
            statModifiers.put(stat, newMod);
            
            String changeText = change > 0 ? "aumenta" : "disminuye";
            String intensity = Math.abs(change) > 1 ? " mucho" : "";
            System.out.println("¡El " + stat + " de " + name + " " + changeText + intensity + "!");
        }
    }
    
    public void addStatusEffect(String effect) {
        if (!statusEffects.contains(effect)) {
            statusEffects.add(effect);
            System.out.println(name + " está " + effect + "!");
        }
    }
    
    public void removeStatusEffect(String effect) {
        if (statusEffects.remove(effect)) {
            System.out.println(name + " se ha curado de " + effect + "!");
        }
    }
    
    public boolean hasStatusEffect(String effect) {
        return statusEffects.contains(effect);
    }
    
    public void clearStatModifiers() {
        initializeStatModifiers();
        System.out.println("¡Los cambios de estadísticas de " + name + " vuelven a la normalidad!");
    }
    
    public void clearStatusEffects() {
        statusEffects.clear();
        System.out.println("¡Los efectos de estado de " + name + " han desaparecido!");
    }
    
    // ============ MÉTODOS DE MOVIMIENTOS ============
    
    public boolean addMove(Movement move) {
        if (moves.size() < 4 && move != null) {
            moves.add(move);
            System.out.println(name + " ha aprendido " + move.getName() + "!");
            return true;
        } else if (moves.size() >= 4) {
            System.out.println(name + " ya conoce 4 movimientos. ¿Desea reemplazar alguno?");
            return false;
        }
        return false;
    }
    
    public boolean replaceMove(int index, Movement newMove) {
        if (index >= 0 && index < moves.size() && newMove != null) {
            Movement oldMove = moves.get(index);
            moves.set(index, newMove);
            System.out.println(name + " olvida " + oldMove.getName() + " y aprende " + newMove.getName() + "!");
            return true;
        }
        return false;
    }
    
    public boolean canUseMove(Movement move) {
        if (!moves.contains(move)) {
            return false;
        }
        
        // Verificar PP (cuando se implemente)
        // if (move.getCurrentPP() <= 0) return false;
        
        // Verificar estados que impiden usar movimientos
        if (hasStatusEffect("sleep") || hasStatusEffect("freeze")) {
            return false;
        }
        
        return true;
    }
    
    // ============ MÉTODOS DE INFORMACIÓN ============
    
    public void displayStatus() {
        System.out.println("=== " + name.toUpperCase() + " ===");
        System.out.println("Nivel: " + level);
        System.out.println("HP: " + ps + "/" + maxPs);
        System.out.println("Tipo: " + primaryType + (secondaryType != null ? "/" + secondaryType : ""));
        System.out.println("ATK: " + getModifiedAttack() + " | DEF: " + getModifiedDefence());
        System.out.println("SP.ATK: " + getModifiedSpecialAttack() + " | SP.DEF: " + getModifiedSpecialDefence());
        System.out.println("SPEED: " + getModifiedSpeed());
        
        if (!statusEffects.isEmpty()) {
            System.out.println("Estados: " + String.join(", ", statusEffects));
        }
        
        System.out.println("Movimientos:");
        for (int i = 0; i < moves.size(); i++) {
            Movement move = moves.get(i);
            System.out.println("  " + (i + 1) + ". " + move.getName() + " (Poder: " + move.getPower() + ", Precisión: " + move.getAcurracy() + "%)");
        }
        System.out.println("========================");
    }
    
    public double getHPPercentage() {
        return maxPs > 0 ? (double) ps / maxPs : 0.0;
    }
    
    public boolean isAlive() {
        return ps > 0;
    }
    
    public boolean isHealthy() {
        return getHPPercentage() > 0.5;
    }
    
    public boolean isCriticallyInjured() {
        return getHPPercentage() < 0.25;
    }
    
    // ============ GETTERS EXISTENTES ============
    
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPs() {
        return ps;
    }
    
    public int getMaxPs() {
        return maxPs;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefence() {
        return defence;
    }

    public int getSpeed() {
        return speed;
    }

    public int getSpecialAttack() {
        return specialAttack;
    }

    public int getSpecialDefence() {
        return specialDefence;
    }

    public Type getPrimaryType() {
        return primaryType;
    }

    public Type getSecondaryType() {
        return secondaryType;
    }

    public ArrayList<Movement> getMoves() {
        return new ArrayList<>(moves);
    }
    
    // ============ NUEVOS GETTERS ============
    
    public int getLevel() {
        return level;
    }
    
    public HashMap<String, Integer> getStats() {
        return new HashMap<>(stats);
    }
    
    public HashMap<String, Integer> getStatModifiers() {
        return new HashMap<>(statModifiers);
    }
    
    public ArrayList<String> getStatusEffects() {
        return new ArrayList<>(statusEffects);
    }
    
    public Image getImage() {
        return image;
    }
    
    // ============ SETTERS NECESARIOS ============
    
    public void setPs(int ps) {
        this.ps = Math.max(0, Math.min(maxPs, ps));
    }
    
    public void setLevel(int level) {
        this.level = Math.max(1, Math.min(100, level));
        adjustStatsForLevel();
    }
    
    public void setImage(Image image) {
        this.image = image;
    }
    
    // ============ MÉTODOS DE UTILIDAD ============
    
    public Pokemon createCopy() {
        Pokemon copy = new Pokemon(name, description, maxPs, attack, defence, speed, specialAttack, specialDefence, primaryType, secondaryType, level);
        copy.setPs(this.ps);
        
        // Copiar movimientos
        for (Movement move : this.moves) {
            copy.moves.add(move);
        }
        
        // Copiar modificadores y estados
        copy.statModifiers = new HashMap<>(this.statModifiers);
        copy.statusEffects = new ArrayList<>(this.statusEffects);
        
        return copy;
    }
    
    @Override
    public String toString() {
        return name + " (Nivel " + level + ", HP: " + ps + "/" + maxPs + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pokemon pokemon = (Pokemon) obj;
        return name.equals(pokemon.name) && level == pokemon.level;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, level);
    }

    // Agregar estos métodos a la clase Pokemon existente:

    // ============ MÉTODOS FALTANTES PARA BATALLA ============

    /**
     * Actualiza el HP actual (usado cuando recibe daño)
     * @param hp Nuevo valor de HP
     */
    public void setCurrentHP(int hp) {
        this.ps = Math.max(0, Math.min(maxPs, hp));
        
        if (this.ps <= 0) {
            System.out.println(name + " se ha debilitado!");
            // Limpiar efectos de estado al debilitarse
            statusEffects.clear();
            clearStatModifiers();
        }
    }

    /**
     * Obtiene el HP actual (alias para getPs() para compatibilidad)
     * @return HP actual
     */
    public int getCurrentHP() {
        return ps;
    }

    /**
     * Aplica daño directamente usando el sistema de cálculo
     * @param attacker Pokémon atacante
     * @param move Movimiento usado
     * @return Cantidad de daño realmente aplicado
     */
    public int receiveDamage(Pokemon attacker, Movement move) {
        int damage = attacker.calculateDamage(move, this);
        int oldHp = this.ps;
        takeDamage(damage);
        return oldHp - this.ps; // Daño real aplicado
    }

    /**
     * Verifica si el Pokémon puede usar un movimiento específico
     * @param move Movimiento a verificar
     * @return true si puede usar el movimiento
     */
    public boolean canUseMovement(Movement move) {
        return canUseMove(move);
    }

    /**
     * Usa un movimiento y reduce su PP (cuando se implemente PP)
     * @param move Movimiento a usar
     * @return true si se usó exitosamente
     */
    public boolean useMovement(Movement move) {
        if (!canUseMove(move)) {
            return false;
        }
        
        // TODO: Reducir PP del movimiento cuando se implemente
        // move.reducePP();
        
        System.out.println(name + " usa " + move.getName() + "!");
        return true;
    }

    /**
     * Restaura completamente el HP
     */
    public void fullHeal() {
        heal(maxPs - ps);
    }

    /**
     * Restaura HP por porcentaje
     * @param percentage Porcentaje a restaurar (0.0 a 1.0)
     */
    public void healPercentage(double percentage) {
        int healAmount = (int) (maxPs * Math.max(0.0, Math.min(1.0, percentage)));
        heal(healAmount);
    }

    /**
     * Verifica si está debilitado
     * @return true si HP es 0
     */
    public boolean isFainted() {
        return ps <= 0;
    }

    /**
     * Verifica si está disponible para batalla
     * @return true si HP > 0
     */
    public boolean isAvailable() {
        return ps > 0;
    }

    /**
     * Restaura todas las estadísticas y efectos al estado base
     */
    public void resetBattleState() {
        clearStatModifiers();
        clearStatusEffects();
        System.out.println(name + " ha sido restaurado a su estado base!");
    }

    /**
     * Aplica efecto de un objeto (será llamado desde Item.use())
     * @param effectType Tipo de efecto
     * @param value Valor del efecto
     */
    public void applyItemEffect(String effectType, int value) {
        switch (effectType.toLowerCase()) {
            case "heal":
                heal(value);
                break;
            case "revive":
                if (isFainted()) {
                    revive(value / 100.0); // value como porcentaje
                }
                break;
            case "cure_status":
                clearStatusEffects();
                break;
            case "boost_attack":
                modifyStat("attack", value);
                break;
            case "boost_defence":
                modifyStat("defence", value);
                break;
            case "boost_speed":
                modifyStat("speed", value);
                break;
            default:
                System.out.println("Efecto desconocido: " + effectType);
        }
    }
}