package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.HORREAT_PROTEGAT;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.MOLLIARE;
import net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link HORREAT_PROTEGAT}, which shrinks a stationary shield spell's radius. Also the base test for
 * {@link CrescereProtegatTest}: {@code CRESCERE_PROTEGAT} only flips the change direction, so it reuses these tests,
 * overriding {@link #getSpellType()} and {@link #expectShrink()}.
 * <p>
 * Each test centers a shield at the projectile's impact point and places a solid block there so the projectile stops
 * with the impact location inside the shield.
 * </p>
 *
 * @author Azami7
 */
public class HorreatProtegatTest extends O2SpellTestSuper {
    /**
     * The radius a test shield is created with. Chosen comfortably between PROTEGO_TOTALUM's min (5) and
     * max (40) so a single shrink or grow stays in range and does not destroy the shield or hit a limit.
     */
    private static final int testShieldRadius = 20;

    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.HORREAT_PROTEGAT;
    }

    /**
     * Whether the spell under test shrinks (true) or grows (false) the shield radius.
     *
     * @return true for the shrink charm, false for the grow charm
     */
    boolean expectShrink() {
        return true;
    }

    /**
     * Create a PROTEGO_TOTALUM shield owned by the given player and register it as active.
     *
     * @param casterId the UUID of the shield's creator
     * @param center   the center location of the shield
     * @return the registered shield
     */
    @NotNull
    private PROTEGO_TOTALUM addShield(@NotNull UUID casterId, @NotNull Location center) {
        PROTEGO_TOTALUM shield = new PROTEGO_TOTALUM(testPlugin, casterId, center, testShieldRadius, PROTEGO_TOTALUM.minDurationConfig);
        Ollivanders2API.getStationarySpells().addStationarySpell(shield);
        return shield;
    }

    /**
     * Tests that the casting constructor sets the spell type and magic branch.
     */
    @Override
    @Test
    void spellConstructionTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Construction");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        HORREAT_PROTEGAT spell = (HORREAT_PROTEGAT) castSpell(caster, location, targetLocation);

        assertEquals(getSpellType(), spell.getSpellType(), "wrong spell type");
        assertEquals(O2MagicBranch.CHARMS, spell.getMagicBranch(), "magic branch is not CHARMS");
    }

    /**
     * Verify the spell changes the caster's own shield radius by exactly {@link HORREAT_PROTEGAT#getSizeChange()} in
     * the expected direction (down to shrink, up to grow) and kills itself.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location impactLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PROTEGO_TOTALUM shield = addShield(caster.getUniqueId(), impactLocation);
        int startRadius = shield.getRadius();
        // solid block at the impact point so the projectile stops inside the shield and doCheckEffect fires
        impactLocation.getBlock().setType(Material.STONE);

        HORREAT_PROTEGAT spell = (HORREAT_PROTEGAT) castSpell(caster, location, impactLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(spell.isKilled(), "spell did not kill itself after hitting a block");

        int change = spell.getSizeChange();
        assertTrue(change > 0, "size change should be positive");
        if (expectShrink())
            assertEquals(startRadius - change, shield.getRadius(), "shield radius did not shrink by the size change amount");
        else
            assertEquals(startRadius + change, shield.getRadius(), "shield radius did not grow by the size change amount");
    }

    /**
     * Verify a shield owned by another player is excluded by the caster-ownership filter and its radius is unchanged.
     */
    @Test
    void notCastersShieldTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "NotCasters");
        Location location = getNextLocation(testWorld);
        Location impactLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock otherPlayer = mockServer.addPlayer();

        PROTEGO_TOTALUM shield = addShield(otherPlayer.getUniqueId(), impactLocation);
        int startRadius = shield.getRadius();
        impactLocation.getBlock().setType(Material.STONE);

        HORREAT_PROTEGAT spell = (HORREAT_PROTEGAT) castSpell(caster, location, impactLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(spell.isKilled(), "spell did not kill itself after hitting a block");
        assertEquals(startRadius, shield.getRadius(), "a shield cast by another player should not be changed");
    }

    /**
     * Verify a non-ShieldSpell stationary spell (MOLLIARE) is excluded by the {@code instanceof ShieldSpell} filter —
     * this is what protects things like the floo network and vanishing cabinets.
     */
    @Test
    void nonShieldSpellIgnoredTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "NonShield");
        Location location = getNextLocation(testWorld);
        Location impactLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        MOLLIARE molliare = new MOLLIARE(testPlugin, caster.getUniqueId(), impactLocation, MOLLIARE.minRadiusConfig, MOLLIARE.minDurationConfig);
        Ollivanders2API.getStationarySpells().addStationarySpell(molliare);
        int startRadius = molliare.getRadius();
        impactLocation.getBlock().setType(Material.STONE);

        HORREAT_PROTEGAT spell = (HORREAT_PROTEGAT) castSpell(caster, location, impactLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(spell.isKilled(), "spell did not kill itself after hitting a block");
        assertEquals(startRadius, molliare.getRadius(), "a non-shield stationary spell should not be changed");
    }

    /**
     * HORREAT_PROTEGAT has no revert action - the radius change is applied instantly on impact.
     */
    @Override
    @Test
    void revertTest() {
        // HORREAT_PROTEGAT does not override revert; its effect is instantaneous
    }
}
