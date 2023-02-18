package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Shoots purple fireworks in to the air.
 */
public final class PORFYRO_ASTERI extends Pyrotechnia {
	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public PORFYRO_ASTERI(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.PORFYRO_ASTERI;
		branch = O2MagicBranch.CHARMS;

		text = "Conjures purple star fireworks in the sky.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public PORFYRO_ASTERI(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);

		spellType = O2SpellType.PORFYRO_ASTERI;
		branch = O2MagicBranch.CHARMS;

		fireworkColors = new ArrayList<>();
		fireworkColors.add(Color.PURPLE);
		fireworkType = Type.STAR;

		maxFireworks = 10;

		initSpell();
	}

	/**
	 * Set the number of fireworks that can be cast based on the user's experience.
	 */
	@Override
	void doInitSpell() {
		setNumberOfFireworks();
	}
}
