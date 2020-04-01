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
package com.gmail.filoghost.ultrahardcore.timers;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import wild.api.sound.EasySound;

import com.gmail.filoghost.ultrahardcore.GameState;
import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.hud.sidebar.SidebarManager;
import com.gmail.filoghost.ultrahardcore.utils.UnitUtils;

public class InvincibilityTimer extends TimerMaster {

	@Getter @Setter
	private int countdown;
	
	private EasySound clickSound = new EasySound(Sound.CLICK);
	private EasySound anvilSound = new EasySound(Sound.ANVIL_LAND);
	
	int blockMovementTaskID;
	
	public InvincibilityTimer() {
		super(0, 20L);
		this.countdown = UltraHardcore.getSettings().invincibilityMinutes * 60;
	}
	@Override
	public void run() {
		
		if (countdown <= 0) {
			anvilSound.playToAll();
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage("" + ChatColor.RED + ChatColor.BOLD + "Il PvP è ora attivo!");
			UltraHardcore.setState(GameState.GAME);
			UltraHardcore.getGameTimer().startNewTask();
			stopTask();
			return;
		}
		
		if (countdown > 60) {
			
			// Ogni 10 secondi
			if (countdown % 60 == 0) {
				Bukkit.broadcastMessage(ChatColor.GREEN + "Il PvP verrà abilitato in " + UnitUtils.formatMinutes(countdown / 60) + ".");
				SidebarManager.setTime(UnitUtils.formatMinutes(countdown / 60));
			}
			
		} else {
			
			if (countdown > 5) {
				
				// Ogni 10 secondi
				if (countdown % 10 == 0) {
					Bukkit.broadcastMessage(ChatColor.GREEN + "Il PvP verrà abilitato in " + UnitUtils.formatSeconds(countdown) + ".");
				}
				
			} else {

				// Meno di 5 secondi
				Bukkit.broadcastMessage(ChatColor.GREEN + "Il PvP verrà abilitato in " + UnitUtils.formatSeconds(countdown) + ".");
				clickSound.playToAll();
					
			}
			
			SidebarManager.setTime(UnitUtils.formatSeconds(countdown));
			
		}

		countdown--;
	}
}
