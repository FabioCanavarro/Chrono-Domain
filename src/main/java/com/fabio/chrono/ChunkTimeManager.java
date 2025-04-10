package com.fabio.chrono;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.chunk.WorldChunk;

import java.util.HashMap;
import java.util.Map;

public class ChunkTimeManager {

    private final Random random = Random.create();

    private final Map<ChunkPos, Float> chunkTimeFactors = new HashMap<>();

    public void registerChunk(ChunkPos chunkPos, float timeFactor) {
        chunkTimeFactors.put(chunkPos, timeFactor);
    }

    public void deleteChunk(ChunkPos chunkPos) {
        chunkTimeFactors.remove(chunkPos);
    }

    public Float getChunkTimeFactor(ChunkPos chunkPos) {
        return chunkTimeFactors.get(chunkPos);
    }

    public void clearChunk(){
        chunkTimeFactors.clear();
    }

    public boolean isChunkAffected(ChunkPos chunkPos) {
        return chunkTimeFactors.containsKey(chunkPos);
    }

    public boolean shouldTickChunkNow(WorldChunk chunk) {

        // Get the chunk position
        ChunkPos chunkPos = chunk.getPos();

        // Check if the entity is registered in the time field
        if (!chunkTimeFactors.containsKey(chunkPos))
            return true;

        // Get the world instance

        // Get the time factor for the entity
        float factor = chunkTimeFactors.get(chunkPos);

        // Get the in-game time

        long gameTime = chunk.getWorld().getTime();

        if (factor >= 1.0f) {
            // Always tick when at normal speed or faster
            return true;
        } else {
            // Skip ticks for slow-down based on the factor
            int skipInterval = (int)(1.0f / factor);
            return gameTime % skipInterval == 0;
        }
    }

    public boolean shouldTickBlockNow(float timeFactor) {
        return random.nextFloat() < timeFactor;
    }

}
