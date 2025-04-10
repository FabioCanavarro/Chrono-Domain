package com.fabio.chrono.mixin;

import com.fabio.chrono.ChronoDomain;
import com.fabio.chrono.ChunkTimeManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ServerWorld.class)
public abstract class RandomTickMixin {
    private static int TICK = 0;

    @ModifyVariable(method = "tickChunk", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int modifyRandomTickSpeed(int randomTickSpeed, WorldChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        final ChunkTimeManager chunkTimeManager = ChronoDomain.getChunkTimeManager();
        TICK += 1;

        if (chunkTimeManager.isChunkAffected(chunkPos)) {
            float timeFactor = chunkTimeManager.getChunkTimeFactor(chunkPos);
            int modifiedSpeed = Math.max(1, Math.round(randomTickSpeed * timeFactor));
            ChronoDomain.LOGGER.info("Random tick speed modified from {} to {} (factor: {})",
                    randomTickSpeed, modifiedSpeed, timeFactor);
            return modifiedSpeed;
        }

        if (TICK >= 40){
            TICK = 0;
            chunkTimeManager.clearChunk();
        }

        return randomTickSpeed;
    }

    // For slowing down the random tick speed
    @Inject(method = "tickChunk", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void slowTimeChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci){
        // Get the world instance
        ChunkPos chunkpos = chunk.getPos();

        final ChunkTimeManager chunkTimeManager = ChronoDomain.getChunkTimeManager();

        if (chunkTimeManager.isChunkAffected(chunkpos) && chunkTimeManager.getChunkTimeFactor(chunkpos) < 1.0f){
            if (chunkTimeManager.shouldTickChunkNow(chunk)){
                ci.cancel();
            }
        }
    }

}
