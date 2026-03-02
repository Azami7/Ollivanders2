package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * CELATUM - The Concealing Charm spell.
 *
 * <p>Enchants written books to hide their text content. When a CELATUM-enchanted book is picked up,
 * the enchanted items system reveals the hidden text. The original book's pages are replaced with
 * blank pages, preserving the author and title while concealing the content.</p>
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Target: Written books only (WRITTEN_BOOK material)</li>
 * <li>Enchantment argument: Book content (all pages concatenated with page delimiters)</li>
 * <li>Item alteration: Original book pages replaced with blank pages</li>
 * <li>Classification: Charms</li>
 * </ul>
 *
 * @see net.pottercraft.ollivanders2.item.enchantment.CELATUM the enchantment that powers this spell
 * @see <a href="https://harrypotter.fandom.com/wiki/Concealing_charms">Harry Potter Wiki - Concealing Charms</a>
 */
public final class CELATUM extends ItemEnchant {
    /**
     * The page delimiter used when the books pages get compressed to one string
     */
    public static final String pageDelimiter = "##PAGE##";

    /**
     * Constructor for generating spell information.
     *
     * <p>Initializes the spell with flavor text and description. Do not use to cast the spell.
     * Use the full constructor with player and wand parameters instead.</p>
     *
     * @param plugin the Ollivanders2 plugin
     */
    public CELATUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.CELATUM;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Concealing Charm");
        }};

        text = "Creates magically hidden text in books.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public CELATUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.CELATUM;
        branch = O2MagicBranch.CHARMS;
        enchantmentType = ItemEnchantmentType.CELATUM;

        // set to only work for written books
        itemTypeAllowlist.add(Material.WRITTEN_BOOK);

        initSpell();
    }

    /**
     * Extract and store the book's text content as enchantment arguments.
     *
     * <p>Concatenates all pages from the written book into a single string, separating pages
     * with the {@link #pageDelimiter} token. This content is stored and later revealed when
     * the enchanted book is picked up.</p>
     *
     * @param bookItem the written book to extract text from
     */
    @Override
    protected void createEnchantmentArgs(ItemStack bookItem) {
        BookMeta bookMeta = (BookMeta) bookItem.getItemMeta();
        if (bookMeta == null)
            return;

        StringBuilder argBuilder = new StringBuilder();
        for (String page : bookMeta.getPages()) {
            argBuilder.append(page);
            argBuilder.append(pageDelimiter);
        }

        enchantmentArgs = argBuilder.toString();
        common.printDebugMessage("Celatum args = " + enchantmentArgs, null, null, false);
    }

    /**
     * Clear the enchanted book's pages and return a blank book.
     *
     * <p>Removes all text content from the enchanted book, leaving it blank while preserving
     * the author and title metadata. The book is re-created and dropped at the same location
     * to work around client caching issues. The concealed text is preserved in the enchantment
     * arguments and revealed when the book is picked up.</p>
     *
     * @param item the enchanted book Item entity
     * @return the blank book (it may be a replacement item if dropped at same location)
     */
    @Override
    @NotNull
    public Item alterItem(Item item) {
        ItemStack bookItem = item.getItemStack();

        BookMeta bookMeta = (BookMeta) bookItem.getItemMeta();
        if (bookMeta == null)
            return item;

        bookMeta.setPages(new ArrayList<>());

        // make a new item because altering the original doesn't seem to work - seems the server or client caches something
        ItemStack newBook = new ItemStack(Material.WRITTEN_BOOK);
        newBook.setItemMeta(bookMeta);

        World world = item.getWorld();
        Location location = item.getLocation();

        // remove the original item
        item.remove();

        return world.dropItem(location, newBook);
    }
}
