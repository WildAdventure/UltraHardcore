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
package com.gmail.filoghost.ultrahardcore;

import lombok.Setter;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.NumberConversions;

import wild.api.sound.EasySound;

import com.gmail.filoghost.ultrahardcore.hud.sidebar.SidebarManager;
import com.gmail.filoghost.ultrahardcore.player.HGamer;
import com.gmail.filoghost.ultrahardcore.player.Status;

// Per ora si restringe ogni secondo
public class WorldBorderManager {
	
	public static BukkitTask task;
	
	@Setter
	private static boolean restrict;
	private static boolean startedRestrict;
	
	private static WorldBorder visualBorder;
	private static int lastRadiusUpdate;
	
	private static int leap;

	public static void startTask(final int startBorder, final int endBorder, final int ticksInterval) {
		if (endBorder > startBorder) {
			throw new IllegalArgumentException("endBorder > startBorder");
		}
		
		visualBorder = UltraHardcore.getGameWorld().getWorldBorder();
		visualBorder.setCenter(0.0, 0.0);
		visualBorder.setDamageBuffer(0); // Prende danno appena uscito
		visualBorder.setDamageAmount(0.1);
		visualBorder.setSize(startBorder * 2);
		
		task = new BukkitRunnable() {

			@Override
			public void run() {
				
				if (restrict && !startedRestrict) {
					visualBorder.setSize(endBorder * 2, ((startBorder - endBorder) * ticksInterval) / 20);
					startedRestrict = true;
				}
				
				double actualRadius = visualBorder.getSize() / 2;
				int roundupActualRadius = NumberConversions.ceil(actualRadius);
				
				if (roundupActualRadius != lastRadiusUpdate) {
					SidebarManager.updateBorderRadius(roundupActualRadius);
					lastRadiusUpdate = roundupActualRadius;
				}
				
				int warningDistance;
				if (actualRadius >= 100) {
					warningDistance = 8;
				} else if (actualRadius >= 50) {
					warningDistance = 4;
				} else {
					warningDistance = 2;
				}
				
				if (visualBorder.getWarningDistance() != warningDistance) {
					visualBorder.setWarningDistance(warningDistance);
				}
				
				if (leap++ % 4 == 0) {
					for (HGamer hGamer : UltraHardcore.getAllGamersUnsafe()) {
						if (hGamer.getStatus() == Status.TRIBUTE && hGamer.getPlayer().getGameMode() == GameMode.SURVIVAL) {
							
							Location loc = hGamer.getPlayer().getLocation();
							double xAbs = Math.abs(loc.getX());
							double zAbs = Math.abs(loc.getZ());
	
							if (xAbs > actualRadius || zAbs > actualRadius) {
								EasySound.quickPlay(hGamer.getPlayer(), Sound.NOTE_BASS);
								hGamer.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "ATTENZIONE!" + ChatColor.RED + " Sei fuori dal bordo, torna all'interno!");
							}
						}
					}
				}
			}
			
		}.runTaskTimer(UltraHardcore.getInstance(), 5, 5);
	}
	
	
	/*
	public static void startTask(int startBorder, int endBorder, int ticksInterval) {
		if (endBorder > startBorder) {
			throw new IllegalArgumentException("endBorder > startBorder");
		}

		actualBorder = startBorder;
		minBorder = endBorder;
		updateVisualBorder(actualBorder);
		
		task = new BukkitRunnable() {

			@Override
			public void run() {
				
				if (restrict && actualBorder > minBorder) {
					actualBorder--;
					SidebarManager.updateBorderRadius(actualBorder);
					updateVisualBorder(actualBorder);
				}

				for (HGamer hGamer : UltraHardcore.getAllGamersUnsafe()) {
					
					if (hGamer.getStatus() == Status.TRIBUTE && hGamer.getPlayer().getGameMode() == GameMode.SURVIVAL) {
						
						Location loc = hGamer.getPlayer().getLocation();
						double xAbs = Math.abs(loc.getX());
						double zAbs = Math.abs(loc.getZ());

						if (xAbs > actualBorder || zAbs > actualBorder) {
							EasySound.quickPlay(hGamer.getPlayer(), Sound.NOTE_BASS);
							hGamer.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "ATTENZIONE!" + ChatColor.RED + " Sei fuori dal bordo, torna all'interno!");
						}
					}
				}
			}
			
		}.runTaskTimer(UltraHardcore.getInstance(), ticksInterval, ticksInterval);
	}
	
	private static void updateVisualBorder(double size) {
		if (visualBorder == null) {
			visualBorder = UltraHardcore.getGameWorld().getWorldBorder();
			visualBorder.setCenter(0.0, 0.0);
			visualBorder.setDamageBuffer(0); // Prende danno appena uscito
			visualBorder.setDamageAmount(0.1);
			visualBorder.setSize(size * 2);
		} else {
			visualBorder.setSize(size * 2, 1);
		}
		
		int warningDistance;
		if (size >= 100) {
			warningDistance = 10;
		} else if (size >= 50) {
			warningDistance = 5;
		} else {
			warningDistance = 2;
		}
		
		if (visualBorder.getWarningDistance() != warningDistance) {
			visualBorder.setWarningDistance(warningDistance);
		}
	}
	*/
}

