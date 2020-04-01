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

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.WorldBorderManager;

public class EndTimer extends TimerMaster {

	private int countdown = 15 * 2;
	private Player[] winners;
	
	int colorsIndex = 0;
	private static final Color[] colors = new Color[] {
		Color.fromRGB(255, 0, 0), 	Color.fromRGB(255, 128, 0),
		Color.fromRGB(255, 255, 0), Color.fromRGB(128, 255, 0),
		Color.fromRGB(0, 255, 0), 	Color.fromRGB(0, 255, 128),
		Color.fromRGB(0, 255, 255), Color.fromRGB(0, 128, 255),
		Color.fromRGB(0, 0, 255), 	Color.fromRGB(128, 0, 255),
		Color.fromRGB(255, 0, 255), Color.fromRGB(255, 0, 128),
	};
	
	public EndTimer() {
		super(0, 10L);
	}
	
	
	public void setWinnersAndStart(Player... players) {
		if (players == null || players.length == 0) {
			throw new IllegalArgumentException("At least one player please");
		}
		
		if (WorldBorderManager.task != null) {
			WorldBorderManager.task.cancel();
		}
		
		super.startNewTask();
		this.winners = players;
	}

	@Override
	public void run() {
		
		if (countdown <= 0) {
			
			for (Player winner : winners) {
				if (winner.isOnline()) {
					winner.kickPlayer(ChatColor.GOLD + "Complimenti, hai vinto la partita!" + "§0§0§0");
				}
			}
			
			if (winners.length > 1) {
				String[] names = new String[winners.length];
				for (int i = 0; i < names.length; i++) {
					names[i] = winners[i].getName();
				}
				UltraHardcore.stopServer(ChatColor.GREEN + "La partita è finita, hanno vinto " + StringUtils.join(names, ", "));
			} else {
				UltraHardcore.stopServer(ChatColor.GREEN + "La partita è finita, ha vinto " + winners[0].getName());
			}
			return;
			
		}
		
		colorsIndex++;
		
		if (colorsIndex >= colors.length) {
			colorsIndex = 0;
		}
		
		for (Player winner : winners) {
			if (winner.isOnline()) {
				winningFirework(getRandomAround(winner), colorsIndex);
			}
		}
		
		countdown--;
	}
	
	public static void winningFirework(Location loc, int index) {
		Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta meta = firework.getFireworkMeta();

		Color currentColor = colors[index];

		int red = currentColor.getRed();
		int green = currentColor.getGreen();
		int blue = currentColor.getBlue();

		FireworkEffect effect = FireworkEffect.builder().withColor(Color.fromRGB(red, green, blue)).withColor(Color.fromRGB((red / 4) * 3, (green / 4) * 3, (blue / 4) * 3)).withColor(Color.fromRGB(red / 2, green / 2, blue / 2)).with(Type.BURST).withTrail().build();

		meta.addEffect(effect);
		meta.setPower(1);
		firework.setFireworkMeta(meta);
	}

	public static Location getRandomAround(Player player) {
		Location playerLoc = player.getLocation();

		double x = playerLoc.getX();
		double y = playerLoc.getY();
		double z = playerLoc.getZ();

		double angle = Math.random() * Math.PI * 2;

		Location loc = new Location(player.getWorld(), x + Math.cos(angle) * 2.0, y, z + Math.sin(angle) * 2.0);
		while (loc.getY() < 250.0 && loc.getBlock().getType() != Material.AIR) {
			loc.setY(loc.getY() + 1.0);
		}
		
		return loc;
	}
}
