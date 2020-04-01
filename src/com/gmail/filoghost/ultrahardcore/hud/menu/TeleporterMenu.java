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
package com.gmail.filoghost.ultrahardcore.hud.menu;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.player.Status;

import wild.api.WildCommons;
import wild.api.bridges.PexBridge;
import wild.api.bridges.PexBridge.PrefixSuffix;
import wild.api.menu.Icon;
import wild.api.menu.IconMenu;
import wild.api.menu.StaticIcon;

public class TeleporterMenu {
	
	private static IconMenu teleporterMenu;
	
	public static void load() {
		teleporterMenu = new IconMenu("Teletrasporto rapido", 6);
		update();
	}
	
	public static void update() {
		teleporterMenu.clearIcons();
		Collection<Player> tributes = UltraHardcore.getByStatus(Status.TRIBUTE);
		
		int index = 0;
		for (Player tribute : tributes) {
			
			ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
			itemMeta.setOwner(tribute.getName());
			
			if (UltraHardcore.isWildChat()) {
				PrefixSuffix prefixSuffix = PexBridge.getCachedPrefixSuffix(tribute);
				itemMeta.setDisplayName(ChatColor.WHITE + WildCommons.color(prefixSuffix.getPrefix() + tribute.getName() + prefixSuffix.getSuffix()));
			} else {
				itemMeta.setDisplayName(ChatColor.WHITE + tribute.getName());
			}
			
			item.setItemMeta(itemMeta);
			final Icon icon = new StaticIcon(item);
			icon.setClickHandler(new TeleportClickHandler(tribute.getName()));
			teleporterMenu.setIconRaw(index, icon);
			index++;
		}
		
		teleporterMenu.refresh();
	}

	public static void open(Player player) {
		teleporterMenu.open(player);
	}
}
