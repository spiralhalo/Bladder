package spiralhalo.bladder.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.util.Identifier;
import spiralhalo.bladder.BladderMod;
import spiralhalo.bladder.util.RenderingUtil;

import static spiralhalo.bladder.block.WaterClosetBlock.WaterClosetEntity.WATER_CLOSET_ENTITY;

public class BladderModClient implements ClientModInitializer {

    public static final Identifier SPAWN_ENTITY_PACKET_ID = BladderMod.createId("spawn_entity");
    private final BladderHud bladderHud = new BladderHud();
    private final ClientPacketConsumer packetConsumer = new ClientPacketConsumer();

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.Factory empty = (dispatcher, context) -> RenderingUtil.createEmptyRenderer(dispatcher);
        EntityRendererRegistry.INSTANCE.register(WATER_CLOSET_ENTITY, empty);
        ClientSidePacketRegistry.INSTANCE.register(SPAWN_ENTITY_PACKET_ID, packetConsumer);
        HudRenderCallback.EVENT.register(bladderHud);
    }
}
