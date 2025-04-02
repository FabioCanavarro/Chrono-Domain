package com.fabio.chrono;

import java.util.function.Function;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {
    public static Item register(String name, Function<Item.Settings,Item> ItemFactory, Item.Settings Settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ChronoDomain.MOD_ID, name));

        Item item = ItemFactory.apply(Settings.registryKey(key));

        Registry.register(Registries.ITEM,key, item);

        return item;
    }


}

