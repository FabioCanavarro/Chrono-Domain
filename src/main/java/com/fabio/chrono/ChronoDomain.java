package com.fabio.chrono;

import net.fabricmc.api.ModInitializer;


import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;







public class ChronoDomain implements ModInitializer {
	public static final String MOD_ID = "chrono";
	private static final TimeFieldManager TIME_FIELD_MANAGER = new TimeFieldManager();

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModItems.initialize();
		ModBlocks.initialize();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("GetEntityRegistered").executes(context -> {
				context.getSource().sendFeedback(() -> Text.literal("Called /test_command." + TIME_FIELD_MANAGER.getEntityRegistered()), false);
				return 1;
			}));
		});

		LOGGER.info("Mod items initialized");
	}

	public static TimeFieldManager getTimeFieldManager() {
		return TIME_FIELD_MANAGER;
	}

	public static void registerTimeFieldEntity(Entity entity, float timeFactor) {
		TIME_FIELD_MANAGER.registerEntityInTimeField(entity, timeFactor);
	}

}