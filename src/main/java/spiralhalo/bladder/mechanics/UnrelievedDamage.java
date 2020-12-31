package spiralhalo.bladder.mechanics;

import net.minecraft.entity.damage.DamageSource;

public class UnrelievedDamage extends DamageSource {
    public static final DamageSource UNRELIEVED = new UnrelievedDamage();
    private UnrelievedDamage() {
        super("bladder.unrelieved");
    }
}
