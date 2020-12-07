package spiralhalo.bladder;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import spiralhalo.bladder.block.WaterClosetBlock;
import spiralhalo.bladder.network.EntitySpawnPacket;

import java.util.UUID;

import static spiralhalo.bladder.block.WaterClosetBlock.WaterClosetRideableEntity.WATER_CLOSET_ENTITY;

public class BladderModClient implements ClientModInitializer, PacketConsumer {

    public static final Identifier packetId = new Identifier("bladder", "spawn_entity");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(WATER_CLOSET_ENTITY, (dispatcher, context) -> {
            return new WaterClosetBlock.WaterClosetRideableEntityRenderer(dispatcher);
        });
        ClientSidePacketRegistry.INSTANCE.register(packetId, this);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void accept(PacketContext packetContext, PacketByteBuf packetByteBuf) {
        EntitySpawnPacket.EntityPacketData data = EntitySpawnPacket.readBuffer(packetByteBuf);
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
