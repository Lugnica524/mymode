package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class CleanMenu extends Screen {
    private static final int PW = 212, PH = 164, BW = 91, BH = 20;

    public CleanMenu() {
        super(Component.literal("Mod Menu"));
    }

    private List<String> names() {
        return new ArrayList<>(ExampleModClient.MODULES.keySet());
    }

    // index 0..n-1 = modules, index n = shulker button
    private int[] rect(int i, int n) {
        int left = (this.width - PW) / 2;
        int top = (this.height - PH) / 2;
        if (i == n) {
            int rows = (n + 1) / 2;
            return new int[]{left + 12, top + 30 + rows * 24 + 4, PW - 24, BH};
        }
        int col = i % 2, row = i / 2;
        return new int[]{left + 12 + col * (BW + 6), top + 30 + row * 24, BW, BH};
    }

    private static void box(GuiGraphicsExtractor g, int x, int y, int w, int h, int border, int fill) {
        g.fill(x, y, x + w, y + h, border);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, fill);
    }

    private static boolean hit(int[] r, double mx, double my) {
        return mx >= r[0] && mx < r[0] + r[2] && my >= r[1] && my < r[1] + r[3];
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        super.extractRenderState(g, mx, my, delta);
        int left = (this.width - PW) / 2;
        int top = (this.height - PH) / 2;

        // panel
        g.fill(left - 2, top - 2, left + PW + 2, top + PH + 2, 0xFF2E8BFF);
        g.fill(left, top, left + PW, top + PH, 0xF00B1426);
        g.fill(left, top, left + PW, top + 3, 0xFF2E8BFF);
        g.centeredText(this.font, "MOD MENU", this.width / 2, top + 12, 0xFF8EC8FF);
        g.fill(left + 12, top + 25, left + PW - 12, top + 26, 0xFF1F3A6B);

        List<String> names = this.names();
        int n = names.size();
        for (int i = 0; i < n; i++) {
            int[] r = rect(i, n);
            boolean on = ExampleModClient.MODULES.get(names.get(i));
            boolean over = hit(r, mx, my);
            int border = on ? 0xFF66B2FF : 0xFF2A3A5C;
            int fill = on ? (over ? 0xFF2B82F5 : 0xFF1E6FE0) : (over ? 0xFF1C2D52 : 0xFF121E38);
            int text = on ? 0xFFFFFFFF : 0xFF8FA3C7;
            box(g, r[0], r[1], r[2], r[3], border, fill);
            g.centeredText(this.font, names.get(i), r[0] + r[2] / 2, r[1] + 6, text);
        }

        int[] s = rect(n, n);
        boolean so = hit(s, mx, my);
        box(g, s[0], s[1], s[2], s[3], 0xFF55D68C, so ? 0xFF22B565 : 0xFF1A8F52);
        g.centeredText(this.font, "Give OP Shulker", s[0] + s[2] / 2, s[1] + 6, 0xFFFFFFFF);
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
                if (mc.getConnection() != null) {
                    mc.getConnection().sendCommand(ExampleModClient.buildShulkerCmd());
                }
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
}
