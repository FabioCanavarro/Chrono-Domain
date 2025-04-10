package com.fabio.chrono;
import java.util.function.Function;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {
    public static Item register(String name, Function<Item.Settings, Item> ItemFactory, Item.Settings Settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ChronoDomain.MOD_ID, name));

        Item item = ItemFactory.apply(Settings.registryKey(key));

        Registry.register(Registries.ITEM, key, item);

        return item;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> content.add(ModItems.TIME_CRYSTAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> content.add(ModItems.SLOW_TIME_CRYSTAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> content.add(ModItems.TIME_CRYSTAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> content.add(ModItems.SLOW_TIME_CRYSTAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SEARCH).register(content -> content.add(ModItems.TIME_CRYSTAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SEARCH).register(content -> content.add(ModItems.SLOW_TIME_CRYSTAL));
    }

    public static final Item TIME_CRYSTAL = register(
            "time_crystal",
            TimeCrystalItem::new,
            new Item.Settings()
                    .maxCount(1)
    );

    public static final Item SLOW_TIME_CRYSTAL = register(
            "slow_time_crystal",
            SlowTimeCrystal::new,
            new Item.Settings()
                    .maxCount(1)
    );



}


