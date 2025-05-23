package Domain;

public class Revive extends Item {

    public Revive(String name) {
        super(name, "Revive a un Pokémon debilitado con 50% HP", "revive", 50);
    }

    @Override
    public boolean applyEffect(Pokemon targetPokemon) {
        if (targetPokemon == null || !targetPokemon.isFainted()) {
            return false;
        }
        
        targetPokemon.applyItemEffect("revive", effectValue);
        return true;
    }

    @Override
    public boolean use(Pokemon targetPokemon) {
        if (!canUseOn(targetPokemon)) {
            System.out.println("No se puede usar " + name + " en " + targetPokemon.getName());
            return false;
        }
        
        if (!targetPokemon.isFainted()) {
            System.out.println(targetPokemon.getName() + " no está debilitado!");
            return false;
        }
        
        System.out.println("Usando " + name + " en " + targetPokemon.getName() + "...");
        return applyEffect(targetPokemon);
    }
    
    @Override
    public boolean canUseOn(Pokemon targetPokemon) {
        return super.canUseOn(targetPokemon) && targetPokemon.isFainted();
    }
}