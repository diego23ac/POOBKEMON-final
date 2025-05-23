package Domain;

public class SuperPotion extends Potion {

    public SuperPotion(String name) {
        super(name);
        setDescription("Restaura 50 HP");
        setEffectValue(50);
    }
}