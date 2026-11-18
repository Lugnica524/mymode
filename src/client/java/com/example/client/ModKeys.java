package com.example.client;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModKeys {
    private static final Map<String, KeyMapping> KEYS = new LinkedHashMap<>();

    public static void register() {
        for (String n : ExampleModClient.MODULES.keySet()) {
            KEYS.put(n, KeyMappingHelper.registerKeyMapping(
                    new KeyMapping("key.modid.toggle." + n, -1, KeyMapping.Category.MISC)));
        }
    }

    public static void tick(Minecraft mc) {
        for (var e : KEYS.entrySet()) {
            while (e.getValue().consumeClick()) {
                ExampleModClient.MODULES.put(e.getKey(), !ExampleModClient.MODULES.get(e.getKey()));
                Config.save();
            }
        }
    }
}
