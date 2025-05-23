package Domain;

public enum Type {

    STELL, WATER, BUG, DRAGON, ELECTRIC, GOSHT, FIRE, FAIRY, ICE, FIGHT, NORMAL, GRASS, PSHYCHIC, ROCK, DARK, GROUND, POSION, FLYING;

    public double calculateMultiplier(Type defenderType){
        switch (this) {
            case STELL:
                if(defenderType == FAIRY || defenderType == ICE || defenderType == ROCK) return 2.0;
                if(defenderType == STELL || defenderType == WATER || defenderType == ELECTRIC || defenderType == FIRE) return 0.5;
                return 1.0;
            
            case WATER:
                if(defenderType == FIRE || defenderType == GROUND || defenderType == ROCK) return 2.0;
                if(defenderType == WATER || defenderType == DRAGON || defenderType == GRASS) return 0.5;
                return 1.0;
            
            case BUG:
                if(defenderType == GRASS || defenderType == PSHYCHIC || defenderType == DARK) return 2.0;
                if(defenderType == STELL || defenderType == GOSHT || defenderType == FIRE || defenderType == FAIRY || defenderType == FIGHT || defenderType == POSION || defenderType == FLYING) return 0.5;
                return 1.0;

            case DRAGON:
                if(defenderType == DRAGON) return 2.0;
                if(defenderType == STELL) return 0.5;
                if(defenderType == FAIRY) return 0.0;
                return 1.0;

            case ELECTRIC:
                if(defenderType == WATER || defenderType == FLYING) return 2.0;
                if(defenderType == DRAGON || defenderType == ELECTRIC || defenderType == GRASS) return 0.5;
                if(defenderType == GROUND)  return 0.0;
                return 1.0;

            case GOSHT:
                if(defenderType == GOSHT || defenderType == PSHYCHIC) return 2.0;
                if(defenderType == DARK) return 0.5;
                if(defenderType == NORMAL) return 0.0;
                return 1.0;

            case FIRE:
                if(defenderType == STELL || defenderType == BUG || defenderType == ICE || defenderType == GRASS) return 2.0;
                if(defenderType == WATER || defenderType == DRAGON || defenderType == FIRE || defenderType == ROCK) return 0.5;
                return 1.0;

            case FAIRY:
                if(defenderType == DRAGON || defenderType == FIGHT || defenderType == DARK) return 2.0;
                if(defenderType == STELL || defenderType == FIRE || defenderType == POSION) return 0.5;
                return 1.0;

            case ICE:
                if(defenderType == DRAGON || defenderType == GRASS || defenderType == GROUND || defenderType == FLYING) return 2.0;
                if(defenderType == STELL || defenderType == WATER || defenderType == FIRE || defenderType == ICE) return 0.5;
                return 1.0;
                
            case FIGHT:
                if(defenderType == STELL || defenderType == ICE || defenderType == NORMAL || defenderType == ROCK || defenderType == DARK) return 2.0;
                if(defenderType == BUG || defenderType == FAIRY || defenderType == PSHYCHIC || defenderType == POSION || defenderType == FLYING) return 0.5;
                if(defenderType == GOSHT) return 0.0;
                return 1.0;

            case NORMAL:
                if(defenderType == STELL || defenderType == ROCK) return 0.5;
                if(defenderType == GOSHT) return 0.0;
                return 1.0;

            case GRASS:
                if(defenderType == WATER || defenderType == ROCK || defenderType == GROUND) return 2.0;
                if(defenderType == STELL || defenderType == BUG || defenderType == DRAGON || defenderType == FIRE || defenderType == GRASS || defenderType == POSION || defenderType == FLYING) return 0.5;
                return 1.0;

            case PSHYCHIC:
                if(defenderType == FIGHT || defenderType == POSION) return 2.0;
                if(defenderType == PSHYCHIC) return 0.5;
                if(defenderType == DARK) return 0.0;
                return 1.0;

            case ROCK:
                if(defenderType == BUG || defenderType == FIRE || defenderType == ICE || defenderType == FLYING) return 2.0;
                if(defenderType == STELL || defenderType == FIGHT || defenderType == GROUND) return 0.5;
                return 1.0;
            
            case DARK:
                if(defenderType == GOSHT || defenderType == PSHYCHIC) return 2.0;
                if(defenderType == FAIRY || defenderType == FIGHT || defenderType == DARK) return 0.5;
                return 1.0;

            case GROUND:
                if(defenderType == STELL || defenderType == ELECTRIC || defenderType == FIRE || defenderType == ROCK || defenderType == POSION) return 2.0;
                if(defenderType == BUG || defenderType == GRASS) return 0.5;
                if(defenderType == FLYING) return 0.0;
                return 1.0;

            case POSION:
                if(defenderType == FAIRY || defenderType == GRASS) return 2.0;
                if(defenderType == GOSHT || defenderType == ROCK || defenderType == GROUND || defenderType == POSION) return 0.5;
                if(defenderType == STELL) return 0.0;
                return 1.0;

            case FLYING:
                if(defenderType == BUG || defenderType == FIGHT ||  defenderType == GRASS) return 2.0;
                if(defenderType == STELL || defenderType == ELECTRIC || defenderType == ROCK) return 0.5;
                return 1.0;

            default:
                return 1.0;
        }
    }
    
