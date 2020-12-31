package spiralhalo.bladder.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import spiralhalo.bladder.util.NetworkUtil;

public class ClientPacketConsumer implements PacketConsumer, Runnable {

    private NetworkUtil.EntityPacketData spawnData;

    @Environment(EnvType.CLIENT)
    @Override
    public void accept(PacketContext packetContext, PacketByteBuf packetByteBuf) {
        setSpawnData(NetworkUtil.readEntityDataPacket(packetByteBuf));
        packetContext.getTaskQueue().execute(this);
    }

    private void setSpawnData(NetworkUtil.EntityPacketData data) {
        this.spawnData = data;
    }

    private void cleanup() {
        this.spawnData = null;
    }

    @Override
    public void run() {
        ClientWorld world = MinecraftClient.getInstance().world;
        Entity entity = Registry.ENTITY_TYPE.get(spawnData.rawType).create(world);
        if(entity != null && world != null) {
            entity.updatePosition(spawnData.x, spawnData.y, spawnData.z);
            entity.updateTrackedPosition(spawnData.x, spawnData.y, spawnData.z);
            entity.pitch = spawnData.pitch;
            entity.yaw = spawnData.yaw;
            entity.setEntityId(spawnData.id);
            entity.setUuid(spawnData.uuid);
            world.addEntity(spawnData.id, entity);
        }
        cleanup();
    }
}
