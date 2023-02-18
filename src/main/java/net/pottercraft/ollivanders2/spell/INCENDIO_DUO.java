package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Creates a larger incendio that strafes and doubles the radius and duration.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Incendio_Duo_Spell
 */
public final class INCENDIO_DUO extends IncendioSuper {
	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public INCENDIO_DUO(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.INCENDIO_DUO;
		branch = O2MagicBranch.CHARMS;

		flavorText = new ArrayList<>() {
			{
				add("A Stronger Fire-Making Charm");
			}
		};

		text = "Incendio Duo will burn blocks and entities it passes by. It's radius is twice that of Incendio and it's duration half.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public INCENDIO_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);

		spellType = O2SpellType.INCENDIO_DUO;
		branch = O2MagicBranch.CHARMS;

		location.add(vector);
		strafe = true;
		blockRadius = 2;
		radius = 2;
		durationModifier = 2;

		initSpell();
	}
}