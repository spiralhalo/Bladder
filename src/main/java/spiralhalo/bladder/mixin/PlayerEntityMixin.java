package spiralhalo.bladder.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spiralhalo.bladder.BladderRule;
import spiralhalo.bladder.component.BladderComponents;
import spiralhalo.bladder.component.BladderPointComponent;
import spiralhalo.bladder.damage.UnrelievedDamage;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(at = @At("HEAD"), method = "eatFood")
    public void eatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> info) {
        if (!world.isClient && stack.isFood()) {
            PlayerEntity playerEntity = (PlayerEntity) (Object) this;
            Object provider = playerEntity;

            BladderPointComponent bladderPointComponent = BladderComponents.BLADDER_POINT.get(provider);
            bladderPointComponent.onEat(stack);

            BladderComponents.BLADDER_POINT.sync(provider);

            if (bladderPointComponent.getBladderPoint() > BladderRule.MAX_BLADDER_POINT) {
                if (world.getBlockState(playerEntity.getBlockPos()).isAir()) {
                    world.setBlockState(playerEntity.getBlockPos(), Blocks.WATER.getDefaultState());
                }
                world.createExplosion(null, UnrelievedDamage.UNRELIEVED, null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), 5, false, Explosion.DestructionType.NONE);
            }
        }
    }
}
