package com.fabio.chrono.mixin;

import com.fabio.chrono.ChronoDomain;
import com.fabio.chrono.TimeFieldManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityTickMixin {
	@Shadow public abstract void baseTick();
	@Shadow public abstract void setVelocity(double x, double y, double z);
	@Shadow public abstract void move(MovementType movementType, Vec3d movement);
	@Shadow public abstract Vec3d getVelocity();
	@Shadow public double fallDistance;
	/**
	 * Main injection point for entity ticking
	 */
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void onTick(CallbackInfo ci) {
		Entity entity = (Entity)(Object)this;
		TimeFieldManager manager = ChronoDomain.getTimeFieldManager();

		// Check if this entity should tick at all this game tick
		if (!manager.shouldTickEntityNow(entity)) {
			ci.cancel(); // Skip this tick entirely
			return;
		}

		if (manager.shouldTickEntityMultipleTimes(entity)) {
			int extraTicks = manager.getExtraTicksForEntity(entity);
			for (int i = 0; i < extraTicks; i++) {
				applyExtraTick(entity);
			}
		}

		if (manager.isEntitySlowed(entity)) {
			applySlowedPhysics(entity);
		}
	}

	@Unique
	private void applyExtraTick(Entity entity) {
		// Basic entity tick
		entity.baseTick();

		// Handle gravity and movement for extra ticks
		Vec3d currentVelocity = entity.getVelocity();

		// Apply gravity effect for falling entities
		if (!entity.isOnGround() && !entity.hasNoGravity()) {
			entity.setVelocity(
					currentVelocity.x,
					currentVelocity.y - 0.01,
					currentVelocity.z
			);

			if (entity instanceof LivingEntity) {
				if (currentVelocity.y < 0) {
					entity.fallDistance -= currentVelocity.y;
				}
			}
		}

		// Move the entity based on its velocity
		entity.move(MovementType.SELF, entity.getVelocity());
		ChronoDomain.LOGGER.info("Extra tick with movement for entity: " + entity.getUuid());
	}

	/**
	 * Scales physics and velocity for slowed entities
	 */
	@Unique
	private void applySlowedPhysics(Entity entity) {
		// Get the time factor for this entity
		TimeFieldManager manager = ChronoDomain.getTimeFieldManager();
		float timeFactor = manager.getTimeFactorForEntity(entity);

		// Scale velocity to match the time factor
		Vec3d currentVelocity = entity.getVelocity();
		entity.setVelocity(
				currentVelocity.x * timeFactor,
				currentVelocity.y * timeFactor,
				currentVelocity.z * timeFactor
		);

		// Log the slowed velocity
        ChronoDomain.LOGGER.info("Applied slowed physics for entity: {} with factor: {}", entity.getUuid(), timeFactor);
	}

	/**
	 * This injection modifies the velocity vector just before it's used
	 * for moving entities that are in a time field.
	 */
	@ModifyVariable(method = "move", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private Vec3d modifyMovementVelocity(Vec3d original) {
		Entity entity = (Entity)(Object)this;
		TimeFieldManager manager = ChronoDomain.getTimeFieldManager();

		// Only modify velocity for entities that are in a time field
		if (!manager.isEntityInTimeField(entity)) {
			return original;
		}

		float timeFactor = manager.getTimeFactorForEntity(entity);

		if (manager.isEntitySlowed(entity)) {
			return new Vec3d(
					original.x * timeFactor,
					original.y * timeFactor,
					original.z * timeFactor
			);
		}

		return original;
	}
}
