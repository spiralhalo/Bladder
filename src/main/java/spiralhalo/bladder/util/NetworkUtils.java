/*
 * NetworkUtils.java
 *
 * The author of this software code waives most copyright protections of this software code under the terms of
 * the CC0 1.0 Universal license as published by the Creative Commons Corporation. You should have received
 * a copy of this license text with this file, but should it be unavailable, you may obtain a copy of it at
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 *
 */

package spiralhalo.bladder.util;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class NetworkUtils {

    public static PacketByteBuf createEntityDataPacket(Entity e) {
        PacketByteBuf out = new PacketByteBuf(Unpooled.buffer());
        out.writeVarInt(Registry.ENTITY_TYPE.getRawId(e.getType()));
        out.writeUuid(e.getUuid());
        out.writeVarInt(e.getEntityId());
        out.writeFloat(e.pitch);
        out.writeFloat(e.yaw);
        out.writeDouble(e.getX());
        out.writeDouble(e.getY());
        out.writeDouble(e.getZ());
        return out;
    }

    public static EntityPacketData readEntityDataPacket(PacketByteBuf in) {
        int rawType = in.readVarInt();
        UUID entityUUID = in.readUuid();
        int entityID = in.readVarInt();
        float pitch = in.readFloat();
        float yaw = in.readFloat();
        double x = in.readDouble();
        double y = in.readDouble();
        double z = in.readDouble();

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
