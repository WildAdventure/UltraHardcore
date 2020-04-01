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

import java.sql.SQLException;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import wild.api.WildConstants;
import wild.api.bridges.CosmeticsBridge;
import wild.api.sound.EasySound;
import wild.api.world.SpectatorAPI;

import com.gmail.filoghost.ultrahardcore.GameState;
import com.gmail.filoghost.ultrahardcore.UltraHardcore;
import com.gmail.filoghost.ultrahardcore.commands.TeamCommand;
import com.gmail.filoghost.ultrahardcore.event.PlayerStatusChangeEvent;
import com.gmail.filoghost.ultrahardcore.hud.menu.TeleporterMenu;
import com.gmail.filoghost.ultrahardcore.hud.sidebar.SidebarManager;
import com.gmail.filoghost.ultrahardcore.hud.tags.TagsManager;
import com.gmail.filoghost.ultrahardcore.mysql.SQLColumns;
import com.gmail.filoghost.ultrahardcore.mysql.SQLManager;
import com.gmail.filoghost.ultrahardcore.player.ExpManager.LevelInfo;
import com.gmail.filoghost.ultrahardcore.tasks.GivePotionEffectTask;
import com.gmail.filoghost.ultrahardcore.utils.PlayerUtils;

public class HGamer {

	@Getter private 			Player player;
	@Getter private 			Status status;
	
	@Getter @Setter private 	Team assignedMates;
	
	private						int exp;
	private						long lastExpCheck;
	
	public HGamer(@NonNull Player bukkitPlayer, @NonNull Status status) {
		this.player = bukkitPlayer;
		setStatus(status, false, false, true, false);
	}
	
	public void updateCachedExp(int exp) {
		this.exp = exp;
		lastExpCheck = System.currentTimeMillis();
	}
	
