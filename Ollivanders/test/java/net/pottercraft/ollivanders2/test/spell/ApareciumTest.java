package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.book.O2BookType;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.APARECIUM;
import net.pottercraft.ollivanders2.spell.CELATUM;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link APARECIUM} spell, which reveals text concealed by {@link CELATUM}.
 *
 * <p>Verifies spell functionality for revealing hidden book text and proper handling of invalid targets.</p>
 *
 * @author Azami7
 * @see APARECIUM
 * @see CELATUM
 */
@Isolated
public class ApareciumTest extends O2SpellTestSuper {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.APARECIUM;
    }

    /** {@inheritDoc} */
    @Override
    @Test
    void spellConstructionTest() {
    }

    /**
     * Verifies that APARECIUM correctly reveals a CELATUM-concealed book.
     *
     * <p>Tests the complete reveal workflow:</p>
     * <ul>
     * <li>Casts CELATUM to conceal a written book's text</li>
     * <li>Casts APARECIUM at the enchanted book</li>
     * <li>Confirms the revealed book has the original author, title, and page count</li>
     * <li>Confirms the revealed book is no longer enchanted</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);

        // add a written book item to the world
        ItemStack writtenBookStack = Ollivanders2API.getBooks().getBookByType(O2BookType.MAGICK_MOSTE_EVILE);
        assertNotNull(writtenBookStack);
        testWorld.dropItem(targetLocation, writtenBookStack);

        // conceal its text with Celatum
        CELATUM celatum = (CELATUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel, O2SpellType.CELATUM);
        mockServer.getScheduler().performTicks(20);

        // get the enchanted book item and confirm celatum was added
        Item concealedBook = EntityCommon.getItemAtLocation(celatum.getLocation());
        assertNotNull(concealedBook);
        assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(concealedBook));

        // cast aparecium at the enchanted book
        APARECIUM aparecium = (APARECIUM) castSpell(caster, location, concealedBook.getLocation());
        mockServer.getScheduler().performTicks(20);

        Item revealedBook = EntityCommon.getItemAtLocation(aparecium.getLocation());
        assertNotNull(revealedBook, "revealed book not dropped by the spell");

        BookMeta originalBookMeta = (BookMeta) writtenBookStack.getItemMeta();
        assertNotNull(originalBookMeta);
        BookMeta revealedBookMeta = (BookMeta) revealedBook.getItemStack().getItemMeta();
        assertNotNull(revealedBookMeta);
        assertFalse(Ollivanders2API.getItems().enchantedItems.isEnchanted(revealedBook), "revealed book is still enchanted");

        // verify book has been fully restored
        assertEquals(originalBookMeta.getAuthor(), revealedBookMeta.getAuthor(), "revealed book author unexpected");
        assertEquals(originalBookMeta.getTitle(), revealedBookMeta.getTitle(), "revealed book title unexpected");
        assertEquals(originalBookMeta.getPageCount(), revealedBookMeta.getPageCount(), "revealed book page count unexpected");
    }

    /**
     * Verifies that APARECIUM correctly ignores non-CELATUM items.
     *
     * <p>Tests two scenarios:</p>
     * <ul>
     * <li>Non-enchanted written book: APARECIUM should not affect the item</li>
     * <li>CELATUM-enchanted non-book item (broomstick): APARECIUM should not attempt to reveal it</li>
     * </ul>
     */
    @Test
    void invalidTargetTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);

        // test on written book that is not enchanted
        ItemStack writtenBookStack = Ollivanders2API.getBooks().getBookByType(O2BookType.MAGICK_MOSTE_EVILE);
        assertNotNull(writtenBookStack);
        Item writtenBook = testWorld.dropItem(targetLocation, writtenBookStack);

        APARECIUM aparecium = (APARECIUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(aparecium.hasHitTarget());
        assertFalse(writtenBook.isDead()); // if aparecium tried to remove an enchantment, it would have removed the original book and dropped a new one

        // test on enchanted item that is not a book
        ItemStack broomstickStack = O2ItemType.BROOMSTICK.getItem(1);
        assertNotNull(broomstickStack);
        Item broomstick = testWorld.dropItem(targetLocation, broomstickStack);
        aparecium = (APARECIUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(aparecium.hasHitTarget());
        assertFalse(broomstick.isDead(), "aparecium removed enchanted item that is not a book"); // if aparecium tried to remove an enchantment, it would have removed the original book and dropped a new one
        assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(broomstick), "aparecium removed enchantment from item that is not a book");
    }

    /** {@inheritDoc} */
    @Override
    @Test
    void revertTest() {
    }
}
