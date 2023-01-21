package xyz.turtletowerz.DynChunks.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import xyz.turtletowerz.DynChunks.DynChunks;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ChunkStorageMixin {
	@Shadow
	@Final
	private ServerWorld world;
	
	// @Inject(
	// 	method = "method_18843",
	// 	at = @At(
	// 		value = "INVOKE",
	// 		target = "Lnet/minecraft/server/world/ServerLightingProvider;updateChunkStatus(Lnet/minecraft/util/math/ChunkPos;)V",
	// 		shift = At.Shift.BEFORE
	// 	)
	// )
	// private void onUnload(ChunkHolder chunkHolder, CompletableFuture<Chunk> chunkFuture, long pos, Chunk chunk, CallbackInfo ci) {//, Chunk chunk
	// 	//System.out.println("unloaded");
	// 	DynChunks.setEnabled(this.world, (WorldChunk) chunk, false);
	// }

	@Inject(method = "setLevel(JILnet/minecraft/server/world/ChunkHolder;I)Lnet/minecraft/server/world/ChunkHolder;", at = @At("RETURN"))
	private void onLevelChange(long pos, int level, @Nullable ChunkHolder holder, int i, CallbackInfoReturnable ci) {
		//System.out.println("unloaded");
		DynChunks.updateMarkerColor(this.world, holder.getPos(), holder.getLevelType());
	}
}
