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
 * The concealment charm - used to hide secret messages in books
 * <p>
 * {@link net.pottercraft.ollivanders2.item.enchantment.CELATUM}
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Concealing_charms">https://harrypotter.fandom.com/wiki/Concealing_charms</a>
 */
public final class CELATUM extends ItemEnchant {
    public static String pageDelimiter = "##PAGE##";

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
     * Set the enchantment arg string to be the text of the book
     *
     * @param bookItem the item to enchant
     */
    @Override
    protected void initEnchantmentArgs(ItemStack bookItem) {
        BookMeta bookMeta = (BookMeta) bookItem.getItemMeta();
        if (bookMeta == null)
            return;

        StringBuilder argBuilder = new StringBuilder();
        for (String page : bookMeta.getPages()) {
            argBuilder.append(page);
            argBuilder.append(pageDelimiter);
        }

        args = argBuilder.toString();
        common.printDebugMessage("Celatum args = " + args, null, null, false);
    }

    /**
     * Delete the pages in the book so it is blank
     *
     * @param item the item to affect
     */
    protected Item alterItem(Item item) {
        ItemStack bookItem = item.getItemStack();

        BookMeta bookMeta = (BookMeta) bookItem.getItemMeta();
        if (bookMeta == null)
            return null;

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
