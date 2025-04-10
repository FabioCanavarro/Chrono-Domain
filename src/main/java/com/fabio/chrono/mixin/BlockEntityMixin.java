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

        for (BlockEntityTickInvoker ticker : tickersToProcess) {
            if (ticker.isRemoved()) {
                continue;
            }

            // Get position data
            BlockPos pos = ticker.getPos();
            ChunkPos chunkPos = new ChunkPos(pos);

            if (timeManager.isChunkAffected(chunkPos)) {
                 float timeFactor = timeManager.getChunkTimeFactor(chunkPos);

                if (timeFactor > 1.0f) {
                    int extraTicks = Math.max(0, Math.round(timeFactor) - 1);

                    for (int i = 0; i < extraTicks; i++) {
                        if (!ticker.isRemoved()) {
                            ticker.tick();
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }
}
