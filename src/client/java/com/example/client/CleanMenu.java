package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CleanMenu extends Screen {
    private static final int PW = 340, PH = 222, BW = 100, BH = 21;

    private static final String[] TABS = {"Savaş", "Hareket", "Oyuncu", "Dünya", "Görsel", "Ayarlar"};
    private static final String[][] CATS = {
        {"KillAura", "TriggerBot", "AutoTotem", "Velocity", "AutoArmor", "AutoClicker", "Criticals", "AimBot", "AutoSword", "FastBow"},
        {"Flight", "Speed", "Step", "Jesus", "Spider", "NoFall", "AutoSprint", "AutoWalk", "Strafe", "AutoJump", "Parkour", "SafeWalk", "LongJump", "FastFall", "Blink"},
        {"AutoEat", "AutoRespawn", "AutoTool", "FastPlace", "AutoReplenish", "Scaffold"},
        {"Nuker", "Lawnmower", "FastBreak", "Reach", "NoWeather"},
        {"Fullbright", "ESP", "HUD", "Freecam", "Tracers", "Nametags", "Zoom"},
        {}
    };
    private static int tab = 0;

    private static final Map<String, String> DESC = new HashMap<>();
    static {
        String[][] d = {
            {"KillAura", "Yakındaki canlılara otomatik vurur."},
            {"TriggerBot", "Nişan aldığın canlıya otomatik vurur."},
            {"AutoTotem", "Totemi otomatik sol ele geçirir."},
            {"Velocity", "Vurulunca geri savrulmazsın."},
            {"AutoArmor", "Envanterdeki en iyi zırhı otomatik giyer."},
            {"AutoClicker", "Sol tık basılıyken otomatik vurur."},
            {"Criticals", "Vuruşları kritik vuruşa çevirir."},
            {"AimBot", "Sol tıkta en yakın canlıya nişan alır."},
            {"AutoSword", "Vururken en iyi kılıcı seçer."},
            {"FastBow", "Yayı çok hızlı çekip bırakır."},
            {"Flight", "Uçmanı sağlar. Space: yukarı, Shift: aşağı."},
            {"Speed", "Yerde ve havada çok hızlı koşarsın."},
            {"Step", "Blokların üstüne zıplamadan çıkarsın."},
            {"Jesus", "Suyun ve lavın üstünde yürürsün."},
            {"Spider", "Duvara yapışıp tırmanırsın."},
            {"NoFall", "Yüksekten düşünce hasar almazsın."},
            {"AutoSprint", "İleri giderken otomatik koşar."},
            {"AutoWalk", "Tuşa basmadan otomatik ileri yürür."},
            {"Strafe", "Havada yön değiştirirken hızını korur."},
            {"AutoJump", "Engelin önünde otomatik zıplar."},
            {"Parkour", "Blok kenarında otomatik zıplar."},
            {"SafeWalk", "Blok kenarından düşmezsin."},
            {"LongJump", "Zıplayınca çok ileri atlarsın."},
            {"FastFall", "Havadayken çok hızlı düşersin."},
            {"Blink", "Hareketini tutar, kapatınca hepsini birden gönderir."},
            {"AutoEat", "Acıkınca hotbar'daki yemeği otomatik yer."},
            {"AutoRespawn", "Ölünce otomatik yeniden doğar."},
            {"AutoTool", "Kırdığın bloğa en uygun aleti seçer."},
            {"FastPlace", "Blokları çok hızlı koyarsın."},
            {"AutoReplenish", "Hotbar'daki eşya bitince envanterden doldurur."},
            {"Scaffold", "Yürürken ayağının altına blok koyar."},
            {"Nuker", "Menzildeki blokları sırayla kırar."},
            {"Lawnmower", "Çimen gibi anında kırılan blokları keser."},
            {"FastBreak", "Blokları daha hızlı kırarsın."},
            {"Reach", "Vuruş ve blok menzilini artırır."},
            {"NoWeather", "Yağmur ve fırtınayı kapatır (sadece ekranında)."},
            {"Fullbright", "Karanlıkta bile her yeri aydınlık görürsün."},
            {"ESP", "Canlıları duvar arkasından bile parlak gösterir."},
            {"HUD", "Sol üstte açık hileleri ve koordinatları gösterir."},
            {"Freecam", "Kamera karakterden ayrılıp serbestçe uçar."},
            {"Tracers", "Canlılara ekranda çizgi çeker."},
            {"Nametags", "Canlıların üstünde isim ve can gösterir."},
            {"Zoom", "Görüşü yakınlaştırır. Oranı Ayarlar'dan değiştir."}
        };
        for (String[] e : d) DESC.put(e[0], e[1]);
    }
    private static final String SHULKER_DESC = "OP eşya dolu Shulker verir. Tek oyunculuda komutsuz çalışır.";

    public CleanMenu() {
        super(Component.literal("Mod Menu"));
    }

    private boolean isSettings() {
        return tab == TABS.length - 1;
    }

    private List<String> names() {
        List<String> out = new ArrayList<>();
        for (String s : CATS[tab]) if (ExampleModClient.MODULES.containsKey(s)) out.add(s);
        return out;
    }

    private int left() { return (this.width - PW) / 2; }
    private int top() { return (this.height - PH) / 2; }

    private int[] tabRect(int j) {
        int tw = (PW - 28 - 4 * (TABS.length - 1)) / TABS.length;
        return new int[]{left() + 14 + j * (tw + 4), top() + 32, tw, 18};
    }

    private int[] rect(int i) {
        int col = i % 3, row = i / 3;
        return new int[]{left() + 14 + col * (BW + 6), top() + 58 + row * 25, BW, BH};
    }

    private int[] shulkerRect() {
        return new int[]{left() + 14, top() + 190, PW - 28, 22};
    }

    private int[] cell(int i) {
        int cw = (PW - 28 - 8) / 2;
        int col = i % 2, row = i / 2;
        return new int[]{left() + 14 + col * (cw + 8), top() + 58 + row * 20, cw, 18};
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

        int left = left();
        int top = top();

        g.fill(left - 3, top - 3, left + PW + 3, top + PH + 3, 0xFF9D4DFF);
        g.fill(left - 1, top - 1, left + PW + 1, top + PH + 1, 0xFF6A2FD0);
        g.fillGradient(left, top, left + PW, top + PH, 0xF02A5BE0, 0xF0132A70);
        g.fillGradient(left, top, left + PW, top + 4, 0xFFB57CFF, 0xFF7A3FE6);

        g.centeredText(this.font, "MOD MENU", this.width / 2, top + 13, 0xFFEBDDFF);

        for (int j = 0; j < TABS.length; j++) {
            int[] r = tabRect(j);
            boolean sel = j == tab;
            boolean over = hit(r, mx, my);
            if (sel) {
                box(g, r[0], r[1], r[2], r[3], 0xFFD2B0FF, 0xFF8B3DFF, 0xFF5B2FD0);
            } else {
                box(g, r[0], r[1], r[2], r[3], over ? 0xFFB07CFF : 0xFF7B4FD6, 0xD0102560, 0xD00B1A48);
            }
            g.centeredText(this.font, TABS[j], r[0] + r[2] / 2, r[1] + 5, sel ? 0xFFFFFFFF : 0xFFC9D8FF);
        }
        g.fill(left + 14, top + 53, left + PW - 14, top + 54, 0xFFB07CFF);

        String tip = null;

        if (isSettings()) {
            for (int i = 0; i < Settings.ALL.size(); i++) {
                Settings.S s = Settings.ALL.get(i);
                int[] c = cell(i);
                box(g, c[0], c[1], c[2], c[3], 0xFF7B4FD6, 0xD0102560, 0xD00B1A48);
                g.text(this.font, s.label(), c[0] + 5, c[1] + 5, 0xFFC9D8FF);
                int[] mn = {c[0] + c[2] - 66, c[1], 16, 18};
                int[] pl = {c[0] + c[2] - 18, c[1], 18, 18};
                box(g, mn[0], mn[1], mn[2], mn[3], 0xFFD2B0FF, hit(mn, mx, my) ? 0xFFA566FF : 0xFF8B3DFF, 0xFF5B2FD0);
                box(g, pl[0], pl[1], pl[2], pl[3], 0xFFD2B0FF, hit(pl, mx, my) ? 0xFFA566FF : 0xFF8B3DFF, 0xFF5B2FD0);
                g.centeredText(this.font, "-", mn[0] + 8, mn[1] + 5, 0xFFFFFFFF);
                g.centeredText(this.font, "+", pl[0] + 9, pl[1] + 5, 0xFFFFFFFF);
                g.centeredText(this.font, Settings.fmt(s), c[0] + c[2] - 34, c[1] + 5, 0xFFFFFFFF);
            }
        } else {
            List<String> names = this.names();
            for (int i = 0; i < names.size(); i++) {
                int[] r = rect(i);
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

            int[] s = shulkerRect();
            boolean so = hit(s, mx, my);
            if (so) tip = SHULKER_DESC;
            box(g, s[0], s[1], s[2], s[3], 0xFFB5ECFF,
                    so ? 0xFF55D4FF : 0xFF2EC4FF, so ? 0xFF2E90E6 : 0xFF1E7FD6);
            g.centeredText(this.font, "Give OP Shulker", s[0] + s[2] / 2, s[1] + 7, 0xFFFFFFFF);
        }

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
            double mx = event.x(), my = event.y();
            for (int j = 0; j < TABS.length; j++) {
                if (hit(tabRect(j), mx, my)) {
                    tab = j;
                    return true;
                }
            }
            if (isSettings()) {
                for (int i = 0; i < Settings.ALL.size(); i++) {
                    int[] c = cell(i);
                    int[] mn = {c[0] + c[2] - 66, c[1], 16, 18};
                    int[] pl = {c[0] + c[2] - 18, c[1], 18, 18};
                    if (hit(mn, mx, my)) {
                        Settings.nudge(Settings.ALL.get(i), -1);
                        Config.save();
                        return true;
                    }
                    if (hit(pl, mx, my)) {
                        Settings.nudge(Settings.ALL.get(i), 1);
                        Config.save();
                        return true;
                    }
                }
            } else {
                List<String> names = this.names();
                for (int i = 0; i < names.size(); i++) {
                    if (hit(rect(i), mx, my)) {
                        String k = names.get(i);
                        ExampleModClient.MODULES.put(k, !ExampleModClient.MODULES.get(k));
                        Config.save();
                        return true;
                    }
                }
                if (hit(shulkerRect(), mx, my)) {
                    Minecraft mc = Minecraft.getInstance();
                    var srv = mc.getSingleplayerServer();
                    if (srv != null) {
                        final String cmd = ShulkerKit.build().replaceFirst("@s", "@a");
                        srv.execute(() -> srv.getCommands().performPrefixedCommand(srv.createCommandSourceStack(), cmd));
                    } else if (mc.getConnection() != null) {
                        mc.getConnection().sendCommand(ShulkerKit.build());
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
}
