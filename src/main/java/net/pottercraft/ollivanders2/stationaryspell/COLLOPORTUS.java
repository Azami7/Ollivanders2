package net.pottercraft.ollivanders2.stationaryspell;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

/**
 * Prevents opening of target door.
 * <p>
 * https://harrypotter.fandom.com/wiki/Locking_Spell
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.COLLOPORTUS}
 */
public class COLLOPORTUS extends O2StationarySpell {
	public static final int minRadiusConfig = 5;
	public static final int maxRadiusConfig = 5;

	/**
	 * Simple constructor used for deserializing saved stationary spells at server
	 * start. Do not use to cast spell.
	 *
	 * @param plugin a callback to the MC plugin
	 */
	public COLLOPORTUS(@NotNull Ollivanders2 plugin) {
		super(plugin);

		spellType = O2StationarySpellType.COLLOPORTUS;
		permanent = true;
		this.radius = minRadius = maxRadius = minRadiusConfig;
	}

	/**
	 * Constructor
	 *
	 * @param plugin   a callback to the MC plugin
	 * @param pid      the player who cast the spell
	 * @param location the center location of the spell
	 */
	public COLLOPORTUS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location) {
		super(plugin);

		spellType = O2StationarySpellType.COLLOPORTUS;
		minRadius = minRadiusConfig;
		maxRadius = maxRadiusConfig;
		permanent = true;

		setPlayerID(pid);
		setLocation(location);
		radius = minRadius = maxRadius = minRadiusConfig;
		duration = 10;

		common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
	}

	/**
	 * No upkeep for this spell
	 */
	@Override
	public void checkEffect() {
	}

	/**
	 * Prevent doors and trapdoors being broken
	 *
	 * @param event the event
	 */
	@Override
	void doOnBlockBreakEvent(@NotNull BlockBreakEvent event) {
		Block block = event.getBlock(); // will never be null

		if (!Ollivanders2Common.doors.contains(block.getType())
				&& !Ollivanders2Common.trapdoors.contains(block.getType()))
			return;

		if (isLocationInside(block.getLocation())) {
			event.setCancelled(true);
			common.printDebugMessage("COLLOPORTUS: canceled BlockBreakEvent", null, null, false);
		}
	}

	/**
	 * Prevent doors from being broken
	 *
	 * @param event the event
	 */
	@Override
	void doOnEntityBreakDoorEvent(@NotNull EntityBreakDoorEvent event) {
		Block block = event.getBlock(); // will never be null

		if (isLocationInside(block.getLocation())) {
			event.setCancelled(true);
			common.printDebugMessage("COLLOPORTUS: canceled EntityBreakDoorEvent", null, null, false);
		}
	}

	/**
	 * Prevent door and trapdoor blocks from being changed
	 *
	 * @param event the event
	 */
	@Override
	void doOnEntityChangeBlockEvent(@NotNull EntityChangeBlockEvent event) {
		Block block = event.getBlock(); // will never be null

		if (!Ollivanders2Common.doors.contains(block.getType())
				&& !Ollivanders2Common.trapdoors.contains(block.getType()))
			return;

		if (isLocationInside(block.getLocation())) {
			event.setCancelled(true);
			common.printDebugMessage("COLLOPORTUS: canceled EntityChangeBlockEvent", null, null, false);
		}
	}

	/**
	 * Prevent doors and trapdoors from being interacted with
	 *
	 * @param event the event
	 */
	@Override
	void doOnEntityInteractEvent(@NotNull EntityInteractEvent event) {
		Block block = event.getBlock(); // will never be null

		if (!Ollivanders2Common.doors.contains(block.getType())
				&& !Ollivanders2Common.trapdoors.contains(block.getType()))
			return;

		if (isLocationInside(block.getLocation())) {
			event.setCancelled(true);
			common.printDebugMessage("COLLOPORTUS: canceled EntityInteractEvent", null, null, false);
		}
	}

	/**
	 * Prevent doors and trapdoors from being interacted with
	 *
	 * @param event the event
	 */
	@Override
	void doOnPlayerInteractEvent(@NotNull PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if ((block == null) || (!Ollivanders2Common.doors.contains(block.getType())
				&& !Ollivanders2Common.trapdoors.contains(block.getType())))
			return;

		if (isLocationInside(block.getLocation())) {
			event.setCancelled(true);
			common.printDebugMessage("COLLOPORTUS: canceled PlayerInteractEvent", null, null, false);
		}
	}

	@Override
	@NotNull
	public Map<String, String> serializeSpellData() {
		return new HashMap<>();
	}

	@Override
	public void deserializeSpellData(@NotNull Map<String, String> spellData) {
	}
}