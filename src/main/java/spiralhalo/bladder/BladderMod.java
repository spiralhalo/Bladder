package spiralhalo.bladder;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.registry.Registry;
import spiralhalo.bladder.block.WaterClosetBlock;

import static spiralhalo.bladder.block.WaterClosetBlock.WaterClosetRideableEntity.WATER_CLOSET_ENTITY;

public class BladderMod implements ModInitializer {

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, WaterClosetBlock.id, WaterClosetBlock.block);
        Registry.register(Registry.ITEM, WaterClosetBlock.id, WaterClosetBlock.blockItem);
        Registry.register(Registry.ENTITY_TYPE, WaterClosetBlock.entityId, WATER_CLOSET_ENTITY);
    }
}
