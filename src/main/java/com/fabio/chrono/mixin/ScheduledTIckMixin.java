package com.fabio.chrono.mixin;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerWorld.class)
public abstract class ScheduledTIckMixin {

    @ModifyArg(
            method = "scheduledTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;scheduleBlockTick(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;I)V"),
            index = 2
    )
    private int modifyScheduledTickDelay(int original) {
        ServerWorld world = (ServerWorld) (Object) this;

        if (world.getTime() % 20 == 0) {
            // If so, return the original value
            return original;
        }

        // Otherwise, return the modified value
        return (int) (original * 0.1f);
    }

}
