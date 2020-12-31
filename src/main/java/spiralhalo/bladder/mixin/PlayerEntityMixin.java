package spiralhalo.bladder.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spiralhalo.bladder.mechanics.BladderComponent;
import spiralhalo.bladder.mechanics.BladderComponents;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(at = @At("HEAD"), method = "eatFood")
    public void eatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> info) {
        if (world.isClient || !stack.isFood()) return;
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        BladderComponent bladderComponent = BladderComponents.BLADDER_POINT.get(playerEntity);
        bladderComponent.onEat(stack);
        BladderComponents.BLADDER_POINT.sync(playerEntity);
        bladderComponent.afterEat(world);
    }
}
