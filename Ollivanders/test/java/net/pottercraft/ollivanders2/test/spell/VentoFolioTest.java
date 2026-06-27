package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.VENTO_FOLIO;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link VENTO_FOLIO} spell, which grants the caster unassisted flight.
 *
 * <p>VENTO_FOLIO is a no-projectile spell that, on a successful roll, applies the
 * {@link net.pottercraft.ollivanders2.effect.FLYING} effect to the caster for a skill-scaled
 * duration. Both the success chance and the duration are derived from the caster's skill in
 * {@code doInitSpell()}.</p>
 *
 * <p>Tests verify:
 * <ul>
 * <li>Construction sets the correct spell type, magic branch, and no-projectile flag</li>
 * <li>A guaranteed-success cast (mastery-level skill) applies flight to the caster, the spell
 * kills itself, and the effect expires after the calculated duration</li>
 * <li>The success rate and duration scale correctly across skill tiers and their boundaries</li>
 * </ul>
 *
 * @author Azami7
 */
public class VentoFolioTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.VENTO_FOLIO;
    }

    /**
     * Tests that the casting constructor configures the spell's static state.
     *
     * <p>Verifies the spell type and magic branch are set, and that the spell is flagged as
     * no-projectile so the base class stops the projectile and applies the effect directly to the
     * caster rather than launching a moving spell.</p>
     */
    @Override
    @Test
    void spellConstructionTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Construction");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        VENTO_FOLIO ventoFolio = (VENTO_FOLIO) castSpell(caster, location, targetLocation);

        assertEquals(O2SpellType.VENTO_FOLIO, ventoFolio.getSpellType(), "spell type is not VENTO_FOLIO");
        assertEquals(O2MagicBranch.DARK_ARTS, ventoFolio.getMagicBranch(), "magic branch is not DARK_ARTS");
        assertTrue(ventoFolio.isNoProjectile(), "VENTO_FOLIO should be a no-projectile spell");
    }

    /**
     * Tests that a successful cast grants the caster flight and that the effect expires after its duration.
     *
     * <p>Casts at {@code spellMasteryLevel * 2} experience, which forces a 100% success rate (so the
     * outcome is deterministic without controlling the random roll), and verifies:
     * <ul>
     * <li>The caster does not have the FLYING effect before the spell runs</li>
     * <li>After the spell ticks, it has killed itself and the caster has the FLYING effect</li>
     * <li>The FLYING effect is no longer present after the calculated flight duration elapses</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(caster.getUniqueId(), O2EffectType.FLYING),
                "caster already has FLYING effect before cast");

        // mastery * 2 experience guarantees a 100% success rate, making the success roll deterministic
        VENTO_FOLIO ventoFolio = (VENTO_FOLIO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);

        mockServer.getScheduler().performTicks(20); // ticks per second - one second is ample for the no-projectile spell to resolve
        assertTrue(ventoFolio.isKilled(), "spell did not kill itself after casting");
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(caster.getUniqueId(), O2EffectType.FLYING),
                "caster does not have FLYING effect after successful cast");

        // advance past the granted flight duration and confirm the effect has expired
        long durationTicks = (long) ventoFolio.getDurationInSeconds() * Ollivanders2Common.ticksPerSecond;
        mockServer.getScheduler().performTicks(durationTicks);
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(caster.getUniqueId(), O2EffectType.FLYING),
                "caster still has FLYING effect after duration expired");
    }

    /**
     * Tests that the success rate and flight duration scale correctly with caster skill.
     *
     * <p>With the destined wand the {@code usesModifier} equals the spell experience, so each cast
     * drives a known skill level. The asserted tier boundaries match the ladders in
     * {@code calculateSuccessRate()} and {@code calculateDuration()}:
     * <ul>
     * <li>200 (mastery * 2): 100% success, 100s flight</li>
     * <li>100: 50% success (half the modifier), 50s flight</li>
     * <li>50: 25% success, 30s flight</li>
     * <li>25: 10% success, 10s flight</li>
     * <li>10: 5% success, 10s flight</li>
     * <li>5: 5% success (floor), 5s flight</li>
     * </ul>
     *
     * <p>All casts share read-only state (each reads its own freshly-constructed spell with no
     * scheduler advancement), so they are kept in a single method per the parallel-execution model.</p>
     */
    @Test
    void successRateAndDurationTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Scaling");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        assertSuccessRateAndDuration(caster, location, targetLocation, O2Spell.spellMasteryLevel * 2, 100, 100);
        assertSuccessRateAndDuration(caster, location, targetLocation, 100, 50, 50);
        assertSuccessRateAndDuration(caster, location, targetLocation, 50, 25, 30);
        assertSuccessRateAndDuration(caster, location, targetLocation, 25, 10, 10);
        assertSuccessRateAndDuration(caster, location, targetLocation, 10, 5, 10);
        assertSuccessRateAndDuration(caster, location, targetLocation, 5, 5, 5);
    }

    /**
     * Casts VENTO_FOLIO at the given experience and asserts the resulting success rate and duration.
     *
     * @param caster              the player casting the spell
     * @param location            the location to cast from
     * @param targetLocation      the location to face (must differ from {@code location} so experience is set)
     * @param experience          the spell experience to cast with (equals usesModifier when using the destined wand)
     * @param expectedSuccessRate the success rate percent the cast should produce
     * @param expectedDuration    the flight duration in seconds the cast should produce
     */
    private void assertSuccessRateAndDuration(@NotNull PlayerMock caster, @NotNull Location location, @NotNull Location targetLocation,
                                              int experience, int expectedSuccessRate, int expectedDuration) {
        VENTO_FOLIO ventoFolio = (VENTO_FOLIO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, experience);

        assertEquals(expectedSuccessRate, ventoFolio.getSuccessRate(),
                "wrong success rate at experience " + experience);
        assertEquals(expectedDuration, ventoFolio.getDurationInSeconds(),
                "wrong duration at experience " + experience);
    }

    /**
     * VENTO_FOLIO has no revert action - the granted flight effect ages out on its own.
     */
    @Override
    @Test
    void revertTest() {
        // VENTO_FOLIO does not override revert; the FLYING effect expires via its own duration
    }
}