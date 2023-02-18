package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.sk89q.worldguard.protection.flags.Flags;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Pyrosvestiras is a fire extinquishing spell if you decide that you want to
 * put out that thing you lit on fire.
 */
public class PYROSVESTIRAS extends BlockTransfiguration {
	private static final int minRadiusConfig = 1;
	private static final int maxRadiusConfig = 15;

	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public PYROSVESTIRAS(Ollivanders2 plugin) {
		super(plugin);

		branch = O2MagicBranch.CHARMS;
		spellType = O2SpellType.PYROSVESTIRAS;

		flavorText = new ArrayList<>() {
			{
				add("A charm that extinguishes fires. Most commonly employed by Dragonologists.");
				add("The Extinguishing Charm");
			}
		};

		text = "A spell that turns fire into air and campfires in to logs.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public PYROSVESTIRAS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);
		spellType = O2SpellType.PYROSVESTIRAS;
		branch = O2MagicBranch.CHARMS;

		successMessage = "A fire is doused by the water.";
		failureMessage = "Nothing seems to happen.";

		permanent = true;
		minRadius = minRadiusConfig;
		maxRadius = maxRadiusConfig;
		radiusModifier = 0.25; // 25% of usesModifier

		// allow list only fire blocks
		materialAllowList.add(Material.FIRE);
		materialAllowList.add(Material.CAMPFIRE);

		// do not pass through fire blocks
		projectilePassThrough.remove(Material.FIRE);

		// what type blocks transfigure in to for this spell
		transfigurationMap.put(Material.FIRE, Material.AIR);
		transfigurationMap.put(Material.CAMPFIRE, Material.OAK_LOG);

		// world guard flags
		if (Ollivanders2.worldGuardEnabled)
			worldGuardFlags.add(Flags.BUILD);

		initSpell();
	}

	/**
	 * Set the radius for this spell based on the caster's experience.
	 */
	@Override
	void doInitSpell() {
		if (usesModifier > 50)
			radius = 10;
		else if (usesModifier < 10)
			radius = 1;
		else
			radius = (int) (usesModifier / 10);
	}
}
