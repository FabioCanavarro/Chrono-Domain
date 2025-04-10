package com.fabio.chrono.mixin;

import com.fabio.chrono.ChronoDomain;
import com.fabio.chrono.ChunkTimeManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.WorldTickScheduler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldTickScheduler.class)
public class ScheduleTickMixin<T> {
    // Shadow the world field from WorldTickScheduler
    @Final private World world;

    @ModifyVariable(method = "scheduleTick", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private OrderedTick<T> modifyScheduledTick(OrderedTick<T> tick) {
        // Get the position and convert to chunk position
        BlockPos pos = tick.pos();
        ChunkPos chunkPos = new ChunkPos(pos);

        // Get the chunk time manager from ChronoDomain
        ChunkTimeManager chunkTimeManager = ChronoDomain.getChunkTimeManager();

        // Check if this chunk is affected by time manipulation
        if (chunkTimeManager.isChunkAffected(chunkPos)) {
            float timeFactor = chunkTimeManager.getChunkTimeFactor(chunkPos);

            // Skip this modification for normal time (factor = 1.0)
            if (timeFactor == 1.0f) {
                return tick;
            }

            long newTriggerTick = getNewTriggerTick(tick, timeFactor);

            // Create a new OrderedTick with the adjusted trigger time
            return new OrderedTick<>(
                    tick.type(),
                    tick.pos(),
                    newTriggerTick,
                    tick.priority(),
                    tick.subTickOrder()
            );
        }

        // Return the original tick if the chunk is not time-affected
        return tick;
    }

    private long getNewTriggerTick(OrderedTick<T> tick, float timeFactor) {
        long currentTriggerTick = tick.triggerTick();
        long currentWorldTime = world.getTime();
        long newTriggerTick;

        long ticksToWait = currentTriggerTick - currentWorldTime;
        long adjustedTicksToWait;
        if (timeFactor > 1.0f) {
            // For sped-up time, reduce the wait time
            adjustedTicksToWait = Math.max(1, Math.round(ticksToWait / timeFactor));
        } else {
            // For slowed-down time, increase the wait time
            adjustedTicksToWait = Math.round(ticksToWait / timeFactor);
        }
        newTriggerTick = currentWorldTime + adjustedTicksToWait;
        return newTriggerTick;
    }
}