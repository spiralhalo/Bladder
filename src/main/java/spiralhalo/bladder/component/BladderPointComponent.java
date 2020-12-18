package spiralhalo.bladder.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;

public class BladderPointComponent implements ComponentV3, AutoSyncedComponent {

    public static String NBT_KEY = "bladder_point";
    private int bladderPoint;
    private final Entity entity;

    public BladderPointComponent(Entity entity) {
        this.entity = entity;
    }

    public int getBladderPoint() {
        return bladderPoint;
    }

    @Override
    public void readFromNbt(CompoundTag compoundTag) {
        this.bladderPoint = compoundTag.getInt(NBT_KEY);
    }

    @Override
    public void writeToNbt(CompoundTag compoundTag) {
        compoundTag.putInt(NBT_KEY, this.bladderPoint);
    }

    public void onEat(ItemStack stack) {
        if(stack != null && stack.isFood()) {
            this.bladderPoint += stack.getItem().getFoodComponent().getHunger();
            entity.sendSystemMessage(new LiteralText("Your bladder point is " + bladderPoint), null);
        }
    }

    public void onRelieve(int relieveAmount) {
        if (this.bladderPoint > 0) {
            this.bladderPoint -= relieveAmount;
            entity.sendSystemMessage(new LiteralText("Your bladder point is " + bladderPoint), null);
            if (this.bladderPoint < 0) {
                this.bladderPoint = 0;
            }
        }
    }
}
