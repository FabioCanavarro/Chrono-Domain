package com.fabio.chrono;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.*;

public class TimeStationBlockEntity extends BlockEntity {
    public int TickCounter = 0;
    public static final int Scan_interval = 20;
    public static HashMap<UUID, Float> EntityInField = new HashMap<>();

    public TimeStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TIME_STATION_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, TimeStationBlockEntity blockEntity) {
        Box field = new Box(0, -1000, 0, 0, -1000, 0);
        if (!state.get(TimeStationBlock.ACTIVATED)){
            return;
        }

        ChunkTimeManager chunkTimeManager = ChronoDomain.getChunkTimeManager();
        Chunk chunk = world.getChunk(pos);
        chunkTimeManager.registerChunk(chunk.getPos(), 10f);

        if (++blockEntity.TickCounter == Scan_interval) {
            blockEntity.TickCounter = 0;
           field = scanTimeFieldEntities(world, pos);
        }
        if (world instanceof ServerWorld serverWorld) {
            Vec3d corner = new Vec3d(field.maxX, field.maxY, field.maxZ);
            Vec3d corner2 = new Vec3d(field.minX, field.minY, field.minZ);
            Vec3d corner3 = new Vec3d(field.maxX, field.minY, field.minZ);
            Vec3d corner4 = new Vec3d(field.minX, field.maxY, field.maxZ);

            for (int i = 0; i < 255; i++) {
                serverWorld.spawnParticles(
                        ParticleTypes.END_ROD,
                        corner.x, i, corner.z,
                        5, 0, 0.1, 0, 0.02
                );
                serverWorld.spawnParticles(
                        ParticleTypes.END_ROD,
                        corner2.x, i, corner2.z,
                        5, 0, 0.1, 0, 0.02
                );
                serverWorld.spawnParticles(
                        ParticleTypes.END_ROD,
                        corner3.x, i, corner3.z,
                        5, 0, 0.1, 0, 0.02
                );
                serverWorld.spawnParticles(
                        ParticleTypes.END_ROD,
                        corner4.x, i, corner4.z,
                        5, 0, 0.1, 0, 0.02
                );
            }

        }
    }

    public static Box scanTimeFieldEntities(World world, BlockPos pos) {
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;

        // Create a temporary map to track currently found entities
        final Map<UUID, Float> currentEntities = new HashMap<>();

        final Box Field = new Box(
                chunkX << 4, world.getBottomY(), chunkZ << 4,
                (chunkX << 4) + 16, world.getHeight(), (chunkZ << 4) + 16
        );

        // First, scan and register all entities currently in the field
        for (Entity entity : world.getEntitiesByClass(
                LivingEntity.class,
                Field,
                entity -> !(entity instanceof PlayerEntity))) {

            // Add to a current entities map
            currentEntities.put(entity.getUuid(), ChronoDomain.timefactor);

            // Register entity if not already registered (or update its time factor)
            ChronoDomain.registerTimeFieldEntity(entity, ChronoDomain.timefactor);

            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        ParticleTypes.PORTAL,
                        entity.getX(), entity.getY(), entity.getZ(),
                        5, 0, 0.1, 0, 0.02
                );
            }
        }

        // Create a list of entities that were previously in the field but are no longer present
        List<UUID> entitiesToRemove = new ArrayList<>();
        for (Map.Entry<UUID, Float> entry : EntityInField.entrySet()) {
            UUID uuid = entry.getKey();
            if (!currentEntities.containsKey(uuid)) {
                // Entity is no longer in the field, mark for removal
                entitiesToRemove.add(uuid);
            }
        }

        // Remove entities that are no longer in the field
        for (UUID uuid : entitiesToRemove) {
            EntityInField.remove(uuid);
            ChronoDomain.removeTimeFieldEntity(uuid);
        }

        // Update our static tracking map with current entities
        EntityInField.clear();
        EntityInField.putAll(currentEntities);

        return Field;
    }



}
