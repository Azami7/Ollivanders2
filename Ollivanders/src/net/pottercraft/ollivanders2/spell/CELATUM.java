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
 * CELATUM - The Concealing Charm.
 *
 * <p>Enchants a written book so its visible pages are blanked while the original text is preserved in the item's
 * enchantment args; the author and title are left intact. {@link APARECIUM} reverses the concealment and restores
 * the pages.</p>
 *
 * @see net.pottercraft.ollivanders2.item.enchantment.CELATUM the enchantment that powers this spell
 * @see APARECIUM
 * @see <a href="https://harrypotter.fandom.com/wiki/Concealing_charms">Harry Potter Wiki - Concealing Charms</a>
 */
public final class CELATUM extends ItemEnchant {
    /**
     * The page delimiter used when the books pages get compressed to one string
     */
    public static final String pageDelimiter = "##PAGE##";

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
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
     * Store the book's text as enchantment args, concatenating all pages separated by the {@link #pageDelimiter}
     * token. {@link APARECIUM} later splits this back into pages to restore the book.
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
     * Blank the enchanted book's pages, preserving author and title. The concealed text remains in the enchantment
     * args for {@link APARECIUM} to restore.
     *
     * <p>The book is re-created and re-dropped rather than edited in place to work around client-side item caching.</p>
     *
     * @param item the enchanted book Item entity
     * @return the blanked book (a replacement Item dropped at the same location)
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
