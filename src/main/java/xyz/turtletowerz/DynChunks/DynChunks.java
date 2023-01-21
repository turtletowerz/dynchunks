package xyz.turtletowerz.DynChunks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.WorldChunk;

import java.util.HashMap;

import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynChunks implements ModInitializer {
	public static final String dynmapSetID = "dynchunks";
	private static final Logger LOGGER = LoggerFactory.getLogger(dynmapSetID);
	private static HashMap<World, HashMap<ChunkPos, AreaMarker>> map = new HashMap<>();
	private static boolean apiEnabled;
	public static MarkerSet markerSet;

	public static void logInfo(String text, boolean info) {
		if (info) {
			LOGGER.info("[DynChunks] " + text);
			return;
		}
		LOGGER.debug("[DynChunks] " + text);
	}

	public static void logInfo(String text) {
		logInfo(text, false);
	}

	public static void purgeUnloaded(World world, boolean all) {
		ChunkManager cm = world.getChunkManager();
		HashMap<ChunkPos, AreaMarker> map = getMap(world);

		// TODO: isChunkLoaded returns false for INACCESSIBLE, do we care?
		map.keySet().stream()
			.filter(key -> (!cm.isChunkLoaded(key.x, key.z) || all))
			.forEach(key -> map.remove(key).deleteMarker());
	}

	private static HashMap<ChunkPos, AreaMarker> getMap(World key) {
		HashMap<ChunkPos, AreaMarker> chunks = map.get(key);
		if (chunks == null) {
			map.put(key, new HashMap<>());
			chunks = map.get(key);
		}
		return chunks;
	}

	public static void updateMarkerColor(ServerWorld world, ChunkPos pos, ChunkHolder.LevelType level) {
		HashMap<ChunkPos, AreaMarker> chunks = getMap(world);
		AreaMarker marker = chunks.get(pos);

		if (marker == null)
			return;

		final double opacity = 0.45;
		switch (level) {
			case ENTITY_TICKING:
				marker.setFillStyle(opacity, 0x0AC80A); // Green
				break;
			case TICKING:
				marker.setFillStyle(opacity, 0xC8C80A); // Yellow
				break;
			case INACCESSIBLE:
			case BORDER:
				marker.setFillStyle(opacity, 0xC80A0A); // Red
				break;
			default:
				logInfo("Could not match level: " + level, true);
		}
	}

	// https://github.com/webbukkit/dynmap/blob/v3.0/fabric-1.19/src/main/java/org/dynmap/fabric_1_19/FabricWorld.java#L39
	private static String getWorldName(World w) {
        RegistryKey<World> key = w.getRegistryKey();

        if (key == World.OVERWORLD) {
            return w.getServer().getSaveProperties().getLevelName();
        } else if (key == World.END) {
            return "DIM1";
        } else if (key == World.NETHER) {
            return "DIM-1";
        }

        return key.getValue().getNamespace() + "_" + key.getValue().getPath();
    }

	// TODO: Find a way to consolidate nearby area labels to combine them
	// TODO: Doesn't remove labels when a player loads and then unloads a chunk
	public static void setEnabled(ServerWorld world, WorldChunk chunk, boolean loaded) {
		if (!apiEnabled || chunk == null)
			return;

		HashMap<ChunkPos, AreaMarker> chunks = getMap(world);
		ChunkPos pos = chunk.getPos();
		//ChunkPosWorld pos = ChunkPosWorld.fromChunk(chunk);
		//AreaMarker marker = markerSet.findAreaMarker(markerID);

		if (chunks.containsKey(pos)) {
			logInfo("Marker found! " + pos);
			if (!loaded) {
				logInfo("Deleting existing label: " + pos);
				//deleteMarker(pos);
				purgeUnloaded(world, false);
			}
			return;
		}

		String worldPath = getWorldName(world);
		String markerID = worldPath + pos.toString();

		logInfo("Adding new label: " + markerID);
		double x[] = {pos.getStartX(), pos.getEndX()};
		double z[] = {pos.getStartZ(), pos.getEndZ()};

		AreaMarker marker = markerSet.createAreaMarker(markerID, "Active Chunk", false, worldPath, x, z, false);
		marker.setLabel("Loaded chunk: " + pos.toString());
		marker.setLineStyle(3, 0.2, 0x474747);

		// Change chunk color based off of level type
		// TODO: Should this be optional?
		// TODO: Maybe hook WorldChunk.setLevelTypeProvider to change the provider for this

		chunks.put(pos, marker);

		updateMarkerColor(world, chunk.getPos(), chunk.getLevelType());
	}

	@Override
	public void onInitialize() {
		Commands.buildCommands();

		// Chunk handling
		ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> setEnabled(world, chunk, true));
		ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> setEnabled(world, chunk, false));

		DynmapCommonAPIListener.register(new DynmapCommonAPIListener() {
			@Override
			public void apiEnabled(DynmapCommonAPI api) {
				MarkerAPI markerAPI = api.getMarkerAPI();

				markerSet = markerAPI.getMarkerSet(dynmapSetID);
                if (markerSet == null)
                    markerSet = markerAPI.createMarkerSet(dynmapSetID, "Chunks", null, false);

				apiEnabled = true;
				logInfo("Started API", true);
			}

			@Override
			public void apiDisabled(DynmapCommonAPI api) {
				apiEnabled = false;
				map.forEach((key, value) -> purgeUnloaded(key, true));
				map.clear();
				logInfo("Closed API", true);
			}
		});
	}
}
