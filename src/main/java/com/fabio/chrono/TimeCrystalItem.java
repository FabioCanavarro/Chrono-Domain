package com.fabio.chrono;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class TimeCrystalItem extends Item {
    public TimeCrystalItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        ChronoDomain.registerTimeFieldEntity(entity, 200.0f); // Example: double the time speed for the entity
        return ActionResult.SUCCESS; // SUCCESS if you handled it, PASS if not
    }
}
