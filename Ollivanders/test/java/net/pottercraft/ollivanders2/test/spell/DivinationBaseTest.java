package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.Divination;
import net.pottercraft.ollivanders2.spell.O2Spell;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract base test class for the divination spells (subclasses of {@link Divination}).
 *
 * <p>Divination spells produce a prophecy about a target player rather than a projectile effect. These tests confirm
 * that the spell, once its requirements are satisfied, reaches the point of creating and running an
 * {@code O2Divination} - observable as a new prophecy registered for the target. The correctness of the prophecy
 * itself is covered by the divination classes' own tests.</p>
 *
 * <p>The tests introspect each spell's requirements ({@link Divination#getItemHeld()},
 * {@link Divination#isConsumeHeld()}) so a single base implementation covers spells with any combination of
 * requirements. Concrete subclasses only supply the spell type.</p>
 *
 * <p><b>Facing-block limitation:</b> spells that require the caster to face a block (such as a crystal ball) cannot
 * have their success path tested, because {@code Player.getLineOfSight()} is unimplemented in MockBukkit 4.71.0 and
 * throws when the facing check runs. {@link #doCheckEffectTest()} is skipped for those spells - see the TODO there.</p>
 *
 * @see Divination
 * @see O2SpellTestSuper for the inherited spell testing framework
 */
abstract class DivinationBaseTest extends O2SpellTestSuper {
    /**
     * Get the block type this spell requires the caster to face, if any, by inspecting a throwaway spell instance.
     *
     * @return the required facing block type, or null if the spell has no facing requirement
     */
    private Material requiredFacingBlock() {
        O2Spell probe = Ollivanders2API.getSpells().createSpell(mockServer.addPlayer(), getSpellType(), O2PlayerCommon.rightWand);
        assertNotNull(probe, "Unable to create spell");
        return ((Divination) probe).getFacingBlock();
    }

    /**
     * Create the divination spell, optionally satisfy its held-item requirement and set its target, then activate it.
     *
     * <p>The spell is created before being added to the active spell list so the held item and target can be set up
     * before the spell resolves on its first tick.</p>
     *
     * @param world       the test world
     * @param caster      the casting player
     * @param target      the prophecy target, or null to leave the target unset
     * @param satisfyItem whether to place the required item in the caster's main hand (if the spell requires one)
     * @return the created, configured, and activated spell
     */
    @NotNull
    private Divination castDivination(@NotNull World world, @NotNull PlayerMock caster, PlayerMock target, boolean satisfyItem) {
        caster.setLocation(new Location(world, 0, 4, 0));

        O2Spell spell = Ollivanders2API.getSpells().createSpell(caster, getSpellType(), O2PlayerCommon.rightWand);
        assertNotNull(spell, "Unable to create spell");
        Divination divination = (Divination) spell;

        if (divination.getItemHeld() != null && satisfyItem)
            caster.getInventory().setItemInMainHand(divination.getItemHeld().getItem(2));

        if (target != null)
            divination.setTarget(target);

        Ollivanders2API.getSpells().addSpell(caster, spell);

        return divination;
    }

    /**
     * Verifies that the divination creates a prophecy for its target once all requirements are satisfied.
     *
     * <p>Satisfies any held-item requirement, sets a target, and confirms a prophecy about that target is registered
     * after the spell resolves. When the held item is consumed, also confirms exactly one was removed from the caster's
     * hand.</p>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        // TODO: spells with a facing-block requirement (INTUEOR, BAO_ZHONG_CHA, OVOGNOSIS) cannot have their success
        //  path tested because Player.getLineOfSight() is unimplemented in MockBukkit 4.71.0 and throws when the facing
        //  check runs. Re-enable this test for those spells once there is a way to mock line-of-sight / facing.
        Assumptions.assumeTrue(requiredFacingBlock() == null,
                getSpellType().getSpellName() + " requires facing a block, which cannot be mocked under MockBukkit (Player.getLineOfSight unimplemented)");

        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock target = mockServer.addPlayer();

        Divination divination = castDivination(testWorld, caster, target, true);

        // no prophecy should exist for this freshly-created target before the spell resolves
        assertNull(Ollivanders2API.getProphecies().getProphecyAboutPlayer(target.getUniqueId()), "target already had a prophecy");

        mockServer.getScheduler().performTicks(20);

        assertTrue(divination.isKilled(), "spell was not killed after resolving");
        assertNotNull(Ollivanders2API.getProphecies().getProphecyAboutPlayer(target.getUniqueId()), "no prophecy was created for the target");

        // a consumable held item should be reduced by exactly one (started at 2)
        if (divination.isConsumeHeld())
            assertEquals(1, caster.getInventory().getItemInMainHand().getAmount(), "consumed held item amount unexpected");
    }

    /**
     * Verifies that the divination does not produce a prophecy when a requirement is not met.
     *
     * <p>For a spell with a held-item requirement, casts holding nothing; otherwise casts with no target. In either
     * case the spell must be killed without registering a prophecy. (Spells with a facing-block requirement reach this
     * same no-prophecy outcome because the facing check fails, so they are exercised here too.)</p>
     */
    @Test
    void requirementsEnforcedTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Enforced");
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock target = mockServer.addPlayer();

        Divination divination;
        if (requiredFacingBlock() != null || hasItemRequirement())
            // a held item is required (or a facing block is) - cast without satisfying it so the spell is rejected
            divination = castDivination(testWorld, caster, target, false);
        else
            // no held/facing prerequisite - leave the target unset so the target check rejects the cast
            divination = castDivination(testWorld, caster, null, true);

        mockServer.getScheduler().performTicks(20);

        assertTrue(divination.isKilled(), "spell was not killed after rejection");
        assertNull(Ollivanders2API.getProphecies().getProphecyAboutPlayer(target.getUniqueId()), "a prophecy was created despite an unmet requirement");
    }

    /**
     * Whether this spell requires the caster to hold an item, by inspecting a throwaway spell instance.
     *
     * @return true if the spell has a held-item requirement
     */
    private boolean hasItemRequirement() {
        O2Spell probe = Ollivanders2API.getSpells().createSpell(mockServer.addPlayer(), getSpellType(), O2PlayerCommon.rightWand);
        assertNotNull(probe, "Unable to create spell");
        return ((Divination) probe).getItemHeld() != null;
    }

    /** {@inheritDoc} */
    @Override
    @Test
    void revertTest() {
        // divination spells have no revert action - the prophecy is created immediately and persists
    }
}