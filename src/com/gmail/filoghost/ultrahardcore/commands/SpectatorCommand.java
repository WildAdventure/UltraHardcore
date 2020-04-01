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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import wild.api.command.CommandFramework;
import wild.api.command.CommandFramework.Permission;

import com.gmail.filoghost.ultrahardcore.GameState;
import com.gmail.filoghost.ultrahardcore.Perms;
import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.listener.DeathListener;
import com.gmail.filoghost.ultrahardcore.player.HGamer;
import com.gmail.filoghost.ultrahardcore.player.Status;

@Permission(Perms.SPECTATOR)
public class SpectatorCommand extends CommandFramework {
	
	public SpectatorCommand() {
		super(UltraHardcore.getInstance(), "spectator");
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		HGamer hGamer = UltraHardcore.getHGamer(CommandValidate.getPlayerSender(sender));
		
		if (hGamer.getStatus() == Status.SPECTATOR) {
			
			CommandValidate.isTrue(UltraHardcore.getState() == GameState.PRE_GAME, "Non puoi diventare un tributo ora.");
			hGamer.setStatus(Status.TRIBUTE, true, true, true, true);
			
		} else {
			
			if (UltraHardcore.getState() == GameState.PRE_GAME && !hGamer.getPlayer().hasPermission(Perms.SPECTATOR_COMMAND_PREGAME)) {
				hGamer.sendMessage(ChatColor.RED + "Non puoi diventare uno spettatore ora.");
				return;
			}
			
			if ((UltraHardcore.getState() == GameState.GAME || UltraHardcore.getState() == GameState.INVINCIBILITY) && !hGamer.getPlayer().hasPermission(Perms.SPECTATOR_COMMAND_GAME)) {
				hGamer.sendMessage(ChatColor.RED + "Non puoi diventare uno spettatore ora.");
				return;
			}
			
			boolean wasTribute = hGamer.getStatus() == Status.TRIBUTE;
			
			hGamer.setStatus(Status.SPECTATOR, true, true, true, true);
			
			if (UltraHardcore.getState() != GameState.PRE_GAME && wasTribute) {
				DeathListener.parseDeath(hGamer, null, ChatColor.RED + hGamer.getName() + " è uscito dalla partita.", false, false);
			}
		}
		
	}

}
