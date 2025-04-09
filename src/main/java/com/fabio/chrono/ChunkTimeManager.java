package com.fabio.chrono;

import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.Map;

public class ChunkTimeManager {

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

    public float getTimeFactorForChunk(ChunkPos chunkPos) {
        return chunkTimeFactors.get(chunkPos);
    }
}
