package com.example.client.mixin;

import com.example.client.ExampleModClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityGlowMixin {
    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void modid$esp(CallbackInfoReturnable<Boolean> cir) {
        if (ExampleModClient.MODULES.getOrDefault("ESP", false)) {
            Entity self = (Entity) (Object) this;
            if (self instanceof LivingEntity && self != Minecraft.getInstance().player) {
                cir.setReturnValue(true);
            }
        }
    }
}
