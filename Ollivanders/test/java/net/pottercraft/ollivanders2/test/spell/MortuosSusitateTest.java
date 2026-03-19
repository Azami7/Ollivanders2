package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.MORTUOS_SUSCITATE;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link MORTUOS_SUSCITATE}.
 *
 * <p>Tests inferi-specific behavior: the per-player inferi count limit based on skill level,
 * count decrement on kill/revert, and the ability to create more inferi after existing ones
 * are removed.</p>
 *
 * @see MORTUOS_SUSCITATE
 */
public class MortuosSusitateTest extends ItemToEntityTransfigurationTest {
    /**
     * Returns the spell type being tested.
     *
     * @return MORTUOS_SUSCITATE spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.MORTUOS_SUSCITATE;
    }

    /**
     * @return ROTTEN_FLESH, the only material that can be transfigured into an Inferius
     */
    @NotNull
    Material getValidMaterialType() {
        return Material.ROTTEN_FLESH;
    }

    /**
     * @return FEATHER, a material not in the transfiguration map
     */
    @Nullable
    Material getInvalidMaterialType() {
        return Material.FEATHER;
    }

    /**
     * Test the per-player inferi count limit. Verifies that:
     *
     * <ol>
     * <li>A player at skill level 5 can create 1 Inferius</li>
     * <li>A player at skill level 5 cannot create a 2nd Inferius</li>
     * <li>After killing the first Inferius, the player can create a new one</li>
     * <li>A player at skill level 25 can have 2 Inferi simultaneously</li>
     * </ol>
     */
    @Test
    void numberOfInferiTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        testWorld.dropItem(targetLocation, new ItemStack(getValidMaterialType(), 1));

        MORTUOS_SUSCITATE mortuosSuscitate1 = (MORTUOS_SUSCITATE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 5);
        mockServer.getScheduler().performTicks(20);
        assertTrue(mortuosSuscitate1.isTransfigured());

        Location target2Location = new Location(testWorld, location.getX(), location.getY(), location.getZ() + 10);
        testWorld.dropItem(target2Location, new ItemStack(getValidMaterialType(), 1));
        MORTUOS_SUSCITATE mortuosSuscitate2 = (MORTUOS_SUSCITATE) castSpell(caster, location, target2Location, O2PlayerCommon.rightWand, 5);
        mockServer.getScheduler().performTicks(20);
        assertFalse(mortuosSuscitate2.isTransfigured(), "Caster able to make 2nd inferi at skill level 5");

        mortuosSuscitate1.kill();
        testWorld.dropItem(targetLocation, new ItemStack(getValidMaterialType(), 1));
        mortuosSuscitate2 = (MORTUOS_SUSCITATE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 5);
        mockServer.getScheduler().performTicks(20);
        assertTrue(mortuosSuscitate2.isTransfigured(), "caster not able to make 2nd inferi after first inferi removed");

        MORTUOS_SUSCITATE mortuosSuscitate3 = (MORTUOS_SUSCITATE) castSpell(caster, location, target2Location, O2PlayerCommon.rightWand, 25);
        mockServer.getScheduler().performTicks(20);
        assertTrue(mortuosSuscitate3.isTransfigured(), "caster not able to make 2nd inferi at skill level 25");
    }
}
