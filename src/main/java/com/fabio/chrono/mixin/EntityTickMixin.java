package com.fabio.chrono.mixin;

import com.fabio.chrono.ChronoDomain;
import com.fabio.chrono.TimeFieldManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
		// Get the entity instance
		Entity entity = (Entity)(Object)this;

		// Get the TimeFieldManager instance
		TimeFieldManager manager = ChronoDomain.getTimeFieldManager();

		// Check if an entity is in a time field, and should tick
		// If not, cancel the tick
		if (!manager.shouldTickEntityNow(entity)) {
			ci.cancel();
			return;
		}

		// Check if the entity is in time field
		// If so, recursively call the base tick method depending on the time factor
		if (manager.shouldTickEntityMultipleTimes(entity)) {
			int extraTicks = manager.getExtraTicksForEntity(entity);
			for (int i = 0; i < extraTicks; i++) {
				entity.baseTick();
			}
		}
	}

	// Injects into the movement method to modify the velocity of the entity based on the Time factor that is found in the TimeFieldManager HashMap
	@ModifyVariable(method = "move", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private Vec3d modifyMovementVelocity(Vec3d original) {
		// Get the entity instance
		Entity entity = (Entity)(Object)this;

		// Get the TimeFieldManager instance
		TimeFieldManager manager = ChronoDomain.getTimeFieldManager();

		// Check if the entity is in a time field
		if (!manager.isEntityInTimeField(entity)) {
			return original;
		}

		// Get the time factor for the entity
		float timeFactor = manager.getTimeFactorForEntity(entity);

		// If the time factor is not 1.0, modify the velocity based on the time factor
		if (timeFactor != 1.0f && original.y < 0) {
			return new Vec3d(
					original.x * timeFactor,
					original.y * timeFactor,
					original.z * timeFactor
			);
		}
		else if (timeFactor != 1.0f) {
			return new Vec3d(
					original.x * timeFactor,
					original.y,
					original.z * timeFactor
			);
		}

		// If the time factor is 1.0, return the original velocity
		return original;
	}
}