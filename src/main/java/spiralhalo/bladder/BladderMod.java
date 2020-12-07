package spiralhalo.bladder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import spiralhalo.bladder.block.WaterClosetBlock;
import spiralhalo.bladder.block.WaterClosetBlock.WaterClosetRideableEntityRenderer;

import static spiralhalo.bladder.block.WaterClosetBlock.WaterClosetRideableEntity.WATER_CLOSET_ENTITY;

public class BladderMod implements ModInitializer {

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, WaterClosetBlock.id, WaterClosetBlock.block);
        Registry.register(Registry.ITEM, WaterClosetBlock.id, WaterClosetBlock.blockItem);
        Registry.register(Registry.ENTITY_TYPE, WaterClosetBlock.entityId, WATER_CLOSET_ENTITY);
    }
}
