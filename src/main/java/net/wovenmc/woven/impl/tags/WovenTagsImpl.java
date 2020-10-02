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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.wovenmc.woven.api.tags.WovenTags;
import net.wovenmc.woven.mixin.tags.DynamicRegistryManagerImplAccessor;
import net.wovenmc.woven.mixin.tags.FluidTagsAccessor;

@ApiStatus.Internal
public class WovenTagsImpl implements WovenTags {
	public static final WovenTagsImpl INSTANCE = new WovenTagsImpl();
	public static DynamicRegistryManager.Impl registryManager;

	private final Map<RegistryKey<? extends Registry<?>>, TagGroup<?>> tagGroupMap = new HashMap<>();

	private WovenTagsImpl() { }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> Tag<T> get(RegistryKey<Registry<T>> registryKey, Identifier id) {
		//this rawtype cast shouldn't be needed, but javac's type inference is awful.
		Registry<T> registry = (Registry<T>) Registry.REGISTRIES.get((RegistryKey) registryKey);

		if (registry == Registry.BLOCK) {
			return (TagDelegate<T>) new TagDelegate<Block>(id, BlockTags::getTagGroup);
		} else if (registry == Registry.ITEM) {
			return (TagDelegate<T>) new TagDelegate<Item>(id, ItemTags::getTagGroup);
		} else if (registry == Registry.ENTITY_TYPE) {
			return (TagDelegate<T>) new TagDelegate<EntityType<?>>(id, EntityTypeTags::getTagGroup);
		} else if (registry == Registry.FLUID) {
			return (TagDelegate<T>) new TagDelegate<Fluid>(id, FluidTagsAccessor.getRequiredTags()::getGroup);
		} else {
			return new TagDelegate<T>(id, () -> (TagGroup<T>) tagGroupMap.get(registryKey));
		}
	}

	public Identifier getIdentifier() {
		return new Identifier("woven_tags", "reload_listener");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<TagLoaderInfo<?>> prepare(ResourceManager manager) {
		tagGroupMap.clear();
		Map<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>> dynRegistries = ((DynamicRegistryManagerImplAccessor) (Object) registryManager)
				.getRegistries();
		List<TagLoaderInfo<?>> loaderList = new ArrayList<>();

		for (Map.Entry<? extends RegistryKey<? extends Registry<?>>, ? extends SimpleRegistry<?>> entry : dynRegistries.entrySet()) {
			RegistryKey<? extends Registry<?>> key = entry.getKey();
			TagGroupLoader<?> loader = javacHasAwfulTypeInference(entry.getValue(),
					"tags/" + getDataLocation(key.getValue()), key.getValue().toString());
			Map<Identifier, Tag.Builder> prepareResult = loader.prepareReload(manager, Runnable::run).join();
			loaderList.add(new TagLoaderInfo(key, loader, prepareResult));
		}

		for (Registry<?> registry : Registry.REGISTRIES) {
			if (!hasVanillaTag(registry)) {
				RegistryKey<? extends Registry<?>> key = registry.getKey();
				TagGroupLoader<?> loader = new TagGroupLoader((Function<Identifier, Optional<?>>) registry::getOrEmpty,
						"tags/" + getDataLocation(key.getValue()), key.getValue().toString());
				Map<Identifier, Tag.Builder> prepareResult = loader.prepareReload(manager, Runnable::run).join();
				loaderList.add(new TagLoaderInfo(key, loader, prepareResult));
			}
		}

		return loaderList;
	}

	/*
	 * This is basically just a hack to get around javac's awful type inference. It
	 * won't acknowledge the {@link Identifier} version of {@code getOrEmpty}
	 * without this for some reason.
	 */
	private <T> TagGroupLoader<T> javacHasAwfulTypeInference(Registry<T> registry, String dataType, String entryType) {
		return new TagGroupLoader<T>(registry::getOrEmpty, dataType, entryType);
	}

	public void apply(List<TagLoaderInfo<?>> loaders) {
		for (TagLoaderInfo<?> info : loaders) {
			TagGroup<?> group = info.loader.applyReload(info.tags);
			tagGroupMap.put(info.registryKey, group);
		}
	}

	private boolean hasVanillaTag(Registry<?> registry) {
		return registry == Registry.BLOCK || registry == Registry.ITEM || registry == Registry.ENTITY_TYPE
				|| registry == Registry.FLUID;
	}

	// gets the location of tags for a registry with the given identifier
	private String getDataLocation(Identifier id) {
		if (id.getNamespace().equals("minecraft")) {
			return id.getPath();
		} else {
			return id.getNamespace() + "/" + id.getPath();
		}
	}

	public static class TagLoaderInfo<T> {
		final RegistryKey<? extends Registry<T>> registryKey;
		final TagGroupLoader<T> loader;
		final Map<Identifier, Tag.Builder> tags;

		TagLoaderInfo(RegistryKey<? extends Registry<T>> registryKey, TagGroupLoader<T> loader,
				Map<Identifier, Tag.Builder> tags) {
			this.registryKey = registryKey;
			this.loader = loader;
			this.tags = tags;
		}
	}
}
