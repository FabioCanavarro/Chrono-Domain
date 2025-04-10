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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ServerWorld.class)
public abstract class RandomTickMixin {

    // For speeding up the random tick speed
    @ModifyArgs(method = "tickChunk", at = @At("HEAD"))
    private void ModifyRandomTickSpeed (Args arg) {
        // Get the world instance
        WorldChunk chunk = arg.get(0);
        ChunkPos chunkpos = chunk.getPos();

        final ChunkTimeManager chunkTimeManager = ChronoDomain.getChunkTimeManager();

        if (chunkTimeManager.isChunkAffected(chunkpos)){
            // Get the time factor for the chunk

            float timeFactor = chunkTimeManager.getChunkTimeFactor(chunkpos);

            arg.set(1, Math.max(1, Math.round(timeFactor)));

            ChronoDomain.LOGGER.info("Random tick speed: {}", timeFactor);
        }
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
