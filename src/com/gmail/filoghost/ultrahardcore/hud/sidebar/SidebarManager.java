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
package com.gmail.filoghost.ultrahardcore.hud.sidebar;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import wild.api.WildCommons;

import com.gmail.filoghost.ultrahardcore.GameState;
import com.gmail.filoghost.ultrahardcore.UltraHardcore;

public class SidebarManager {

	private static Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
	private static Objective side;
	
	private static Team timeTeam;
	private static Team stateTeam;
	private static Team killsTeam;
	private static Team playersTeam;
	private static Team bordersTeam;
	
	private static final String	SMALL_TITLE_PREFIX = ChatColor.RED + "" + ChatColor.BOLD;
	
	private static Map<String, Integer> playerKills = new HashMap<String, Integer>();
	private static Team killsTitleTeam;
	
	
	public static void initialize(GameState initialState, int teamsSize) {
		
		// Rimuove gli obiettivi precedenti
		safeRemoveObjective(scoreboard.getObjective(DisplaySlot.SIDEBAR));
		safeRemoveObjective(scoreboard.getObjective("info"));
		
		side = scoreboard.registerNewObjective("info", "dummy");
		side.setDisplayName("    " + ChatColor.DARK_RED + ChatColor.BOLD + ChatColor.UNDERLINE + "Ultra Hardcore" + ChatColor.RESET + "    ");
		showSidebar();
		
		setScore(emptyLine(15), 15);
		setScore(SMALL_TITLE_PREFIX + "Giocatori:", 14);
		String playersEntry = setScore(emptyLine(13), 13);
		setScore(emptyLine(12), 12);
		setScore(SMALL_TITLE_PREFIX + "Bordi:", 11);
		String bordersEntry = setScore(emptyLine(10), 10);
		setScore(emptyLine(9), 9);
		setScore(SMALL_TITLE_PREFIX + "Tempo:", 8);
		String timeEntry = setScore(emptyLine(7), 7);
		setScore(emptyLine(6), 6);
		setScore(SMALL_TITLE_PREFIX + "Fase:", 5);
		String stateEntry = setScore(emptyLine(4), 4);
		setScore(emptyLine(3), 3);
		String killsTitleEntry = setScore(emptyLine(2), 2);
		String killsEntry = setScore(emptyLine(1), 1);
		
		// Crea i team per i prefissi
		timeTeam = createSafeTeam("time");
		timeTeam.addEntry(timeEntry);
		stateTeam = createSafeTeam("state");
		stateTeam.addEntry(stateEntry);
		killsTeam = createSafeTeam("kills");
		killsTeam.addEntry(killsEntry);
		killsTitleTeam = createSafeTeam("killsTitle");
		killsTitleTeam.addEntry(killsTitleEntry);
		playersTeam = createSafeTeam("players");
		playersTeam.addEntry(playersEntry);
		bordersTeam = createSafeTeam("borders");
		bordersTeam.addEntry(bordersEntry);
		
		setTime("-");
		updateState(initialState);
		killsTitleTeam.setPrefix(SMALL_TITLE_PREFIX + "Modalità:");
		
		if (teamsSize <= 1) {
			killsTeam.setPrefix("Individuale");
		} else {
			killsTeam.setPrefix("Team da " + teamsSize);
		}
		
		bordersTeam.setPrefix("-");
	}
	
	public static void switchToKills() {
		killsTitleTeam.setPrefix(SMALL_TITLE_PREFIX + "Uccisioni:");
		killsTeam.setPrefix("0");
	}
	
	public static void updateBorderRadius(int radius) {
		bordersTeam.setPrefix("+" + radius + ", -"  + radius);
	}
	
	private static String emptyLine(int sideNumber) {
		if (sideNumber > 15 || sideNumber < 0) return "";
		return ChatColor.values()[sideNumber].toString();
	}
	
	private static Team createSafeTeam(String name) {
		if (scoreboard.getTeam(name) != null) {
			scoreboard.getTeam(name).unregister();
		}
		
		return scoreboard.registerNewTeam(name);
	}
	
	private static void safeRemoveObjective(Objective o) {
		if (o != null) o.unregister();
	}
	
	private static String setScore(String entry, int score) {
		side.getScore(entry).setScore(score);
		return entry;
	}
	
	public static void showSidebar() {
		side.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public static void hideSidebar() {
		side.setDisplaySlot(null);
	}
	
	public static void updateState(GameState state) {
		switch (state) {
			case END:
				stateTeam.setPrefix("Fine");
				break;
				
			case FINAL_BATTLE:
				stateTeam.setPrefix("Battaglia finale");
				break;
				
			case GAME:
				stateTeam.setPrefix("Combattimento");
				break;
				
			case INVINCIBILITY:
				stateTeam.setPrefix("Preparazione");
				break;
				
			case PRE_GAME:
				stateTeam.setPrefix("Attesa");
				break;
				
			default:
				stateTeam.setPrefix("ERROR");
				break;
		}
	}
	
	public static void setTime(String time) {
		if (!timeTeam.getPrefix().equals(time)) {
			timeTeam.setPrefix(time);
		}
	}
	
	public static void setPlayers(int i) {
		String number = String.valueOf(i);
		
		if (!playersTeam.getPrefix().equals(number)) {
			playersTeam.setPrefix(number);
		}
	}
	
	public static void addKill(Player player) {
		int kills = 0;
		
		String playerName = player.getName();
		
		if (playerKills.containsKey(playerName)) {
			kills = playerKills.get(playerName).intValue();
		}
		
		kills++;
		playerKills.put(playerName, Integer.valueOf(kills));
		
		if (player.getScoreboard().equals(scoreboard)) {
			
			try {
				WildCommons.Unsafe.sendTeamPrefixSuffixChangePacket(player, killsTeam, Integer.toString(kills), "");
			} catch (Exception ex) {
				ex.printStackTrace();
				UltraHardcore.logPurple("Impossibile aggiornare il punteggio di " + playerName + " sulla scoreboard!");
			}
		}
	}

}
