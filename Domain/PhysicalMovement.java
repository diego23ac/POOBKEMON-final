package Domain;

public class PhysicalMovement extends Movement {

    public PhysicalMovement(String name, int power, int accuracy, int pp, double sideEffect, int priority) {
        super(name, power, accuracy, pp, sideEffect, priority);
        setCategory("Physical");
        typeMovement = "Physical";
    }
    
    public PhysicalMovement(String name, int power, int accuracy, int pp, double sideEffect, int priority, Type moveType) {
        super(name, power, accuracy, pp, sideEffect, priority, moveType);
        setCategory("Physical");
        typeMovement = "Physical";
    }

    @Override
    public boolean applySpecialEffect(Pokemon user, Pokemon target) {
        // Los movimientos físicos pueden tener efectos como reducir defensa
        if (triggersSideEffect()) {
            String moveName = getName().toLowerCase();
            
            // Ejemplos de efectos comunes en movimientos físicos
            if (moveName.contains("crunch") || moveName.contains("bite")) {
                target.modifyStat("defence", -1);
                System.out.println("¡La defensa de " + target.getName() + " disminuye!");
                return true;
            } else if (moveName.contains("close combat")) {
                user.modifyStat("defence", -1);
                user.modifyStat("specialDefence", -1);
                System.out.println("¡La defensa de " + user.getName() + " disminuye!");
                return true;
            } else if (moveName.contains("hammer arm")) {
                user.modifyStat("speed", -1);
                System.out.println("¡La velocidad de " + user.getName() + " disminuye!");
                return true;
            }
            
            // Efecto genérico para movimientos físicos
            if (Math.random() < 0.1) { // 10% de probabilidad de crítico extra
                System.out.println("¡Golpe especialmente poderoso!");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canBeUsed(Pokemon user) {
        if (!hasPP()) {
            System.out.println(getName() + " no tiene PP restantes!");
            return false;
        }
        
        // Los movimientos físicos pueden ser bloqueados por parálisis
        if (user.hasStatusEffect("paralysis")) {
            if (Math.random() < 0.25) { // 25% de probabilidad de no poder moverse
                System.out.println(user.getName() + " está paralizado y no puede moverse!");
                return false;
            }
        }
        
        // No se pueden usar si está dormido o congelado
        if (user.hasStatusEffect("sleep") || user.hasStatusEffect("freeze")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Calcula el daño base considerando el ataque físico
     * @param user Pokémon atacante
     * @param target Pokémon defensor
     * @return Daño base calculado
     */
    public int calculatePhysicalDamage(Pokemon user, Pokemon target) {
        if (power <= 0) return 0;
        
        double damage = ((2.0 * user.getLevel() + 10) / 250.0) * 
                       (user.getModifiedAttack() / (double) target.getModifiedDefence()) * 
                       power + 2;
        
        // Aplicar efectividad de tipo si está definida
        if (moveType != null) {
            double typeEffectiveness = moveType.calculateMultiplier(target.getPrimaryType());
            if (target.getSecondaryType() != null) {
                typeEffectiveness *= moveType.calculateMultiplier(target.getSecondaryType());
            }
            damage *= typeEffectiveness;
        }
        
        // STAB (Same Type Attack Bonus)
        if (moveType != null && (moveType == user.getPrimaryType() || moveType == user.getSecondaryType())) {
            damage *= 1.5;
        }
        
        // Efecto de quemadura reduce daño físico
        if (user.hasStatusEffect("burn")) {
            damage *= 0.5;
        }
        
        return Math.max(1, (int) damage);
    }
}