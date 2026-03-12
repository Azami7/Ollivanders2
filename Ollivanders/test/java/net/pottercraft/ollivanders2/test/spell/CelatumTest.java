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
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the CELATUM spell.
 *
 * <p>CELATUM is a concealment charm that enchants written books to hide their text content.
 * When a CELATUM-enchanted book is picked up, the enchanted items system reveals the hidden text.
 * The spell stores book content as enchantment arguments and replaces the original pages with blank
 * pages, preserving the author and title metadata.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.CELATUM for the spell implementation
 * @see ItemEnchantTest for inherited test framework
 */
@Isolated
public class CelatumTest extends ItemEnchantTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.CELATUM
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.CELATUM;
    }

    /**
     * Get a valid item type for CELATUM testing.
     *
     * @return Material.WRITTEN_BOOK
     */
    @Override
    @NotNull
    Material getValidItemType() {
        return Material.WRITTEN_BOOK;
    }

    /**
     * Get an invalid item type for CELATUM testing.
     *
     * <p>CELATUM only enchants written books, so other materials like STICK are invalid.</p>
     *
     * @return Material.STICK (invalid for this spell)
     */
    @Override
    @Nullable
    Material getInvalidItemType() {
        return Material.STICK;
    }

    /**
     * Create an invalid O2ItemType for CELATUM testing.
     *
     * @return a Book item stack
     */
    @Override
    @NotNull
    ItemStack createInvalidItem() {
        return new ItemStack(Material.BOOK, 1);
    }

    /**
     * Test item stack splitting (not applicable for books).
     *
     * <p>Written books typically appear as single items, not stacks, so this test is skipped.</p>
     */
    @Override
    @Test
    void enchantStackWithMultipleItemsTest() {
        // this cannot happen with written books
    }

    /**
     * Test that enchantment arguments store the book's text content.
     *
     * <p>Verifies that CELATUM extracts and stores the book's pages as enchantment arguments,
     * using the page delimiter to separate pages. The stored text is later revealed when
     * the enchanted book is picked up.</p>
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
     * Test that enchanted books have their pages cleared.
     *
     * <p>Verifies that CELATUM removes all text from the book while preserving author and title
     * metadata. The blank book is dropped as a replacement at the same location due to client
     * caching issues with direct item alteration.</p>
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
