package net.wovenmc.woven.mixin.tags;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.profiler.Profiler;
import net.wovenmc.woven.impl.tags.WovenTagsImpl;

@Mixin(TagManagerLoader.class)
public class TagManagerLoaderMixin {
	@Unique
	private List<WovenTagsImpl.TagLoaderInfo<?>> loaders;

	@Inject(at = @At("HEAD"), method = "reload")
	private void tagPrepareCallback(ResourceReloadListener.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> ci) {
		loaders = WovenTagsImpl.INSTANCE.prepare(manager);
	}

	@Inject(at = @At(value = "INVOKE", target = "net/minecraft/tag/TagGroupLoader.applyReload(Ljava/util/Map;)Lnet/minecraft/tag/TagGroup;", ordinal = 0), method = "*(Ljava/lang/Void;)V")
	private void tagApplyCallback(CallbackInfo ci) {
		WovenTagsImpl.INSTANCE.apply(loaders);
	}
}
