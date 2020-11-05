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

package net.wovenmc.woven.test.mixin.tags;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.wovenmc.woven.test.tags.TagsTestMod;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Inject(at = @At("HEAD"), method = "runServer")
	private void onStart(CallbackInfo info) {
		TagsTestMod.test((MinecraftServer) (Object) this);
	}

	@Inject(at = @At("RETURN"), method = "reloadResources", cancellable = true)
	private void onReload(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		cir.setReturnValue(cir.getReturnValue().thenRun(() -> TagsTestMod.test((MinecraftServer) (Object) this)));
	}
}
