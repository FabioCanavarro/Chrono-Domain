package com.fabio.chrono.mixin;

import com.fabio.chrono.ChronoDomain;
import com.fabio.chrono.TimeFieldManager;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityTickMixin {
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void onTick(CallbackInfo ci) {
		Entity entity = (Entity)(Object)this;
		TimeFieldManager manager = ChronoDomain.getTimeFieldManager();

		if (!manager.shouldTickEntityNow(entity)) {
			ci.cancel(); // Skip this tick
		} else if (manager.shouldTickEntityMultipleTimes(entity)) {
			int extraTicks = manager.getExtraTicksForEntity(entity);
			for (int i = 0; i < extraTicks; i++) {
				entity.baseTick(); // Additional ticks
			}
		}
	}
}