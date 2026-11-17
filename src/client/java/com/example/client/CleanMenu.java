package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CleanMenu extends Screen {
    private static final int PW = 268, PH = 172, BW = 119, BH = 21;

    private static final Map<String, String> DESC = Map.of(
        "Flight", "Uçmanı sağlar. Space: yukarı, Shift: aşağı.",
        "Speed", "Yerde ve havada çok hızlı koşarsın.",
        "Fullbright", "Karanlıkta bile her yeri aydınlık görürsün.",
        "ESP", "Yakındaki canlıları duvar arkasından bile parlak gösterir.",
        "KillAura", "Yakındaki canlılara otomatik vurur.",
        "AutoTotem", "Totem elinden gidince yenisini sol ele geçirir.",
        "AutoSprint", "İleri giderken otomatik koşar.",
        "AutoWalk", "Tuşa basmadan otomatik ileri yürür."
    );
    private static final String SHULKER_DESC = "OP eşya dolu Shulker verir. Tek oyunculuda komutsuz çalışır.";

    public CleanMenu() {
        super(Component.literal("Mod Menu"));
    }

    private List<String> names() {
        return new ArrayList<>(ExampleModClient.MODULES.keySet());
    }

    private int[] rect(int i, int n) {
        int left = (this.width - PW) / 2;
        int top = (this.height - PH) / 2;
        if (i == n) {
            int rows = (n + 1) / 2;
            return new int[]{left + 14, top + 34 + rows * 25 + 4, PW - 28, BH + 1};
        }
        int col = i % 2, row = i / 2;
        return new int[]{left + 14 + col * (BW + 8), top + 34 + row * 25, BW, BH};
    }

    private static void box(GuiGraphicsExtractor g, int x, int y, int w, int h, int border, int top, int bottom) {
        g.fill(x, y, x + w, y + h, border);
        g.fillGradient(x + 1, y + 1, x + w - 1, y + h - 1, top, bottom);
    }

    private static boolean hit(int[] r, double mx, double my) {
        return mx >= r[0] && mx < r[0] + r[2] && my >= r[1] && my < r[1] + r[3];
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        super.extractRenderState(g, mx, my, delta);
        g.fill(0, 0, this.width, this.height, 0x5A050A28);

        int left = (this.width - PW) / 2;
        int top = (this.height - PH) / 2;

        g.fill(left - 3, top - 3, left + PW + 3, top + PH + 3, 0xFF9D4DFF);
        g.fill(left - 1, top - 1, left + PW + 1, top + PH + 1, 0xFF6A2FD0);
        g.fillGradient(left, top, left + PW, top + PH, 0xF02A5BE0, 0xF0132A70);
        g.fillGradient(left, top, left + PW, top + 4, 0xFFB57CFF, 0xFF7A3FE6);

        g.centeredText(this.font, "MOD MENU", this.width / 2, top + 14, 0xFFEBDDFF);
        g.fill(left + 14, top + 28, left + PW - 14, top + 29, 0xFFB07CFF);

        String tip = null;
        List<String> names = this.names();
        int n = names.size();
        for (int i = 0; i < n; i++) {
            int[] r = rect(i, n);
            boolean on = ExampleModClient.MODULES.get(names.get(i));
            boolean over = hit(r, mx, my);
            if (over) tip = DESC.getOrDefault(names.get(i), "");
            if (on) {
                box(g, r[0], r[1], r[2], r[3], 0xFFD2B0FF,
                        over ? 0xFFA566FF : 0xFF8B3DFF, over ? 0xFF6D3FE0 : 0xFF5B2FD0);
                g.centeredText(this.font, names.get(i), r[0] + r[2] / 2, r[1] + 7, 0xFFFFFFFF);
            } else {
                box(g, r[0], r[1], r[2], r[3], over ? 0xFFB07CFF : 0xFF7B4FD6,
                        over ? 0xE01C3A8C : 0xD0102560, over ? 0xE0142C78 : 0xD00B1A48);
                g.centeredText(this.font, names.get(i), r[0] + r[2] / 2, r[1] + 7, 0xFFC9D8FF);
            }
        }

        int[] s = rect(n, n);
        boolean so = hit(s, mx, my);
        if (so) tip = SHULKER_DESC;
        box(g, s[0], s[1], s[2], s[3], 0xFFB5ECFF,
                so ? 0xFF55D4FF : 0xFF2EC4FF, so ? 0xFF2E90E6 : 0xFF1E7FD6);
        g.centeredText(this.font, "Give OP Shulker", s[0] + s[2] / 2, s[1] + 7, 0xFFFFFFFF);

        if (tip != null && !tip.isEmpty()) {
            int tw = this.font.width(tip) + 10;
            int tx = mx + 12;
            if (tx + tw > this.width - 2) tx = mx - tw - 8;
            int ty = my - 4;
            g.fill(tx - 1, ty - 1, tx + tw + 1, ty + 17, 0xFFB07CFF);
            g.fill(tx, ty, tx + tw, ty + 16, 0xF0100A30);
            g.text(this.font, tip, tx + 5, ty + 4, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            List<String> names = this.names();
            int n = names.size();
            for (int i = 0; i < n; i++) {
                if (hit(rect(i, n), event.x(), event.y())) {
                    String k = names.get(i);
                    ExampleModClient.MODULES.put(k, !ExampleModClient.MODULES.get(k));
                    return true;
                }
            }
            if (hit(rect(n, n), event.x(), event.y())) {
                Minecraft mc = Minecraft.getInstance();
                var srv = mc.getSingleplayerServer();
                if (srv != null) {
                    final String cmd = ExampleModClient.buildShulkerCmd().replaceFirst("@s", "@a");
                    srv.execute(() -> srv.getCommands().performPrefixedCommand(srv.createCommandSourceStack(), cmd));
                } else if (mc.getConnection() != null) {
                    mc.getConnection().sendCommand(ExampleModClient.buildShulkerCmd());
                }
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
}
