package xyz.turtletowerz.DynChunks;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import xyz.turtletowerz.DynChunks.mixin.ChunkStorageInvokerMixin;

public class Commands {
	public static void buildCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			CommandManager.literal(DynChunks.dynmapSetID)
			.requires(source -> source.hasPermissionLevel(4))
			.then(
				CommandManager.literal("enabled")
				.then(
					CommandManager.argument("enabled", BoolArgumentType.bool())
					.executes(ctx -> {
						boolean result = BoolArgumentType.getBool(ctx, "enabled");
						ctx.getSource().sendMessage(Text.literal("Enabled: " + result));
						DynChunks.logInfo("Enabled: " + result);

						if (DynChunks.markerSet != null)
							DynChunks.markerSet.setHideByDefault(!result);

						return 1;
					})
				)
			)

			.then(
				CommandManager.literal("loaded")
				.then(
					// CommandManager.argument("dimension", DimensionArgumentType.dimension())
					// .then(
						//CommandManager.argument("chunkpos", Vec2ArgumentType.vec2())
						// CommandManager.argument("pos", ChunkPosArgumentType.chunkpos())
						// .executes(ctx -> {
						// 	//ctx.getSource().getWorld().
						// 	ChunkPos pos = ChunkPosArgumentType.getChunkPos(ctx, "pos");
						// 	//Vec2f vec = Vec2ArgumentType.getVec2(ctx, "chunkpos");
						// 	//ChunkPos pos = new ChunkPos((int)Math.rint(vec.x), (int)Math.rint(vec.y));
						// 	boolean loaded = ctx.getSource().getWorld().getChunkManager().isChunkLoaded(pos.x, pos.z);
						// 	ctx.getSource().sendMessage(Text.literal(pos.toString() + " Loaded: " + loaded));						
						// 	return 1;
						// })
						CommandManager.argument("chunk-x", IntegerArgumentType.integer())
						.then(
							CommandManager.argument("chunk-z", IntegerArgumentType.integer())
							.executes(ctx -> {
								int x = IntegerArgumentType.getInteger(ctx, "chunk-x");
								int z = IntegerArgumentType.getInteger(ctx, "chunk-z");
								ChunkPos pos = new ChunkPos(x, z);
								boolean loaded = ctx.getSource().getWorld().getChunkManager().isChunkLoaded(pos.x, pos.z);
								ctx.getSource().sendMessage(Text.literal(pos.toString() + " Loaded: " + loaded));						
								return 1;
							})
						)
					//)
				)
				.executes(ctx -> {
					ServerCommandSource source = ctx.getSource();
					ServerWorld world = source.getServer().getWorld(source.getEntity().getWorld().getRegistryKey());
					source.sendMessage(Text.literal("Loaded: " + world.getChunkManager().getLoadedChunkCount()));
					return 1;
				})
			)

			.then(
				CommandManager.literal("clear")
				.executes(ctx -> {
					DynChunks.purgeUnloaded(ctx.getSource().getWorld(), false);
					ctx.getSource().sendMessage(Text.literal("Cleared bad markers"));
					DynChunks.logInfo("Cleared bad markers");
					return 1;
				})
			)

			.then(
				CommandManager.literal("reload")
				.executes(ctx -> {
					try {
						ServerWorld world = ctx.getSource().getWorld();
						ServerChunkManager manager = world.getChunkManager();
						ChunkStorageInvokerMixin storage = (ChunkStorageInvokerMixin)manager.threadedAnvilChunkStorage;
	
						DynChunks.purgeUnloaded(world, true);
	
						for (ChunkHolder holder : storage.invokeEntryIterator()) {
							ChunkPos pos = holder.getPos();
	
							if (manager.isChunkLoaded(pos.x, pos.z)) {
								DynChunks.setEnabled(world, holder.getWorldChunk(), true);
							}
						}
	
						ctx.getSource().sendMessage(Text.literal("Reloaded all markers"));
						DynChunks.logInfo("Reloaded all markers");
					} catch (Exception e) {
						ctx.getSource().sendMessage(Text.literal("Error: " + e.toString()));
					}

					return 1;
				})
			)
		));
	}
}
