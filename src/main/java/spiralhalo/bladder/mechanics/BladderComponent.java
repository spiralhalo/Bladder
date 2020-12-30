package spiralhalo.bladder.mechanics;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
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
        if(stack != null && stack.isFood()) {
            this.prevBladderPoint = this.bladderPoint;
            this.bladderPoint += stack.getItem().getFoodComponent().getHunger();
        }
    }

    public void afterEat(World world) {
        if (this.bladderPoint > BladderRule.MAX_BLADDER_POINT && this.prevBladderPoint >= BladderRule.MAX_BLADDER_POINT) {
            if (world.getBlockState(entity.getBlockPos()).isAir()) {
                world.setBlockState(entity.getBlockPos(), Blocks.WATER.getDefaultState());
            }
            world.createExplosion(null, UnrelievedDamage.UNRELIEVED, null, entity.getX(), entity.getY(), entity.getZ(), 5, false, Explosion.DestructionType.NONE);
        }
    }

    public void onRelieve(int relieveAmount) {
        if (this.bladderPoint > 0) {
            this.prevBladderPoint = this.bladderPoint;
            this.bladderPoint -= relieveAmount;
            if (this.bladderPoint < 0) {
                this.bladderPoint = 0;
            }
        }
    }
}
