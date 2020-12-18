package spiralhalo.bladder.damage;

import net.minecraft.entity.damage.DamageSource;

public class UnrelievedDamage extends DamageSource {
    public static final DamageSource UNRELIEVED = new UnrelievedDamage();
    public UnrelievedDamage() {
        super("bladder.unrelieved");
    }
}
