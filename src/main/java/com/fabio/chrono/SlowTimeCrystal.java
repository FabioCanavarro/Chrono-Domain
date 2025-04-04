package com.fabio.chrono;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SlowTimeCrystal extends Item {


    public SlowTimeCrystal(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        World world = user.getWorld();

        ChronoDomain.registerTimeFieldEntity(entity, ChronoDomain.slowtimefactor);
        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS,
                1.0F, 0.5F);

        if (world instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 20; i++) {
                serverWorld.spawnParticles(
                        ParticleTypes.END_ROD,
                        entity.getX() + (world.getRandom().nextDouble() - 0.5) * entity.getWidth(),
                        entity.getY() + world.getRandom().nextDouble() * entity.getHeight(),
                        entity.getZ() + (world.getRandom().nextDouble() - 0.5) * entity.getWidth(),
                        10,
                        0,
                        0.1,
                        0,
                        0.01
                );
            }
        }

        // Only consume the item in survival mode
        if (!user.getAbilities().creativeMode) {
            stack.decrement(1);
        }
        return ActionResult.SUCCESS;
    }
}
