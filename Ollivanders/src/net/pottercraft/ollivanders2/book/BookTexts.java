package net.pottercraft.ollivanders2.book;

import java.util.Map;
import java.util.HashMap;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.potion.O2Potion;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.potion.O2Potions;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2Spells;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * Caches the book page content for every Ollivanders2 spell and potion. The text is generated once at plugin
 * enable and reused, since the same spell or potion can appear in many books.
 *
 * @author Azami7
 */
public final class BookTexts {
    Ollivanders2Common common;

    /**
     * The content for one spell or potion. May exceed a single Minecraft book page; it is split across pages when the
     * book is assembled.
     */
    private static class BookPage {
        /**
         * The spell or potion name.
         */
        String heading;

        String text;

        /**
         * Optional quote or tie-in to the game or HP universe, shown before the main text.
         */
        String flavorText;

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
     * The cached page for each spell or potion, keyed by its enum name.
     */
    private final Map<String, BookPage> O2MagicTextMap = new HashMap<>();

    private final Ollivanders2 p;

    /**
     * @param plugin a callback to the plugin
     */
    BookTexts(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(p);
    }

    /**
     * Add all spells and potions when the plugin is enabled.
     *
     * <p>This should be called *after* spells are loaded in to O2Spells and potions loaded in to O2Potions or the lists will
     * be empty.</p>
     */
    public void onEnable() {
        addSpells();
        addPotions();
    }

    /**
     * Loads and caches the text for every registered spell.
     * <p>
     * Instantiates each spell type and extracts its display name, description text, and flavor text
     * for storage in the text cache. Skips spells with missing or invalid text.
     * </p>
     */
    private void addSpells() {
        for (O2SpellType spellType : O2Spells.getAllSpellTypes()) {
            O2Spell spell;
            Class<?> spellClass = spellType.getClassName();

            try {
                spell = (O2Spell) spellClass.getConstructor(Ollivanders2.class).newInstance(p);
            }
            catch (Exception e) {
                common.printDebugMessage("BookTexts: exception trying to add book text for " + spellType, e, null, true);
                continue;
            }

            String text = spell.getText();
            String flavorText = spell.getFlavorText();

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
            }
            catch (Exception e) {
                common.printDebugMessage("BookTexts: exception trying to add book text for " + potionType, e, null, true);
                continue;
            }

            String text = potion.getText();
            String flavorText = potion.getFlavorText();

            String name = potion.getName();

            BookPage sText = new BookPage(name, text, flavorText);
            O2MagicTextMap.put(potionType.toString(), sText);
        }
    }

    /**
     * Retrieves the flavor text for a spell or potion by its enum name.
     *
     * @param magic the spell or potion enum name (e.g., "EXPELLIARMUS")
     * @return the flavor text for that spell/potion, or null if none is present or not found
     */
    @Nullable
    String getFlavorText(@NotNull String magic) {
        if (O2MagicTextMap.containsKey(magic))
            return O2MagicTextMap.get(magic).getFlavorText();
        else
            return null;
    }

    /**
     * Retrieves the description text for a spell or potion by its enum name.
     *
     * @param magic the spell or potion enum name (e.g., "EXPELLIARMUS")
     * @return the description text for that spell/potion, or null if not found
     */
    @Nullable
    String getText(@NotNull String magic) {
        if (O2MagicTextMap.containsKey(magic))
            return O2MagicTextMap.get(magic).getText();
        else
            return null;
    }

    /**
     * Retrieves the display name (heading) for a spell or potion by its enum name.
     *
     * @param magic the spell or potion enum name (e.g., "EXPELLIARMUS")
     * @return the display name for that spell/potion, or null if not found
     */
    @Nullable
    public String getName(@NotNull String magic) {
        if (O2MagicTextMap.containsKey(magic))
            return O2MagicTextMap.get(magic).getHeading();
        else
            return null;
    }
}
