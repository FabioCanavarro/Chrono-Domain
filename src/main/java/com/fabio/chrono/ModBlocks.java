package com.fabio.chrono;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {
    private static Block register(String name, Function<Block.Settings, Block> blockFactory, Block.Settings settings, boolean shouldRegisterItem) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ChronoDomain.MOD_ID, name));

        Block block = blockFactory.apply(settings.registryKey(key));


        if (shouldRegisterItem) {
            RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, key.getValue());
            Item item = new BlockItem(block, new Item.Settings().registryKey(itemKey));
            Registry.register(Registries.ITEM, itemKey, item);
        }

        return Registry.register(Registries.BLOCK, key, block);
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> content.add(ModBlocks.TIME_STATION));
    }

    public static final Block TIME_STATION = register(
            "time_station",
            Block::new,
            AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                    .requiresTool()
                    .strength(5.0f, 6.0f)
                    .luminance(state -> 5),
            true
    );
}