	public int getCachedExp() {
		if (System.currentTimeMillis() - lastExpCheck > 120000) {
			try {
				exp = SQLManager.getStat(player.getName(), SQLColumns.EXP);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return exp;
	}
	
	public void giveExpAndSendMessage(int exp, String cause) throws SQLException {
		SQLManager.increaseStat(getName(), SQLColumns.EXP, exp);
		this.exp += exp;
				
		sendMessage(ChatColor.GOLD + "+" + exp + " punti esperienza (" + cause + ")");
		
		LevelInfo levelInfo = ExpManager.getCurrentLevelInfo(this.exp);
		
		if (levelInfo.getCurrentLevelExp() - exp < 0) {
			// Significa che ha passato un livello
			EasySound.quickPlay(player, Sound.LEVEL_UP, 1.5F);
			sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "LIVELLO SUCCESSIVO!" + ChatColor.GRAY + " Sei ora al livello " + ChatColor.WHITE + ChatColor.BOLD + levelInfo.getLevel() + ChatColor.GRAY + ".");
		}
		
	}

	public String getName() {
		return player.getName();
	}
	
	public void sendMessage(String message) {
		player.sendMessage(message);
	}
	
	public boolean hasEmptyHand() {
		ItemStack inHand = player.getItemInHand();
		return inHand == null || inHand.getType() == Material.AIR;
	}
		
	public boolean hasInHand(Material mat) {
		ItemStack inHand = player.getItemInHand();
		return inHand != null && inHand.getType() == mat;
	}

	public void showPlayer(HGamer other) {
		player.showPlayer(other.getPlayer());
	}
	
	public void hidePlayer(HGamer other) {
		player.hidePlayer(other.getPlayer());
	}
	
	public void teleportDismount(Location loc) {
		PlayerUtils.teleportDismount(player, loc);
	}
	
	public void teleportDismount(Entity entity) {
		PlayerUtils.teleportDismount(player, entity);
	}
	
	public void cleanCompletely(GameMode mode) {
		PlayerUtils.cleanCompletely(player, mode);
	}
	
	public void onRespawn() {
		switch (status) {
			
			case GAMEMAKER:
				cleanCompletely(GameMode.CREATIVE);
				giveSpectatorStuff();
				Bukkit.getScheduler().scheduleSyncDelayedTask(UltraHardcore.getInstance(), new GivePotionEffectTask(PotionEffectType.INVISIBILITY, player));
				Bukkit.getScheduler().scheduleSyncDelayedTask(UltraHardcore.getInstance(), new GivePotionEffectTask(PotionEffectType.NIGHT_VISION, player));
				break;
				
			case SPECTATOR:
				cleanCompletely(GameMode.CREATIVE);
				giveSpectatorStuff();
				Bukkit.getScheduler().scheduleSyncDelayedTask(UltraHardcore.getInstance(), new GivePotionEffectTask(PotionEffectType.INVISIBILITY, player));
				Bukkit.getScheduler().scheduleSyncDelayedTask(UltraHardcore.getInstance(), new GivePotionEffectTask(PotionEffectType.NIGHT_VISION, player));
				break;
				
			case TRIBUTE:
				cleanCompletely(GameMode.SURVIVAL);
				givePregameStuff();
				break;
		}
		
		sendMessage(ChatColor.YELLOW + "Ora sei " + status.getNameAndArticle() + "!");
	}
	
	public void setStatus(Status newStatus, boolean sendMessage, boolean updatePlayers, boolean cleanPlayer, boolean updateTeleporter) {
		
		if (newStatus == this.status) {
			if (sendMessage) sendMessage(ChatColor.RED + "Sei già " + status.getNameAndArticle() + "!");
			return;
		}
		
		this.status = newStatus;
		
		switch (newStatus) {
			
			case GAMEMAKER:
				if (cleanPlayer) {
					cleanCompletely(GameMode.CREATIVE);
					giveSpectatorStuff();
				}
				TagsManager.setGhost(player);
				player.spigot().setCollidesWithEntities(false);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0), true);
				SpectatorAPI.setSpectator(player);
				TeamCommand.playerNotTributeAnymoreOrQuit(player);
				break;
				
			case SPECTATOR:
				if (cleanPlayer) {
					cleanCompletely(GameMode.CREATIVE);
					giveSpectatorStuff();
				}
				TagsManager.setGhost(player);
				player.spigot().setCollidesWithEntities(false);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0), true);
				SpectatorAPI.setSpectator(player);
				TeamCommand.playerNotTributeAnymoreOrQuit(player);
				break;
				
			case TRIBUTE:
				if (cleanPlayer) {
					cleanCompletely(GameMode.SURVIVAL);
					givePregameStuff();
				}

				TagsManager.setTribute(player);
				player.spigot().setCollidesWithEntities(true);
				SpectatorAPI.removeSpectator(player);
				break;
		}
		
		VanishManager.updatePlayer(this);
		
		if (sendMessage) sendMessage(ChatColor.YELLOW + "Ora sei " + newStatus.getNameAndArticle() + "!");
		if (updatePlayers) SidebarManager.setPlayers(UltraHardcore.countTributes());
		if (updateTeleporter) TeleporterMenu.update();
		
		if (newStatus != Status.TRIBUTE && UltraHardcore.getState() == GameState.PRE_GAME) {
			TeamCommand.playerNotTributeAnymoreOrQuit(player);
		}
		
		Bukkit.getPluginManager().callEvent(new PlayerStatusChangeEvent(player, status));
	}
	
	public void givePregameStuff() {
		UltraHardcore.getBookTutorial().giveTo(player);
		CosmeticsBridge.giveCosmeticsItems(player.getInventory());
		CosmeticsBridge.updateCosmetics(player, CosmeticsBridge.Status.LOBBY);
		
	}
	
	public void giveSpectatorStuff() {
		player.getInventory().setItem(0, WildConstants.Spectator.TELEPORTER);
		player.getInventory().setItem(8, WildConstants.Spectator.BACK_TO_HUB);
		CosmeticsBridge.updateCosmetics(player, CosmeticsBridge.Status.SPECTATOR);
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
