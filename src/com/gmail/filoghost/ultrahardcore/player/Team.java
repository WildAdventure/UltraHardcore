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
package com.gmail.filoghost.ultrahardcore.player;

import java.util.Collection;
import java.util.List;

import lombok.Getter;

import org.bukkit.entity.Player;

import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.utils.PlayerUtils;
import com.google.common.collect.Lists;

public class Team {
	
	@Getter
	private List<Player> members;
	
	@Getter
	private List<String> initialMembers;
	
	public Team(Collection<Player> members) {
		this.members = Lists.newArrayList();
		this.members.addAll(members);
		this.initialMembers = Lists.newArrayList();
	}
	
	public Team(Player maker) {
		members = Lists.newArrayList();
		members.add(maker);
		this.initialMembers = Lists.newArrayList();
	}
	
	public boolean add(Player e) {
		return members.add(e);
	}

	public boolean contains(Player player) {
		return members.contains(player);
	}
	
	public boolean remove(Player player) {
		return members.remove(player);
	}
	
	public Player removeLast() {
		if (members.isEmpty()) {
			return null;
		}
		
		return members.remove(members.size() - 1);
	}

	public int size() {
		return members.size();
	}
	
	public boolean isEmpty() {
		return members.isEmpty();
	}
	
	public String membersToString() {
		List<String> names = Lists.newArrayList();
		for (Player member : members) {
			names.add(member.getName());
		}
		
		return PlayerUtils.formatWithAnd(names);
	}
	
	public String initialMembersToString() {
		return PlayerUtils.formatWithAnd(initialMembers);
	}
	
	public void saveInitialMembers() {
		initialMembers.clear();
		for (Player player : members) {
			initialMembers.add(player.getName());
		}
	}

	@Override
	public String toString() {
		return "Team(" + size() + ")[members = " + membersToString() + "]";
	}

	public void sendAll(String string) {
		for (Player player : members) {
			player.sendMessage(string);
		}
	}

	public void absorbTeam(Team otherTeam) {
		members.addAll(otherTeam.getMembers());
		otherTeam.getMembers().clear();
	}
	
	public void setAsTeamForMembers() {
		for (Player member : members) {
			HGamer hGamer = UltraHardcore.getHGamer(member);
			
			if (hGamer != null) {
				hGamer.setAssignedMates(this);
			}
		}
	}
}
