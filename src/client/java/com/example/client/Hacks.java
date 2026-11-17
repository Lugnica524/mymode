package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Hacks {
    private static int ticks = 0;
    private static boolean espWas = false;
    private static boolean fbWas = false;

    private static boolean on(String n) {
        return ExampleModClient.MODULES.getOrDefault(n, false);
    }

    private static double[] dir(Minecraft mc) {
        var o = mc.options;
        double f = (o.keyUp.isDown() ? 1 : 0) - (o.keyDown.isDown() ? 1 : 0);
        double s = (o.keyLeft.isDown() ? 1 : 0) - (o.keyRight.isDown() ? 1 : 0);
        if (f == 0 && s == 0) return new double[]{0, 0};
        double r = Math.toRadians(mc.player.getYRot());
        double x = -Math.sin(r) * f + Math.cos(r) * s;
        double z = Math.cos(r) * f + Math.sin(r) * s;
        double l = Math.sqrt(x * x + z * z);
        return new double[]{x / l, z / l};
    }

    public static void tick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null || mc.level == null || mc.gameMode == null) return;
        ticks++;

        // Flight / Speed
        if (on("Flight")) {
            double[] d = dir(mc);
            double y = mc.options.keyJump.isDown() ? 0.5 : (mc.options.keyShift.isDown() ? -0.5 : 0.0);
            p.setDeltaMovement(d[0] * 0.8, y, d[1] * 0.8);
        } else if (on("Speed")) {
            double[] d = dir(mc);
            if (d[0] != 0 || d[1] != 0) {
                Vec3 m = p.getDeltaMovement();
                p.setDeltaMovement(d[0] * 0.45, m.y, d[1] * 0.45);
            }
        }

        // AutoWalk / AutoSprint
        if (on("AutoWalk")) mc.options.keyUp.setDown(true);
        if (on("AutoSprint") && mc.options.keyUp.isDown()) p.setSprinting(true);

        // Fullbright
        if (on("Fullbright")) {
            if (ticks % 20 == 0 || !fbWas) {
                p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1000, 0, false, false, false));
            }
            fbWas = true;
        } else if (fbWas) {
            p.removeEffect(MobEffects.NIGHT_VISION);
            fbWas = false;
        }

        // ESP
        if (on("ESP")) {
            List<LivingEntity> list = mc.level.getEntitiesOfClass(LivingEntity.class, p.getBoundingBox().inflate(64));
            for (LivingEntity e : list) {
                if (e != p) e.setGlowingTag(true);
            }
            espWas = true;
        } else if (espWas) {
            List<LivingEntity> list = mc.level.getEntitiesOfClass(LivingEntity.class, p.getBoundingBox().inflate(128));
            for (LivingEntity e : list) {
                if (e != p) e.setGlowingTag(false);
            }
            espWas = false;
        }

        // KillAura
        if (on("KillAura") && ticks % 8 == 0) {
            List<LivingEntity> list = mc.level.getEntitiesOfClass(LivingEntity.class, p.getBoundingBox().inflate(4.5));
            LivingEntity best = null;
            double bd = 1e9;
            for (LivingEntity e : list) {
                if (e == p || !e.isAlive()) continue;
                double dd = e.distanceToSqr(p);
                if (dd < bd) { bd = dd; best = e; }
            }
            if (best != null) {
                mc.gameMode.attack(p, best);
                p.swing(InteractionHand.MAIN_HAND);
            }
        }

        // AutoTotem
        if (on("AutoTotem") && ticks % 5 == 0 && !p.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) {
            for (int i = 0; i < 36; i++) {
                if (p.getInventory().getItem(i).is(Items.TOTEM_OF_UNDYING)) {
                    int slot = i < 9 ? 36 + i : i;
                    mc.gameMode.handleContainerInput(p.containerMenu.containerId, slot, 40, ContainerInput.SWAP, p);
                    break;
                }
            }
        }
    }
}
