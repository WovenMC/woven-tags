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

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;

class TagDelegate<T> implements Tag.Identified<T> {
	private final Identifier id;
	private final Supplier<TagGroup<T>> groupGetter;
	// store cached tags and groups as a single immutable object to not introduce any concurrency issues not present in the underlying system
	private CachedTag<T> cached;

	TagDelegate(Identifier id, Supplier<TagGroup<T>> groupGetter) {
		this.id = id;
		this.groupGetter = groupGetter;
	}

	private Tag<T> getTag() {
		CachedTag<T> cached = this.cached;
		TagGroup<T> currentGroup = groupGetter.get();

		if (cached == null || cached.group != currentGroup) {
			this.cached = new CachedTag<T>(currentGroup.getTagOrEmpty(id), currentGroup);
		}

		return this.cached.tag;
	}

	@Override
	public boolean contains(T entry) {
		return getTag().contains(entry);
	}

	@Override
	public List<T> values() {
		return getTag().values();
	}

	@Override
	public Identifier getId() {
		return id;
	}

	private static class CachedTag<T> {
		final Tag<T> tag;
		final TagGroup<T> group;

		CachedTag(Tag<T> tag, TagGroup<T> group) {
			this.tag = tag;
			this.group = group;
		}
	}
}
