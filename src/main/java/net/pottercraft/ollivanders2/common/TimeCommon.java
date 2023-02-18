package net.pottercraft.ollivanders2.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Times of day for minecraft servers, based on day relative tick.
 * <p>
 * Reference: https://www.digminecraft.com/game_commands/time_set_day.php
 */
public enum TimeCommon {
	MIDNIGHT(18000), DAWN(23000), MIDDAY(6000), SUNSET(12000);

	/**
	 * The day-relative tick for this enumerated time of day
	 */
	final private int gameTick;

	/**
	 * Constructor
	 *
	 * @param tick the game tick value for this enumerated time of day
	 */
	TimeCommon(int tick) {
		gameTick = tick;
	}

	/**
	 * Get the day-relative tick value for this enumerated time of day
	 *
	 * @return the day-relative tick for this time of day
	 */
	public int getTick() {
		return gameTick;
	}

	/**
	 * Get the current server time for the default world for this server.
	 *
	 * @return the time for the default world
	 */
	static public long getDefaultWorldTime() {
		return Bukkit.getServer().getWorlds().get(0).getFullTime();
	}

	/**
	 * Get the current timestamp as a string.
	 *
	 * @return timestamp in the format 2018-09-30-12-15-30
	 */
	@NotNull
	static public String getCurrentTimestamp() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date date = new Date();

		return dateFormat.format(date);
	}
}
