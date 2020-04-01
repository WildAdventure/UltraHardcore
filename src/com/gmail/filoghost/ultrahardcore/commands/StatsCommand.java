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
package com.gmail.filoghost.ultrahardcore.commands;

import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import wild.api.command.CommandFramework;
import com.gmail.filoghost.ultrahardcore.Perms;
import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.mysql.SQLManager;
import com.gmail.filoghost.ultrahardcore.mysql.SQLPlayerData;
import com.gmail.filoghost.ultrahardcore.mysql.SQLTask;
import com.gmail.filoghost.ultrahardcore.player.ExpManager;
import com.gmail.filoghost.ultrahardcore.player.ExpManager.LevelInfo;
import com.gmail.filoghost.ultrahardcore.player.HGamer;

public class StatsCommand extends CommandFramework {
	
	public StatsCommand() {
		super(UltraHardcore.getInstance(), "stats");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {

		if (args.length > 0) {
			CommandValidate.isTrue(sender.hasPermission(Perms.VIEW_OTHERS_STATS), "Non puoi vedere le statistiche degli altri.");
			
			String playerName = args[0];
			
			try {
				CommandValidate.isTrue(SQLManager.playerExists(playerName), "Quel giocatore non ha mai giocato qui!");
				SQLPlayerData data = SQLManager.getPlayerData(playerName);
				
				LevelInfo levelInfo = ExpManager.getCurrentLevelInfo(data.getExp());
				
				sender.sendMessage(ChatColor.GOLD + "----- Statistiche di " + playerName + " -----");
				sender.sendMessage(ChatColor.GRAY + "Vittorie individuali: " + ChatColor.YELLOW + data.getWinsSolo());
				sender.sendMessage(ChatColor.GRAY + "Vittorie in team: " + ChatColor.YELLOW + data.getWinsTeam());
				sender.sendMessage(ChatColor.GRAY + "Uccisioni: " + ChatColor.YELLOW + data.getKills());
				sender.sendMessage(ChatColor.GRAY + "Morti: " + ChatColor.YELLOW + data.getDeaths());
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GRAY + "Livello: " + ChatColor.YELLOW + levelInfo.getLevel() + ChatColor.GRAY + (levelInfo.isMax() ? " §8[§7Livello masssimo§8]" : " §8[" + ChatColor.WHITE + levelInfo.getCurrentLevelExp() + ChatColor.GRAY + "/" + ChatColor.WHITE + levelInfo.getTotalExpForNextLevel() + ChatColor.GRAY + " esperienza§8]"));
				
			} catch (SQLException e) {
				e.printStackTrace();
				sender.sendMessage(ChatColor.RED + "Errore nel database, informa lo staff se persiste.");
			}
			
			return;
		}
		
		final HGamer hGamer = UltraHardcore.getHGamer(CommandValidate.getPlayerSender(sender));
		
		hGamer.sendMessage("");
		hGamer.sendMessage(ChatColor.GOLD + "----- Le tue statistiche -----");
		
		new SQLTask() {
			@Override
			public void execute() throws SQLException {
				SQLPlayerData stats = SQLManager.getPlayerData(hGamer.getName());
				
				LevelInfo levelInfo = ExpManager.getCurrentLevelInfo(stats.getExp());
				
				hGamer.sendMessage(ChatColor.GRAY + "Vittorie individuali: " + ChatColor.YELLOW + stats.getWinsSolo());
				hGamer.sendMessage(ChatColor.GRAY + "Vittorie in team: " + ChatColor.YELLOW + stats.getWinsTeam());
				hGamer.sendMessage(ChatColor.GRAY + "Uccisioni: " + ChatColor.YELLOW + stats.getKills());
				hGamer.sendMessage(ChatColor.GRAY + "Morti: " + ChatColor.YELLOW + stats.getDeaths());
				hGamer.sendMessage("");
				hGamer.sendMessage(ChatColor.GRAY + "Livello: " + ChatColor.YELLOW + levelInfo.getLevel() + ChatColor.GRAY + (levelInfo.isMax() ? " §8[§7Livello masssimo§8]" : " §8[" + ChatColor.WHITE + levelInfo.getCurrentLevelExp() + ChatColor.GRAY + "/" + ChatColor.WHITE + levelInfo.getTotalExpForNextLevel() + ChatColor.GRAY + " esperienza§8]"));
				hGamer.updateCachedExp(stats.getExp());
			}
		}.submitAsync(hGamer.getPlayer());
	}

}
