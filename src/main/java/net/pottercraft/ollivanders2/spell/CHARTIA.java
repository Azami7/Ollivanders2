package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.divination.O2DivinationType;
import net.pottercraft.ollivanders2.item.O2ItemType;

/**
 * Cartomancy is the art of reading cards to gain insight into future events.
 * http://harrypotter.wikia.com/wiki/Cartomancy
 *
 * @author Azami7
 * @since 2.2.9
 */
public final class CHARTIA extends Divination {
	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public CHARTIA(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.CHARTIA;
		divinationType = O2DivinationType.CARTOMANCY;
		branch = O2MagicBranch.DIVINATION;

		flavorText = new ArrayList<>() {
			{
				add("\"‘Two of spades: conflict,’ she murmured, as she passed the place where Harry crouched, hidden. ‘Seven of spades: an ill omen. Ten of spades: violence. Knave of spades: a dark young man, possibly troubled, one who dislikes the questioner –‘\"");
				add("\"Harry proceeded through deserted corridors, though he had to step hastily behind a statue when Professor Trelawney appeared round a corner, muttering to herself as she shuffled a pack of dirty-looking playing cards, reading them as she walked.\"");
			}
		};

		text = "A true seer may gain insight in to future events by accurately reading playing cards.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public CHARTIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);
		spellType = O2SpellType.CHARTIA;
		divinationType = O2DivinationType.CARTOMANCY;
		branch = O2MagicBranch.DIVINATION;

		itemHeld = O2ItemType.PLAYING_CARDS;
		itemHeldString = "playing cards";

		initSpell();
	}
}
