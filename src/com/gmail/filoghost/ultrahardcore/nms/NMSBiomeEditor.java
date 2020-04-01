/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gmail.filoghost.ultrahardcore.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.BiomeForest;
import wild.api.WildCommons;

public class NMSBiomeEditor {
	
	
	public static void removeOceans() throws Exception {
		Class<?> biomeBaseClass = Class.forName("net.minecraft.server." + WildCommons.getBukkitVersion() + ".BiomeBase");
		
		setFinalStatic(biomeBaseClass.getDeclaredField("OCEAN"), makePlainBiome(0, "Ocean"));
		setFinalStatic(biomeBaseClass.getDeclaredField("FROZEN_OCEAN"), makePlainBiome(10, "FrozenOcean"));
		setFinalStatic(biomeBaseClass.getDeclaredField("DEEP_OCEAN"), makePlainBiome(24, "Deep Ocean"));
	}
	
	private static Object makePlainBiome(int id, String name) throws Exception {
		Constructor<BiomeForest> plainsConstructor = BiomeForest.class.getDeclaredConstructor(int.class, int.class);
		plainsConstructor.setAccessible(true);
		BiomeForest newBiome = plainsConstructor.newInstance(id, 0);
		
		Method b = BiomeBase.class.getDeclaredMethod("b", int.class);
		b.setAccessible(true);
		b.invoke(newBiome, 353825);
		
		Method a = BiomeBase.class.getDeclaredMethod("a", String.class);
		a.setAccessible(true);
		a.invoke(newBiome, name);
		
		return newBiome;
	}

	private static void setFinalStatic(Field field, Object newValue) throws Exception {
		field.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);
	}

}
