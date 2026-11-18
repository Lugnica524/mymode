package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Hacks2 {
    public static final List<Packet<?>> BLINK = new ArrayList<>();
    private static int t = 0;
    private static boolean reachWas = false;
    private static boolean eating = false;
    private static BlockPos nukeTarget = null;
    private static Field destroyDelay, destroyProgress;
    private static boolean fbTried = false;

    static boolean onGround(LocalPlayer p) {
        try {
            return (boolean) Entity.class.getMethod("onGround").invoke(p);
        } catch (Throwable ignored) { }
        try {
            return (boolean) Entity.class.getMethod("isOnGround").invoke(p);
        } catch (Throwable ignored) { }
        return false;
    }

    static String path(ItemStack s) {
        return BuiltInRegistries.ITEM.getKey(s.getItem()).getPath();
    }

    static int rank(String n) {
        if (n.contains("netherite")) return 7;
        if (n.contains("diamond")) return 6;
        if (n.contains("iron")) return 5;
        if (n.contains("copper") || n.contains("chainmail")) return 4;
        if (n.contains("stone")) return 3;
        if (n.contains("golden")) return 2;
        return 1;
    }

    public static void crit(Minecraft mc, LocalPlayer p) {
        if (!Hacks.on("Criticals") || mc.getConnection() == null || !onGround(p)) return;
        try {
            var c = ServerboundMovePlayerPacket.Pos.class.getConstructor(
                    double.class, double.class, double.class, boolean.class, boolean.class);
            mc.getConnection().send((Packet<?>) c.newInstance(p.getX(), p.getY() + 0.0625, p.getZ(), false, p.horizontalCollision));
            mc.getConnection().send((Packet<?>) c.newInstance(p.getX(), p.getY(), p.getZ(), false, p.horizontalCollision));
        } catch (Throwable ignored) { }
    }

    public static void tick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null || mc.level == null || mc.gameMode == null) return;
        t++;
        Vec3 m = p.getDeltaMovement();
        double[] d = Hacks.dir(mc);
        boolean moving = d[0] != 0 || d[1] != 0;
        boolean ground = onGround(p);
        boolean fly = Hacks.on("Flight") || Hacks.on("Freecam");

        // Blink: bekleyen paketleri birak
        if (!Hacks.on("Blink") && !BLINK.isEmpty() && mc.getConnection() != null) {
            List<Packet<?>> copy = new ArrayList<>(BLINK);
            BLINK.clear();
            for (Packet<?> pk : copy) mc.getConnection().send(pk);
        }

        // Hareket
        if (!fly && moving) {
            if (Hacks.on("Strafe") && !ground) {
                double sp = Math.hypot(m.x, m.z);
                if (sp > 0.05) p.setDeltaMovement(d[0] * sp, m.y, d[1] * sp);
            }
            if (Hacks.on("LongJump") && ground && mc.options.keyJump.isDown()) {
                double b = Settings.get("LongJumpBoost");
                p.setDeltaMovement(d[0] * b, 0.42, d[1] * b);
            }
            if (Hacks.on("AutoJump") && ground && p.horizontalCollision) {
                p.setDeltaMovement(m.x, 0.42, m.z);
            }
            if (ground && (Hacks.on("Parkour") || Hacks.on("SafeWalk"))) {
                BlockPos ahead = BlockPos.containing(p.getX() + d[0] * 0.5, p.getY() - 0.5, p.getZ() + d[1] * 0.5);
                if (mc.level.getBlockState(ahead).isAir()) {
                    if (Hacks.on("Parkour")) p.setDeltaMovement(m.x, 0.42, m.z);
                    else p.setDeltaMovement(0.0, m.y, 0.0);
                }
            }
        }
        if (!fly && Hacks.on("FastFall") && !ground && m.y < -0.2) {
            p.setDeltaMovement(m.x, -Settings.get("FastFallSpeed"), m.z);
        }

        // Reach
        AttributeInstance er = p.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        AttributeInstance br = p.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if (Hacks.on("Reach")) {
            double r = Settings.get("Reach");
            if (er != null) er.setBaseValue(r);
            if (br != null) br.setBaseValue(4.5 + (r - 3.0));
            reachWas = true;
        } else if (reachWas) {
            if (er != null) er.setBaseValue(3.0);
            if (br != null) br.setBaseValue(4.5);
            reachWas = false;
        }

        // NoWeather
        if (Hacks.on("NoWeather")) {
            mc.level.setRainLevel(0.0f);
            mc.level.setThunderLevel(0.0f);
        }

        // FastBreak
        if (Hacks.on("FastBreak")) {
            try {
                if (!fbTried) {
                    fbTried = true;
                    destroyDelay = MultiPlayerGameMode.class.getDeclaredField("destroyDelay");
                    destroyDelay.setAccessible(true);
                    destroyProgress = MultiPlayerGameMode.class.getDeclaredField("destroyProgress");
                    destroyProgress.setAccessible(true);
                }
                if (destroyDelay != null) destroyDelay.setInt(mc.gameMode, 0);
                if (destroyProgress != null && destroyProgress.getFloat(mc.gameMode) > 0.65f) {
                    destroyProgress.setFloat(mc.gameMode, 1.0f);
                }
            } catch (Throwable ignored) { }
        }

        // Nuker / Lawnmower
        boolean nuke = Hacks.on("Nuker");
        boolean mow = Hacks.on("Lawnmower");
        if ((nuke || mow) && mc.screen == null) {
            int r = (int) Settings.get("NukerRange");
            if (nukeTarget != null && !mc.level.getBlockState(nukeTarget).isAir()
                    && nukeTarget.distSqr(p.blockPosition()) <= (r + 1) * (r + 1)) {
                mc.gameMode.continueDestroyBlock(nukeTarget, Direction.UP);
                p.swing(InteractionHand.MAIN_HAND);
            } else {
                nukeTarget = null;
                if (t % (int) Settings.get("NukerDelay") == 0) {
                    BlockPos base = p.blockPosition();
                    BlockPos best = null;
                    double bd = 1e9;
                    for (int x = -r; x <= r; x++) {
                        for (int y = -r; y <= r; y++) {
                            for (int z = -r; z <= r; z++) {
                                BlockPos bp = base.offset(x, y, z);
                                var st = mc.level.getBlockState(bp);
                                if (st.isAir() || !st.getFluidState().isEmpty()) continue;
                                float hard = st.getDestroySpeed(mc.level, bp);
                                if (hard < 0) continue;
                                if (!nuke && hard > 0) continue;
                                double dd = bp.distSqr(base);
                                if (dd < bd) { bd = dd; best = bp; }
                            }
                        }
                    }
                    if (best != null) {
                        nukeTarget = best;
                        mc.gameMode.startDestroyBlock(best, Direction.UP);
                        p.swing(InteractionHand.MAIN_HAND);
                    }
                }
            }
        } else {
            nukeTarget = null;
        }

        // AutoRespawn
        if (Hacks.on("AutoRespawn") && mc.screen instanceof DeathScreen) {
            p.respawn();
        }

        // AutoEat
        if (Hacks.on("AutoEat")) {
            boolean hungry = p.getFoodData().getFoodLevel() <= (int) Settings.get("AutoEatLevel");
            int slot = -1;
            if (hungry && mc.screen == null) {
                for (int i = 0; i < 9; i++) {
                    ItemStack st = p.getInventory().getItem(i);
                    if (st.has(DataComponents.FOOD) && !st.is(Items.ROTTEN_FLESH) && !st.is(Items.SPIDER_EYE)
                            && !st.is(Items.POISONOUS_POTATO) && !st.is(Items.PUFFERFISH)) {
                        slot = i;
                        break;
                    }
                }
            }
            if (slot >= 0) {
                Hacks.setSel(p, slot);
                mc.options.keyUse.setDown(true);
                eating = true;
            } else if (eating) {
                mc.options.keyUse.setDown(false);
                eating = false;
            }
        }

        // Vurulan canli (AutoClicker / AutoSword)
        LivingEntity hit = (mc.hitResult instanceof EntityHitResult eh && eh.getEntity() instanceof LivingEntity le && le.isAlive()) ? le : null;

        if (Hacks.on("AutoClicker") && mc.screen == null && mc.options.keyAttack.isDown() && hit != null
                && p.getAttackStrengthScale(0.0f) >= 1.0f) {
            crit(mc, p);
            mc.gameMode.attack(p, hit);
            p.swing(InteractionHand.MAIN_HAND);
        }

        if (Hacks.on("AutoSword") && mc.options.keyAttack.isDown() && hit != null) {
            int cur = Hacks.getSel(p);
            int best = -1;
            int bestRank = 0;
            for (int i = 0; i < 9; i++) {
                String n = path(p.getInventory().getItem(i));
                if (n.endsWith("_sword") && rank(n) > bestRank) { bestRank = rank(n); best = i; }
            }
            if (best >= 0 && best != cur) Hacks.setSel(p, best);
        }

        // AimBot
        if (Hacks.on("AimBot") && mc.options.keyAttack.isDown() && mc.screen == null) {
            LivingEntity tg = null;
            double bd = 1e9;
            for (LivingEntity e : mc.level.getEntitiesOfClass(LivingEntity.class, p.getBoundingBox().inflate(6.0))) {
                if (e == p || !e.isAlive()) continue;
                double dd = e.distanceToSqr(p);
                if (dd < bd) { bd = dd; tg = e; }
            }
            if (tg != null) {
                Vec3 eye = p.getEyePosition();
                double dx = tg.getX() - eye.x;
                double dz = tg.getZ() - eye.z;
                double dy = tg.getY() + tg.getBbHeight() * 0.5 - eye.y;
                float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
                float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.hypot(dx, dz)));
                float sp = (float) Settings.get("AimSpeed");
                p.setYRot(p.getYRot() + Mth.clamp(Mth.wrapDegrees(yaw - p.getYRot()), -sp, sp));
                p.setXRot(Mth.clamp(p.getXRot() + Mth.clamp(pitch - p.getXRot(), -sp, sp), -90.0f, 90.0f));
            }
        }

        // FastBow
        if (Hacks.on("FastBow") && p.isUsingItem() && p.getUseItem().is(Items.BOW) && p.getTicksUsingItem() >= 3) {
            mc.gameMode.releaseUsingItem(p);
        }

        // AutoArmor
        if (Hacks.on("AutoArmor") && t % 10 == 0 && mc.screen == null) {
            String[] suf = {"_helmet", "_chestplate", "_leggings", "_boots"};
            EquipmentSlot[] es = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
            int[] menu = {5, 6, 7, 8};
            for (int k = 0; k < 4; k++) {
                ItemStack cur = p.getItemBySlot(es[k]);
                int curRank = 0;
                if (!cur.isEmpty()) {
                    String cn = path(cur);
                    if (!cn.endsWith(suf[k])) continue;
                    curRank = rank(cn);
                }
                int best = -1;
                int bestRank = curRank;
                for (int i = 9; i < 36; i++) {
                    String n = path(p.getInventory().getItem(i));
                    if (n.endsWith(suf[k]) && rank(n) > bestRank) { bestRank = rank(n); best = i; }
                }
                if (best >= 0) {
                    int id = p.containerMenu.containerId;
                    mc.gameMode.handleContainerInput(id, best, 0, ContainerInput.PICKUP, p);
                    mc.gameMode.handleContainerInput(id, menu[k], 0, ContainerInput.PICKUP, p);
                    mc.gameMode.handleContainerInput(id, best, 0, ContainerInput.PICKUP, p);
                    break;
                }
            }
        }
    }
}
