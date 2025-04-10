package com.fabio.chrono;

import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class ChronoDomain implements ModInitializer {
	public static final String MOD_ID = "chrono";
	private static final TimeFieldManager TIME_FIELD_MANAGER = new TimeFieldManager();
	public static final ChunkTimeManager CHUNK_TIME_MANAGER = new ChunkTimeManager();

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static float timefactor = 10.0f;
	public static float slowtimefactor = 0.1f;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModItems.initialize();
		ModBlocks.initialize();
		ModBlockEntities.initialize();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("GetEntityRegistered").executes(context -> {
            context.getSource().sendFeedback(() -> Text.literal(TIME_FIELD_MANAGER.getEntityRegistered()), false);
            return 1;
        })));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("GetTimeFactor").executes(context -> {
            context.getSource().sendFeedback(() -> Text.literal("Time Factor: " + timefactor + "SlowTimeFactor: " + slowtimefactor), false);
            return 1;
        })));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("SetTimeFactor")
                .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                .executes(context -> {
                        timefactor = FloatArgumentType.getFloat(context, "value");
                        context.getSource().sendFeedback(() -> Text.literal("Set TimeFactor to " + timefactor), false);
                        return 1;
                    }
                ))
        ));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("SetSlowTimeFactor")
                .then(CommandManager.argument("value", FloatArgumentType.floatArg())
                        .executes(context -> {
                                    slowtimefactor = FloatArgumentType.getFloat(context, "value");
                                    context.getSource().sendFeedback(() -> Text.literal("Set TimeFactor to " + slowtimefactor), false);
                                    return 1;
                                }
                        ))
        ));

		LOGGER.info("Mod items initialized");
	}

	public static TimeFieldManager getTimeFieldManager() {
		return TIME_FIELD_MANAGER;
	}

	public static void registerTimeFieldEntity(Entity entity, float timeFactor) {
		TIME_FIELD_MANAGER.registerEntityInTimeField(entity, timeFactor);
	}

	public static void switchTimeFieldEntities(Map<UUID, Float> entitymap) {
		TIME_FIELD_MANAGER.SwitchTimeField(entitymap);
	}

	public static void removeTimeFieldEntity(UUID uuid) {
		TIME_FIELD_MANAGER.removeEntity(uuid);
	}

	public static ChunkTimeManager getChunkTimeManager(){
		return CHUNK_TIME_MANAGER;
	}

	public static void registerChunk(BlockPos pos, float timeFactor) {
		CHUNK_TIME_MANAGER.registerChunk(new ChunkPos(pos), timeFactor);
	}

	public static void deleteChunk(BlockPos pos) {
		CHUNK_TIME_MANAGER.deleteChunk(new ChunkPos(pos));
	}


}