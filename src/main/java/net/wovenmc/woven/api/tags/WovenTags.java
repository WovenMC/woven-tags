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

package net.wovenmc.woven.api.tags;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.wovenmc.woven.impl.tags.WovenTagsImpl;

/**
 * <p>
 * Woven tag API.
 *
 * <p>
 * Can return tags for any registry. Registries in the {@code minecraft}
 * namespace use {@code tags/registry_id} for consistency with vanilla tag
 * types, while modded registries use {@code tags/mod_id/registry_id}.
 *
 * <p>
 * While this API can be called at any time, returned tags will be invalid until
 * data packs are loaded, and querying them is undefined behavior until then.
 * Returned tags will automatically update when data packs are (re)loaded, and
 * are safe to store as {@code static final} constants.
 */
@ApiStatus.NonExtendable
public interface WovenTags {
	WovenTags INSTANCE = WovenTagsImpl.INSTANCE;

	<T> @NotNull Tag<T> get(@NotNull RegistryKey<Registry<T>> registry, @NotNull Identifier id);
}
