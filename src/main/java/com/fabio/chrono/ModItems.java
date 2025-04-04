package com.fabio.chrono;

import java.util.function.Function;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {
    public static Item register(String name, Function<Item.Settings,Item> ItemFactory, Item.Settings Settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ChronoDomain.MOD_ID,name));

        Item item = ItemFactory.apply(Settings.registryKey(key));

        Registry.register(Registries.ITEM,key, item);

        return item;
    }

    public static void intialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> content.add(ModItems.TimeCrystal));
    }

    public static final Item TimeCrystal = register(
            "time_crystal",
            Item::new,
            new Item.Settings()
                    .maxCount(16)
    );



}


