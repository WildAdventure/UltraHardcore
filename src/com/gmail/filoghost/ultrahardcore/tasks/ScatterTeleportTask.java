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
package com.gmail.filoghost.ultrahardcore.tasks;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.utils.scatter.TeleportGroup;

public class ScatterTeleportTask extends BukkitRunnable {
	
	private List<TeleportGroup> teleportGroups;
	private List<Location> spots;
	
	private int current;
	

	public ScatterTeleportTask(List<TeleportGroup> teleportGroups, List<Location> spots) {
		this.teleportGroups = teleportGroups;
		this.spots = spots;
	}

	@Override
	public void run() {
		if (current >= teleportGroups.size()) {
			teleportGroups.clear();
			spots.clear();
			cancel();
			return;
		}
		
		if (current < spots.size()) {
			System.out.println("Teleporting " + teleportGroups.get(current) + " to " + spots.get(current));
			teleportGroups.get(current).teleport(spots.get(current));
		} else {
			UltraHardcore.getInstance().getLogger().warning("Could not teleport group (" + teleportGroups.get(current) + ") to a safe spot.");
			teleportGroups.get(current).teleport(UltraHardcore.getGameWorldSpawn());
		}
		
		current++;
	}

}
