package com.fabio.chrono;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
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
        
    }
}
