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
package com.gmail.filoghost.ultrahardcore.utils.scatter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.filoghost.ultrahardcore.utils.PlayerUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TeleportGroup {
	
	@Getter
	private Set<Player> players;

	public TeleportGroup(Player player) {
		players = Sets.newHashSet();
		players.add(player);
	}
	
	public TeleportGroup(Collection<Player> players) {
		this.players = new HashSet<>(players);
	}
	
	public void teleport(Location loc) {
		if (!loc.getChunk().isLoaded()) {
			loc.getChunk().load(true);
		}
		
		for (Player player : players) {
			if (player.isOnline()) {
				player.setFallDistance(0f);
				player.setNoDamageTicks(50);
				PlayerUtils.teleportDismount(player, loc);
			}
		}
	}

	@Override
	public String toString() {
		List<String> names = Lists.newArrayList();
		for (Player member : players) {
			names.add(member.getName());
		}
		
		return "TeleportGroup [players=" + StringUtils.join(names, ", ") + "]";
	}
	
}
