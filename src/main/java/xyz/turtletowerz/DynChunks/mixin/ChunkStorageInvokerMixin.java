package xyz.turtletowerz.DynChunks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;

@Mixin(ThreadedAnvilChunkStorage.class)
public interface ChunkStorageInvokerMixin {
	@Invoker("entryIterator")
	public Iterable<ChunkHolder> invokeEntryIterator();
}
