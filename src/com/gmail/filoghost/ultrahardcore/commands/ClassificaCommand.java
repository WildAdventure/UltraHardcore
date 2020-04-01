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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import wild.api.command.CommandFramework;

import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.mysql.SQLColumns;
import com.gmail.filoghost.ultrahardcore.mysql.SQLManager;
import com.gmail.filoghost.ultrahardcore.mysql.SQLTask;
import com.gmail.filoghost.ultrahardcore.tasks.SendRankingTask;

public class ClassificaCommand extends CommandFramework {

	public ClassificaCommand() {
		super(UltraHardcore.getInstance(), "classifica");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(final CommandSender sender, String label, String[] args) {
		
		if (args.length == 0) {
			sender.sendMessage(ChatColor.GOLD + "----- Comandi classifica -----");
			sender.sendMessage(ChatColor.YELLOW + "/classifica uccisioni");
			sender.sendMessage(ChatColor.YELLOW + "/classifica vittorie individuale");
			sender.sendMessage(ChatColor.YELLOW + "/classifica vittorie team");
			sender.sendMessage(ChatColor.YELLOW + "/classifica morti");
			sender.sendMessage(ChatColor.YELLOW + "/stats " + ChatColor.GRAY + "- Le tue statistiche");
			sender.sendMessage("");
			return;
		}
		
		if (args[0].equalsIgnoreCase("uccisioni")) {
			Bukkit.getScheduler().scheduleAsyncDelayedTask(UltraHardcore.getInstance(), new SendRankingTask(sender, SQLColumns.KILLS, "uccisioni"));
			return;
		}
		
		if (args[0].equalsIgnoreCase("vittorie")) {
			
			if (args.length <= 1) {
				sender.sendMessage(ChatColor.GOLD + "Scegli uno dei due tipi di vittoria:");
				sender.sendMessage(ChatColor.YELLOW + "/classifica vittorie individuale");
				sender.sendMessage(ChatColor.YELLOW + "/classifica vittorie team");
				return;
			}
			
			if (args[1].equalsIgnoreCase("individuale") || args[1].equalsIgnoreCase("solo")) {
				Bukkit.getScheduler().scheduleAsyncDelayedTask(UltraHardcore.getInstance(), new SendRankingTask(sender, SQLColumns.WINS_SOLO, "vittorie individuali"));
			} else if (args[1].equalsIgnoreCase("team") || args[1].equalsIgnoreCase("gruppo")) {
				Bukkit.getScheduler().scheduleAsyncDelayedTask(UltraHardcore.getInstance(), new SendRankingTask(sender, SQLColumns.WINS_TEAM, "vittorie in team"));
			} else {
				sender.sendMessage(ChatColor.RED + "Il tipo di vittoria deve essere \"individuale\" o \"team\". \"" + args[1] + "\" non è un tipo valido.");
			}
			return;
		}
		
		if (args[0].equalsIgnoreCase("morti")) {
			Bukkit.getScheduler().scheduleAsyncDelayedTask(UltraHardcore.getInstance(), new SendRankingTask(sender, SQLColumns.DEATHS, "morti"));
			return;
		}
		
		if (args[0].equalsIgnoreCase("reset") && sender instanceof ConsoleCommandSender) {
			sender.sendMessage(ChatColor.GREEN + "Pulizia uccisioni, morti e vittorie...");
			new SQLTask() {
				public void execute() throws SQLException {
					SQLManager.getMysql().update("UPDATE uhc_players SET " +
							SQLColumns.KILLS + " = 0, " +
							SQLColumns.DEATHS + " = 0, " +
							SQLColumns.WINS_SOLO + " = 0, " +
							SQLColumns.WINS_TEAM + " = 0" +
							";"
					);
					sender.sendMessage(ChatColor.GREEN + "Finita pulizia!");
				}
			}.submitAsync(sender);
			return;
		}
		
		sender.sendMessage(ChatColor.RED + "Comando sconosciuto. Scrivi \"/classifica\" per i comandi.");
	}

}
