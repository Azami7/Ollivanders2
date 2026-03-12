package net.pottercraft.ollivanders2.test.spell;

import net.kyori.adventure.text.Component;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.ALIQUAM_FLOO;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.ALIQUAM_FLOO} spell, which registers fireplaces
 * with the Floo Network.
 *
 * <p>Verifies spell construction, fireplace registration, invalid fireplace handling, and duplicate name
 * prevention.</p>
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.spell.ALIQUAM_FLOO
 * @see net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO
 */
@Isolated
public class AliquamFlooTest extends O2SpellTestSuper {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.ALIQUAM_FLOO;
    }

    /**
     * Verifies that fire is not in the projectile pass-through materials, since the spell must stop at fire blocks.
     */
    @Override
    @Test
    void spellConstructionTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        ALIQUAM_FLOO aliquamFloo = (ALIQUAM_FLOO) castSpell(caster, location, targetLocation);
        assertFalse(aliquamFloo.getProjectilePassThroughMaterials().contains(Material.FIRE));
    }

    /**
     * Verifies that casting at a valid fireplace registers it with the Floo Network and that casting at the same
     * location a second time does not create a duplicate stationary spell.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        String flooName = "spell test one";
        createFireplace(targetLocation, flooName);

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertFalse(Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(targetLocation, O2StationarySpellType.ALIQUAM_FLOO).isEmpty(), "floo stationary spell not created at target location");
        assertTrue(net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO.getFlooFireplaceNames().contains(flooName), "floo is not registered in the network");

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertEquals(1, Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(targetLocation, O2StationarySpellType.ALIQUAM_FLOO).size(), "second floo stationary spell added at same location");
    }

    /**
     * Helper method that creates a fireplace structure at the given location with a named sign above the fire.
     *
     * @param location the location for the fire block
     * @param flooName the name to write on the sign
     */
    void createFireplace(Location location, String flooName) {
        Block fire = location.getBlock();
        fire.setType(Material.FIRE);

        Block signBlock = fire.getRelative(BlockFace.UP);
        signBlock.setType(Material.ACACIA_WALL_SIGN);

        Sign sign = (Sign) signBlock.getState();
        sign.getSide(Side.FRONT).line(0, Component.text(flooName));

        fire.getRelative(BlockFace.DOWN).setType(Material.NETHERRACK);
        fire.getRelative(BlockFace.EAST).setType(Material.STONE);
        signBlock.getRelative(BlockFace.EAST).setType(Material.STONE);
    }

    /**
     * Verifies that casting at a non-fire block does not create a floo fireplace and that casting at a fireplace
     * with a blank sign sends the caster a failure message.
     */
    @Test
    void invalidFireplaceTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        testWorld.getBlockAt(targetLocation).setType(Material.STONE);
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(targetLocation, O2StationarySpellType.ALIQUAM_FLOO).isEmpty(), "floo fireplace created at invalid block type");
        String message = caster.nextMessage();
        assertNull(message, "caster received error message when casting at invalid target block type");

        createFireplace(targetLocation, "");
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(targetLocation, O2StationarySpellType.ALIQUAM_FLOO).isEmpty(), "floo fireplace created with blank name");
        message = caster.nextMessage();
        assertNotNull(message, "caster did not get failure message for blank sign");
    }

    /**
     * Verifies that attempting to register a fireplace with a name already in use fails with a message to the caster,
     * and that registering with a different name at the same location succeeds.
     */
    @Test
    void duplicateFlooNameTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 20, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        String flooName = "dupe test one";
        createFireplace(targetLocation, flooName);

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO.getFlooFireplaceNames().contains(flooName));

        Location secondLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        createFireplace(secondLocation, flooName);

        castSpell(caster, location, secondLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(secondLocation, O2StationarySpellType.ALIQUAM_FLOO).isEmpty(), "created floo fireplace with name already in use");
        assertTrue(net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO.getFlooFireplaceNames().contains(flooName), "floo 1 no longer in network");

        String message = caster.nextMessage();
        assertNotNull(message, "caster did not get failure message for duplicate floo name");

        String floo2Name = "dupe test two";
        createFireplace(secondLocation, floo2Name);
        castSpell(caster, location, secondLocation);
        mockServer.getScheduler().performTicks(20);
        assertFalse(Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(secondLocation, O2StationarySpellType.ALIQUAM_FLOO).isEmpty(), "failed to create second floo fireplace");
        assertTrue(net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO.getFlooFireplaceNames().contains(floo2Name), "floo 2 not in network");
    }

    /** {@inheritDoc} */
    @Override
    @Test
    void revertTest() {
    }
}
