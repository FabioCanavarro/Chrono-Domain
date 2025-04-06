package com.fabio.chrono;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    private static <T extends BlockEntity> BlockEntityType<T> register( String name, FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory, Block... blocks) {
        return Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(ChronoDomain.MOD_ID, name),
                FabricBlockEntityTypeBuilder
                        .<T>create(entityFactory, blocks)
                        .build());
    }


    public static void initialize() {
        // This method is intentionally left empty. The static initializer will handle registration.
    }

    public static final BlockEntityType<TimeStationBlockEntity> TIME_STATION_BLOCK_ENTITY = register("counter", TimeStationBlockEntity::new, ModBlocks.TIME_STATION);
}
