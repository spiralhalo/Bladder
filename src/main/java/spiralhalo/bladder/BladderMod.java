package spiralhalo.bladder;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import spiralhalo.bladder.block.WaterClosetBlock;

import static spiralhalo.bladder.block.WaterClosetBlock.WaterClosetEntity.WATER_CLOSET_ENTITY;

public class BladderMod implements ModInitializer {

    public static String MODID = "bladder";

    public static Identifier createId(String key) {
        return new Identifier(MODID, key);
    }

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, WaterClosetBlock.ID, WaterClosetBlock.BLOCK);
        Registry.register(Registry.ITEM, WaterClosetBlock.ID, WaterClosetBlock.BLOCK_ITEM);
        Registry.register(Registry.ENTITY_TYPE, WaterClosetBlock.ENTITY_ID, WATER_CLOSET_ENTITY);
    }
}
