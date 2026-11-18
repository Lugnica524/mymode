package com.example.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Overlay2D {
    private static Method camMethod;
    private static boolean camTried = false;
    private static List<LivingEntity> ents = new ArrayList<>();
    private static List<Object[]> boxes = new ArrayList<>();
    private static int tick = 0;
    private static final Matrix4f M = new Matrix4f();
    private static final Vector4f V = new Vector4f();

    private static Camera findCamera(Minecraft mc) {
        try {
            if (!camTried) {
                camTried = true;
                for (Method me : mc.gameRenderer.getClass().getMethods()) {
                    if (me.getParameterCount() == 0 && me.getReturnType() == Camera.class) {
                        camMethod = me;
                        break;
                    }
                }
            }
            if (camMethod != null) return (Camera) camMethod.invoke(mc.gameRenderer);
        } catch (Throwable ignored) { }
        return null;
    }

    public static void tick(Minecraft mc) {
        if (mc.player == null || mc.level == null) {
            ents = new ArrayList<>();
            boxes = new ArrayList<>();
            return;
        }
        tick++;
        boolean ent = Hacks.on("Tracers") || Hacks.on("Nametags");
        if (ent && tick % 5 == 0) {
            List<LivingEntity> list = new ArrayList<>(mc.level.getEntitiesOfClass(
                    LivingEntity.class, mc.player.getBoundingBox().inflate(48)));
            list.removeIf(e -> e == mc.player || !e.isAlive());
            list.sort(Comparator.comparingDouble(e -> e.distanceToSqr(mc.player)));
            ents = new ArrayList<>(list.subList(0, Math.min(12, list.size())));
        } else if (!ent) {
            ents = new ArrayList<>();
        }
        if (Hacks.on("ChestESP")) {
            if (tick % 20 == 0) {
                List<Object[]> out = new ArrayList<>();
                int cx = mc.player.blockPosition().getX() >> 4;
                int cz = mc.player.blockPosition().getZ() >> 4;
                for (int dx = -4; dx <= 4; dx++) {
                    for (int dz = -4; dz <= 4; dz++) {
                        LevelChunk ch = mc.level.getChunk(cx + dx, cz + dz);
                        for (BlockEntity be : ch.getBlockEntities().values()) {
                            int c = 0;
                            String name = "";
                            if (be instanceof EnderChestBlockEntity) { c = 0xFFAA55FF; name = "Ender"; }
                            else if (be instanceof ShulkerBoxBlockEntity) { c = 0xFFFF55FF; name = "Shulker"; }
                            else if (be instanceof ChestBlockEntity) { c = 0xFFFFAA33; name = "Sandık"; }
                            if (c != 0) out.add(new Object[]{be.getBlockPos(), c, name});
                        }
                    }
                }
                boxes = out;
            }
        } else {
            boxes = new ArrayList<>();
        }
    }

    private static float[] project(Vec3 cp, double x, double y, double z, int w, int h) {
        V.set((float) (x - cp.x), (float) (y - cp.y), (float) (z - cp.z), 1.0f);
        M.transform(V);
        if (V.w <= 0.01f) return null;
        return new float[]{(V.x / V.w * 0.5f + 0.5f) * w, (1.0f - (V.y / V.w * 0.5f + 0.5f)) * h};
    }

    private static void line(GuiGraphicsExtractor g, float x0, float y0, float x1, float y1, int col) {
        float dx = x1 - x0, dy = y1 - y0;
        int n = (int) Math.min(Math.hypot(dx, dy) / 5.0, 45);
        for (int i = 1; i <= n; i++) {
            float f = (float) i / n;
            int px = (int) (x0 + dx * f);
            int py = (int) (y0 + dy * f);
            g.fill(px, py, px + 2, py + 2, col);
        }
    }

    public static void draw(GuiGraphicsExtractor g, Minecraft mc) {
        boolean tr = Hacks.on("Tracers");
        boolean nt = Hacks.on("Nametags");
        boolean ce = Hacks.on("ChestESP");
        if ((!tr && !nt && !ce) || mc.player == null || mc.level == null || mc.options.hideGui) return;
        Camera cam = findCamera(mc);
        if (cam == null) return;
        cam.getViewRotationProjectionMatrix(M);
        Vec3 cp = cam.position();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        float ox = w / 2.0f, oy = h;

        for (LivingEntity e : ents) {
            if (!e.isAlive()) continue;
            int col = e instanceof Player ? 0xFFFF66CC : 0xFF66CCFF;
            if (tr) {
                float[] s = project(cp, e.getX(), e.getY() + e.getBbHeight() * 0.5, e.getZ(), w, h);
                if (s != null) line(g, ox, oy, s[0], s[1], col);
            }
            if (nt) {
                float[] s = project(cp, e.getX(), e.getY() + e.getBbHeight() + 0.4, e.getZ(), w, h);
                if (s != null) {
                    String label = e.getName().getString() + " " + (int) e.getHealth();
                    g.text(mc.font, label, (int) s[0] - mc.font.width(label) / 2, (int) s[1], col);
                }
            }
        }

        if (ce) {
            for (Object[] b : boxes) {
                net.minecraft.core.BlockPos bp = (net.minecraft.core.BlockPos) b[0];
                int col = (Integer) b[1];
                float[] s = project(cp, bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5, w, h);
                if (s == null) continue;
                line(g, ox, oy, s[0], s[1], col);
                int dist = (int) Math.sqrt(bp.distSqr(mc.player.blockPosition()));
                String label = b[2] + " " + dist + "m";
                g.text(mc.font, label, (int) s[0] - mc.font.width(label) / 2, (int) s[1] - 10, col);
            }
        }
    }
}
