package spiralhalo.bladder.block;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;

class WaterClosetBlockItem extends BlockItem {
    WaterClosetBlockItem(Block block) {
        super(block, new Settings().group(ItemGroup.MISC));
    }
}
