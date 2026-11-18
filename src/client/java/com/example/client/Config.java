package com.example.client;

import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Config {
    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("modid-hacks.properties");
    }

    public static void load() {
        try {
            Path f = file();
            if (!Files.exists(f)) return;
            Properties p = new Properties();
            try (InputStream in = Files.newInputStream(f)) {
                p.load(in);
            }
            for (String k : ExampleModClient.MODULES.keySet()) {
                if (p.containsKey(k)) {
                    ExampleModClient.MODULES.put(k, Boolean.parseBoolean(p.getProperty(k)));
                }
            }
            for (Settings.S s : Settings.ALL) {
                String v = p.getProperty("s." + s.key());
                if (v != null) Settings.put(s.key(), Double.parseDouble(v));
            }
        } catch (Throwable ignored) { }
    }

    public static void save() {
        try {
            Properties p = new Properties();
            for (var e : ExampleModClient.MODULES.entrySet()) {
                p.setProperty(e.getKey(), String.valueOf(e.getValue()));
            }
            for (Settings.S s : Settings.ALL) {
                p.setProperty("s." + s.key(), String.valueOf(Settings.get(s.key())));
            }
            try (OutputStream out = Files.newOutputStream(file())) {
                p.store(out, "mod ayarlari");
            }
        } catch (Throwable ignored) { }
    }
}
