package com.fabio.chrono.mixin;

import com.fabio.chrono.ChronoDomain;
import com.fabio.chrono.TimeFieldManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
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

		if (!manager.shouldTickEntityNow(entity)) {
			ci.cancel(); // Skip this tick entirely
			return;
		}

		if (manager.shouldTickEntityMultipleTimes(entity)) {
			int extraTicks = manager.getExtraTicksForEntity(entity);
			for (int i = 0; i < extraTicks; i++) {
				entity.baseTick();
			}
		}
	}

	/**
	 * This injection modifies the velocity vector just before it's used
	 * for moving entities that are in a time field.
	 * This is the ONLY place we should modify velocity to avoid double-application.
	 */
	@ModifyVariable(method = "move", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private Vec3d modifyMovementVelocity(Vec3d original) {
		Entity entity = (Entity)(Object)this;
		TimeFieldManager manager = ChronoDomain.getTimeFieldManager();

		// Only modify velocity for entities in a time field
		if (!manager.isEntityInTimeField(entity)) {
			return original;
		}

		float timeFactor = manager.getTimeFactorForEntity(entity);

		// Scale movement based on time factor (both for speeding up and slowing down)
		if (timeFactor != 1.0f) {
			return new Vec3d(
					original.x * timeFactor,
					original.y * timeFactor,
					original.z * timeFactor
			);
		}

		return original;
	}
}