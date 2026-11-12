package com.example.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExampleModClient implements ClientModInitializer {

    public static final Map<String, Boolean> MODULES = new LinkedHashMap<>();
    static {
        for (String n : new String[]{"Flight", "Speed", "Fullbright", "ESP", "KillAura", "AutoTotem"}) {
            MODULES.put(n, false);
        }
    }

    private static KeyMapping menuKey;

    @Override
    public void onInitializeClient() {
        menuKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.modid.menu", InputConstants.KEY_K, KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (menuKey.consumeClick()) {
                mc.setScreenAndShow(new ModMenu());
            }
        });
    }

    public static class ModMenu extends Screen {
        public ModMenu() {
            super(Component.literal("Mod Menu"));
        }

        @Override
        protected void init() {
            int x = this.width / 2 - 80;
            int y = this.height / 2 - 70;
            for (String name : MODULES.keySet()) {
                addRenderableWidget(Button.builder(label(name), b -> {
                    MODULES.put(name, !MODULES.get(name));
                    b.setMessage(label(name));
                }).bounds(x, y, 160, 20).build());
                y += 24;
            }
        }

        private Component label(String name) {
            return Component.literal(name + ": " + (MODULES.get(name) ? "ON" : "OFF"));
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
            int l = this.width / 2 - 95, r = this.width / 2 + 95;
            int t = this.height / 2 - 100, b = this.height / 2 + 85;
            g.fill(l - 2, t - 2, r + 2, b + 2, 0xFF1E90FF);
            g.fill(l, t, r, b, 0xE00A1A33);
            g.centeredText(this.font, "MOD MENU", this.width / 2, t + 8, 0xFF66CCFF);
            super.extractRenderState(g, mx, my, delta);
        }
    }
}
