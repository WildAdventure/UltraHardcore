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

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.utils.scatter.SquareSpotsGenerator.XZ;

public class StartupGenerateChunksTask extends BukkitRunnable {

	private int index;

	private int chunksPerTask;
	private List<XZ> chunksToGen;
	private int totalChunksToGenerate;
	private World world;
	
	
	public StartupGenerateChunksTask(List<XZ> chunksToGen, int chunksPerTask, World world) {
		this.chunksToGen = chunksToGen;
		totalChunksToGenerate = chunksToGen.size();
		this.chunksPerTask = chunksPerTask;
		this.world = world;
	}



	@Override
	public void run() {
		int genChunksLocal = 0;
		
		while (true) {
			
			if (index >= totalChunksToGenerate) {
				UltraHardcore.setGeneratingWorld(false);
				UltraHardcore.setGenerationPercentage(100);
				UltraHardcore.getInstance().getLogger().info("Finished generating chunks.");
				cancel();
				return;
			}
			
			XZ current = chunksToGen.get(index);

			if (!world.isChunkLoaded(current.getX(), current.getZ())) {
				world.loadChunk(current.getX(), current.getZ(), true);
				genChunksLocal++;
				
			}
			
			index++;
			if (index % 100 == 0) {
				UltraHardcore.getInstance().getLogger().info("Generated " + index + " chunks (" + (int) UltraHardcore.getGenerationPercentage() + "%).");
			}
				
			if (genChunksLocal >= chunksPerTask) {
				UltraHardcore.setGenerationPercentage(index * 100.0 / totalChunksToGenerate);
				return;
			}
		}
	}
	
	

}
