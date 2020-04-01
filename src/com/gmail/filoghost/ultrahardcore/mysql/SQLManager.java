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
package com.gmail.filoghost.ultrahardcore.mysql;

import java.sql.SQLException;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.Cleanup;
import lombok.Getter;
import wild.api.mysql.MySQL;
import wild.api.mysql.SQLResult;

public class SQLManager {
	
	@Getter private static MySQL mysql;
	
	public static void connect(String host, int port, String database, String user, String pass) throws SQLException {
		mysql = new MySQL(host, port, database, user, pass);
		mysql.connect();
	}

	public static void checkConnection() throws SQLException {
		mysql.isConnectionValid();
	}
	
	public void close() {
		if (mysql != null) {
			mysql.close();
		}
	}
	
	public static boolean playerExists(String playerName) throws SQLException {
		@Cleanup SQLResult result = mysql.preparedQuery("SELECT null FROM uhc_players where " + SQLColumns.NAME + " = ?;", playerName);
		
		return result.next();
	}
	
	public static SQLPlayerData getPlayerData(String playerName) throws SQLException {
		@Cleanup SQLResult result = mysql.preparedQuery("SELECT * FROM uhc_players where " + SQLColumns.NAME + " = ?;", playerName);
		
		SQLPlayerData output;
		
		if (result.next()) {
			output = new SQLPlayerData(
					result.getInt(SQLColumns.KILLS),
					result.getInt(SQLColumns.DEATHS),
					result.getInt(SQLColumns.WINS_SOLO),
					result.getInt(SQLColumns.WINS_TEAM),
					result.getInt(SQLColumns.EXP)
				);
			
		} else {
			output = new SQLPlayerData(0, 0, 0, 0, 0);
			createPlayerData(playerName);
		}
		
		return output;
	}
	
	public static void createPlayerData(String name) throws SQLException {
		mysql.preparedUpdate(
				"INSERT INTO uhc_players (" + SQLColumns.NAME + ", " +
											SQLColumns.KILLS + ", " +
											SQLColumns.DEATHS + ", " +
											SQLColumns.WINS_SOLO  + ", " +
											SQLColumns.WINS_TEAM  + ", " +
											SQLColumns.EXP  + ") VALUES (?, 0, 0, 0, 0, 0);", name);
	}
	

	public static void setStat(String playerName, String column, int amount) throws SQLException {
		mysql.preparedUpdate(
				"UPDATE uhc_players SET " +
				column + " = " + amount + " " +
				"WHERE " + SQLColumns.NAME + " = ?;", playerName);
	}
	
	public static void decreaseStat(String playerName, String column, int amount) throws SQLException {
		mysql.preparedUpdate(
				"UPDATE uhc_players SET " +
				column + " = " + column + " - " + amount + " " +
				"WHERE " + SQLColumns.NAME + " = ?;", playerName);
	}
	
	public static void increaseStat(String playerName, String column, int amount) throws SQLException {
		mysql.preparedUpdate(
				"UPDATE uhc_players SET " +
				column + " = " + column + " + " + amount + " " +
				"WHERE " + SQLColumns.NAME + " = ?;", playerName);
	}
	
	public static int getStat(String playerName, String column) throws SQLException {
		@Cleanup SQLResult result = mysql.preparedQuery("SELECT * FROM uhc_players where " + SQLColumns.NAME + " = ?;", playerName);
		
		if (result.next()) {
			return result.getInt(column);
		} else {
			return 0;
		}
	}
	
	public static List<SQLStat> getTop(String stat, int limit) throws SQLException {
		@Cleanup SQLResult result = mysql.query("SELECT * FROM uhc_players ORDER BY " + stat + " DESC LIMIT " + limit + ";");
			
		List<SQLStat> stats = Lists.newArrayList();
		while (result.next()) {
			stats.add(new SQLStat(result.getString(SQLColumns.NAME), result.getInt(stat)));
		}
			
		return stats;
	}
}
