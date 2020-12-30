/*
 * VoxelShapeUtils.java
 *
 * The author of this software code waives most copyright protections of this software code under the terms of
 * the CC0 1.0 Universal license as published by the Creative Commons Corporation. You should have received
 * a copy of this license text with this file, but should it be unavailable, you may obtain a copy of it at
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 *
 */

package spiralhalo.bladder.util;

import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class VoxelShapeUtil {

    public static VoxelShape rotateCW(VoxelShape shape, int amount) {
        if (amount < 1 || amount > 3) {
            throw new IllegalArgumentException("Rotation amount must be between 1 to 3");
        }

        VoxelShape[] rotatedShape = new VoxelShape[]{VoxelShapes.empty()};
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            rotatedShape[0] = VoxelShapes.union(rotatedShape[0],
                    VoxelShapes.cuboid(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX));
        });

        if(amount > 1) {
            return rotateCW(rotatedShape[0], amount - 1);
        }

        return rotatedShape[0];
    }
}
