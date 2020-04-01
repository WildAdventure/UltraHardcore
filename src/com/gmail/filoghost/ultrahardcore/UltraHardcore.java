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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import net.cubespace.yamler.YamlerConfigurationException;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import wild.api.WildCommons;
import wild.api.item.BookTutorial;

import com.gmail.filoghost.ultrahardcore.commands.ClassificaCommand;
import com.gmail.filoghost.ultrahardcore.commands.DebugCommand;
import com.gmail.filoghost.ultrahardcore.commands.FixCommand;
import com.gmail.filoghost.ultrahardcore.commands.GamemakerCommand;
import com.gmail.filoghost.ultrahardcore.commands.GlobalChatCommand;
import com.gmail.filoghost.ultrahardcore.commands.SpawnCommand;
import com.gmail.filoghost.ultrahardcore.commands.SpectatorCommand;
import com.gmail.filoghost.ultrahardcore.commands.StartCommand;
import com.gmail.filoghost.ultrahardcore.commands.StatsCommand;
import com.gmail.filoghost.ultrahardcore.commands.TeamChatCommand;
import com.gmail.filoghost.ultrahardcore.commands.TeamCommand;
import com.gmail.filoghost.ultrahardcore.files.Settings;
import com.gmail.filoghost.ultrahardcore.hud.menu.TeleporterMenu;
import com.gmail.filoghost.ultrahardcore.hud.sidebar.SidebarManager;
import com.gmail.filoghost.ultrahardcore.hud.tags.TagsManager;
import com.gmail.filoghost.ultrahardcore.listener.BoatFixListener;
import com.gmail.filoghost.ultrahardcore.listener.ChatListener;
import com.gmail.filoghost.ultrahardcore.listener.ChunkUnloadPreventer;
import com.gmail.filoghost.ultrahardcore.listener.DeathListener;
import com.gmail.filoghost.ultrahardcore.listener.EntityLagListener;
import com.gmail.filoghost.ultrahardcore.listener.GoldenAppleListener;
import com.gmail.filoghost.ultrahardcore.listener.InventoryToolsListener;
import com.gmail.filoghost.ultrahardcore.listener.InvisibleFireFixListener;
import com.gmail.filoghost.ultrahardcore.listener.JoinQuitListener;
import com.gmail.filoghost.ultrahardcore.listener.LastDamageCauseListener;
import com.gmail.filoghost.ultrahardcore.listener.PingListener;
import com.gmail.filoghost.ultrahardcore.listener.StrengthFixListener;
import com.gmail.filoghost.ultrahardcore.listener.protection.BlockListener;
import com.gmail.filoghost.ultrahardcore.listener.protection.CommandListener;
import com.gmail.filoghost.ultrahardcore.listener.protection.EntityListener;
import com.gmail.filoghost.ultrahardcore.listener.protection.WeatherListener;
import com.gmail.filoghost.ultrahardcore.mysql.SQLColumns;
import com.gmail.filoghost.ultrahardcore.mysql.SQLManager;
import com.gmail.filoghost.ultrahardcore.mysql.SQLTask;
import com.gmail.filoghost.ultrahardcore.nms.NMSBiomeEditor;
import com.gmail.filoghost.ultrahardcore.player.ExpManager;
import com.gmail.filoghost.ultrahardcore.player.HGamer;
import com.gmail.filoghost.ultrahardcore.player.Status;
import com.gmail.filoghost.ultrahardcore.player.Team;
import com.gmail.filoghost.ultrahardcore.tasks.StartupGenerateChunksTask;
import com.gmail.filoghost.ultrahardcore.timers.CheckWinnerTimer;
import com.gmail.filoghost.ultrahardcore.timers.CompassUpdateTimer;
import com.gmail.filoghost.ultrahardcore.timers.EndTimer;
import com.gmail.filoghost.ultrahardcore.timers.GameTimer;
import com.gmail.filoghost.ultrahardcore.timers.InvincibilityTimer;
import com.gmail.filoghost.ultrahardcore.timers.MySQLKeepAliveTimer;
import com.gmail.filoghost.ultrahardcore.timers.PregameTimer;
import com.gmail.filoghost.ultrahardcore.timers.TimerMaster;
import com.gmail.filoghost.ultrahardcore.utils.scatter.SquareSpotsGenerator.XZ;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class UltraHardcore extends JavaPlugin {

	@Getter private static 				UltraHardcore instance;
	@Getter private static 				Settings settings;
	@Getter	private static 				GameState state;
	private static 						Set<String> spectatorCommandBlacklist;
	
	// Tutti i timer
	@Getter private static 				PregameTimer pregameTimer;
	@Getter private static 				TimerMaster invincibilityTimer;
	@Getter private static 				GameTimer gameTimer;
	@Getter private static 				TimerMaster checkWinnerTimer;
	@Getter private static 				EndTimer endTimer;
	
	@Getter private static 				int actualWorldBorder;
	
	@Getter private static 				Random randomGenerator;
	
	public static 						Map<Player, HGamer> players;
	
	@Getter private static				Location lobbySpawn;
	@Getter private static				Location gameWorldSpawn;
	
	@Getter private static				BookTutorial bookTutorial;

	@Getter private static 				boolean wildChat;
	
	@Getter @Setter private static 		boolean isGeneratingWorld = true;
	@Getter @Setter private static 		double generationPercentage;
	
	@Getter private static 				World lobbyWorld, gameWorld;
	
	@Override
	public void onLoad() {
		// Prima di tutto
		instance = this;
		randomGenerator = new Random();
		
		// Configurazione
		try {
			settings = new Settings();
			settings.init();
		} catch (YamlerConfigurationException e) {
			e.printStackTrace();
			logPurple("config.yml non caricato! Spegnimento server fra 10 secondi...");
			WildCommons.pauseThread(10000);
			Bukkit.shutdown();
			return;
		}
		
		logAqua("Rimuovendo il bioma oceano...");
		try {
			NMSBiomeEditor.removeOceans();
		} catch (Exception e) {
			e.printStackTrace();
			logPurple("Impossbile rimuovere oceani! Spegnimento server fra 10 secondi...");
			WildCommons.pauseThread(10000);
			Bukkit.shutdown();
		}
		
		logAqua("Cancellazione mondo vecchio.");
		Bukkit.getServer().unloadWorld("world", false);
		try {
			FileUtils.deleteDirectory(new File("world"));
			FileUtils.deleteDirectory(new File("world_game"));
			
			File lobby = new File(settings.lobbyFolder);
			if (lobby.isDirectory()) {
				FileUtils.copyDirectory(lobby, new File("world"));
			} else {
				logPurple("Impossibile trovare la lobby!");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			logPurple("Impossibile cancellare il vecchio mondo o copiare la lobby!");
		}
	}
	
	@Override
	public void onEnable() {
		if (!Bukkit.getPluginManager().isPluginEnabled("WildCommons")) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + this.getName() + "] Richiesto WildCommons!");
			WildCommons.pauseThread(10000);
			Bukkit.shutdown();
			return;
		}
		
		try {
			gameWorld = new WorldCreator("world_game").environment(Environment.NORMAL).type(WorldType.NORMAL).createWorld();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("WildChat")) {
			wildChat = true;
		}
		
		// Database MySQL
		try {
			SQLManager.connect(settings.mysql_host, settings.mysql_port, settings.mysql_database, settings.mysql_user, settings.mysql_pass);
			SQLManager.checkConnection();
					
			SQLManager.getMysql().update("CREATE TABLE IF NOT EXISTS uhc_players ("
					+ SQLColumns.NAME + " varchar(20) NOT NULL ,"
					+ SQLColumns.KILLS + " MEDIUMINT unsigned NOT NULL, "
					+ SQLColumns.DEATHS + " MEDIUMINT unsigned NOT NULL, "
					+ SQLColumns.WINS_SOLO + " MEDIUMINT unsigned NOT NULL, "
					+ SQLColumns.WINS_TEAM + " MEDIUMINT unsigned NOT NULL, "
					+ SQLColumns.EXP + " INT unsigned NOT NULL"
					+ ") ENGINE = InnoDB DEFAULT CHARSET = UTF8;");
					
		} catch (Exception ex) {
			ex.printStackTrace();
			logPurple("Impossibile connettersi al database! Il server verrà spento in 10 secondi...");
			WildCommons.pauseThread(10000);
			Bukkit.shutdown();
			return;
		}
				
		// Variabili
		lobbyWorld = Bukkit.getWorld("world");
		final World gameWorld = Bukkit.getWorld("world_game");
		
		if (lobbyWorld == null) {
			logPurple("Impossibile trovare il mondo lobby! Il server verrà spento in 10 secondi...");
			WildCommons.pauseThread(10000);
			Bukkit.shutdown();
			return;
		}
		
		if (gameWorld == null) {
			logPurple("Impossibile trovare il mondo game! Il server verrà spento in 10 secondi...");
			WildCommons.pauseThread(10000);
			Bukkit.shutdown();
			return;
		}
		
		lobbySpawn = lobbyWorld.getSpawnLocation();
		
		state = GameState.PRE_GAME;
		spectatorCommandBlacklist = Sets.newHashSet();
		
		players = Maps.newConcurrentMap();
		
		// Lettura items
		bookTutorial = new BookTutorial(this, ChatColor.GREEN + "Tutorial", "Wild Adventure");
		
		// Comandi bloccati
		for (String blacklistedCommand : settings.spectatorCommandBlacklist) {
			spectatorCommandBlacklist.add(blacklistedCommand.toLowerCase());
		}
		
		// Livelli
		ExpManager.loadLevels(settings.expLevels);
		logAqua("Livello massimo: " + ExpManager.getMaxLevel());

		// Teleporter
		TeleporterMenu.load();
		
		// Sidebar & teams
		SidebarManager.initialize(state, settings.teamSize);
		TagsManager.initialize();
		
		// Impostazioni del mondo
		try {
			gameWorld.setDifficulty(Difficulty.valueOf(settings.difficulty.toUpperCase()));
		} catch (IllegalArgumentException e) {
			gameWorld.setDifficulty(Difficulty.HARD);
			logPurple("Difficoltà non valida. Default: hard");
		}
		gameWorld.setPVP(true);
		gameWorld.setSpawnFlags(true, true);
		gameWorld.setStorm(false);
		gameWorld.setThundering(false);
		gameWorld.setKeepSpawnInMemory(false);
		gameWorld.setGameRuleValue("doFireTick", "true");
		gameWorld.setGameRuleValue("doMobLoot", "true");
		gameWorld.setGameRuleValue("doMobSpawning", "true");
		gameWorld.setGameRuleValue("doTileDrops", "true");
		gameWorld.setGameRuleValue("keepInventory", "false");
		gameWorld.setGameRuleValue("mobGriefing", "true");
		gameWorld.setGameRuleValue("naturalRegeneration", "false");
		gameWorld.setGameRuleValue("doDaylightCycle", "false");
		
		lobbyWorld.setDifficulty(Difficulty.EASY);
		lobbyWorld.setSpawnFlags(false, false);
		lobbyWorld.setPVP(false);
		lobbyWorld.setKeepSpawnInMemory(true);
		lobbyWorld.setTime(3000);
		lobbyWorld.setGameRuleValue("doDaylightCycle", "false");
		
		lobbySpawn = lobbyWorld.getSpawnLocation().add(0.5, 0, 0.5);
		
		for (Entity e : lobbyWorld.getEntities()) {
			if (e.getType() != EntityType.PLAYER) {
				e.remove();
			}
		}
		
		
		// Comandi
		new StartCommand();
		new DebugCommand();
		new FixCommand();
		new GamemakerCommand();
		new SpectatorCommand();
		new StatsCommand();
		new SpawnCommand();
		new ClassificaCommand();
		new TeamCommand();
		new TeamChatCommand();
		new GlobalChatCommand();
		
		
		// Listeners
		Bukkit.getPluginManager().registerEvents(new JoinQuitListener(), this);
		Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
		Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
		Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryToolsListener(), this);
		Bukkit.getPluginManager().registerEvents(new PingListener(), this);
		Bukkit.getPluginManager().registerEvents(new WeatherListener(), this);
		Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new CommandListener(), this);
		Bukkit.getPluginManager().registerEvents(new LastDamageCauseListener(), this);
		
		// Tutti i fix
		Bukkit.getPluginManager().registerEvents(new BoatFixListener(), this);
		Bukkit.getPluginManager().registerEvents(new StrengthFixListener(), this);
		Bukkit.getPluginManager().registerEvents(new InvisibleFireFixListener(), this);
		Bukkit.getPluginManager().registerEvents(new EntityLagListener(), this);
		Bukkit.getPluginManager().registerEvents(new GoldenAppleListener(), this);
		
		// Timer iniziali
		invincibilityTimer = new InvincibilityTimer();
		gameTimer = new GameTimer();
		endTimer = new EndTimer();
		//(worldBorderTimer = new WorldBorderTimer(world, worldBorderLimit)).startNewTask();
		(pregameTimer = new PregameTimer()).startNewTask();
		(checkWinnerTimer = new CheckWinnerTimer()).startNewTask();
		new CompassUpdateTimer(gameWorld).startNewTask();
		new MySQLKeepAliveTimer().startNewTask();
		
		//world.setAutoSave(false); NO perché i chunk non si scaricano...
		
		// Task per generare i chunk
		
		final int viewDistance = 6;
		
		final int outerSquareMax = (settings.startBorderRadius * 2 / 3) / 16 + viewDistance;
		final int outerSquareMin = (settings.startBorderRadius * 2 / 3) / 16 - viewDistance;
		
		final int innerSquareMax = (settings.startBorderRadius * 1 / 3) / 16 + viewDistance;
		final int innerSquareMin = (settings.startBorderRadius * 1 / 3) / 16 - viewDistance;
		
		final int startRadius = outerSquareMax + 1;
		
		final List<XZ> chunksToGen = Lists.newArrayList();
		
		for (int x = -startRadius; x <= startRadius; x++) {
			for (int z = -startRadius; z <= startRadius; z++) {
				
				int xAbs = Math.abs(x);
				int zAbs = Math.abs(z);
				
				if (
						((isBetween(xAbs, innerSquareMin, innerSquareMax) && zAbs < innerSquareMax) ||
						(isBetween(zAbs, innerSquareMin, innerSquareMax) && xAbs < innerSquareMax))
								
							||
						
						((isBetween(xAbs, outerSquareMin, outerSquareMax) && zAbs < outerSquareMax) ||
						(isBetween(zAbs, outerSquareMin, outerSquareMax) && xAbs < outerSquareMax))
							
					) {
					
					chunksToGen.add(new XZ(x, z));
				}
			}
		}
		
		Bukkit.getPluginManager().registerEvents(new ChunkUnloadPreventer(innerSquareMin + 1, innerSquareMax - 1, outerSquareMin + 1, outerSquareMax - 1), this);
		new StartupGenerateChunksTask(chunksToGen, settings.advanced_chunksPerTickGeneration, gameWorld).runTaskTimer(this, 1, 1);
	}
	
	private boolean isBetween(int value, int min, int max) {
		return value >= min && value <= max;
	}
	
	@Override
	public void onDisable() {
		for (World world : Bukkit.getWorlds()) {
			Bukkit.unloadWorld(world, false);
		}
	}
	
	public static HGamer registerHGamer(Player bukkitPlayer, Status status) {
		HGamer hGamer = new HGamer(bukkitPlayer, status);
		players.put(bukkitPlayer, hGamer);
		return hGamer;
	}
	
	public static HGamer unregisterHGamer(Player bukkitPlayer) {
		return players.remove(bukkitPlayer);
	}
	
	public static HGamer getHGamer(String name) {
		name = name.toLowerCase();
		for (HGamer hGamer : players.values()) {
			if (hGamer.getName().toLowerCase().equals(name)) {
				return hGamer;
			}
		}
		return null;
	}
	
	public static HGamer getHGamer(Player bukkitPlayer) {
		if (bukkitPlayer == null) {
			return null;
		}
		return players.get(bukkitPlayer);
	}
	
	public static void setState(GameState state) {
		UltraHardcore.state = state;
		SidebarManager.updateState(state);
	}
	
	public static void checkWinners() {
		
		if (state == GameState.INVINCIBILITY || state == GameState.GAME || state == GameState.FINAL_BATTLE) {
			
			if (settings.teamSize <= 1) {
			
				HGamer winner = null;
				for (HGamer hGamer : players.values()) {
					
					if (hGamer.getStatus() == Status.TRIBUTE) {
						
						if (winner == null) {
							winner = hGamer;
						} else {
							return; // Già settato quindi sono almeno 2
						}
					}
				}
				
				setState(GameState.END);
				SidebarManager.setTime("-");
				
				gameTimer.stopTask();
				
				if (winner == null) {
					
					logAqua("Nessun vincitore!");
					
					stopServer(ChatColor.RED + "Non c'è stato nessun vincitore, riavvio del server.");
					
				} else {
					
					for (HGamer other : UltraHardcore.getAllGamersUnsafe()) {
						if (other != winner) {
							other.sendMessage(ChatColor.GOLD + winner.getName() + " ha vinto la partita!");
						}
					}
					
					winner.sendMessage("");
					winner.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "HAI VINTO LA PARTITA!");
					winner.getPlayer().setGameMode(GameMode.CREATIVE);
					
					final HGamer winnerFinal = winner;
					
					new SQLTask() {
						@Override
						public void execute() throws SQLException {
							SQLManager.increaseStat(winnerFinal.getName(), SQLColumns.WINS_SOLO, 1);
							winnerFinal.giveExpAndSendMessage(settings.exp_winSolo, "vittoria individuale");
						}
					}.submitAsync(winner.getPlayer());
					
					endTimer.setWinnersAndStart(winner.getPlayer());
				}
			} else {

				Team winnerTeam = null;
				for (HGamer hGamer : players.values()) {
					
					if (hGamer.getStatus() == Status.TRIBUTE) {
						
						if (winnerTeam == null) {
							winnerTeam = hGamer.getAssignedMates();
							
						} else {
							
							if (winnerTeam != hGamer.getAssignedMates()) {
								return; // Già settato quindi sono almeno 2
							}
						}
					}
				}
				
				setState(GameState.END);
				SidebarManager.setTime("-");
				
				gameTimer.stopTask();
				
				if (winnerTeam == null) {
					
					logAqua("Nessun vincitore!");
					
					stopServer(ChatColor.RED + "Non c'è stato nessun vincitore, riavvio del server.");
					
				} else {
					
					for (HGamer other : UltraHardcore.getAllGamersUnsafe()) {
						if (winnerTeam.getInitialMembers().size() > 1) {
							other.sendMessage(ChatColor.GOLD + winnerTeam.initialMembersToString() + " hanno vinto la partita!");
						} else {
							other.sendMessage(ChatColor.GOLD + winnerTeam.initialMembersToString() + " ha vinto la partita!");
						}
					}
					
					for (Player player : winnerTeam.getMembers()) {
						player.setGameMode(GameMode.CREATIVE);
					}
					
					final List<HGamer> winnersFinal = Lists.newArrayList();
					for (String name : winnerTeam.getInitialMembers()) {
						Player player = Bukkit.getPlayerExact(name);
						if (player != null) {
							winnersFinal.add(getHGamer(player));
						}
					}
					
					new SQLTask() {
						@Override
						public void execute() throws SQLException {
							for (HGamer hGamer : winnersFinal) {
								SQLManager.increaseStat(hGamer.getName(), SQLColumns.WINS_TEAM, 1);
								hGamer.giveExpAndSendMessage(settings.exp_winTeam, "vittoria in team");
							}
						}
					}.submitAsync(null);
					
					endTimer.setWinnersAndStart(winnerTeam.getMembers().toArray(new Player[winnerTeam.getMembers().size()]));
				}
				
			}
		}
	}
	
	public static void stopServer(String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.kickPlayer(message + "§0§0§0");
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
			
			@Override
			public void run() {
				Bukkit.shutdown();
			}
		}, 20L);
		
	}
	
	public static Collection<HGamer> getAllGamersUnsafe() {
		return players.values();
	}
	
	public static Collection<Player> getByStatus(Status status) {
		Set<Player> match = Sets.newHashSet();
		
		for (HGamer hGamer : players.values()) {
			if (hGamer.getStatus() == status) {
				match.add(hGamer.getPlayer());
			}
		}
		
		return match;
	}
	
	public static int countTributes() {
		int count = 0;
		
		for (HGamer hGamer : players.values()) {
			if (hGamer.getStatus() == Status.TRIBUTE) {
				count++;
			}
		}
		
		return count;
	}
	
	public static Collection<HGamer> getNearTributes(Player nearWho, double distance) {
		return getNearTributes(nearWho.getLocation(), distance, nearWho);
	}
	
	public static Collection<HGamer> getNearTributes(Location loc, double distance, Player excluded) {
		
		double distanceSquared = distance * distance;
		Set<HGamer> near = Sets.newHashSet();
		
		for (HGamer hGamer : players.values()) {
			if (hGamer.getPlayer() != excluded && hGamer.getStatus() == Status.TRIBUTE && hGamer.getPlayer().getLocation().distanceSquared(loc) <= distanceSquared) {
				near.add(hGamer);
			}
		}
		
		return near;
	}

	// Scritte di errore
	public static void logPurple(String log) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + log);
	}
	
	// Scritte normali
	public static void logAqua(String log) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + log);
	}

	public static boolean isSpectatorBlacklistCommand(String command) {
		return spectatorCommandBlacklist.contains(command.toLowerCase());
	}
	
	public static <T> T randomInList(List<T> list) {
		return list.get(randomGenerator.nextInt(list.size()));
	}
	
}
