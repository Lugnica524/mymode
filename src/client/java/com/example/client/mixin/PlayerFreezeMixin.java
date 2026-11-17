package com.example.client.mixin;

import com.example.client.ExampleModClient;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class PlayerFreezeMixin {
    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void modid$freeze(CallbackInfo ci) {
        if (ExampleModClient.MODULES.getOrDefault("Freecam", false)) {
            ci.cancel();
        }
    }
}
