/*
 * Copyright (c) 2020 WovenMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
