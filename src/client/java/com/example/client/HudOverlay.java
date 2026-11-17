package com.example.client;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class HudOverlay {
    public static void register() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("modid", "hud"), (g, dt) -> {
            if (!ExampleModClient.MODULES.getOrDefault("HUD", false)) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.options.hideGui) return;
            int x = 4, y = 30;
            g.text(mc.font, "MOD MENU", x, y, 0xFFB57CFF);
            y += 11;
            for (var e : ExampleModClient.MODULES.entrySet()) {
                if (e.getValue() && !e.getKey().equals("HUD")) {
                    g.text(mc.font, e.getKey(), x, y, 0xFF8EC8FF);
                    y += 10;
                }
            }
            String c = String.format("XYZ %.1f %.1f %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
            g.text(mc.font, c, x, y + 4, 0xFFFFFFFF);
        });
    }
}
