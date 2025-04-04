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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(Entity.class)
public abstract class EntityTickMixin {
	@Shadow public abstract void baseTick();
	@Shadow public abstract void setVelocity(double x, double y, double z);
	@Shadow protected abstract void move(MovementType movementType, net.minecraft.util.math.Vec3d movement);
	@Shadow public abstract net.minecraft.util.math.Vec3d getVelocity();

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void onTick(CallbackInfo ci) {
		Entity entity = (Entity)(Object)this;
		TimeFieldManager manager = ChronoDomain.getTimeFieldManager();

		if (!manager.shouldTickEntityNow(entity)) {
			ci.cancel(); // Skip this tick
		} else if (manager.shouldTickEntityMultipleTimes(entity)) {
			int extraTicks = manager.getExtraTicksForEntity(entity);
			for (int i = 0; i < extraTicks; i++) {
				// Basic entity tick
				entity.baseTick();

				// Handle gravity and movement for extra ticks
				net.minecraft.util.math.Vec3d currentVelocity = entity.getVelocity();

				// Apply gravity effect for falling entities
				if (!entity.isOnGround() && !entity.hasNoGravity()) {
					// Apply additional gravity for each extra tick
					entity.setVelocity(currentVelocity.x,
							currentVelocity.y - 0.08, // Minecraft's gravity constant
							currentVelocity.z);

					// Update fall distance
					if (entity instanceof LivingEntity) {
						if (currentVelocity.y < 0) {
							((LivingEntity)entity).fallDistance += -currentVelocity.y;
						}
					}
				}

				entity.move(MovementType.SELF, entity.getVelocity());

				ChronoDomain.LOGGER.info("Extra tick with movement for entity: " + entity.getUuid());
			}
		}
	}
}
