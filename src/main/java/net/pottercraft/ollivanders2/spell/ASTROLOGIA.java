package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.divination.O2DivinationType;

/**
 * Astrology is the system of using the relative positions of celestial bodies
 * (including the sun, moon, and planets) to try to predict future events or
 * gain insight into personality, relationships, and health.
 * http://harrypotter.wikia.com/wiki/Astrology
 *
 * @author Azami7
 * @since 2.2.9
 */
public final class ASTROLOGIA extends Divination {
	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public ASTROLOGIA(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.ASTROLOGIA;
		divinationType = O2DivinationType.ASTROLOGY;
		branch = O2MagicBranch.DIVINATION;

		flavorText = new ArrayList<>() {
			{
				add("\"Professor Trelawney did astrology with us! Mars causes accidents and burns and things like that, and when it makes an angle to Saturn, like now, that means people need to be extra careful when handling hot things.\" -Parvati Patil");
				add("\"My dears, it is time for use to consider the stars.\" -Sybill Trelawny");
				add("\"The movements of the planets and the mysterious portents they reveal only to those who understand the steps of the celestial dance. Human destiny may be deciphered by the planetary rays, which intermingle...\" -Sybill Trelawny");
			}
		};

		text = "Through the study of the position of celestial bodies, one may divine future events or gain insight in to the health or relationships of others.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public ASTROLOGIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);
		spellType = O2SpellType.ASTROLOGIA;
		divinationType = O2DivinationType.ASTROLOGY;
		branch = O2MagicBranch.DIVINATION;

		initSpell();
	}
}
