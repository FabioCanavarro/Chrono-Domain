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
        ChronoDomain.registerTimeFieldEntity(entity, ChronoDomain.timefactor);
        return ActionResult.SUCCESS;
    }
}
