package com.example.client;

import java.util.ArrayList;
import java.util.List;

public class ShulkerKit {
    private static String item(int slot, String id, int count, String comps) {
        return "{slot:" + slot + ",item:{id:\"minecraft:" + id + "\",count:" + count
                + (comps == null ? "" : ",components:{" + comps + "}") + "}}";
    }

    private static String enc(String e) {
        return "\"minecraft:enchantments\":{" + e + "}";
    }

    private static String chest(int slot, List<String> items) {
        return item(slot, "chest", 1, "\"minecraft:container\":[" + String.join(",", items) + "]");
    }

    public static String build() {
        String base = "\"minecraft:protection\":4,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:thorns\":3";
        String helmet = enc(base + ",\"minecraft:respiration\":3,\"minecraft:aqua_affinity\":1");
        String chestplate = enc(base);
        String leggings = enc(base + ",\"minecraft:swift_sneak\":3");
        String boots = enc(base + ",\"minecraft:feather_falling\":4,\"minecraft:depth_strider\":3,\"minecraft:soul_speed\":3");
        String elytra = enc("\"minecraft:unbreaking\":3,\"minecraft:mending\":1");
        String sword = enc("\"minecraft:sharpness\":5,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:fire_aspect\":2,\"minecraft:looting\":3,\"minecraft:sweeping_edge\":3,\"minecraft:knockback\":2");
        String pick = enc("\"minecraft:efficiency\":5,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:fortune\":3");
        String axe = enc("\"minecraft:sharpness\":5,\"minecraft:efficiency\":5,\"minecraft:unbreaking\":3,\"minecraft:mending\":1");
        String spear = enc("\"minecraft:sharpness\":5,\"minecraft:fire_aspect\":2,\"minecraft:looting\":3,\"minecraft:knockback\":2,\"minecraft:unbreaking\":3,\"minecraft:mending\":1,\"minecraft:lunge\":3");
        String trident = enc("\"minecraft:impaling\":5,\"minecraft:loyalty\":3,\"minecraft:channeling\":1,\"minecraft:unbreaking\":3,\"minecraft:mending\":1");
        String mace = enc("\"minecraft:density\":5,\"minecraft:wind_burst\":3,\"minecraft:fire_aspect\":2,\"minecraft:unbreaking\":3,\"minecraft:mending\":1");

        // Sandik 1: malzeme
        List<String> supplies = new ArrayList<>();
        supplies.add(item(0, "wind_charge", 64, null));
        supplies.add(item(1, "wind_charge", 64, null));
        supplies.add(item(2, "enchanted_golden_apple", 64, null));
        supplies.add(item(3, "golden_carrot", 64, null));

        // Sandik 2: firlatilan iksirler
        String[] pots = {
            "strong_swiftness", "strong_regeneration", "strong_healing", "strong_strength",
            "long_fire_resistance", "long_invisibility", "long_night_vision", "strong_harming",
            "strong_poison", "long_weakness", "strong_slowness", "long_slow_falling"
        };
        List<String> potions = new ArrayList<>();
        for (int i = 0; i < pots.length; i++) {
            potions.add(item(i, "splash_potion", 1, "\"minecraft:potion_contents\":{potion:\"minecraft:" + pots[i] + "\"}"));
        }

        // Sandik 3: totemler
        List<String> totems = new ArrayList<>();
        for (int i = 0; i < 27; i++) totems.add(item(i, "totem_of_undying", 1, null));

        List<String> main = new ArrayList<>();
        main.add(item(0, "netherite_helmet", 1, helmet));
        main.add(item(1, "netherite_chestplate", 1, chestplate));
        main.add(item(2, "netherite_leggings", 1, leggings));
        main.add(item(3, "netherite_boots", 1, boots));
        main.add(item(4, "elytra", 1, elytra));
        main.add(item(5, "netherite_sword", 1, sword));
        main.add(item(6, "netherite_pickaxe", 1, pick));
        main.add(item(7, "netherite_shovel", 1, pick));
        main.add(item(8, "netherite_axe", 1, axe));
        main.add(item(9, "netherite_spear", 1, spear));
        main.add(item(10, "trident", 1, trident));
        main.add(item(11, "mace", 1, mace));
        main.add(item(12, "firework_rocket", 64, "\"minecraft:fireworks\":{flight_duration:3}"));
        main.add(chest(13, supplies));
        main.add(chest(14, potions));
        main.add(chest(15, totems));

        return "give @s shulker_box[container=[" + String.join(",", main) + "]]";
    }
}
