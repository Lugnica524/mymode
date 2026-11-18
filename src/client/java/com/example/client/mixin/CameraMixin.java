package com.example.client.mixin;

import com.example.client.ExampleModClient;
import com.example.client.Hacks;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Shadow
    public abstract float getCameraEntityPartialTicks(DeltaTracker dt);

    @Shadow
    private boolean detached;

    @Inject(method = "update", at = @At("TAIL"))
    private void modid$freecam(DeltaTracker dt, CallbackInfo ci) {
        if (ExampleModClient.MODULES.getOrDefault("Freecam", false)) {
            double pt = this.getCameraEntityPartialTicks(dt);
            double x = Hacks.fxo + (Hacks.fx - Hacks.fxo) * pt;
            double y = Hacks.fyo + (Hacks.fy - Hacks.fyo) * pt;
            double z = Hacks.fzo + (Hacks.fz - Hacks.fzo) * pt;
            this.setPosition(x, y, z);
            this.detached = true;
        }
    }
}
