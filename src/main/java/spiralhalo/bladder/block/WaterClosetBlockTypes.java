package spiralhalo.bladder.block;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import spiralhalo.bladder.BladderMod;

public enum WaterClosetBlockTypes {
    WATER_CLOSET("water_closet", 30),
    CLAY_WATER_CLOSET("clay_water_closet", 40),
    QUARTZ_WATER_CLOSET("quartz_water_closet", 20);

    public static void registerAll() {
        for (WaterClosetBlockTypes w:WaterClosetBlockTypes.values()) {
            Registry.register(Registry.BLOCK, w.id, w.block);
            Registry.register(Registry.ITEM, w.id, w.blockItem);
        }
    }

    WaterClosetBlockTypes(String id, int bpReductionTick) {
        this.id = BladderMod.createId(id);
        this.block = new WaterClosetBlock(bpReductionTick);
        this.blockItem = new WaterClosetBlockItem(this.block);
    }

    public final Identifier id;
    public final WaterClosetBlock block;
    public final WaterClosetBlockItem blockItem;
}
