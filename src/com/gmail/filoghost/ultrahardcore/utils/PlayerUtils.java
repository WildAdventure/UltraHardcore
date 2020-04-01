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
package com.gmail.filoghost.ultrahardcore.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import wild.api.WildCommons;

import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.player.HGamer;
import com.gmail.filoghost.ultrahardcore.player.Team;
import com.google.common.collect.Maps;

public class PlayerUtils {

	public static String formatWithAnd(List<String> names) {
		
		if (names.size() == 0) {
			return "";
		} else if (names.size() == 1) {
			return names.get(0);
		} else {

			StringBuilder sb = new StringBuilder();

			sb.append(names.get(0));

			for (int i = 1; i < names.size() - 1; i++) {
				sb.append(", ");
				sb.append(names.get(i));
			}

			sb.append(" e ");
			sb.append(names.get(names.size() - 1));

			return sb.toString();
		}
	}

	public static void teleportDismount(Player player, Location loc) {
		if (player.isInsideVehicle())
			player.leaveVehicle();
		if (player.getPassenger() != null)
			player.eject();

		player.teleport(loc);
	}

	public static <T> T removeRandom(Set<T> set) {
		if (set.isEmpty()) {
			return null;
		}

		int indexToRemove = UltraHardcore.getRandomGenerator().nextInt(set.size());

		Iterator<T> iter = set.iterator();
		int index = 0;
		T removed = null;

		while (iter.hasNext()) {

			removed = iter.next();

			if (index == indexToRemove) {
				iter.remove();
				break;
			}

			index++;
		}

		return removed;
	}

	public static void teleportDismount(Player player, Entity entity) {
		if (player.isInsideVehicle())
			player.leaveVehicle();
		if (player.getPassenger() != null)
			player.eject();

		player.teleport(entity);
	}

	// Accetta null
	public static boolean isPlayer(Entity entity) {
		return entity != null && entity.getType() == EntityType.PLAYER;
	}

	public static boolean isOrHasPassenger(Player player) {
		return player.isInsideVehicle() || player.getPassenger() != null;
	}

	public static Player getNearestMate(HGamer nearWho) {
		Team mates = nearWho.getAssignedMates();

		if (mates == null) {
			return null;
		}

		Player target = null;
		double minDistanceFound = 9999999;
		double actualDistance = minDistanceFound + 1;

		for (Player member : mates.getMembers()) {

			if (member != nearWho.getPlayer()) {

				actualDistance = member.getLocation().distanceSquared(nearWho.getPlayer().getLocation());

				if (actualDistance < minDistanceFound) {
					minDistanceFound = actualDistance;
					target = member;
				}
			}
		}

		return target;
	}

	// Resetta tutto
	public static void cleanCompletely(Player player, GameMode mode) {
		WildCommons.clearInventoryFully(player);
		WildCommons.removePotionEffects(player);
		player.resetMaxHealth();
		player.setHealth(((Damageable) player).getMaxHealth());
		player.setFoodLevel(20);
		player.setSaturation(10F);
		player.setExhaustion(0);
		player.setExp(0f);
		player.setLevel(0);
		if (mode != GameMode.CREATIVE) {
			player.setFlying(false);
			player.setAllowFlight(false);
		}
		player.getEnderChest().clear();
		player.setFireTicks(0);

		if (player.getGameMode() != mode) {
			player.setGameMode(mode);
		}

		if (player.getOpenInventory() != null) {
			player.getOpenInventory().close();
		}
	}

	public static boolean isHelmet(ItemStack itemStack) {
		switch (itemStack.getType()) {
			case LEATHER_HELMET:
			case CHAINMAIL_HELMET:
			case IRON_HELMET:
			case GOLD_HELMET:
			case DIAMOND_HELMET:
				return true;
			default:
				return false;
		}
	}

