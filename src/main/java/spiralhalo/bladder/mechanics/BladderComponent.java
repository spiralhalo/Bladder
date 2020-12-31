package spiralhalo.bladder.mechanics;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class BladderComponent implements ComponentV3, AutoSyncedComponent {

    public static String COMPONENT_ID = "bladder_point";
    private static String NBT_KEY_BP = "bladder_point";
    private static String NBT_KEY_PREV_BP = "previous_bladder_point";
    private int bladderPoint;
    private int prevBladderPoint;
    private final Entity entity;

    public BladderComponent(Entity entity) {
        this.entity = entity;
    }

    public int getBladderPoint() {
        return bladderPoint;
    }

    @Override
    public void readFromNbt(CompoundTag compoundTag) {
        this.bladderPoint = compoundTag.getInt(NBT_KEY_BP);
        this.prevBladderPoint = compoundTag.getInt(NBT_KEY_PREV_BP);
    }

    @Override
    public void writeToNbt(CompoundTag compoundTag) {
        compoundTag.putInt(NBT_KEY_BP, this.bladderPoint);
        compoundTag.putInt(NBT_KEY_PREV_BP, this.prevBladderPoint);
    }

    public void onEat(ItemStack stack) {
        if (stack == null) return;
        FoodComponent foodComponent = stack.getItem().getFoodComponent();
        if (foodComponent == null) return;
        this.prevBladderPoint = this.bladderPoint;
        this.bladderPoint += foodComponent.getHunger();
    }

    public void afterEat(World world) {
        if (this.bladderPoint < BladderRule.MAX_BLADDER_POINT) return;
        if (this.prevBladderPoint < BladderRule.MAX_BLADDER_POINT) return;
        BlockPos pos = entity.getBlockPos();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        DamageSource source = UnrelievedDamage.UNRELIEVED;
        Explosion.DestructionType type = Explosion.DestructionType.NONE;
        if (world.getBlockState(pos).isAir()) world.setBlockState(pos, Blocks.WATER.getDefaultState());
        world.createExplosion(null, source, null, x, y, z, 5, false, type);
    }

    public void onRelieve(int relieveAmount) {
        if (this.bladderPoint <= 0) return;
        this.prevBladderPoint = this.bladderPoint;
        this.bladderPoint -= relieveAmount;
        this.bladderPoint = Math.max(0, this.bladderPoint);
    }
}
