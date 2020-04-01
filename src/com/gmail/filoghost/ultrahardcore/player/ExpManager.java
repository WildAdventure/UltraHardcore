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
package com.gmail.filoghost.ultrahardcore.player;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import com.google.common.collect.Lists;

public class ExpManager {
	
	private static List<Integer> expLevels = Lists.newArrayList();
	
	public static void loadLevels (List<Integer> levels) {
		expLevels.clear();
		
		for (Integer i : levels) {
			if (i <= 0) {
				throw new IllegalArgumentException("Levels must be positive");
			}
			
			expLevels.add(i);
		}
	}
	
	public static int calculateLevel(int exp) {
		return calculateLevel0(exp) + 1;
	}
	
	public static LevelInfo getCurrentLevelInfo(int exp) {
		int level0 = 0;
		
		while (true) {
			
			if (level0 >= expLevels.size()) {
				return new LevelInfo(level0 + 1, exp, -1);
			}
			
			int toNextLevel = expLevels.get(level0);
			
			if (exp < toNextLevel) {
				return new LevelInfo(level0 + 1, exp, toNextLevel);
			}
			
			exp -= toNextLevel;
			level0++;
		}
	}
	
	public static int getMaxLevel() {
		return expLevels.size() + 1;
	}
	
	public static boolean isMaxLevel(int level) {
		return isMaxLevel0(level - 1);
	}
	
	private static boolean isMaxLevel0(int level0) {
		return level0 >= expLevels.size();
	}

	private static int calculateLevel0(int exp) {
		int index = 0;
		
		while (exp > 0 && index < expLevels.size()) {
			exp -= expLevels.get(index);
			
			if (exp >= 0) {
				index++;
			}
		}
		
		return index;
	}
	
	@AllArgsConstructor
	@Getter
	@ToString
	public static class LevelInfo {
		
		private int level, currentLevelExp, totalExpForNextLevel;
		
		public boolean isMax() {
			return totalExpForNextLevel == -1;
		}
		
	}
	
}
