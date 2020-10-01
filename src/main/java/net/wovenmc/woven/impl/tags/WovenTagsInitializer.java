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

package net.wovenmc.woven.impl.tags;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.wovenmc.woven.api.resource.ResourceManagerHelper;

@ApiStatus.Internal
public class WovenTagsInitializer implements ModInitializer {
	// Needed to lookup dynamic registries for tags.
	public static DynamicRegistryManager registryManager = null;

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(WovenTagsImpl.INSTANCE);
	}
}