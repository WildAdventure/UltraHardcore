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

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import com.gmail.filoghost.ultrahardcore.utils.PlayerUtils;
import com.google.common.collect.Lists;

public class TeamPreference {
	
	@Getter
	private List<Player> members;
	
	@Getter
	private List<Player> invited;
	
	public TeamPreference(Collection<Player> members) {
		this.members = Lists.newArrayList();
		invited = Lists.newArrayList();
		this.members.addAll(members);
	}
	
	public TeamPreference(Player maker) {
		members = Lists.newArrayList();
		invited = Lists.newArrayList();
		members.add(maker);
	}
	
	public String membersToString() {
		List<String> names = Lists.newArrayList();
		for (Player member : members) {
			names.add(member.getName());
		}
		
		return PlayerUtils.formatWithAnd(names);
	}
	
	public String invitedToString() {
		List<String> names = Lists.newArrayList();
		for (Player member : invited) {
			names.add(member.getName());
		}
		
		return StringUtils.join(names, ", ");
	}

	public void sendAll(String string) {
		for (Player player : members) {
			player.sendMessage(string);
		}
	}
	
	@Override
	public String toString() {
		return "TeamPreference [members = " + membersToString() + ", invited = " + invitedToString() + "]";
	}
}
