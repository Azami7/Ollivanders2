package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.book.O2BookType;
import net.pottercraft.ollivanders2.spell.CELATUM;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.CELATUM}. Extends {@link ItemEnchantTest} for the shared
 * enchantment tests. CELATUM blanks a written book's pages, stashing the original text in the enchantment args for
 * {@link net.pottercraft.ollivanders2.spell.APARECIUM} to restore.
 */
public class CelatumTest extends ItemEnchantTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.CELATUM;
    }

    @Override
    @NotNull
    Material getValidItemType() {
        return Material.WRITTEN_BOOK;
    }

    @Override
    @Nullable
    Material getInvalidItemType() {
        return Material.STICK;
    }

    /**
     * @return a plain {@link Material#BOOK} (not a WRITTEN_BOOK), which CELATUM cannot enchant
     */
    @Override
    @NotNull
    ItemStack createInvalidItem() {
        return new ItemStack(Material.BOOK, 1);
    }

    /**
     * Overridden to a no-op: written books never stack, so there is no multi-item stack to enchant.
     */
    @Override
    @Test
    void enchantStackWithMultipleItemsTest() {
        // this cannot happen with written books
    }

    /**
     * Verify CELATUM stores the book's pages as enchantment args, separated by the page delimiter.
     */
    @Override
    @Test
    void createEnchantmentArgsTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        ItemStack writtenBookStack = Ollivanders2API.getBooks().getBookByType(O2BookType.TETRABIBLIOS);
        assertNotNull(writtenBookStack);
        testWorld.dropItem(targetLocation, writtenBookStack);

        CELATUM celatum = (CELATUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(celatum.isKilled());
        String enchantmentArgs = celatum.getEnchantmentArgs();
        assertFalse(enchantmentArgs.isEmpty(), "Enchantment args not set");
        assertTrue(enchantmentArgs.contains(CELATUM.pageDelimiter), "Enchantment args did not contain page delimiter");
    }

    /**
     * Verify CELATUM blanks the book's pages while preserving its author and title.
     */
    @Override
    @Test
    void alterItemTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        ItemStack writtenBookStack = Ollivanders2API.getBooks().getBookByType(O2BookType.TETRABIBLIOS);
        assertNotNull(writtenBookStack);
        Item originalBook = testWorld.dropItem(targetLocation, writtenBookStack);
        BookMeta originalMeta = (BookMeta) writtenBookStack.getItemMeta();
        assertNotNull(originalMeta);

        CELATUM celatum = (CELATUM) castSpell(caster, location, targetLocation);
        Item alteredItem = celatum.alterItem(originalBook);

        BookMeta bookMeta = (BookMeta) alteredItem.getItemStack().getItemMeta();
        assertNotNull(bookMeta, "altered book meta is null");
        assertTrue(bookMeta.getPages().isEmpty(), "getPages() did not return an empty list");
        assertEquals(originalMeta.getAuthor(), bookMeta.getAuthor(), "Author not expected");
        assertEquals(originalMeta.getTitle(), bookMeta.getTitle(), "Title not expected");
    }
}
