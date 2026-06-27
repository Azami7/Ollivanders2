package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.FELIFORS;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link FELIFORS}.
 *
 * <p>Verifies the cauldron-into-cat transfiguration. Material allowlisting, the cauldron-to-cat mapping,
 * and entity death handling are covered by the inherited {@link ItemToEntityTransfigurationTest} suite.</p>
 *
 * @see FELIFORS
 */
public class FeliforsTest extends ItemToEntityTransfigurationTest {
    /**
     * @return FELIFORS spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.FELIFORS;
    }

    /**
     * @return CAULDRON, the material type that Felifors transfigures
     */
    @NotNull
    Material getValidMaterialType() {
        return Material.CAULDRON;
    }

    /**
     * @return FEATHER, a material not in the transfiguration map
     */
    @Nullable
    Material getInvalidMaterialType() {
        return Material.FEATHER;
    }

    /**
     * Test that a cauldron is transfigured into a cat.
     */
    @Test
    void transfiguresCauldronToCatTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        testWorld.dropItem(targetLocation, new ItemStack(getValidMaterialType(), 1));

        FELIFORS felifors = (FELIFORS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        mockServer.getScheduler().performTicks(20);

        assertTrue(felifors.isTransfigured(), "cauldron was not transfigured");
        Entity cat = felifors.getTransfiguredEntity();
        assertEquals(EntityType.CAT, cat.getType(), "transfigured entity was not a cat");
        assertTrue(cat instanceof Cat, "transfigured entity was not a Cat instance");
    }
}