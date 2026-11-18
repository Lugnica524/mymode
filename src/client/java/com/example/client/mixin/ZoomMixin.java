package com.example.client.mixin;

import com.example.client.ExampleModClient;
import com.example.client.Settings;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class ZoomMixin {
    @Inject(method = "calculateFov", at = @At("RETURN"), cancellable = true, require = 0)
    private void modid$zoom(float partial, CallbackInfoReturnable<Float> cir) {
        if (ExampleModClient.MODULES.getOrDefault("Zoom", false)) {
            cir.setReturnValue(cir.getReturnValue() / (float) Settings.get("ZoomFactor"));
        }
    }
}
