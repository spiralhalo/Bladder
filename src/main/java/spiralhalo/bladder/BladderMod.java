package spiralhalo.bladder;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import spiralhalo.bladder.block.WaterClosetBlock;
import spiralhalo.bladder.block.WaterClosetBlockTypes;

import static spiralhalo.bladder.block.WaterClosetBlock.WaterClosetEntity.WATER_CLOSET_ENTITY;

public class BladderMod implements ModInitializer {

    public static String MODID = "bladder";

    public static Identifier createId(String key) {
        return new Identifier(MODID, key);
    }

    @Override
    public void onInitialize() {
        WaterClosetBlockTypes.registerAll();
        Registry.register(Registry.ENTITY_TYPE, WaterClosetBlock.ENTITY_ID, WATER_CLOSET_ENTITY);
    }
}
