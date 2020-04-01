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
package com.gmail.filoghost.ultrahardcore.timers;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import wild.api.WildCommons;
import wild.api.bridges.CosmeticsBridge;
import wild.api.item.ItemBuilder;
import wild.api.sound.EasySound;

import com.gmail.filoghost.ultrahardcore.GameState;
import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.WorldBorderManager;
import com.gmail.filoghost.ultrahardcore.commands.TeamCommand;
import com.gmail.filoghost.ultrahardcore.hud.sidebar.SidebarManager;
import com.gmail.filoghost.ultrahardcore.player.HGamer;
import com.gmail.filoghost.ultrahardcore.player.Status;
import com.gmail.filoghost.ultrahardcore.player.Team;
import com.gmail.filoghost.ultrahardcore.player.TeamPreference;
import com.gmail.filoghost.ultrahardcore.tasks.ScatterTeleportTask;
import com.gmail.filoghost.ultrahardcore.utils.TeamMaker;
import com.gmail.filoghost.ultrahardcore.utils.UnitUtils;
import com.gmail.filoghost.ultrahardcore.utils.scatter.TeleportGroup;
import com.gmail.filoghost.ultrahardcore.utils.scatter.TeleportLocationFinder;
import com.google.common.collect.Lists;

public class PregameTimer extends TimerMaster {

	private static DecimalFormat shortFormat = new DecimalFormat("#.0");
	
	@Getter @Setter private int countdown;
	
	@Getter private boolean started;
	
	private EasySound clickSound = new EasySound(Sound.CLICK);
	
	private ItemStack compass = ItemBuilder.of(Material.COMPASS).name("Localizzatore Team").build();
	
	@Getter private String lastCountdownMessage;
	
	public PregameTimer() {
		super(0, 20L);
		resetCountdown();
		lastCountdownMessage = "N/A";
	}
	
	private void resetCountdown() {
		started = false;
		this.countdown = UltraHardcore.getSettings().startCountdown;
		SidebarManager.setTime("-");
	}