    // ============ MÉTODOS ADICIONALES ÚTILES ============
    
    /**
     * Obtiene el nombre formateado del tipo
     * @return Nombre del tipo en formato legible
     */
    public String getFormattedName() {
        switch (this) {
            case STELL: return "Steel";
            case WATER: return "Water";
            case BUG: return "Bug";
            case DRAGON: return "Dragon";
            case ELECTRIC: return "Electric";
            case GOSHT: return "Ghost";
            case FIRE: return "Fire";
            case FAIRY: return "Fairy";
            case ICE: return "Ice";
            case FIGHT: return "Fighting";
            case NORMAL: return "Normal";
            case GRASS: return "Grass";
            case PSHYCHIC: return "Psychic";
            case ROCK: return "Rock";
            case DARK: return "Dark";
            case GROUND: return "Ground";
            case POSION: return "Poison";
            case FLYING: return "Flying";
            default: return "Unknown";
        }
    }
    
    /**
     * Obtiene el color asociado al tipo (para interfaz gráfica)
     * @return Código de color
     */
    public String getTypeColor() {
        switch (this) {
            case FIRE: return "#FF4444";
            case WATER: return "#4444FF";
            case GRASS: return "#44FF44";
            case ELECTRIC: return "#FFFF44";
            case ICE: return "#44FFFF";
            case FIGHT: return "#CC4444";
            case POSION: return "#CC44CC";
            case GROUND: return "#DDAA44";
            case FLYING: return "#8888FF";
            case PSHYCHIC: return "#FF44FF";
            case BUG: return "#88CC44";
            case ROCK: return "#CCCC44";
            case GOSHT: return "#6666CC";
            case DRAGON: return "#7766EE";
            case DARK: return "#775544";
            case STELL: return "#BBBBCC";
            case FAIRY: return "#FFAAFF";
            default: return "#CCCCCC";
        }
    }
    
    /**
     * Verifica si este tipo es efectivo contra el tipo dado
     * @param defenderType Tipo defensor
     * @return true si es super efectivo (>1.0)
     */
    public boolean isEffectiveAgainst(Type defenderType) {
        return calculateMultiplier(defenderType) > 1.0;
    }
    
    /**
     * Verifica si este tipo es resistido por el tipo dado
     * @param defenderType Tipo defensor
     * @return true si es poco efectivo (<1.0)
     */
    public boolean isResistedBy(Type defenderType) {
        return calculateMultiplier(defenderType) < 1.0;
    }
    
    /**
     * Verifica si este tipo no afecta al tipo dado
     * @param defenderType Tipo defensor
     * @return true si no tiene efecto (0.0)
     */
    public boolean hasNoEffectOn(Type defenderType) {
        return calculateMultiplier(defenderType) == 0.0;
    }
}