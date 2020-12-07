package spiralhalo.bladder.network;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class EntitySpawnPacket {

    public static PacketByteBuf createBuffer(Entity e) {
        PacketByteBuf out = new PacketByteBuf(Unpooled.buffer());
        out.writeVarInt(Registry.ENTITY_TYPE.getRawId(e.getType()));
        out.writeUuid(e.getUuid());
        out.writeVarInt(e.getEntityId());
        out.writeDouble(e.getX());
        out.writeDouble(e.getY());
        out.writeDouble(e.getZ());
        out.writeFloat(e.pitch);
        out.writeFloat(e.yaw);
        return out;
    }

    public static EntityPacketData readBuffer(PacketByteBuf in) {
        int rawType = in.readVarInt();
        UUID entityUUID = in.readUuid();
        int entityID = in.readVarInt();
        double x = in.readDouble();
        double y = in.readDouble();
        double z = in.readDouble();
        float pitch = in.readFloat();
        float yaw = in.readFloat();

        return new EntityPacketData(rawType, entityUUID, entityID, x, y, z, pitch, yaw);
    }

    public static class EntityPacketData {
        public final int rawType;
        public final UUID uuid;
        public final int id;
        public final double x, y, z;
        public final float pitch, yaw;

        public EntityPacketData(int rawType, UUID uuid, int id, double x, double y, double z, float pitch, float yaw) {
            this.rawType = rawType;
            this.uuid = uuid;
            this.id = id;
            this.x = x;
            this.y = y;
            this.z = z;
            this.pitch = pitch;
            this.yaw = yaw;
        }
    }
}
