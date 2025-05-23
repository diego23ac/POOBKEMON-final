package Domain;

public class Potion extends Item {

    public Potion(String name) {
        super(name, "Restaura 20 HP", "heal", 20);
    }

    @Override
    public boolean applyEffect(Pokemon targetPokemon) {
        if (targetPokemon == null || targetPokemon.isFainted()) {
            return false;
        }
        
        int oldHp = targetPokemon.getCurrentHP();
        targetPokemon.applyItemEffect("heal", effectValue);
        
        // Verificar si realmente se curó
        return targetPokemon.getCurrentHP() > oldHp;
    }

    @Override
    public boolean use(Pokemon targetPokemon) {
        if (!canUseOn(targetPokemon)) {
            System.out.println("No se puede usar " + name + " en " + targetPokemon.getName());
            return false;
        }
        
        if (targetPokemon.getCurrentHP() >= targetPokemon.getMaxPs()) {
            System.out.println(targetPokemon.getName() + " ya tiene el HP al máximo!");
            return false;
        }
        
        System.out.println("Usando " + name + " en " + targetPokemon.getName() + "...");
        return applyEffect(targetPokemon);
    }
    
    @Override
    public boolean canUseOn(Pokemon targetPokemon) {
        return super.canUseOn(targetPokemon) && 
               !targetPokemon.isFainted() && 
               targetPokemon.getCurrentHP() < targetPokemon.getMaxPs();
    }
}