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
package com.gmail.filoghost.ultrahardcore.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.filoghost.ultrahardcore.player.Team;
import com.gmail.filoghost.ultrahardcore.player.TeamPreference;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TeamMaker {
	
	private static final String PREFERENCES_NOT_RESPECTED = ChatColor.RED + "[Team] " + ChatColor.GRAY + "Spiacente, non è stato possibile rispettare del tutto le tue preferenze su con chi stare in team.";
	
	// Restituisce tutti i team creati
	public static Set<Team> assignTeams(int teamsSize, List<TeamPreference> preferences, List<Player> freePlayers) {
		
		List<Team> initialFullTeams = Lists.newArrayList(); // Questi sono i team completi dell'inizio
		List<Team> fullTeams = Lists.newArrayList(); // I team completi MA che sono stati formati dal metodo
		List<Team> partialTeams = Lists.newArrayList(); // I team incompleti
		
		for (TeamPreference preference : preferences) {
			if (preference.getMembers().size() >= teamsSize) {
				initialFullTeams.add(new Team(preference.getMembers()));
			} else {
				partialTeams.add(new Team(preference.getMembers()));
			}
		}
		
		System.out.println("----------- STATUS INIZIALE --------");
		System.out.println("Initial complete teams: " + initialFullTeams.toString());
		System.out.println("Complete teams: " + fullTeams.toString());
		System.out.println("Partial: " + partialTeams.toString());
		System.out.println("Free: " + freePlayers.toString());
		System.out.println("------------------------------------");
		
		// Cerca di unire i team liberi
		
		for (int i = 0; i < 100; i++) { // Tentativi per unire i team piccoli

			boolean changedSomething = false;
		
			for (Team partialTeam : partialTeams) {
				for (Team otherPartialTeam : partialTeams) {
				
					if (partialTeam != otherPartialTeam && !partialTeam.isEmpty() && !otherPartialTeam.isEmpty()) {
						
						if (partialTeam.size() + otherPartialTeam.size() <= teamsSize) {
							
							changedSomething = true;
							partialTeam.absorbTeam(otherPartialTeam); // Traferisce i giocatori all'altro team
						}
					}
				}
			}
			
			Iterator<Team> partialTeamsIter = partialTeams.iterator();
			
			while (partialTeamsIter.hasNext()) {
				Team partialTeam = partialTeamsIter.next();
				
				if (partialTeam.isEmpty()) {
					// Toglie i team vuoti
					partialTeamsIter.remove();
					
				} else if (partialTeam.size() >= teamsSize) {
					// Trasferisce i team completi nella lista dei completi
					partialTeamsIter.remove();
					fullTeams.add(partialTeam);
					
				}
			}
			
			if (!changedSomething) {
				break;
			}
		}
		
		// Cerca di assegnare i giocatori soli
		Iterator<Player> freeIter = freePlayers.iterator();
		while (freeIter.hasNext()) {
			
			Player freePlayer = freeIter.next();
			
			if (partialTeams.isEmpty()) {
				// Crea un nuovo team parziale, non ce ne sono abbastanza!
				partialTeams.add(new Team(freePlayer));
				freeIter.remove();
				continue;
			}
			
			// Riempe prima più team possibili, cercando i più grandi
			Team biggestPartial = findBiggest(partialTeams, null);

			biggestPartial.add(freePlayer);
			if (biggestPartial.size() >= teamsSize) {
				partialTeams.remove(biggestPartial);
				fullTeams.add(biggestPartial);
			}
		}
		
		// E adesso cerchiamo di sistemare tutti i team parziali rimasti, che sono i più piccoli
		while (!isNumberOfTeamsFine(fullTeams, partialTeams, teamsSize)) {
			
			if (partialTeams.size() <= 1) {
				// C'è rimasto solo un team parziale, tutti gli altri quindi sono completi ed è ok
				break;
			}
			
			Team smallest = findSmallest(partialTeams, null);
			Iterator<Player> smallestIter = smallest.getMembers().iterator();
			
			// Separa il team piccolo per unirlo ai più grandi
			while (smallestIter.hasNext()) {
				
				Player spreadPlayer = smallestIter.next();
				Team biggest = findBiggest(partialTeams, smallest); // Esclude il team più piccolo, IMPORTANTE!
				
				if (biggest != smallest) {
					smallestIter.remove();
					biggest.add(spreadPlayer);
					spreadPlayer.sendMessage(PREFERENCES_NOT_RESPECTED);
					
					if (biggest.size() >= teamsSize) {
						partialTeams.remove(biggest);
						fullTeams.add(biggest);
					}
				}
			}
			
			if (smallest.isEmpty()) {
				partialTeams.remove(smallest);
			}
		}
		
		while (!partialTeams.isEmpty() && teamsSize - findSmallest(partialTeams, null).size() > 1) {
			
			// Necessita di giocatori
			Team smallest = findSmallest(partialTeams, null);
			
			Team biggestPartial = findBiggest(partialTeams, null);
			
			if (smallest != biggestPartial && biggestPartial.size() - smallest.size() > 1) {
				// Usiamo un team parziale
				
				Player removed = biggestPartial.removeLast();
				removed.sendMessage(PREFERENCES_NOT_RESPECTED);
				smallest.add(removed);
				continue;
				
			}
			
			// Prendiamo dei giocatori dai team completi MA che sono stati formati da noi
			if (fullTeams.isEmpty()) {
				
				if (initialFullTeams.isEmpty()) {
					break; // Non si può fare nulla
				}
				
				// Siamo costretti a prenderlo da qui
				Team team = initialFullTeams.remove(0);
				fullTeams.add(team);
			}
			
			Team teamToCut = fullTeams.remove(0);
			partialTeams.add(teamToCut);
			
			Player removed = teamToCut.removeLast();
			removed.sendMessage(PREFERENCES_NOT_RESPECTED);
			smallest.add(removed);
		}
		
		System.out.println("----------- STATUS FINALE ----------");
		System.out.println("Initial complete teams: " + initialFullTeams.toString());
		System.out.println("Complete teams: " + fullTeams.toString());
		System.out.println("Partial: " + partialTeams.toString());
		System.out.println("Free: " + freePlayers.toString());
		System.out.println("------------------------------------");
		
		for (Team team : initialFullTeams) {
			team.setAsTeamForMembers();
		}
		for (Team team : fullTeams) {
			team.setAsTeamForMembers();
		}
		for (Team team : partialTeams) {
			team.setAsTeamForMembers();
		}
		
		Set<Team> allTeams = Sets.newHashSet();
		allTeams.addAll(initialFullTeams);
		allTeams.addAll(fullTeams);
		allTeams.addAll(partialTeams);
		
		return allTeams;
	}
	
	
	/**
	 * 
	 * Metodi di utilità
	 * 
	 */

	
	private static boolean isNumberOfTeamsFine(List<Team> fullTeams, List<Team> partialTeams, int teamsSize) {
		int totalPlayersCount = 0;
		
		for (Team team : fullTeams) {
			totalPlayersCount += team.size();
		}
		for (Team team : partialTeams) {
			totalPlayersCount += team.size();
		}
		
		int teamsAmount = fullTeams.size() + partialTeams.size();
		
		int minimumAmountOfTeams = totalPlayersCount / teamsSize;
		if (totalPlayersCount % teamsSize > 0) {
			minimumAmountOfTeams++;
		}
		
		return teamsAmount <= minimumAmountOfTeams;
	}
	
	
	
	private static Team findSmallest(List<Team> teams, Team excluded) {

		Team smallest = null;
		
		for (Team team : teams) {
			
			if (excluded != null && excluded == team) {
				continue;
			}
			
			if (smallest == null) {
				smallest = team;
			} else {
				if (team.size() < smallest.size()) {
					smallest = team;
				}
			}
		}
		
		return smallest;
	}
	
	private static Team findBiggest(List<Team> teams, Team excluded) {

		Team biggest = null;
		
		for (Team team : teams) {
			
			if (excluded != null && excluded == team) {
				continue;
			}
			
			if (biggest == null) {
				biggest = team;
			} else {
				if (team.size() > biggest.size()) {
					biggest = team;
				}
			}
		}
		
		return biggest;
	}

}
