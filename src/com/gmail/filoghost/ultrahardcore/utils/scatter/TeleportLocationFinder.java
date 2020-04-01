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
package com.gmail.filoghost.ultrahardcore.utils.scatter;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.gmail.filoghost.ultrahardcore.utils.scatter.SquareSpotsGenerator.XZ;
import com.google.common.collect.Lists;

public class TeleportLocationFinder {
	
	private static XZ[] locDiffsToTry = new XZ[]{
		new XZ(5, 5),
		new XZ(-5, 5),
		new XZ(5, -5),
		new XZ(-5, -5),
		new XZ(0, 5),
		new XZ(0, -5),
		new XZ(5, 0),
		new XZ(-5, 0)
	};

	public static List<Location> getSafeSpots(World world, int amount, int worldBorder) {
		List<Location> safeLocations = Lists.newArrayList();
		
		int outerSquareRadius = worldBorder * 2 / 3;
		int innerSquareRadius = worldBorder * 1 / 3;
		
		SquareSpotsGenerator genBig = new SquareSpotsGenerator(outerSquareRadius);
		SquareSpotsGenerator genSmall = new SquareSpotsGenerator(innerSquareRadius);
		
		boolean firstGeneration = true;
		
		for (int i = 0; i < 1000; i++) { // Tentativi
						
			if (firstGeneration) {
				XZ[] one = genBig.generate();
				XZ[] two = genBig.generate();
				XZ[] three = genSmall.generate();
				
				for (XZ xz : one) {
					Location safeSpot = findSafeSpotNear(world, xz.getX(), xz.getZ());
					if (safeSpot != null) {
						safeLocations.add(safeSpot);
						
						if (safeLocations.size() >= amount) {
							return safeLocations;
						}
					} else {
						System.out.println("Discarding " + xz.getX() + ", " + xz.getZ());
					}
				}
				
				for (XZ xz : two) {
					Location safeSpot = findSafeSpotNear(world, xz.getX(), xz.getZ());
					if (safeSpot != null) {
						safeLocations.add(safeSpot);
						
						if (safeLocations.size() >= amount) {
							return safeLocations;
						}
					} else {
						System.out.println("Discarding " + xz.getX() + ", " + xz.getZ());
					}
				}
				
				for (XZ xz : three) {
					Location safeSpot = findSafeSpotNear(world, xz.getX(), xz.getZ());
					if (safeSpot != null) {
						safeLocations.add(safeSpot);
						
						if (safeLocations.size() >= amount) {
							return safeLocations;
						}
					} else {
						System.out.println("Discarding " + xz.getX() + ", " + xz.getZ());
					}
				}
				
				firstGeneration = false;
			} else {
				
				XZ[] one = genBig.generate();
				XZ[] two = genSmall.generate();
				
				for (XZ xz : one) {
					Location safeSpot = findSafeSpotNear(world, xz.getX(), xz.getZ());
					if (safeSpot != null) {
						safeLocations.add(safeSpot);
						
						if (safeLocations.size() >= amount) {
							return safeLocations;
						}
					} else {
						System.out.println("Discarding " + xz.getX() + ", " + xz.getZ());
					}
				}
				
				for (XZ xz : two) {
					Location safeSpot = findSafeSpotNear(world, xz.getX(), xz.getZ());
					if (safeSpot != null) {
						safeLocations.add(safeSpot);
						
						if (safeLocations.size() >= amount) {
							return safeLocations;
						}
					} else {
						System.out.println("Discarding " + xz.getX() + ", " + xz.getZ());
					}
				}
			}
		}
		
		return safeLocations;
	}
	
	private static Location findSafeSpotNear(World world, int x, int z) {
		
		Block block = getHighestIgnoreTrees(world, x, z);
		
		if (isSafeBlock(block.getType())) {
			return block.getLocation().add(0.5, 2.0, 0.5);
		}
		
		for (XZ diff : locDiffsToTry) {
			block = getHighestIgnoreTrees(world, x + diff.getX(), z + diff.getZ());
			
			if (isSafeBlock(block.getType())) {
				return block.getLocation().add(0.5, 2.0, 0.5);
			} else {
				System.out.println("Block ("+block.getType()+") is not safe x = "+x+", z="+z+", diff= " + diff);
			}
		}
		
		return null;
	}
	
	private static boolean isSafeBlock(Material mat) {
		return mat != Material.STATIONARY_WATER &&
				mat != Material.WATER &&
				mat != Material.STATIONARY_LAVA &&
				mat != Material.LAVA &&
				mat != Material.CACTUS &&
				mat != Material.VINE &&
				mat.isSolid();
	}
	
	private static boolean hasSpaceAbove(Block block) {
		return 	isPassable(block.getRelative(BlockFace.UP, 1).getType()) &&
				isPassable(block.getRelative(BlockFace.UP, 2).getType()) &&
				isPassable(block.getRelative(BlockFace.UP, 3).getType());
	}
	
	private static boolean isPassable(Material mat) {
		return 	mat == Material.AIR ||
				mat == Material.LONG_GRASS ||
				mat == Material.RED_ROSE ||
				mat == Material.YELLOW_FLOWER ||
				mat == Material.VINE ||
				mat == Material.RED_MUSHROOM ||
				mat == Material.BROWN_MUSHROOM ||
				mat == Material.DEAD_BUSH ||
				mat == Material.DOUBLE_PLANT ||
				mat == Material.SNOW ||
				mat == Material.SAPLING;
	}
	
	private static Block getHighestIgnoreTrees(World world, int x, int z) {
		Block highestBlock = getHighestBlockIgnorePassable(world, x, z);
		
		if (isTreeBlock(highestBlock.getType())) {
			
			int y = highestBlock.getY();
			
			while (y > 40) {
				
				Block current = world.getBlockAt(x, y, z);
				
				if (!isTreeBlock(current.getType()) && !current.isEmpty()) {
					// Forse abbiamo un blocco solido
					if (hasSpaceAbove(current)) {
						return current;
					} else {
						return highestBlock;
					}
				}
				
				y--;
			}
		}
		
		return highestBlock;
	}
	
	private static Block getHighestBlockIgnorePassable(World world, int x, int z) {
		int y = world.getMaxHeight() - 1;
		
		Block b;
		while (y > 0) {
			b = world.getBlockAt(x, y, z);
			
			if (!b.isEmpty() && !isPassable(b.getType())) {
				return b;
			}
			
			y--;
		}
		
		return world.getBlockAt(x, 1, z);
	}
	
	private static boolean isTreeBlock(Material mat) {
		return mat == Material.LEAVES || mat == Material.LEAVES_2 || mat == Material.LOG || mat == Material.LOG_2;
	}
	
}
