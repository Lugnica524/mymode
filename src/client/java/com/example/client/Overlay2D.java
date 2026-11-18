package com.example.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.lang.reflect.Method;

public class Overlay2D {
    private static Camera findCamera(Minecraft mc) {
        try {
            for (Method me : mc.gameRenderer.getClass().getMethods()) {
                if (me.getParameterCount() == 0 && me.getReturnType() == Camera.class) {
                    return (Camera) me.invoke(mc.gameRenderer);
                }
            }
        } catch (Throwable ignored) { }
        return null;
    }

    private static float[] project(Matrix4f m, Vec3 cp, double x, double y, double z, int w, int h) {
        Vector4f v = new Vector4f((float) (x - cp.x), (float) (y - cp.y), (float) (z - cp.z), 1.0f);
        m.transform(v);
        if (v.w <= 0.01f) return null;
        float nx = v.x / v.w;
        float ny = v.y / v.w;
        return new float[]{(nx * 0.5f + 0.5f) * w, (1.0f - (ny * 0.5f + 0.5f)) * h};
    }

    public static void draw(GuiGraphicsExtractor g, Minecraft mc) {
        boolean tr = Hacks.on("Tracers");
        boolean nt = Hacks.on("Nametags");
        if ((!tr && !nt) || mc.player == null || mc.level == null || mc.options.hideGui) return;
        Camera cam = findCamera(mc);
        if (cam == null) return;
        Matrix4f m = cam.getViewRotationProjectionMatrix(new Matrix4f());
        Vec3 cp = cam.position();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        for (LivingEntity e : mc.level.getEntitiesOfClass(LivingEntity.class, mc.player.getBoundingBox().inflate(64))) {
            if (e == mc.player || !e.isAlive()) continue;
            int col = e instanceof Player ? 0xFFFF66CC : 0xFF66CCFF;
            if (tr) {
                float[] s = project(m, cp, e.getX(), e.getY() + e.getBbHeight() * 0.5, e.getZ(), w, h);
                if (s != null) {
                    float x0 = w / 2.0f, y0 = h;
                    float dx = s[0] - x0, dy = s[1] - y0;
                    int n = (int) Math.min(Math.hypot(dx, dy) / 3.0, 150);
                    for (int i = 0; i <= n; i++) {
                        int px = (int) (x0 + dx * i / Math.max(n, 1));
                        int py = (int) (y0 + dy * i / Math.max(n, 1));
                        g.fill(px, py, px + 1, py + 1, col);
                    }
                }
            }
            if (nt) {
                float[] s = project(m, cp, e.getX(), e.getY() + e.getBbHeight() + 0.4, e.getZ(), w, h);
                if (s != null) {
                    String label = e.getName().getString() + " " + (int) e.getHealth();
                    g.text(mc.font, label, (int) s[0] - mc.font.width(label) / 2, (int) s[1], col);
                }
            }
        }
    }
}
