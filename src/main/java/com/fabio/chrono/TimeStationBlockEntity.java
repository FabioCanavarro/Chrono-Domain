package com.fabio.chrono;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeStationBlockEntity extends BlockEntity {
    public int TickCounter = 0;
    public static final int Scan_interval = 20;

    public TimeStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TIME_STATION_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, TimeStationBlockEntity blockEntity) {
        if (!state.get(TimeStationBlock.ACTIVATED)){
            return;
        }
        if (++blockEntity.TickCounter == Scan_interval) {
            blockEntity.TickCounter = 0;
            scanTimeFieldEntities(world, pos);
        }
    }

    public static Box scanTimeFieldEntities(World world, BlockPos pos) {
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        final Map<UUID, Float> entityTimeFactors = new HashMap<>();
        final Box Field =  new Box(
                chunkX << 4, world.getBottomY(), chunkZ << 4,
                (chunkX << 4) + 16, world.getHeight(), (chunkZ << 4) + 16
            );
        for (Entity entity : world.getEntitiesByClass(
                LivingEntity.class,
                Field,
                entity -> entity instanceof LivingEntity)) {

            // Put all in the HashMap
            entityTimeFactors.put(entity.getUuid(), ChronoDomain.timefactor);
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        ParticleTypes.PORTAL,
                        entity.getX(), entity.getY(), entity.getZ(),
                        5, 0, 0.1, 0, 0.02
                );
            }
        }
        ChronoDomain.switchTimeFieldEntities(entityTimeFactors);

        return Field;


    }



}
