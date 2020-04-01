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

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.WildCommons;
import wild.api.command.CommandFramework;

import com.gmail.filoghost.ultrahardcore.GameState;
import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.player.HGamer;
import com.gmail.filoghost.ultrahardcore.player.Status;
import com.gmail.filoghost.ultrahardcore.player.Team;
import com.gmail.filoghost.ultrahardcore.player.TeamPreference;
import com.google.common.collect.Lists;

public class TeamCommand extends CommandFramework {
	
	public static List<TeamPreference> teams = Lists.newArrayList();
	
	public static final String TEAM_PREFIX = ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "Team" + ChatColor.DARK_AQUA + "] " + ChatColor.WHITE;
	
	public TeamCommand() {
		super(UltraHardcore.getInstance(), "team");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		if (UltraHardcore.getSettings().teamSize <= 1) {
			sender.sendMessage(ChatColor.RED + "Spiacente, la modalità è individuale. Dovrai combattere da solo.");
			return;
		}
		
		if (args.length == 0) {
			sender.sendMessage("§b/team info §7- Informazioni sul tuo team e sugli invitati");
			sender.sendMessage("§b/team esci §7- Esci dal tuo team");
			sender.sendMessage("§b/team invita <giocatore> §7- Invita un giocatore nel tuo team");
			sender.sendMessage("§b/team kick <giocatore> §7- Caccia un giocatore dal tuo team");
			sender.sendMessage("§b/team accetta <giocatore> §7- Accetta l'invito di un giocatore");
			sender.sendMessage("§b/tc <messaggio> §7- Manda un messaggio solo al team");
			return;
		}
		
		int teamsSize = UltraHardcore.getSettings().teamSize;
		Player executor = CommandValidate.getPlayerSender(sender);
		CommandValidate.isTrue(UltraHardcore.getHGamer(executor).getStatus() == Status.TRIBUTE, "Devi essere in partita per usare i team.");
		
		if (args[0].equalsIgnoreCase("info")) {
			
			// Se è già iniziata, mostra il team attuale
			if (UltraHardcore.getState() != GameState.PRE_GAME) {
				Team team = UltraHardcore.getHGamer(executor).getAssignedMates();
				
				if (team == null) {
					executor.sendMessage(ChatColor.RED + "Non sei in nessun team.");
					return;
				}
				
				executor.sendMessage(ChatColor.AQUA + "Il tuo team: " + ChatColor.DARK_AQUA + team.membersToString());
				return;
			}
			
			
			TeamPreference team = findTeam(executor);
			
			if (team == null || (team.getMembers().size() <= 1 && team.getInvited().isEmpty())) {
				executor.sendMessage(ChatColor.RED + "Non sei in nessun team.");
				return;
			}
			
			if (team.getMembers().size() > 1) {
				executor.sendMessage(ChatColor.AQUA + "Il tuo team: " + ChatColor.DARK_AQUA + team.membersToString());
			} else {
				executor.sendMessage(ChatColor.AQUA + "Sei ancora da solo nel team.");
			}
			executor.sendMessage(ChatColor.AQUA + "Invitati: " + ChatColor.DARK_AQUA + (team.getInvited().isEmpty() ? "nessuno" : team.invitedToString()));
			return;
		}
		
		
		
		
		if (args[0].equalsIgnoreCase("esci")) {
			
			CommandValidate.isTrue(UltraHardcore.getState() == GameState.PRE_GAME, "I team possono cambiare solo prima dell'inizio della partita.");
			
			TeamPreference team = findTeam(executor);
			
			if (team == null || (team.getMembers().size() <= 1 && team.getInvited().isEmpty())) {
				executor.sendMessage(ChatColor.RED + "Non sei in nessun team.");
				return;
			}
			
			boolean hadMoreThanOneMember = team.getMembers().size() > 1;
			
			if (!hadMoreThanOneMember) {
				team.getInvited().clear();
				team.getMembers().clear();
				teams.remove(team);
				executor.sendMessage(ChatColor.AQUA + "Non eri ancora in team, tuttavia sono stati rimossi gli inviti agli altri giocatori.");
			} else {
				team.getMembers().remove(executor);
				team.sendAll(TEAM_PREFIX + executor.getName() + " è uscito dal team.");
				executor.sendMessage(ChatColor.AQUA + "Sei uscito dal team.");
			}
			
			if (team.getMembers().size() <= 1 && team.getInvited().isEmpty()) {
				team.sendAll(ChatColor.RED + "Non sei più in alcun team, è uscito l'ultimo membro.");
				return;
			}
			
			return;
		}
		
		
		
		
		if (args[0].equalsIgnoreCase("invita")) {
			
			CommandValidate.isTrue(UltraHardcore.getState() == GameState.PRE_GAME, "I team possono cambiare solo prima dell'inizio della partita.");
			
			CommandValidate.minLength(args, 2, "Utilizzo comando: /team invita <giocatore>");
			
			Player invited = Bukkit.getPlayerExact(args[1]);
			
			CommandValidate.isTrue(invited != executor, "Non puoi invitarti da solo.");
			
			CommandValidate.notNull(invited, "Quel giocatore non è online.");
			CommandValidate.isTrue(UltraHardcore.getHGamer(invited).getStatus() == Status.TRIBUTE, "Quel giocatore non è online.");
			
			TeamPreference inviterTeam = findTeam(executor);
			if (inviterTeam == null) {
				teams.add(inviterTeam = new TeamPreference(executor));
			}
			
			List<Player> executorInvites = inviterTeam.getInvited();
			
			CommandValidate.isTrue(!executorInvites.contains(invited), "Hai già invitato quel giocatore.");
			
			TeamPreference invitedTeam = findTeam(invited);

			if (invitedTeam != null && invitedTeam == inviterTeam) {
				executor.sendMessage(ChatColor.RED + "Quel giocatore è già nel tuo team.");
				return;
			}
			
			if (executorInvites.size() + inviterTeam.getMembers().size() >= teamsSize) {
				executor.sendMessage(ChatColor.RED + "Nel team ci sono già abbastanza giocatori, contando anche gli invitati. Se vuoi rimuovere l'invito o cacciare qualcuno dal team, scrivi /team kick <giocatore>");
				return;
			}
			
			// Controlla che sia da solo
			CommandValidate.isTrue(invitedTeam == null || invitedTeam.getMembers().size() <= 1, "Quel giocatore è già in un altro team.");
			
			executorInvites.add(invited);
			
			executor.sendMessage(TEAM_PREFIX + "Hai invitato " + invited.getName() + " nel team.");
			for (Player member : inviterTeam.getMembers()) {
				if (member != executor) {
					member.sendMessage(TEAM_PREFIX + executor.getName() + " ha invitato " + invited.getName() + " nel team.");
				}
			}
			WildCommons.fancyMessage("[").color(ChatColor.DARK_AQUA).then("Team").color(ChatColor.AQUA).then("] ").color(ChatColor.DARK_AQUA).then(executor.getName() + " ti ha invitato nel suo team. ").color(ChatColor.WHITE).then("Accetta").color(ChatColor.GREEN).style(ChatColor.BOLD, ChatColor.UNDERLINE).command("/team accetta " + executor.getName()).tooltip(ChatColor.GRAY + "Clicca per accettare l'invito.").send(invited);
			return;
		}
		
		
		
		
		if (args[0].equalsIgnoreCase("kick")) {
			
			CommandValidate.isTrue(UltraHardcore.getState() == GameState.PRE_GAME, "I team possono cambiare solo prima dell'inizio della partita.");
			
			CommandValidate.minLength(args, 2, "Utilizzo comando: /team kick <giocatore>");
			
			String toKickName = args[1];
			
			TeamPreference executorTeam = findTeam(executor);
			
			Player toKick = null;
			boolean onlyRemovedFromInvited = false;
			
			for (Player p : executorTeam.getMembers()) {
				if (p.getName().equals(toKickName)) {
					toKick = p;
					break;
				}
			}
			
			if (toKick == null) {
				for (Player p : executorTeam.getInvited()) {
					if (p.getName().equals(toKickName)) {
						onlyRemovedFromInvited = true;
						toKick = p;
						break;
					}
				}
			}
			
			CommandValidate.notNull(toKick, "Quel giocatore non fa parte del tuo team.");
			CommandValidate.isTrue(executor != toKick, "Non puoi cacciarti da solo.");
			
			if (onlyRemovedFromInvited) {
				executorTeam.getInvited().remove(toKick);
				sender.sendMessage(TEAM_PREFIX + "Hai rimosso " + toKick.getName() + " dagli invitati.");
				for (Player member : executorTeam.getMembers()) {
					if (member != executor) {
						member.sendMessage(TEAM_PREFIX + executor.getName() + " ha rimosso " + toKick.getName() + " dagli invitati.");
					}
				}
			} else {
				executorTeam.getMembers().remove(toKick);
				sender.sendMessage(TEAM_PREFIX + "Hai cacciato " + toKick.getName() + " dal team.");
				for (Player member : executorTeam.getMembers()) {
					if (member != executor) {
						member.sendMessage(TEAM_PREFIX + executor.getName() + " ha cacciato " + toKick.getName() + " dal team.");
					}
				}
				toKick.sendMessage(TEAM_PREFIX + executor.getName() + " ti ha cacciato dal team.");
				
				if (executorTeam.getMembers().size() <= 1 && executorTeam.getInvited().isEmpty()) {
					executor.sendMessage(ChatColor.RED + "Non sei più in alcun team, hai cacciato l'ultimo membro.");
					return;
				}
			}
			
			return;
		}
		
		
		
		if (args[0].equalsIgnoreCase("accetta")) {
			
			CommandValidate.isTrue(UltraHardcore.getState() == GameState.PRE_GAME, "I team possono cambiare solo prima dell'inizio della partita.");
			
			CommandValidate.minLength(args, 2, "Utilizzo comando: /team accetta <giocatore>");
			
			Player acceptFrom = Bukkit.getPlayerExact(args[1]);
			
			CommandValidate.isTrue(acceptFrom != executor, "Non puoi accettare l'invito da solo.");
			
			CommandValidate.notNull(acceptFrom, "Quel giocatore non è online.");
			CommandValidate.isTrue(UltraHardcore.getHGamer(acceptFrom).getStatus() == Status.TRIBUTE, "Quel giocatore non è online.");
			
			TeamPreference executorTeam = findTeam(executor);
			
			if (executorTeam != null && executorTeam.getMembers().size() > 1) {
				executor.sendMessage(ChatColor.RED + "Sei già in un team, usa \"/team esci\" per uscire.");
				return;
			}
			
			TeamPreference invitedFromTeam = findTeam(acceptFrom);
		
			if (invitedFromTeam == null || !invitedFromTeam.getInvited().contains(executor)) {
				executor.sendMessage(ChatColor.RED + "Non sei stato invitato da quel giocatore, oppure ha cancellato il suo invito.");
				return;
			}
			
			if (invitedFromTeam.getMembers().size() >= teamsSize) {
				executor.sendMessage(ChatColor.RED + "Quel team ha già troppi giocatori.");
				return;
			}
			
			if (executorTeam != null) {
				executorTeam.getMembers().clear();
				executorTeam.getInvited().clear();
				teams.remove(executorTeam);
			}
			
			invitedFromTeam.sendAll(TEAM_PREFIX + executor.getName() + " è entrato nel team.");
			
			invitedFromTeam.getInvited().remove(executor);
			invitedFromTeam.getMembers().add(executor);
			
			executor.sendMessage(TEAM_PREFIX + "Sei entrato nel team di " + acceptFrom.getName() + ".");
			return;
		}
		
		
		
		sender.sendMessage(ChatColor.RED + "Sotto-comando sconosciuto. Scrivi /team per una lista dei comandi.");
	}
	
