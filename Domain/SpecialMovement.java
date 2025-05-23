package Domain;

public class SpecialMovement extends Movement {
    
    public SpecialMovement(String name, int power, int accuracy, int pp, double sideEffect, int priority, Type type) {
        super(name, power, accuracy, pp, sideEffect, priority, type);
        setCategory("Special");
        typeMovement = "Special";
    }

    @Override
    public boolean applySpecialEffect(Pokemon user, Pokemon target) {
        if (triggersSideEffect()) {
            String moveName = getName().toLowerCase();
            
            // Ejemplos de efectos comunes en movimientos especiales
            if (moveName.contains("thunderbolt") || moveName.contains("thunder")) {
                target.addStatusEffect("paralysis");
                System.out.println("¡" + target.getName() + " está paralizado!");
                return true;
            } else if (moveName.contains("flamethrower") || moveName.contains("fire")) {
                target.addStatusEffect("burn");
                System.out.println("¡" + target.getName() + " está quemado!");
                return true;
            } else if (moveName.contains("ice") || moveName.contains("blizzard")) {
                target.addStatusEffect("freeze");
                System.out.println("¡" + target.getName() + " está congelado!");
                return true;
            } else if (moveName.contains("psychic")) {
                target.modifyStat("specialDefence", -1);
                System.out.println("¡La defensa especial de " + target.getName() + " disminuye!");
                return true;
            } else if (moveName.contains("shadow ball")) {
                target.modifyStat("specialDefence", -1);
                return true;
            }
            
            // Efecto genérico para movimientos especiales
            if (Math.random() < 0.1) {
                System.out.println("¡Ataque especialmente concentrado!");
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
        
        // Los movimientos especiales no son afectados por parálisis tanto como los físicos
        if (user.hasStatusEffect("paralysis")) {
            if (Math.random() < 0.1) { // Solo 10% de probabilidad de fallar
                System.out.println(user.getName() + " está paralizado pero logra concentrarse!");
            }
        }
        
        // No se pueden usar si está dormido o congelado
        if (user.hasStatusEffect("sleep") || user.hasStatusEffect("freeze")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Calcula el daño base considerando el ataque especial
     * @param user Pokémon atacante
     * @param target Pokémon defensor
     * @return Daño base calculado
     */
    public int calculateSpecialDamage(Pokemon user, Pokemon target) {
        if (power <= 0) return 0;
        
        double damage = ((2.0 * user.getLevel() + 10) / 250.0) * 
                       (user.getModifiedSpecialAttack() / (double) target.getModifiedSpecialDefence()) * 
                       power + 2;
        
        // Aplicar efectividad de tipo
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
        
        return Math.max(1, (int) damage);
    }
    
    // Getter para el tipo (necesario para las batallas)
    public Type getType() {
        return moveType;
    }
}