package com.fabio.chrono.mixin;

import com.fabio.chrono.ChronoDomain;
import com.fabio.chrono.ChunkTimeManager;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;


@Mixin(World.class)
public class BlockEntityMixin {

    // Shadow the blockEntityTickers list which contains all ticking block entities
    @Shadow @Final protected List<BlockEntityTickInvoker> blockEntityTickers;

    /**
     * This injection happens at the end of the tickBlockEntities method
     * When this runs, all normal ticks have been processed once
     */
    @Inject(method = "tickBlockEntities", at = @At("TAIL"))
    private void afterBlockEntitiesTick(CallbackInfo ci) {
        ChunkTimeManager timeManager = ChronoDomain.getChunkTimeManager();

        // We'll create a temporary list to avoid concurrent modification
        List<BlockEntityTickInvoker> tickersToProcess = List.copyOf(this.blockEntityTickers);

        // Iterate through the blockEntityTickers list
        for (BlockEntityTickInvoker ticker : tickersToProcess) {
            if (ticker.isRemoved()) {
                continue;
            }

            // Get position data
            BlockPos pos = ticker.getPos();
            ChunkPos chunkPos = new ChunkPos(pos);

            if (timeManager.isChunkAffected(chunkPos) && chunkPos != null) {
                 float timeFactor = timeManager.getChunkTimeFactor(chunkPos);

                if (timeFactor > 1.0f) {
                    int extraTicks = Math.max(0, Math.round(timeFactor) - 1);

                    for (int i = 0; i < extraTicks; i++) {
                        if (!ticker.isRemoved()) {
                            ticker.tick();
                            ChronoDomain.LOGGER.info("DEBUG: Ticking block entity at {} with time factor {}", pos, timeFactor);
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }


    /**
     * This injection replaces the original method that checks if a block
     * position should be ticked. We use this to implement slow time.
     */
    @Inject(method = "shouldTickBlockPos", at = @At("RETURN"), cancellable = true)
    private void modifyShouldTickBlockPos(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        // If the original result is false, just return
        if (!cir.getReturnValue()) {
            return;
        }

        // Get chunk data
        ChunkPos chunkPos = new ChunkPos(pos);
        ChunkTimeManager timeManager = ChronoDomain.getChunkTimeManager();

        // Check if this chunk is time-affected and slowed down
        if (timeManager.isChunkAffected(chunkPos) && chunkPos != null) {
            float timeFactor = timeManager.getChunkTimeFactor(chunkPos);

            // Handle slow time (factor < 1.0)
            if (timeFactor < 1.0f) {
                // Use probability to determine if this position should tick
                boolean shouldTick = timeManager.shouldTickBlockNow(pos, timeFactor);

                // Override the original result
                cir.setReturnValue(shouldTick);
                ChronoDomain.LOGGER.info("DEBUG: Block at {} should tick with time factor {}", pos, timeFactor);
            }
        }
    }


}
