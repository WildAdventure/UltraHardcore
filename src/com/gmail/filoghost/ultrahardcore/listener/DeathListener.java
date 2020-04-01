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

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import wild.api.sound.EasySound;
import wild.api.world.Particle;

import com.gmail.filoghost.ultrahardcore.Perms;
import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.commands.TeamCommand;
import com.gmail.filoghost.ultrahardcore.hud.sidebar.SidebarManager;
import com.gmail.filoghost.ultrahardcore.mysql.SQLColumns;
import com.gmail.filoghost.ultrahardcore.mysql.SQLManager;
import com.gmail.filoghost.ultrahardcore.mysql.SQLTask;
import com.gmail.filoghost.ultrahardcore.player.HGamer;
import com.gmail.filoghost.ultrahardcore.player.Status;
import com.gmail.filoghost.ultrahardcore.utils.DamageEventData;
import com.gmail.filoghost.ultrahardcore.utils.PlayerUtils;
import com.gmail.filoghost.ultrahardcore.utils.UnitUtils;

public class DeathListener implements Listener {
	
	@Getter
	private static Map<Player, DamageEventData> lastPlayerDamageEvent = new WeakHashMap<>();
	
	public static void setLastDamager(Player victim, Player damager) {
		lastPlayerDamageEvent.put(victim, new DamageEventData(damager.getName(), System.currentTimeMillis()));
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent event) {
		
		// Nasconde sempre il messaggio di morte originale
		String deathMessage = event.getDeathMessage();
		event.setDeathMessage(null);
		
		Iterator<ItemStack> dropsIter = event.getDrops().iterator();
		while (dropsIter.hasNext()) {
			if (dropsIter.next().getType() == Material.COMPASS) {
				dropsIter.remove();
			}
		}
		
		for (Entity entity : event.getEntity().getWorld().getEntities()) {
			if (entity.getType() == EntityType.WOLF) {
				Wolf wolf = (Wolf) entity;
				if (wolf.getOwner() != null && wolf.getOwner() == event.getEntity()) {
					Particle.CLOUD.display(wolf.getLocation(), 0.2F, 0.2F, 0.2F, 0, 20);
					wolf.remove();
				}
			}
		}
		
		Player killer = PlayerUtils.getRealDamager(event.getEntity().getLastDamageCause());
		
		if (killer == null && lastPlayerDamageEvent.containsKey(event.getEntity())) {
			
			DamageEventData lastDamage = lastPlayerDamageEvent.get(event.getEntity());
			HGamer hKiller = UltraHardcore.getHGamer(lastDamage.getDamager());
			
			if (hKiller != null && System.currentTimeMillis() - lastDamage.getTimestamp() < 5000) {
				killer = hKiller.getPlayer();
			}
		}
		
		if (killer != null) {
			event.getDrops().add(new ItemStack(Material.GOLDEN_APPLE));
		}
		parseDeath(UltraHardcore.getHGamer(event.getEntity()), killer != null ? UltraHardcore.getHGamer(killer) : null, deathMessage, true, true);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent event) {
		UltraHardcore.getHGamer(event.getPlayer()).onRespawn();
		event.setRespawnLocation(UltraHardcore.getLobbySpawn());
	}
	
	/**
	 *  Messaggio sulla chat, kick, coins... (static perché utilizzato da altre classi, come per gli headshot)
	 */
	public static void parseDeath(final HGamer hVictim, final HGamer hKiller, String message, boolean kick, boolean trySpectate) {
		if (message == null) message = "";
		
		new SQLTask() {
			@Override
			public void execute() throws SQLException {
				
				SQLManager.increaseStat(hVictim.getName(), SQLColumns.DEATHS, 1);
				
				if (hKiller != null && hKiller != hVictim) {
					SQLManager.increaseStat(hKiller.getName(), SQLColumns.KILLS, 1);
					hKiller.giveExpAndSendMessage(UltraHardcore.getSettings().exp_kill, "uccisione");
					
					if (hKiller.getAssignedMates() != null) {
						for (Player player : hKiller.getAssignedMates().getMembers()) {
							if (player != hKiller.getPlayer()) {
								UltraHardcore.getHGamer(player).giveExpAndSendMessage(UltraHardcore.getSettings().exp_killTeamMate, "uccisione di un compagno");
							}
						}
					}
				}
			}
		}.submitAsync(null);
		
		if (hKiller != null && hKiller.getStatus() == Status.TRIBUTE) {
			
			EasySound.quickPlay(hKiller.getPlayer(), Sound.ORB_PICKUP);
			SidebarManager.addKill(hKiller.getPlayer());
		}
		
		if (trySpectate) {
			
			if (hVictim.getPlayer().hasPermission(Perms.GAMEMAKER)) {
				hVictim.setStatus(Status.GAMEMAKER, false, true, false, true);
				
			} else if (hVictim.getPlayer().hasPermission(Perms.SPECTATOR)) {
				hVictim.setStatus(Status.SPECTATOR, false, true, false, true);
				
			} else if (hVictim.getPlayer().hasPermission(Perms.SPECTATOR_MINI)) {
				hVictim.setStatus(Status.SPECTATOR, false, true, false, true);
				
				final Player victimPlayer = hVictim.getPlayer();
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(UltraHardcore.getInstance(), new Runnable() {
					public void run() {
						
						if (victimPlayer.isOnline()) {
							victimPlayer.spigot().respawn();
							victimPlayer.sendMessage("" + ChatColor.YELLOW + ChatColor.BOLD + "Potrai essere spettatore per 45 secondi, dopodiché verrai portato all'hub.");
						}
					}
				}, 5L);
				
				new BukkitRunnable() {
					
					int seconds = 45;

					public void run() {
						
						if (!victimPlayer.isOnline()) {
							cancel();
							return;
						}
						
						if (seconds > 5) {
							if (seconds % 10 == 0) {
								victimPlayer.sendMessage("" + ChatColor.YELLOW + "Hai ancora " + seconds + " secondi da spettatore.");
							}
						} else {
							EasySound.quickPlay(victimPlayer, Sound.CLICK, 1.5f);
							victimPlayer.sendMessage("" + ChatColor.YELLOW + "Hai ancora " + UnitUtils.formatSeconds(seconds) + " da spettatore.");
						}
						
						if (seconds <= 0) {
							victimPlayer.kickPlayer(ChatColor.YELLOW + "Hai esaurito il tempo da spettatore." + "§0§0§0");
							cancel();
							return;
						}
						
						seconds--;
					}
				}.runTaskTimer(UltraHardcore.getInstance(), 20, 20);
				
			} else {
				if (kick) {
					JoinQuitListener.kickedOnDeath.add(hVictim);
					hVictim.getPlayer().kickPlayer(message + "§0§0§0");
				}
			}
			
		} else if (kick) {
			JoinQuitListener.kickedOnDeath.add(hVictim);
			hVictim.getPlayer().kickPlayer(message + "§0§0§0");
		}
		
		Location thunderLoc = hVictim.getPlayer().getLocation();
		thunderLoc.setY(255);
		hVictim.getPlayer().getWorld().strikeLightningEffect(thunderLoc);

		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(message);
		int tributes = UltraHardcore.countTributes();
		if (tributes > 1) {
			Bukkit.broadcastMessage(ChatColor.RED.toString() + tributes + " giocatori rimanenti.");
		} else {
			Bukkit.broadcastMessage(ChatColor.RED.toString() + tributes + " giocatore rimanente.");
		}
		
		TeamCommand.playerDied(hVictim.getPlayer());
	}
}
