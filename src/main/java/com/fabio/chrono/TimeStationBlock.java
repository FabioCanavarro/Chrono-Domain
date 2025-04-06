package com.fabio.chrono;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class TimeStationBlock extends Block {
    public static final BooleanProperty ACTIVATED = BooleanProperty.of("activated");

    public TimeStationBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(ACTIVATED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.isHolding(ModItems.TIME_CRYSTAL)) {
            return ActionResult.PASS;
        } else {
            if (!state.get(ACTIVATED)) {
                // decrement the item stack
                if (!player.getAbilities().creativeMode) {
                    player.getStackInHand(player.getActiveHand()).decrement(1);
                }

                boolean activated = state.get(ACTIVATED);

                world.setBlockState(pos, state.with(ACTIVATED, !activated));

                world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.addFireworkParticle(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        1.0,
                        1.0,
                        1.0,
                        List.of(FireworkExplosionComponent.DEFAULT)
                );

            }
            else {
                world.setBlockState(pos, state.with(ACTIVATED, false));
                world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 1.0F, 0.5F);
            }
            return ActionResult.SUCCESS;
        }
    }

}
