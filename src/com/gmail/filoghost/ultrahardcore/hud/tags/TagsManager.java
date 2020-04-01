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
package com.gmail.filoghost.ultrahardcore.hud.tags;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TagsManager {

	private static Scoreboard scoreboard;
	
	private static Team ghostsTeam;
	
	private static Objective healthObjectiveTab;
	private static Objective healthObjectiveBelowName;
	
	public static void initialize() {
		scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		
		ghostsTeam = createTeamSafe("ghosts");
		
		ghostsTeam.setPrefix(ChatColor.DARK_GRAY + "");
		
		ghostsTeam.setCanSeeFriendlyInvisibles(true);
		
		if (scoreboard.getObjective("healthTab") != null) {
			scoreboard.getObjective("healthTab").unregister();
		}
		
		if (scoreboard.getObjective("healthBelow") != null) {
			scoreboard.getObjective("healthBelow").unregister();
		}
		
		healthObjectiveTab = scoreboard.registerNewObjective("healthTab", "health");
		healthObjectiveTab.setDisplayName(ChatColor.RED + "Salute");
		healthObjectiveTab.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		
		healthObjectiveBelowName = scoreboard.registerNewObjective("healthBelow", "health");
		healthObjectiveBelowName.setDisplayName(ChatColor.RED + "\u2764");
		healthObjectiveBelowName.setDisplaySlot(DisplaySlot.BELOW_NAME);
	}
	
	private static Team createTeamSafe(String name) {
		if (scoreboard.getTeam(name) != null) {
			scoreboard.getTeam(name).unregister();
		}
		
		return scoreboard.registerNewTeam(name);
	}
	
	private static void addPlayerToTeamSafe(Player player, Team team) {
		Team oldTeam = scoreboard.getPlayerTeam(player);
		if (oldTeam != null) oldTeam.removePlayer(player);
		
		if (team != null) team.addPlayer(player);
	}
	
	public static void setGhost(Player player) {
		addPlayerToTeamSafe(player, ghostsTeam);
	}
	
	public static void setTribute(Player player) {
		addPlayerToTeamSafe(player, null);
	}
}
