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

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.command.CommandFramework;

import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.player.HGamer;
import com.gmail.filoghost.ultrahardcore.player.Status;

public class TeamChatCommand extends CommandFramework {
	
	public TeamChatCommand() {
		super(UltraHardcore.getInstance(), "teamchat", "tchat", "tc");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		if (UltraHardcore.getSettings().teamSize <= 1) {
			sender.sendMessage(ChatColor.RED + "Spiacente, la modalità è individuale. Non esiste la chat di team.");
			return;
		}
		
		if (args.length == 0) {
			sender.sendMessage("§bUtilizzo comando: /tc <messaggio>");
			return;
		}
		
		Player executor = CommandValidate.getPlayerSender(sender);
		
		HGamer hExec = UltraHardcore.getHGamer(executor);
		CommandValidate.isTrue(hExec.getStatus() == Status.TRIBUTE, "Devi essere in partita per usare la chat del team.");
		
		if (hExec.getAssignedMates() == null || hExec.getAssignedMates().size() <= 1) {
			sender.sendMessage(ChatColor.RED + "Sei rimasto da solo in team.");
			return;
		}
		
		String message = StringUtils.join(args, " ");
		hExec.getAssignedMates().sendAll(ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "TeamChat" + ChatColor.DARK_AQUA + "] " + ChatColor.WHITE + executor.getName() + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + message);
	}
}
