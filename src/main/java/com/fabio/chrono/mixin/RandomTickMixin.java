package com.fabio.chrono.mixin;

import com.fabio.chrono.ChronoDomain;
import com.fabio.chrono.ChunkTimeManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.Map;

@Mixin(ServerWorld.class)
public abstract class RandomTickMixin {
    // Use a map to track ticks per world to avoid conflicts in multi-world environments
    @Unique
    private static final Map<ServerWorld, Integer> TICK_COUNTERS = new HashMap<>();
    @Unique
    private static final int CLEAR_INTERVAL = 40;

    @ModifyVariable(method = "tickChunk", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int modifyRandomTickSpeed(int randomTickSpeed, WorldChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        final ChunkTimeManager chunkTimeManager = ChronoDomain.getChunkTimeManager();

        // Get current world
        ServerWorld world = (ServerWorld)(Object)this;

        // Initialize counter for this world if needed
        TICK_COUNTERS.putIfAbsent(world, 0);

        // Increment the tick counter
        int currentTicks = TICK_COUNTERS.get(world) + 1;
        TICK_COUNTERS.put(world, currentTicks);

        if (chunkTimeManager.isChunkAffected(chunkPos)) {
            float timeFactor = chunkTimeManager.getChunkTimeFactor(chunkPos);
            int modifiedSpeed = Math.max(1, Math.round(randomTickSpeed * timeFactor));
            ChronoDomain.LOGGER.info("DEBUG: Random tick speed modified from {} to {} (factor: {})",
                    randomTickSpeed, modifiedSpeed, timeFactor);
            return modifiedSpeed;
        }

        // Periodically clear unused chunks to prevent memory leaks
        if (currentTicks >= CLEAR_INTERVAL) {
            TICK_COUNTERS.put(world, 0);
            chunkTimeManager.clearChunk();
        }

        return randomTickSpeed;
    }

    // For slowing down the random tick speed
    @Inject(method = "tickChunk", at = @At("HEAD"), cancellable = true)
    public void slowTimeChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci){
        // Get the chunk position
        ChunkPos chunkpos = chunk.getPos();

        final ChunkTimeManager chunkTimeManager = ChronoDomain.getChunkTimeManager();

        // Cancel the tick if the time is slowed and this tick should be skipped
        if (chunkTimeManager.isChunkAffected(chunkpos) &&
                chunkTimeManager.getChunkTimeFactor(chunkpos) < 1.0f &&
                !chunkTimeManager.shouldTickChunkNow(chunk)) {
            ci.cancel();
        }
    }
}