	@Override
	public void run() {
		
		if (!started) {
			
			if (UltraHardcore.countTributes() >= UltraHardcore.getSettings().minPlayers) {
				started = true;
				SidebarManager.setTime(UnitUtils.formatMinutes(countdown / 60));
			} else {
				return;
			}
		}
		
		if (countdown <= 0) {
			
			if (UltraHardcore.countTributes() < UltraHardcore.getSettings().minPlayers) {
				Bukkit.broadcastMessage(ChatColor.GREEN + "Ci sono pochi giocatori, il conto alla rovescia riparte.");
				resetCountdown();
				return;
			}
			
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage(ChatColor.GREEN + "La partita è iniziata!");
			
			final int teamsSize = UltraHardcore.getSettings().teamSize;
			Set<Team> createdTeams = null;
			
			if (teamsSize > 1) {
				List<TeamPreference> preferences = Lists.newArrayList();
				List<Player> freePlayers = Lists.newArrayList();
				
				for (TeamPreference pregameTeam : TeamCommand.teams) {
					
					if (pregameTeam.getMembers().size() <= 1) {
						continue;
					}
						
					TeamPreference newTeam = new TeamPreference(pregameTeam.getMembers());
					
					if (newTeam.getMembers().size() > teamsSize) {
						UltraHardcore.logPurple("Errore, team con più membri del necessario: " + newTeam.toString());
					}
					
					preferences.add(newTeam);
				}
				
				for (HGamer tribute : UltraHardcore.getAllGamersUnsafe()) {
					if (tribute.getStatus() == Status.TRIBUTE) {
						
						boolean hasTeam = false;
						
						for (TeamPreference team : preferences) {
							if (team.getMembers().contains(tribute.getPlayer())) {
								hasTeam = true;
								break;
							}
						}
						
						if (!hasTeam) {
							freePlayers.add(tribute.getPlayer());
						}
					}
				}
				
				// Il pezzo più importante
				createdTeams = TeamMaker.assignTeams(teamsSize, preferences, freePlayers);
			}
			
			for (TeamPreference pref : TeamCommand.teams) {
				pref.getMembers().clear();
				pref.getInvited().clear();
			}
			
			TeamCommand.teams.clear();
			
			
			for (HGamer tribute : UltraHardcore.getAllGamersUnsafe()) {
				if (tribute.getStatus() == Status.TRIBUTE) {
					
					CosmeticsBridge.updateCosmetics(tribute.getPlayer(), CosmeticsBridge.Status.GAME);
					tribute.cleanCompletely(GameMode.SURVIVAL);
					
					if (teamsSize > 1) {
						tribute.getPlayer().getInventory().addItem(compass);
						
						List<String> matesNames = Lists.newArrayList();
						
						for (Player mate : tribute.getAssignedMates().getMembers()) {
							if (mate != tribute.getPlayer()) {
								matesNames.add(mate.getName());
							}
						}
						
						if (!matesNames.isEmpty()) {
							tribute.sendMessage(TeamCommand.TEAM_PREFIX + "Sei in team con " + StringUtils.join(matesNames, ", ") + ".");
						} else {
							tribute.sendMessage(TeamCommand.TEAM_PREFIX + "Sei da solo.");
						}
					}
				}
			};
			
			if (teamsSize > 1) {
				
				int teamNumber = 0;
				
				Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
				
				for (Team team : createdTeams) {
					
					team.saveInitialMembers(); // IMPORTANTE!
					
					String teamName = "PlayerTeam-" + teamNumber++;
					if (scoreboard.getTeam(teamName) != null) {
						scoreboard.getTeam(teamName).unregister();
					}
					org.bukkit.scoreboard.Team bukkitTeam = scoreboard.registerNewTeam(teamName);
					bukkitTeam.setAllowFriendlyFire(false);
					bukkitTeam.setCanSeeFriendlyInvisibles(true);
					
					for (Player player : team.getMembers()) {
						bukkitTeam.addPlayer(player);
					}
					
					for (Player player : team.getMembers()) {
						try {
							WildCommons.Unsafe.sendTeamPrefixSuffixChangePacket(player, bukkitTeam, ChatColor.GREEN + "", ""); // Verde per i compagni
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
			}
			
			final List<TeleportGroup> teleportGroups = Lists.newArrayList();
			
			if (teamsSize > 1) {
				for (Team team : createdTeams) {
					teleportGroups.add(new TeleportGroup(team.getMembers()));
				}
			} else {
				for (HGamer tribute : UltraHardcore.getAllGamersUnsafe()) {
					if (tribute.getStatus() == Status.TRIBUTE) {
						teleportGroups.add(new TeleportGroup(tribute.getPlayer()));
					}
				};
			}
			
			final int spotsToFind = teleportGroups.size();
			
			final List<Location> spots = TeleportLocationFinder.getSafeSpots(Bukkit.getWorld("world_game"), spotsToFind, UltraHardcore.getSettings().startBorderRadius);
			
			final int ticksBetweenTeleport = 8;
			
			for (int i = 0; i < spotsToFind; i++) {
				double seconds = (i+1) * ((double) ticksBetweenTeleport / 20.0); // 20 ticks per second
				TeleportGroup group = teleportGroups.get(i);
				
				if (seconds > 1) {
					for (Player player : group.getPlayers()) {
						player.sendMessage(ChatColor.GRAY + "Teletrasporto in circa " + shortFormat.format(seconds) + " secondi, attendi...");
					}
				} else {
					for (Player player : group.getPlayers()) {
						player.sendMessage(ChatColor.GRAY + "Teletrasporto in corso, attendi...");
					}
				}
			}

			new ScatterTeleportTask(teleportGroups, spots).runTaskTimer(UltraHardcore.getInstance(), ticksBetweenTeleport, ticksBetweenTeleport);
			
			SidebarManager.switchToKills();
			TeamCommand.teams.clear();
			
			UltraHardcore.setState(GameState.INVINCIBILITY);
			UltraHardcore.getInvincibilityTimer().startNewTask();
			
			SidebarManager.updateBorderRadius(UltraHardcore.getSettings().startBorderRadius);
			
			WorldBorderManager.startTask(UltraHardcore.getSettings().startBorderRadius, UltraHardcore.getSettings().endBorderRadius, UltraHardcore.getSettings().ticksToRestrictBorder);
			new BukkitRunnable() {
				public void run() {
					new EasySound(Sound.NOTE_PIANO).playToAll();
					Bukkit.broadcastMessage(ChatColor.YELLOW + "I bordi del mondo cominciano a restringersi!");
					WorldBorderManager.setRestrict(true);
				}
			}.runTaskLater(UltraHardcore.getInstance(), 5 * 60 * 20);
			
			stopTask();
			return;
		}
		
		
		if (countdown >= 60) {
			
			// Ogni 15 secondi
			if (countdown % 15 == 0) {
				lastCountdownMessage = UnitUtils.formatMinutes(countdown / 60);
				Bukkit.broadcastMessage(ChatColor.GREEN + "La partita inizia in " + lastCountdownMessage + ".");
				SidebarManager.setTime(lastCountdownMessage);
			}
			
		} else if (countdown > 5) {
			
			// Ogni 10 secondi
			if (countdown % 10 == 0) {
				lastCountdownMessage = UnitUtils.formatSeconds(countdown);
				Bukkit.broadcastMessage(ChatColor.GREEN + "La partita inizia in " + lastCountdownMessage + ".");
			}
			SidebarManager.setTime(UnitUtils.formatSeconds(countdown));
			
		} else {
			
			// Countdown finale
			clickSound.playToAll();
			lastCountdownMessage = UnitUtils.formatSeconds(countdown);
			Bukkit.broadcastMessage(ChatColor.GREEN + "La partita inizia in " + lastCountdownMessage + ".");
			SidebarManager.setTime(UnitUtils.formatSeconds(countdown));
			
		}

		countdown--;
	}
}
