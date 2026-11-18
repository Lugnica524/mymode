package com.example.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {
    public record S(String key, String label, double def, double min, double max, double step) {}

    public static final List<S> ALL = List.of(
        new S("FlightSpeed", "Flight hızı", 0.8, 0.1, 3.0, 0.1),
        new S("SpeedValue", "Speed hızı", 0.45, 0.1, 2.0, 0.05),
        new S("StepHeight", "Step yüksekliği", 2.0, 0.6, 5.0, 0.5),
        new S("KillAuraRange", "KillAura menzil", 4.5, 2.0, 6.0, 0.5),
        new S("KillAuraDelay", "KillAura bekleme", 8, 1, 20, 1),
        new S("Reach", "Reach menzil", 3.0, 3.0, 6.0, 0.5),
        new S("NukerRange", "Nuker menzil", 4, 1, 6, 1),
        new S("NukerDelay", "Nuker bekleme", 2, 1, 20, 1),
        new S("ZoomFactor", "Zoom oranı", 3.0, 1.5, 10.0, 0.5),
        new S("AimSpeed", "AimBot hızı", 12, 1, 90, 1),
        new S("AutoEatLevel", "AutoEat açlık", 14, 1, 19, 1),
        new S("LongJumpBoost", "LongJump gücü", 0.9, 0.3, 2.0, 0.1),
        new S("FastFallSpeed", "FastFall hızı", 1.0, 0.3, 3.0, 0.1),
        new S("TracerRange", "Tracer menzil", 64, 16, 128, 16),
        new S("MaxTargets", "Max hedef", 10, 3, 30, 1),
        new S("TracerDots", "Çizgi nokta", 16, 6, 45, 1),
        new S("ChestRange", "Sandık chunk", 10, 2, 16, 1),
        new S("ChestMax", "Max sandık", 12, 3, 40, 1)
    );

    private static final Map<String, Double> VAL = new HashMap<>();

    public static double get(String k) {
        Double v = VAL.get(k);
        if (v != null) return v;
        for (S s : ALL) if (s.key().equals(k)) return s.def();
        return 0;
    }

    public static void put(String k, double v) {
        VAL.put(k, v);
    }

    public static void nudge(S s, int dir) {
        double v = get(s.key()) + dir * s.step();
        v = Math.max(s.min(), Math.min(s.max(), v));
        VAL.put(s.key(), Math.round(v * 100.0) / 100.0);
    }

    public static String fmt(S s) {
        double v = get(s.key());
        return s.step() >= 1 ? String.valueOf((int) Math.round(v)) : String.format("%.2f", v);
    }
}
