/*
 * RenderingUtils.java
 *
 * The author of this software code waives most copyright protections of this software code under the terms of
 * the CC0 1.0 Universal license as published by the Creative Commons Corporation. You should have received
 * a copy of this license text with this file, but should it be unavailable, you may obtain a copy of it at
 * https://creativecommons.org/publicdomain/zero/1.0/legalcode
 *
 */

package spiralhalo.bladder.util;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class RenderingUtils {

    public static EmptyRenderer createEmptyRenderer(EntityRenderDispatcher dispatcher) {
        return new EmptyRenderer(dispatcher);
    }

    private static class EmptyRenderer extends EntityRenderer<Entity> {

        public EmptyRenderer(EntityRenderDispatcher dispatcher) {
            super(dispatcher);
        }

        @Override
        public boolean shouldRender(Entity entity, Frustum frustum, double d, double e, double f) {
            return false;
        }

        @Override
        public Identifier getTexture(Entity entity) {
            return null;
        }

    }
}