package com.example.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExampleModClient implements ClientModInitializer {

    public static final Map<String, Boolean> MODULES = new LinkedHashMap<>();
    static {
        for (String n : new String[]{"Flight", "Speed", "Fullbright", "ESP", "KillAura", "AutoTotem", "AutoSprint", "AutoWalk"}) {
            MODULES.put(n, false);
        }
    }

    private static String item(int slot, String id, int count, String comps) {
        return "{slot:" + slot + ",item:{id:\"minecraft:" + id + "\",count:" + count
                + (comps == null ? "" : ",components:{" + comps + "}") + "}}";
    }

    private static String enc(String e) {
        return "\"minecraft:enchantments\":{" + e + "}";
    }

    public static String buildShulkerCmd() {
        String armorEnch = enc("\"minecraft:protection\":4,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:thorns\":3");

        StringBuilder totems = new StringBuilder();
        for (int i = 0; i < 27; i++) {
            if (i > 0) totems.append(",");
            totems.append(item(i, "totem_of_undying", 1, null));
        }

        String[] pots = {
            "strong_healing", "strong_strength", "strong_regeneration", "long_fire_resistance",
            "strong_swiftness", "long_night_vision", "long_water_breathing", "long_invisibility",
            "long_slow_falling", "strong_leaping"
        };
        StringBuilder potions = new StringBuilder();
        for (int i = 0; i < pots.length; i++) {
            if (i > 0) potions.append(",");
            potions.append(item(i, "potion", 1, "\"minecraft:potion_contents\":{potion:\"minecraft:" + pots[i] + "\"}"));
        }

        StringBuilder sb = new StringBuilder("give @s shulker_box[container=[");
        sb.append(item(0, "netherite_helmet", 1, armorEnch)).append(",");
        sb.append(item(1, "netherite_chestplate", 1, armorEnch)).append(",");
        sb.append(item(2, "netherite_leggings", 1, armorEnch)).append(",");
        sb.append(item(3, "netherite_boots", 1, armorEnch)).append(",");
        sb.append(item(4, "netherite_sword", 1, enc("\"minecraft:sharpness\":5,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:fire_aspect\":2,\"minecraft:looting\":3"))).append(",");
        sb.append(item(5, "netherite_pickaxe", 1, enc("\"minecraft:efficiency\":5,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:fortune\":3"))).append(",");
        sb.append(item(6, "netherite_axe", 1, enc("\"minecraft:efficiency\":5,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:sharpness\":5"))).append(",");
        sb.append(item(7, "trident", 1, enc("\"minecraft:impaling\":5,\"minecraft:loyalty\":3,\"minecraft:unbreaking\":3,\"minecraft:mending\":1"))).append(",");
        sb.append(item(8, "mace", 1, enc("\"minecraft:density\":5,\"minecraft:breach\":4,\"minecraft:wind_burst\":3,\"minecraft:unbreaking\":3,\"minecraft:mending\":1"))).append(",");
        sb.append(item(9, "enchanted_golden_apple", 64, null)).append(",");
        sb.append(item(10, "golden_carrot", 64, null)).append(",");
        sb.append(item(11, "chest", 1, "\"minecraft:container\":[" + totems + "]")).append(",");
        sb.append(item(12, "chest", 1, "\"minecraft:container\":[" + potions + "]"));
        sb.append("]]");
        return sb.toString();
    }

    private static KeyMapping menuKey;

    @Override
    public void onInitializeClient() {
        menuKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.modid.menu", InputConstants.KEY_K, KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            Hacks.tick(mc);
            while (menuKey.consumeClick()) {
                mc.setScreenAndShow(new CleanMenu());
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
            int y = this.height / 2 - 130;
            for (String name : MODULES.keySet()) {
                addRenderableWidget(Button.builder(label(name), b -> {
                    MODULES.put(name, !MODULES.get(name));
                    b.setMessage(label(name));
                }).bounds(x, y, 160, 20).build());
                y += 22;
            }

            addRenderableWidget(Button.builder(Component.literal("Give OP Shulker"), b -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.getConnection() != null) {
                    mc.getConnection().sendCommand(buildShulkerCmd());
                }
            }).bounds(x, y + 6, 160, 20).build());
        }

        private Component label(String name) {
            return Component.literal(name + ": " + (MODULES.get(name) ? "ON" : "OFF"));
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
            int l = this.width / 2 - 95, r = this.width / 2 + 95;
            int t = this.height / 2 - 160, b = this.height / 2 + 90;
            g.fill(l - 2, t - 2, r + 2, b + 2, 0xFF1E90FF);
            g.fill(l, t, r, b, 0xE00A1A33);
            g.centeredText(this.font, "MOD MENU", this.width / 2, t + 8, 0xFF66CCFF);
            super.extractRenderState(g, mx, my, delta);
        }
    }
}
