package com.fabio.chrono;

import net.minecraft.entity.Entity;
import java.util.*;

public class TimeFieldManager {

    // HashMap to store the time factors for each entity
    private Map<UUID, Float> entityTimeFactors = new HashMap<>();

    public void SwitchTimeField( Map<UUID, Float> entitymap) {
        entityTimeFactors = entitymap;
    }

    // Get the list of entities registered in the time field
    public String getEntityRegistered() {
        // Turn the UUIDs of the entities into a list
        final List<UUID> entityList = entityTimeFactors.keySet().stream().toList();

        // Parse the list into a string
        return entityList.toString();
    }

    public void removeEntity(UUID entity) {
        // Remove the entity from the time field
        entityTimeFactors.remove(entity);
    }

    // Register an entity with a specific time factor
    public void registerEntityInTimeField(Entity entity, float timeFactor) {
        entityTimeFactors.put(entity.getUuid(), timeFactor);
    }

    // Check if an entity should tick multiple times (for speed-up effects)
    public boolean shouldTickEntityMultipleTimes(Entity entity) {

        // Check if the entity is registered in the time field
        if (!entityTimeFactors.containsKey(entity.getUuid()))
            return false; // Normal speed

        // Get the time factor for the entity
        float factor = entityTimeFactors.get(entity.getUuid());

        // Check if the time factor is greater than 1.0
        return factor > 1.0f;
    }

    // Check if an entity is in any time field
    public boolean isEntityInTimeField(Entity entity) {
        return entityTimeFactors.containsKey(entity.getUuid());
    }

    // Get the time factor for an entity
    public float getTimeFactorForEntity(Entity entity) {
        // Check if the entity is registered in the time field
        if (!entityTimeFactors.containsKey(entity.getUuid()))
            return 1.0f;

        return entityTimeFactors.get(entity.getUuid());
    }

    // Get how many extra ticks to apply for accelerated time
    public int getExtraTicksForEntity(Entity entity) {

        // Check if the entity is registered in the time field
        if (!entityTimeFactors.containsKey(entity.getUuid()))
            return 0;

        // Get the time factor for the entity
        float factor = entityTimeFactors.get(entity.getUuid());

        // No extra ticks for normal or slowed time
        if (factor <= 1.0f)
            return 0;

        ChronoDomain.LOGGER.info("Extra ticks for entity: {} = {}", entity.getUuid(), (int) (factor - 1.0f));

        // Calculate the number of extra ticks based on the time factor
        return Math.max(0, (int)(factor - 1.0f));
    }

    // Check if an entity should tick at all this game tick
    public boolean shouldTickEntityNow(Entity entity) {
        // Check if the entity is registered in the time field
        if (!entityTimeFactors.containsKey(entity.getUuid()))
            return true;

        // Get the time factor for the entity
        float factor = entityTimeFactors.get(entity.getUuid());

        // Get the in-game time
        long gameTime = entity.getWorld().getTime();

        if (factor >= 1.0f) {
            // Always tick when at normal speed or faster
            return true;
        } else {
            // Skip ticks for slow-down based on the factor
            int skipInterval = (int)(1.0f / factor);
            return gameTime % skipInterval == 0;
        }
    }



}
