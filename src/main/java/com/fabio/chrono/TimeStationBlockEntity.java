package com.fabio.chrono;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TimeStationBlockEntity extends BlockEntity {
    public int TickCounter = 0;
    public static final int Scan_interval = 0;

    public TimeStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TIME_STATION_BLOCK_ENTITY, pos, state);
    }

    public static <CounterBlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, CounterBlockEntity entity) {
    }


}
