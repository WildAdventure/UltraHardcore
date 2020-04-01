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

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.hud.menu.TeleporterMenu;
import com.gmail.filoghost.ultrahardcore.player.HGamer;
import com.gmail.filoghost.ultrahardcore.player.Status;
import com.gmail.filoghost.ultrahardcore.utils.PlayerUtils;

public class InventoryToolsListener implements Listener {
	
	private DecimalFormat format = new DecimalFormat("#.0");
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onInteract(PlayerInteractEvent event) {
		if (event.hasItem() && isRightClick(event.getAction())) {
			
			Material mat = event.getItem().getType();
			if (mat == Material.COMPASS) {
				
				HGamer hGamer = UltraHardcore.getHGamer(event.getPlayer());
				
				if (hGamer.getStatus() == Status.TRIBUTE) {
					
					if (hGamer.getAssignedMates() == null || hGamer.getAssignedMates().size() <= 1) {
						hGamer.sendMessage(ChatColor.GREEN + "Non hai compagni ancora in vita.");
					} else {
						
						Player nearest = PlayerUtils.getNearestMate(hGamer);
						hGamer.sendMessage(ChatColor.GREEN + "Compagno più vicino: " + nearest.getName() + ChatColor.DARK_GRAY + " | " + ChatColor.GREEN + "Distanza: " + format.format(nearest.getLocation().distance(hGamer.getPlayer().getLocation())));
					}
				} else {
					
					TeleporterMenu.open(event.getPlayer());
				}
			} else if (mat == Material.BED) {
				
				HGamer hGamer = UltraHardcore.getHGamer(event.getPlayer());
				
				if (hGamer.getStatus() != Status.TRIBUTE) {
					hGamer.getPlayer().kickPlayer("Hai scelto di uscire." + "§0§0§0");
				}
			}
		}
	}
	
	private boolean isRightClick(Action action) {
		return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
	}

}
