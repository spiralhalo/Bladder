package spiralhalo.bladder.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import spiralhalo.bladder.mechanics.BladderComponents;
import spiralhalo.bladder.mechanics.BladderRule;

public class BladderHud implements HudRenderCallback {

    @Override
    public void onHudRender(MatrixStack matrixStack, float v) {
        final MinecraftClient client = MinecraftClient.getInstance();
        final Entity player = client.player;
        if (player != null) {
            final int playerBp = BladderComponents.BLADDER_POINT.get(player).getBladderPoint();
            final int color;
            final float x = 10f;
            final float y = 150f;
            final String bladderElement = generateElement(playerBp);
            if (playerBp < BladderRule.MAX_BLADDER_POINT) color = 0xFFFFFF;
            else color = 0xFF3300;
            client.textRenderer.draw(matrixStack, bladderElement, x, y, color);
        }
    }

    private static String generateElement(int playerBladderPoint) {
        String bpName = TranslationStorage.getInstance().get("component.bladder.bladder_point.name");
        StringBuilder builder = new StringBuilder(bpName).append(' ').append('[');
        int remaining = playerBladderPoint;
        for (int i = 0; i < BladderRule.MAX_BLADDER_POINT; i += 2) {
            if (remaining > 1) builder.append('=');
            else if (remaining == 1) builder.append('-');
            else builder.append('_');
            remaining -= 2;
        }
        return builder.append(']').toString();
    }
}
