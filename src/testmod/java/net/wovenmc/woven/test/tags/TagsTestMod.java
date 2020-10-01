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

package net.wovenmc.woven.test.tags;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.wovenmc.woven.api.tags.WovenTags;

public class TagsTestMod {
	private static final Tag<Enchantment> STATIC_REGISTRY_TEST = WovenTags.INSTANCE.get(Registry.ENCHANTMENT_KEY, new Identifier("woven_tags_test", "test"));
	private static final Tag<Biome> DYNAMIC_REGISTRY_TEST = WovenTags.INSTANCE.get(Registry.BIOME_KEY, new Identifier("woven_tags_test", "test"));
	private static final Logger LOGGER = LogManager.getLogger();

	public static void test(MinecraftServer s) {
		LOGGER.info("Static registry tag test (expect true, false): " + STATIC_REGISTRY_TEST.contains(Enchantments.AQUA_AFFINITY) + ", " + STATIC_REGISTRY_TEST.contains(Enchantments.EFFICIENCY));
		LOGGER.info("Dynamic registry tag test (expect true, false): " + DYNAMIC_REGISTRY_TEST.contains(resolveBiome(s, new Identifier("desert"))) + ", " + DYNAMIC_REGISTRY_TEST.contains(resolveBiome(s, new Identifier("forest"))));
	}

	private static Biome resolveBiome(MinecraftServer s, Identifier id) {
		System.out.println(s.getRegistryManager().get(Registry.BIOME_KEY).get(id));
		return s.getRegistryManager().get(Registry.BIOME_KEY).get(id);
	}
}
