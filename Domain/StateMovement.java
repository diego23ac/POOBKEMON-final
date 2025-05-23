package Domain;

public class StateMovement extends Movement {

    public StateMovement(String name, int power, int accuracy, int pp, double sideEffect, int priority) {
        super(name, power, accuracy, pp, sideEffect, priority);
        setCategory("Status");
        typeMovement = "State";
        // Los movimientos de estado típicamente no tienen poder
        if (this.power == 0) {
            this.power = 0; // Asegurar que sea 0
        }
    }
    
    public StateMovement(String name, int power, int accuracy, int pp, double sideEffect, int priority, Type moveType) {
        super(name, power, accuracy, pp, sideEffect, priority, moveType);
        setCategory("Status");
        typeMovement = "State";
    }

    @Override
    public boolean applySpecialEffect(Pokemon user, Pokemon target) {
        String moveName = getName().toLowerCase();
        
        // Movimientos de estado que afectan estadísticas
        if (moveName.contains("swords dance")) {
            user.modifyStat("attack", 2);
            System.out.println("¡El ataque de " + user.getName() + " aumenta mucho!");
            return true;
        } else if (moveName.contains("dragon dance")) {
            user.modifyStat("attack", 1);
            user.modifyStat("speed", 1);
            System.out.println("¡El ataque y la velocidad de " + user.getName() + " aumentan!");
            return true;
        } else if (moveName.contains("calm mind")) {
            user.modifyStat("specialAttack", 1);
            user.modifyStat("specialDefence", 1);
            System.out.println("¡El ataque especial y la defensa especial de " + user.getName() + " aumentan!");
            return true;
        } else if (moveName.contains("iron defense")) {
            user.modifyStat("defence", 2);
            System.out.println("¡La defensa de " + user.getName() + " aumenta mucho!");
            return true;
        } else if (moveName.contains("agility")) {
            user.modifyStat("speed", 2);
            System.out.println("¡La velocidad de " + user.getName() + " aumenta mucho!");
            return true;
        }
        
        // Movimientos que causan estados
        else if (moveName.contains("sleep powder") || moveName.contains("hypnosis")) {
            target.addStatusEffect("sleep");
            System.out.println("¡" + target.getName() + " se queda dormido!");
            return true;
        } else if (moveName.contains("thunder wave")) {
            target.addStatusEffect("paralysis");
            System.out.println("¡" + target.getName() + " está paralizado!");
            return true;
        } else if (moveName.contains("toxic")) {
            target.addStatusEffect("poison");
            System.out.println("¡" + target.getName() + " está gravemente envenenado!");
            return true;
        } else if (moveName.contains("will-o-wisp")) {
            target.addStatusEffect("burn");
            System.out.println("¡" + target.getName() + " está quemado!");
            return true;
        }
        
        // Movimientos de curación
        else if (moveName.contains("recover") || moveName.contains("roost")) {
            int healAmount = user.getMaxPs() / 2;
            user.heal(healAmount);
            System.out.println("¡" + user.getName() + " recupera HP!");
            return true;
        } else if (moveName.contains("aromatherapy")) {
            user.clearStatusEffects();
            System.out.println("¡" + user.getName() + " se cura de todos los estados!");
            return true;
        }
        
        // Movimientos que afectan estadísticas del oponente
        else if (moveName.contains("growl")) {
            target.modifyStat("attack", -1);
            System.out.println("¡El ataque de " + target.getName() + " disminuye!");
            return true;
        } else if (moveName.contains("leer")) {
            target.modifyStat("defence", -1);
            System.out.println("¡La defensa de " + target.getName() + " disminuye!");
            return true;
        } else if (moveName.contains("charm")) {
            target.modifyStat("attack", -2);
            System.out.println("¡El ataque de " + target.getName() + " disminuye mucho!");
            return true;
        }
        
        // Efecto genérico si no se reconoce el movimiento
        if (triggersSideEffect()) {
            System.out.println("¡" + getName() + " tiene un efecto especial!");
            return true;
        }
        
        return false;
    }

    @Override
    public boolean canBeUsed(Pokemon user) {
        if (!hasPP()) {
            System.out.println(getName() + " no tiene PP restantes!");
            return false;
        }
        
        // Los movimientos de estado pueden ser usados incluso con algunos estados
        if (user.hasStatusEffect("sleep") || user.hasStatusEffect("freeze")) {
            return false;
        }
        
        // La parálisis afecta menos a los movimientos de estado
        if (user.hasStatusEffect("paralysis")) {
            if (Math.random() < 0.15) { // 15% de probabilidad de fallar
                System.out.println(user.getName() + " está paralizado y no puede concentrarse!");
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Los movimientos de estado no causan daño directo
     * @return Siempre 0
     */
    public int calculateStateDamage() {
        return 0; // Los movimientos de estado no causan daño
    }
    
    /**
     * Verifica si es un movimiento de curación
     * @return true si cura HP
     */
    public boolean isHealingMove() {
        String moveName = getName().toLowerCase();
        return moveName.contains("recover") || moveName.contains("roost") || 
               moveName.contains("synthesis") || moveName.contains("moonlight");
    }
    
    /**
     * Verifica si es un movimiento que mejora estadísticas
     * @return true si mejora stats del usuario
     */
    public boolean isBoostingMove() {
        String moveName = getName().toLowerCase();
        return moveName.contains("swords dance") || moveName.contains("dragon dance") || 
               moveName.contains("calm mind") || moveName.contains("iron defense") ||
               moveName.contains("agility");
               }
   
   /**
    * Verifica si es un movimiento que debilita al oponente
    * @return true si reduce stats del oponente
    */
   public boolean isDebuffingMove() {
       String moveName = getName().toLowerCase();
       return moveName.contains("growl") || moveName.contains("leer") || 
              moveName.contains("charm") || moveName.contains("screech") ||
              moveName.contains("scary face");
   }
   
   /**
    * Verifica si causa efectos de estado
    * @return true si puede causar parálisis, sueño, etc.
    */
   public boolean causesStatusCondition() {
       String moveName = getName().toLowerCase();
       return moveName.contains("sleep") || moveName.contains("thunder wave") || 
              moveName.contains("toxic") || moveName.contains("will-o-wisp") ||
              moveName.contains("hypnosis") || moveName.contains("stun spore");
   }
}