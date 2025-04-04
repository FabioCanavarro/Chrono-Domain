package com.fabio.chrono;

import net.minecraft.entity.Entity;

import java.util.*;
import java.util.logging.Logger;

public class TimeFieldManager {
    private final Map<UUID, Float> entityTimeFactors = new HashMap<>();

    public String getEntityRegistered() {
        final List<UUID> entityList = entityTimeFactors.keySet().stream().toList();
        return entityList.toString();
    }
    // Register an entity with a specific time factor
    public void registerEntityInTimeField(Entity entity, float timeFactor) {
        entityTimeFactors.put(entity.getUuid(), timeFactor);
    }

    // Check if an entity should tick multiple times (for speed-up effects)
    public boolean shouldTickEntityMultipleTimes(Entity entity) {
        if (!entityTimeFactors.containsKey(entity.getUuid()))
            return false; // Normal speed

        float factor = entityTimeFactors.get(entity.getUuid());
        return factor > 1.0f; // Time is accelerated if factor > 1
    }

    // Get how many extra ticks to apply for accelerated time
    public int getExtraTicksForEntity(Entity entity) {
        if (!entityTimeFactors.containsKey(entity.getUuid()))
            return 0; // No extra ticks at normal speed

        float factor = entityTimeFactors.get(entity.getUuid());
        if (factor <= 1.0f)
            return 0; // No extra ticks for normal or slowed time

        // For a time factor of 2.0, return 1 extra tick
        // For a time factor of 3.0, return 2 extra ticks
        ChronoDomain.LOGGER.info("Extra ticks for entity: {} = {}", entity.getUuid(), (int) (factor - 1.0f));
        return Math.max(0, (int)(factor - 1.0f));
    }

    // Check if an entity should tick at all this game tick
    public boolean shouldTickEntityNow(Entity entity) {
        if (!entityTimeFactors.containsKey(entity.getUuid()))
            return true; // Normal ticking

        float factor = entityTimeFactors.get(entity.getUuid());
        long gameTime = entity.getWorld().getTime();

        if (factor >= 1.0f) {
            // Always tick when at normal speed or faster
            return true;
        } else {
            // Skip ticks for slow-down based on factor
            // e.g., if factor = 0.5, tick only on every other game tick
            return gameTime % (int)(1/factor) == 0;
        }
    }

}