	public static boolean isChestplate(ItemStack itemStack) {
		switch (itemStack.getType()) {
			case LEATHER_CHESTPLATE:
			case CHAINMAIL_CHESTPLATE:
			case IRON_CHESTPLATE:
			case GOLD_CHESTPLATE:
			case DIAMOND_CHESTPLATE:
				return true;
			default:
				return false;
		}
	}

	public static boolean isLeggings(ItemStack itemStack) {
		switch (itemStack.getType()) {
			case LEATHER_LEGGINGS:
			case CHAINMAIL_LEGGINGS:
			case IRON_LEGGINGS:
			case GOLD_LEGGINGS:
			case DIAMOND_LEGGINGS:
				return true;
			default:
				return false;
		}
	}

	public static boolean isBoots(ItemStack itemStack) {
		switch (itemStack.getType()) {
			case LEATHER_BOOTS:
			case CHAINMAIL_BOOTS:
			case IRON_BOOTS:
			case GOLD_BOOTS:
			case DIAMOND_BOOTS:
				return true;
			default:
				return false;
		}
	}

	public static Player getRealDamager(EntityDamageEvent event) {
		return event instanceof EntityDamageByEntityEvent ? getRealDamager((EntityDamageByEntityEvent) event) : null;
	}

	public static Player getRealDamager(EntityDamageByEntityEvent event) {
		return getRealDamagerBehind(event.getDamager());
	}

	public static Player getOnlineShooter(Projectile projectile) {
		
		if (projectile.getShooter() instanceof Player) {
			Player shooter = (Player) projectile.getShooter();
			if (shooter.isOnline()) {
				return shooter;
			}
		}

		return null;
	}

	private static Player getRealDamagerBehind(Entity damager) {

		if (damager == null) {
			return null;
		}

		if (damager.getType() == EntityType.PLAYER) {

			Player playerDamager = (Player) damager;
			return playerDamager.isOnline() ? playerDamager : null;

		} else if (damager instanceof Projectile) {

			Projectile projectileDamager = (Projectile) damager;

			if (projectileDamager.getShooter() instanceof Player) {
				return (Player) projectileDamager.getShooter();
			}
		}

		return null;
	}

	private static Map<Material, Double> armorValues = Maps.newHashMap();
	static {
		armorValues.put(Material.LEATHER_HELMET, 0.5);
		armorValues.put(Material.LEATHER_CHESTPLATE, 1.5);
		armorValues.put(Material.LEATHER_LEGGINGS, 1.0);
		armorValues.put(Material.LEATHER_BOOTS, 0.5);
		armorValues.put(Material.GOLD_HELMET, 1.0);
		armorValues.put(Material.GOLD_CHESTPLATE, 2.5);
		armorValues.put(Material.GOLD_LEGGINGS, 1.5);
		armorValues.put(Material.GOLD_BOOTS, 0.5);
		armorValues.put(Material.CHAINMAIL_HELMET, 1.0);
		armorValues.put(Material.CHAINMAIL_CHESTPLATE, 2.5);
		armorValues.put(Material.CHAINMAIL_LEGGINGS, 2.0);
		armorValues.put(Material.CHAINMAIL_BOOTS, 0.5);
		armorValues.put(Material.IRON_HELMET, 1.0);
		armorValues.put(Material.IRON_CHESTPLATE, 3.0);
		armorValues.put(Material.IRON_LEGGINGS, 2.5);
		armorValues.put(Material.IRON_BOOTS, 1.0);
		armorValues.put(Material.DIAMOND_HELMET, 1.5);
		armorValues.put(Material.DIAMOND_CHESTPLATE, 4.0);
		armorValues.put(Material.DIAMOND_LEGGINGS, 3.0);
		armorValues.put(Material.DIAMOND_BOOTS, 1.5);
	}

	public static double getArmorLevel(Player player) {
		double armorLevel = 0.0;
		for (ItemStack item : player.getInventory().getArmorContents()) {
			if (item != null) {
				Double value = armorValues.get(item.getType());
				if (value != null) {
					armorLevel += value.doubleValue();
				}
			}
		}
		return armorLevel;
	}
}
