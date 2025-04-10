package com.fabio.chrono.mixin;

import com.fabio.chrono.ChronoDomain;
import com.fabio.chrono.ChunkTimeManager;
import com.fabio.chrono.TimeFieldManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public class ProjectileTickMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onProjectileTick(CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        TimeFieldManager timeFieldManager = ChronoDomain.getTimeFieldManager();
        ChunkTimeManager chunkTimeManager = ChronoDomain.getChunkTimeManager();

        // First, check if the projectile is in a time-affected entity field
        if (timeFieldManager.isEntityInTimeField(entity)) {
            float timeFactor = timeFieldManager.getTimeFactorForEntity(entity);
            modifyProjectileVelocity(entity, timeFactor);
            return;
        }

        // If not in an entity time field, check if it's in a time-affected chunk
        ChunkPos chunkPos = new ChunkPos(entity.getBlockPos());
        if (chunkTimeManager.isChunkAffected(chunkPos)) {
            float timeFactor = chunkTimeManager.getChunkTimeFactor(chunkPos);
            modifyProjectileVelocity(entity, timeFactor);

            // Register the projectile in the time field so we can track it
            if (!timeFieldManager.isEntityInTimeField(entity)) {
                ChronoDomain.registerTimeFieldEntity(entity, timeFactor);
            }
        }
    }

    /**
     * Modifies the projectile's velocity based on the time factor
     */
    @Unique
    private void modifyProjectileVelocity(Entity entity, float timeFactor) {
        if (timeFactor == 1.0f) {
            return; // No modification needed for normal time
        }

        Vec3d velocity = entity.getVelocity();

        // Scale the velocity by the time factor
        Vec3d modifiedVelocity = new Vec3d(
                velocity.x * timeFactor,
                velocity.y * timeFactor,
                velocity.z * timeFactor
        );

        // Apply the modified velocity
        entity.setVelocity(modifiedVelocity);

        // Log for debugging
        if (Math.abs(timeFactor - 1.0f) > 0.1f) {
            ChronoDomain.LOGGER.info("DEBUG: Modified projectile velocity from {} to {} with factor {}",
                    velocity, modifiedVelocity, timeFactor);
        }
    }

}