	public static TeamPreference findTeam(Player player) {
		
		for (TeamPreference team : teams) {
			if (team.getMembers().contains(player)) {
				return team;
			}
		}
		
		return null;
	}

	
	public static void playerNotTributeAnymoreOrQuit(Player player) {
		
		if (!teams.isEmpty()) {
			for (TeamPreference team : teams) {
				if (team.getMembers().remove(player)) {
					team.sendAll(TEAM_PREFIX + player.getName() + " è stato rimosso perché ha abbandonato la partita.");
				}
				
				if (team.getInvited().remove(player)) {
					team.sendAll(TEAM_PREFIX + player.getName() + " è stato rimosso dagli invitati perché ha abbandonato la partita.");
				}
			}
		}
		
		if (UltraHardcore.getState() != GameState.PRE_GAME) {
			for (HGamer hGamer : UltraHardcore.getAllGamersUnsafe()) {
				if (hGamer.getPlayer() == player) {
					hGamer.setAssignedMates(null);
					// Non svuotiamo il set di compagni perché è in comune fra tutti i membri!
				} else {
					if (hGamer.getAssignedMates() != null) {
						if (hGamer.getAssignedMates().remove(player)) {
							hGamer.getAssignedMates().sendAll(TEAM_PREFIX + player.getName() + " è stato rimosso perché ha abbandonato la partita.");
						}
					}
				}
			}
		}
	}
	
	public static void playerDied(Player player) {
		
		for (HGamer hGamer : UltraHardcore.getAllGamersUnsafe()) {
			if (hGamer.getPlayer() == player) {
				if (hGamer.getAssignedMates() != null) {
					hGamer.setAssignedMates(null);
				}
			} else {
				if (hGamer.getAssignedMates() != null) {
					if (hGamer.getAssignedMates().remove(player)) {
						hGamer.getAssignedMates().sendAll(TEAM_PREFIX + "Il compagno " + player.getName() + " è morto.");
					}
				}
			}
		}
	}

}
