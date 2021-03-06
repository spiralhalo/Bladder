package spiralhalo.bladder.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import spiralhalo.bladder.BladderConfig;
import spiralhalo.bladder.mechanics.BladderComponents;
import spiralhalo.bladder.mechanics.BladderRule;

public class BladderHud implements HudRenderCallback {

    @Override
    public void onHudRender(MatrixStack matrixStack, float v) {
        final MinecraftClient client = MinecraftClient.getInstance();
        final Window window = client.getWindow();
        final TextRenderer renderer = client.textRenderer;
        final Entity player = client.player;
        if (player == null) return;
        final int playerBp = BladderComponents.BLADDER_POINT.get(player).getBladderPoint();
        final int color;
        final String bpElement = generateElement(playerBp);
        final float pivotOffsetX = BladderConfig.hudRightToLeft ? -renderer.getWidth(bpElement) : 0.0f;
        final float pivotOffsetY = -renderer.fontHeight;
        final float x = window.getScaledWidth() * 0.5f + BladderConfig.hudOffsetX + pivotOffsetX;
        final float y = window.getScaledHeight() + BladderConfig.hudOffsetY + pivotOffsetY;
        if (playerBp < BladderRule.MAX_BLADDER_POINT) color = 0xFFFFFF;
        else color = 0xFF3300;
        renderer.draw(matrixStack, bpElement, x, y, color);
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
