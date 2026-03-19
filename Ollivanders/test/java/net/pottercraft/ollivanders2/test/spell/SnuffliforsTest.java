package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.SNUFFLIFORS;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link SNUFFLIFORS}.
 *
 * <p>Tests Snufflifors-specific behavior: verifies the transfigured fox is set to baby.</p>
 *
 * @see SNUFFLIFORS
 */
public class SnuffliforsTest extends ItemToEntityTransfigurationTest {
    /**
     * Returns the spell type being tested.
     *
     * @return SNUFFLIFORS spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.SNUFFLIFORS;
    }

    /**
     * @return BOOK, one of the material types that can be transfigured by Snufflifors
     */
    @NotNull
    Material getValidMaterialType() {
        return Material.BOOK;
    }

    /**
     * @return FEATHER, a material not in the transfiguration map
     */
    @Nullable
    Material getInvalidMaterialType() {
        return Material.FEATHER;
    }

    /**
     * Test that the transfigured entity is a baby fox.
     */
    @Test
    void customizeEntityTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        testWorld.dropItem(targetLocation, new ItemStack(getValidMaterialType(), 1));

        SNUFFLIFORS snufflifors = (SNUFFLIFORS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        mockServer.getScheduler().performTicks(20);

        assertTrue(snufflifors.isTransfigured());
        Entity fox = snufflifors.getTransfiguredEntity();
        assertEquals(EntityType.FOX, fox.getType());
        assertFalse(((Ageable) fox).isAdult(), "Fox was not set to baby");
    }
}
