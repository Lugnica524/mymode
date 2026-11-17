package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;
import java.util.List;

public class Hacks {
    private static int ticks = 0;
    public static double fx, fy, fz;
    private static boolean freeWas = false;
    private static boolean fbWas = false;
    private static boolean stepWas = false;
    private static final Item[] last = new Item[9];
    private static Field delayField;

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

    private static int getSel(LocalPlayer p) {
        Inventory inv = p.getInventory();
        try {
            return ((Number) Inventory.class.getMethod("getSelectedSlot").invoke(inv)).intValue();
        } catch (Throwable ignored) { }
        try {
            Field f = Inventory.class.getDeclaredField("selected");
            f.setAccessible(true);
            return f.getInt(inv);
        } catch (Throwable ignored) { }
        return 0;
    }

    private static void setSel(LocalPlayer p, int i) {
        Inventory inv = p.getInventory();
        try {
            Inventory.class.getMethod("setSelectedSlot", int.class).invoke(inv, i);
            return;
        } catch (Throwable ignored) { }
        try {
            Field f = Inventory.class.getDeclaredField("selected");
            f.setAccessible(true);
            f.setInt(inv, i);
        } catch (Throwable ignored) { }
    }

    public static void tick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null || mc.level == null || mc.gameMode == null) return;
        ticks++;
        if (on("Freecam")) {
            if (!freeWas) { fx = p.getX(); fy = p.getEyeY(); fz = p.getZ(); freeWas = true; }
            double[] fd = dir(mc);
            fx += fd[0] * 0.9;
            fz += fd[1] * 0.9;
            if (mc.options.keyJump.isDown()) fy += 0.9;
            if (mc.options.keyShift.isDown()) fy -= 0.9;
        } else {
            freeWas = false;
        }

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

        // Step
        if (on("Step")) {
            AttributeInstance ai = p.getAttribute(Attributes.STEP_HEIGHT);
            if (ai != null) ai.setBaseValue(2.0);
            stepWas = true;
        } else if (stepWas) {
            AttributeInstance ai = p.getAttribute(Attributes.STEP_HEIGHT);
            if (ai != null) ai.setBaseValue(0.6);
            stepWas = false;
        }

        // Jesus
        if (on("Jesus") && !on("Flight") && (p.isInWater() || p.isInLava()) && !mc.options.keyShift.isDown()) {
            Vec3 m = p.getDeltaMovement();
            p.setDeltaMovement(m.x, 0.12, m.z);
        }

        // Spider
        if (on("Spider") && !on("Flight") && p.horizontalCollision) {
            Vec3 m = p.getDeltaMovement();
            p.setDeltaMovement(m.x, 0.25, m.z);
        }

        // Velocity (anti knockback)
        if (on("Velocity") && !on("Flight") && p.hurtTime > 7) {
            Vec3 m = p.getDeltaMovement();
            p.setDeltaMovement(0.0, m.y, 0.0);
        }

        // NoFall
        if (on("NoFall") && p.fallDistance > 2.5 && mc.getConnection() != null) {
            mc.getConnection().send(new ServerboundMovePlayerPacket.StatusOnly(true, p.horizontalCollision));
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

        // TriggerBot
        if (on("TriggerBot") && mc.screen == null && mc.hitResult instanceof EntityHitResult eh
                && eh.getEntity() instanceof LivingEntity le && le.isAlive()
                && p.getAttackStrengthScale(0.0f) >= 1.0f) {
            mc.gameMode.attack(p, le);
            p.swing(InteractionHand.MAIN_HAND);
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

        // AutoTool
        if (on("AutoTool") && mc.screen == null && mc.options.keyAttack.isDown()
                && mc.hitResult instanceof BlockHitResult bh && bh.getType() == HitResult.Type.BLOCK) {
            var st = mc.level.getBlockState(bh.getBlockPos());
            int cur = getSel(p);
            int best = cur;
            float bs = p.getInventory().getItem(cur).getDestroySpeed(st);
            for (int i = 0; i < 9; i++) {
                float s = p.getInventory().getItem(i).getDestroySpeed(st);
                if (s > bs) { bs = s; best = i; }
            }
            if (best != cur) setSel(p, best);
        }

        // FastPlace
        if (on("FastPlace")) {
            try {
                if (delayField == null) {
                    delayField = Minecraft.class.getDeclaredField("rightClickDelay");
                    delayField.setAccessible(true);
                }
                delayField.setInt(mc, 0);
            } catch (Throwable ignored) { }
        }

        // AutoReplenish
        if (on("AutoReplenish") && mc.screen == null) {
            for (int i = 0; i < 9; i++) {
                var st = p.getInventory().getItem(i);
                if (st.isEmpty() && last[i] != null) {
                    for (int j = 9; j < 36; j++) {
                        if (p.getInventory().getItem(j).is(last[i])) {
                            mc.gameMode.handleContainerInput(p.containerMenu.containerId, j, i, ContainerInput.SWAP, p);
                            break;
                        }
                    }
                }
                last[i] = st.isEmpty() ? null : st.getItem();
            }
        }

        // Scaffold
        if (on("Scaffold") && ticks % 2 == 0 && mc.screen == null) {
            BlockPos pos = p.blockPosition().below();
            if (mc.level.getBlockState(pos).isAir()) {
                int slot = -1;
                for (int i = 0; i < 9; i++) {
                    if (p.getInventory().getItem(i).getItem() instanceof BlockItem) { slot = i; break; }
                }
                if (slot >= 0) {
                    for (Direction d : Direction.values()) {
                        BlockPos nb = pos.relative(d);
                        if (mc.level.getBlockState(nb).isAir()) continue;
                        Direction face = d.getOpposite();
                        Vec3 hv = Vec3.atCenterOf(nb).add(face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
                        setSel(p, slot);
                        mc.gameMode.useItemOn(p, InteractionHand.MAIN_HAND, new BlockHitResult(hv, face, nb, false));
                        p.swing(InteractionHand.MAIN_HAND);
                        break;
                    }
                }
            }
        }
    }
}
