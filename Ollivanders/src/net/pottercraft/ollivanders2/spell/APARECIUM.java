package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The Revealing Charm revals text that has been magically concealed.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Revealing_Charm">https://harrypotter.fandom.com/wiki/Revealing_Charm</a>
 */
public final class APARECIUM extends O2Spell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public APARECIUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.APARECIUM;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"The Revealing Charm will reveal invisible ink and messages hidden by magical means. Simply tap a book or parchment with your wand and any hidden message will be revealed. This spell is more than sufficient to overcome the basic concealing charms and so is a favourite of parents and teachers alike.\"");
            add("The Revealing Charm");
        }};

        text = "Reveals invisible book text.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public APARECIUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.APARECIUM;
        branch = O2MagicBranch.CHARMS;

        initSpell();
    }

    /**
     * If any enchanted books are in the spell projectile area, reveal the text.
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitTarget()) {
            kill();
            return;
        }

        List<Item> items = getItems(1.5);
        for (Item item : items) {
            if (Ollivanders2API.getItems().enchantedItems.isEnchanted(item)) {
                if (Ollivanders2API.getItems().enchantedItems.getEnchantmentType(item.getItemStack()) == ItemEnchantmentType.CELATUM) {
                    // remove the Celatum enchantment from the book
                    ItemStack disenchantedItemStack = Ollivanders2API.getItems().enchantedItems.removeEnchantment(item);

                    if (Ollivanders2API.getItems().enchantedItems.isEnchanted(disenchantedItemStack)) {
                        common.printDebugMessage("APARECIUM.doCheckEffect: item still enchanted after removeEnchantment", null, null, true);
                    }

                    ItemStack newBook = revealText(disenchantedItemStack);

                    if (newBook == null) {
                        common.printDebugMessage("APARECIUM: failed to reveal text", null, null, false);
                        return;
                    }

                    // drop the new book
                    World world = item.getWorld();
                    world.dropItem(location, newBook);

                    // remove the old book
                    item.remove();

                    // kill the spell
                    kill();
                    return;
                }
            }
        }
    }

    /**
     * Reveal the hidden text and remove the concealment enchantment from this book.
     *
     * @param bookItem the concealed book
     */
    @Nullable
    private ItemStack revealText(ItemStack bookItem) {
        if (bookItem.getType() != Material.WRITTEN_BOOK) {
            common.printDebugMessage("APARECIUM: celatum cast on a non-book item", null, null, true);
            return null;
        }

        BookMeta bookMeta = (BookMeta) bookItem.getItemMeta();
        if (bookMeta == null) { // this should never happen
            return null;
        }

        String bookText = Ollivanders2API.getItems().enchantedItems.getEnchantmentArgs(bookItem);
        if (bookText == null) {
            common.printDebugMessage("APARECIUM: celatum enchantment args are empty", null, null, true);
            return null;
        }

        // rebuild the pages
        String[] pages = bookText.split(CELATUM.pageDelimiter);
        bookMeta.setPages(pages);

        // make a new item because altering the original doesn't seem to work - seems the server or client caches something
        ItemStack newBook = new ItemStack(Material.WRITTEN_BOOK);
        newBook.setItemMeta(bookMeta);

        return newBook;
    }
}