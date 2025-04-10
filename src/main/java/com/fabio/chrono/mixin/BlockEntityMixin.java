package com.fabio.chrono.mixin;

import com.fabio.chrono.ChronoDomain;
import com.fabio.chrono.ChunkTimeManager;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(World.class)
public abstract class BlockEntityMixin {

    @Shadow public abstract WorldChunk getWorldChunk(BlockPos pos);

    /**
     * This mixin intercepts the call to blockEntityTickInvoker.tick() in the
     * tickBlockEntities method and applies our time manipulation logic.
     * The Redirect injection targets specifically the tick() call within the loop,
     * allowing us to either skip ticks or add extra ticks based on the time factor.
     */
    @Redirect(
            method = "tickBlockEntities",
            at = @At(value = "TAIL")
    )
    private void handleTimeAffectedTick(BlockEntityTickInvoker invoker) {
        // Get the block position and corresponding chunk position
        BlockPos pos = invoker.getPos();
        WorldChunk chunk = getWorldChunk(pos);
        ChunkPos chunkPos = chunk.getPos();

        // Get the time manager and check if this chunk is affected
        ChunkTimeManager timeManager = ChronoDomain.getChunkTimeManager();

        if (!timeManager.isChunkAffected(chunkPos)) {
            return;
        }

        // Get the time factor for this chunk
        float timeFactor = timeManager.getChunkTimeFactor(chunkPos);

        if (timeFactor > 1.0f) {
            int extraTicks = Math.max(0, Math.round(timeFactor) - 1);
            for (int i = 0; i < extraTicks; i++) {
                invoker.tick();
            }
        }
    }
}
