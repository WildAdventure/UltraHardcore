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
package com.gmail.filoghost.ultrahardcore.listener;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.player.ExpManager;
import com.gmail.filoghost.ultrahardcore.player.ExpManager.LevelInfo;
import com.google.common.collect.Sets;

import lombok.Getter;

import com.gmail.filoghost.ultrahardcore.player.HGamer;
import com.gmail.filoghost.ultrahardcore.player.Status;

public class ChatListener implements Listener {
	
	@Getter private static Set<Player> nextIsGlobal = Sets.newConcurrentHashSet();
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event) {

		HGamer hGamer = UltraHardcore.getHGamer(event.getPlayer());
		
		LevelInfo info = ExpManager.getCurrentLevelInfo(hGamer.getCachedExp());
		
		event.setFormat(event.getFormat().replace("{LEVEL_COLOR}",  ExpManager.isMaxLevel(info.getLevel()) ? ChatColor.GOLD.toString() : ChatColor.GRAY.toString()));
		event.setFormat(event.getFormat().replace("{LEVEL}", Integer.toString(info.getLevel())));
		
		boolean global = nextIsGlobal.remove(event.getPlayer());
		
		if (hGamer.getStatus() == Status.SPECTATOR && !global) {
			event.setFormat(ChatColor.LIGHT_PURPLE + "Chat spettatori > " + ChatColor.WHITE + event.getFormat());
			event.getRecipients().removeAll(UltraHardcore.getByStatus(Status.TRIBUTE));
		}
		
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		nextIsGlobal.remove(event.getPlayer());
	}
	
}
