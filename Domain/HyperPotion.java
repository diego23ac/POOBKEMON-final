package Domain;

public class HyperPotion extends Potion {

    public HyperPotion(String name) {
        super(name);
        setDescription("Restaura 200 HP");
        setEffectValue(200);
    }
}