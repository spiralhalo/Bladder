package spiralhalo.bladder.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import spiralhalo.bladder.BladderMod;
import spiralhalo.bladder.util.NetworkUtils;
import spiralhalo.bladder.util.RenderingUtils;

import static spiralhalo.bladder.block.WaterClosetBlock.WaterClosetEntity.WATER_CLOSET_ENTITY;

public class BladderModClient implements ClientModInitializer, PacketConsumer {

    public static final Identifier SPAWN_ENTITY_PACKET_ID = BladderMod.createId("spawn_entity");
    public final BladderHud bladderHud = new BladderHud();

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(WATER_CLOSET_ENTITY, (dispatcher, context) ->
            RenderingUtils.createEmptyRenderer(dispatcher));
        ClientSidePacketRegistry.INSTANCE.register(SPAWN_ENTITY_PACKET_ID, this);
        HudRenderCallback.EVENT.register(bladderHud);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void accept(PacketContext packetContext, PacketByteBuf packetByteBuf) {
        NetworkUtils.EntityPacketData data = NetworkUtils.readEntityDataPacket(packetByteBuf);
        packetContext.getTaskQueue().execute(() -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            Entity entity = Registry.ENTITY_TYPE.get(data.rawType).create(world);
            if(entity != null && world != null) {
                entity.updatePosition(data.x, data.y, data.z);
                entity.updateTrackedPosition(data.x, data.y, data.z);
                entity.pitch = data.pitch;
                entity.yaw = data.yaw;
                entity.setEntityId(data.id);
                entity.setUuid(data.uuid);
                world.addEntity(data.id, entity);
            }
        });
    }
}
