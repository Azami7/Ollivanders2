package net.pottercraft.ollivanders2.book;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.potion.O2Potion;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.potion.O2Potions;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.O2Spells;

/**
 * The text and flavor text for all Ollivanders2 magic.
 */
public final class BookTexts {
	/**
	 * Common functions
	 */
	Ollivanders2Common common;

	/**
	 * The text for a "page" of a book. This may actually be longer than a real page
	 * in a MC book (and will be split accordingly to multiple pages.
	 */
	private static class BookPage {
		/**
		 * The heading of the page - spell or potion name.
		 */
		String heading;

		/**
		 * The primary text for this page.
		 */
		String text;

		/**
		 * The optional flavor text - this is usually a quote or some other tie-in to
		 * the game or the HP universe to make the book more fun to read.
		 */
		String flavorText;

		/**
		 * Constructor
		 *
		 * @param heading    the title for this page
		 * @param text       the primary text for the page
		 * @param flavorText the optional flavor text for this page
		 */
		BookPage(@NotNull String heading, @NotNull String text, @Nullable String flavorText) {
			this.heading = heading;
			this.text = text;

			this.flavorText = flavorText;
		}

		/**
		 * Get the heading for this page
		 *
		 * @return the heading
		 */
		@NotNull
		public String getHeading() {
			return heading;
		}

		/**
		 * Get the primary text for this page
		 *
		 * @return the text
		 */
		@NotNull
		public String getText() {
			return text;
		}

		/**
		 * Get the optional flavorText for this page
		 *
		 * @return the flavor text or null if none present
		 */
		@Nullable
		public String getFlavorText() {
			return flavorText;
		}
	}

	/**
	 * A map of pages and the spell/potion each covers.
	 * <p>
	 * This is a master list of the text for every spell or potion loaded so that we
	 * do not have to generate this text every time a book is created, since many
	 * spells/potions exist in more than one book.
	 */
	private final Map<String, BookPage> O2MagicTextMap = new HashMap<>();

	/**
	 * A reference to the plugin
	 */
	private final Ollivanders2 p;

	/**
	 * Constructor.
	 *
	 * @param plugin the MC plugin
	 */
	BookTexts(@NotNull Ollivanders2 plugin) {
		p = plugin;
		common = new Ollivanders2Common(p);
	}

	/**
	 * Add all spells and potions when the plugin is enabled.
	 * <p>
	 * This should be called *after* spells are loaded in to O2Spells and potions
	 * loaded in to O2Potions or the lists will be empty.
	 */
	public void onEnable() {
		// add all spells' texts
		addSpells();
		// add all potions' texts
		addPotions();
	}

	/**
	 * Add the learnable text for every registered spell projectile.
	 */
	private void addSpells() {
		for (O2SpellType spellType : O2Spells.getAllSpellTypes()) {
			O2Spell spell;
			Class<?> spellClass = spellType.getClassName();

			try {
				spell = (O2Spell) spellClass.getConstructor(Ollivanders2.class).newInstance(p);
			} catch (Exception e) {
				common.printDebugMessage("Exception trying to add book text for " + spellType.toString(), e, null,
						true);
				continue;
			}

			String text = null;
			String flavorText = null;

			try {
				text = spell.getText();
				flavorText = spell.getFlavorText();
			} catch (Exception e) {
				common.printDebugMessage("Exception getting book text for " + spellType.toString(), e, null, true);
			}

			if (text == null) {
				common.printDebugMessage("No book text for " + spellType.toString(), null, null, false);
				continue;
			}

			String name = spell.getName();

			BookPage sText = new BookPage(name, text, flavorText);
			O2MagicTextMap.put(spellType.toString(), sText);
		}
	}

	/**
	 * Add the learnable text for every potion.
	 */
	private void addPotions() {
		for (O2PotionType potionType : O2Potions.getAllPotionTypes()) {
			Class<?> potionClass = potionType.getClassName();
			O2Potion potion;

			try {
				potion = (O2Potion) potionClass.getConstructor(Ollivanders2.class).newInstance(p);
			} catch (Exception e) {
				common.printDebugMessage("Exception trying to add book text for " + potionType.toString(), e, null,
						true);
				continue;
			}

			String text = potion.getText();
			String flavorText = potion.getFlavorText();

			String name = Ollivanders2Common
					.firstLetterCapitalize(Ollivanders2Common.enumRecode(potionType.toString().toLowerCase()));

			BookPage sText = new BookPage(name, text, flavorText);
			O2MagicTextMap.put(potionType.toString(), sText);
		}
	}

	/**
	 * Get the flavor text for a specific magic.
	 *
	 * @param magic the name of the magic topic
	 * @return the flavor text for that spell or null if it has none.
	 */
	@Nullable
	String getFlavorText(@NotNull String magic) {
		String flavorText = null;

		if (O2MagicTextMap.containsKey(magic))
			flavorText = O2MagicTextMap.get(magic).getFlavorText();

		return flavorText;
	}

	/**
	 * Get the description text for a specific magic.
	 *
	 * @param magic the name of the magic topic
	 * @return the description text for this spell
	 */
	@Nullable
	String getText(@NotNull String magic) {
		String text = null;

		if (O2MagicTextMap.containsKey(magic))
			text = O2MagicTextMap.get(magic).getText();

		return text;
	}

	/**
	 * Get the printable name for a specific magic.
	 *
	 * @param magic the name of the magic topic
	 * @return the printable name for this magic
	 */
	@Nullable
	public String getName(@NotNull String magic) {
		String name = null;

		if (O2MagicTextMap.containsKey(magic))
			name = O2MagicTextMap.get(magic).getHeading();

		return name;
	}
}
