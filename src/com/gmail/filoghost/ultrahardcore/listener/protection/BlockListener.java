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
package com.gmail.filoghost.ultrahardcore.listener.protection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import com.gmail.filoghost.ultrahardcore.GameState;
import com.gmail.filoghost.ultrahardcore.Perms;
import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.player.Status;

public class BlockListener implements Listener {

	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent event) {
		if (!canDestroyBlock(event.getPlayer(), event.getBlock().getType())) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlace(BlockPlaceEvent event) {
		if (!canPlaceBlock(event.getPlayer(), event.getBlock().getType())) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onBucketFill(PlayerBucketFillEvent event) {
		if (!canDestroyBlock(event.getPlayer(), event.getBucket())) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		if (!canPlaceBlock(event.getPlayer(), event.getBucket())) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onIgnite(BlockIgniteEvent event) {
		if (UltraHardcore.getState() == GameState.PRE_GAME) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onBurn(BlockBurnEvent event) {
		if (UltraHardcore.getState() == GameState.PRE_GAME) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onFade(BlockFadeEvent event) {
		if (UltraHardcore.getState() == GameState.PRE_GAME) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onExplode(EntityExplodeEvent event) {
		if (UltraHardcore.getState() == GameState.PRE_GAME) {
			event.blockList().clear();
		}
	}
	
	public static boolean canDestroyBlock(Player player, Material block) {
		Status status = UltraHardcore.getHGamer(player).getStatus();
		
		switch (status) {
			case GAMEMAKER:
				return player.hasPermission(Perms.INTERACT_GAMEMAKER);

			case TRIBUTE:
				return
						player.getWorld() != UltraHardcore.getLobbyWorld() &&
						UltraHardcore.getState() != GameState.PRE_GAME &&
						UltraHardcore.getState() != GameState.END;
				
			default:
				return false;
		}
	}
	
	public static boolean canPlaceBlock(Player player, Material block) {
		Status status = UltraHardcore.getHGamer(player).getStatus();
		
		switch (status) {
			case GAMEMAKER:
				return player.hasPermission(Perms.INTERACT_GAMEMAKER);

			case TRIBUTE:
				return
						player.getWorld() != UltraHardcore.getLobbyWorld() &&
						UltraHardcore.getState() != GameState.PRE_GAME &&
						UltraHardcore.getState() != GameState.END;
				
			default:
				return false;
		}
	}
}
