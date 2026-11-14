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
        for (String n : new String[]{"Flight", "Speed", "Fullbright", "ESP", "KillAura", "AutoTotem"}) {
            MODULES.put(n, false);
        }
    }

    private static final String SHULKER_CMD =
        "give @s shulker_box[container=[" +
        "{slot:0,item:{id:\"minecraft:netherite_helmet\",count:1,components:{\"minecraft:enchantments\":{\"minecraft:protection\":4,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:thorns\":3}}}}," +
        "{slot:1,item:{id:\"minecraft:netherite_chestplate\",count:1,components:{\"minecraft:enchantments\":{\"minecraft:protection\":4,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:thorns\":3}}}}," +
        "{slot:2,item:{id:\"minecraft:netherite_leggings\",count:1,components:{\"minecraft:enchantments\":{\"minecraft:protection\":4,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:thorns\":3}}}}," +
        "{slot:3,item:{id:\"minecraft:netherite_boots\",count:1,components:{\"minecraft:enchantments\":{\"minecraft:protection\":4,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:thorns\":3}}}}," +
        "{slot:4,item:{id:\"minecraft:netherite_sword\",count:1,components:{\"minecraft:enchantments\":{\"minecraft:sharpness\":5,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:fire_aspect\":2,\"minecraft:looting\":3}}}}," +
        "{slot:5,item:{id:\"minecraft:netherite_pickaxe\",count:1,components:{\"minecraft:enchantments\":{\"minecraft:efficiency\":5,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:fortune\":3}}}}," +
        "{slot:6,item:{id:\"minecraft:netherite_axe\",count:1,components:{\"minecraft:enchantments\":{\"minecraft:efficiency\":5,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:sharpness\":5}}}}," +
        "{slot:7,item:{id:\"minecraft:trident\",count:1,components:{\"minecraft:enchantments\":{\"minecraft:impaling\":5,\"minecraft:loyalty\":3,\"minecraft:unbreaking\":3,\"minecraft:mending\":1}}}}," +
        "{slot:8,item:{id:\"minecraft:mace\",count:1,components:{\"minecraft:enchantments\":{\"minecraft:density\":5,\"minecraft:breach\":4,\"minecraft:wind_burst\":3,\"minecraft:unbreaking\":3,\"minecraft:mending\":1}}}}," +
        "{slot:9,item:{id:\"minecraft:enchanted_golden_apple\",count:16}}," +
        "{slot:10,item:{id:\"minecraft:totem_of_undying\",count:8}}" +
        "]]";

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
            int y = this.height / 2 - 95;
            for (String name : MODULES.keySet()) {
                addRenderableWidget(Button.builder(label(name), b -> {
                    MODULES.put(name, !MODULES.get(name));
                    b.setMessage(label(name));
                }).bounds(x, y, 160, 20).build());
                y += 24;
            }

            addRenderableWidget(Button.builder(Component.literal("Give OP Shulker"), b -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.getConnection() != null) {
                    mc.getConnection().sendCommand(SHULKER_CMD);
                }
            }).bounds(x, y + 6, 160, 20).build());
        }

        private Component label(String name) {
            return Component.literal(name + ": " + (MODULES.get(name) ? "ON" : "OFF"));
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
            int l = this.width / 2 - 95, r = this.width / 2 + 95;
            int t = this.height / 2 - 125, b = this.height / 2 + 120;
            g.fill(l - 2, t - 2, r + 2, b + 2, 0xFF1E90FF);
            g.fill(l, t, r, b, 0xE00A1A33);
            g.centeredText(this.font, "MOD MENU", this.width / 2, t + 8, 0xFF66CCFF);
            super.extractRenderState(g, mx, my, delta);
        }
    }
}
