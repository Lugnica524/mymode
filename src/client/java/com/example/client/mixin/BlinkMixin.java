package com.example.client.mixin;

import com.example.client.ExampleModClient;
import com.example.client.Hacks2;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class BlinkMixin {
    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void modid$blink(Packet<?> packet, CallbackInfo ci) {
        if (ExampleModClient.MODULES.getOrDefault("Blink", false) && packet instanceof ServerboundMovePlayerPacket) {
            Hacks2.BLINK.add(packet);
            ci.cancel();
        }
    }
}
