package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.spell.ET_INTERFICIAM_ANIMAM_LIGAVERIS;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the {@link ET_INTERFICIAM_ANIMAM_LIGAVERIS} spell.
 * <p>
 * The spell turns an item in the projectile's path into a {@link O2StationarySpellType#HORCRUX}
 * at a permanent cost: it spends one of the caster's collected souls and halves their maximum
 * health. These tests cover the success path and every gate the spell enforces — block hit with
 * no item, no collected soul, an already-owned horcrux, too little health to survive the halving,
 * and the wand / enchanted-item skips.
 * </p>
 * <p>
 * The spell's effects are irreversible and observable only through caster state, so each scenario
 * asserts against the soul count, the {@code MAX_HEALTH} attribute, and the set of active
 * stationary spells rather than any spell-internal field. Every method uses its own world and
 * caster so the shared MockBukkit state cannot leak across the parallel test methods.
 * </p>
 *
 * @author Azami7
 */
public class EtInterficiamAnimamLigaverisTest extends O2SpellTestSuper {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.ET_INTERFICIAM_ANIMAM_LIGAVERIS;
    }

    /**
     * No-op: the spell has no skill-scaled construction state. {@code doInitSpell} is not overridden,
     * and the WorldGuard flag is only added when WorldGuard is enabled (it is not, under test). The
     * meaningful behavior is exercised by {@link #doCheckEffectTest()}.
     */
    @Override
    @Test
    void spellConstructionTest() {
        // no special spell construction code
    }

    /**
     * Cover the block-hit failure and the full success path.
     * <p>
     * Asserts that:
     * </p>
     * <ul>
     * <li>Hitting a block with no item nearby kills the spell and creates no horcrux.</li>
     * <li>With a collected soul and a usable item in range, the spell anchors a horcrux to the item,
     *     spends the soul, and halves the caster's maximum health.</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());

        // block hit, no item: spell ends without making a horcrux
        PlayerMock blockCaster = mockServer.addPlayer();
        TestCommon.createBlockBase(new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        ET_INTERFICIAM_ANIMAM_LIGAVERIS spell = (ET_INTERFICIAM_ANIMAM_LIGAVERIS) castSpell(blockCaster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(spell.isKilled(), "spell not killed when it hit a block with no item");
        assertEquals(0, horcruxCountFor(blockCaster), "horcrux created when no item was present");

        // success: caster has a soul and full health, a usable item is in range
        PlayerMock caster = mockServer.addPlayer();
        O2Player o2Player = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        assertNotNull(o2Player);
        o2Player.addSoul();

        AttributeInstance health = caster.getAttribute(Attribute.MAX_HEALTH);
        assertNotNull(health);
        double originalHealth = health.getBaseValue();

        testWorld.dropItem(targetLocation, new ItemStack(Material.DIAMOND));
        spell = (ET_INTERFICIAM_ANIMAM_LIGAVERIS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(spell.isKilled(), "spell not killed after creating a horcrux");
        assertEquals(1, horcruxCountFor(caster), "horcrux not created on the success path");
        assertEquals(0, o2Player.getSouls(), "soul not spent when horcrux created");
        assertEquals(originalHealth / 2, health.getBaseValue(), "max health not halved when horcrux created");
    }

    /**
     * Verify the spell refuses to create a horcrux for a caster with no collected souls.
     * <p>
     * The caster has a usable item in range but zero souls; the spell must be killed without making
     * a horcrux and without altering the caster's max health.
     * </p>
     */
    @Test
    void noSoulsTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "NoSouls");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        AttributeInstance health = caster.getAttribute(Attribute.MAX_HEALTH);
        assertNotNull(health);
        double originalHealth = health.getBaseValue();

        TestCommon.createBlockBase(new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        testWorld.dropItem(targetLocation, new ItemStack(Material.DIAMOND));

        ET_INTERFICIAM_ANIMAM_LIGAVERIS spell = (ET_INTERFICIAM_ANIMAM_LIGAVERIS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(spell.isKilled(), "spell not killed when caster had no souls");
        assertEquals(0, horcruxCountFor(caster), "horcrux created for a caster with no souls");
        assertEquals(originalHealth, health.getBaseValue(), "max health changed when no horcrux was created");
    }

    /**
     * Verify a caster cannot create a second horcrux while they already own one.
     * <p>
     * The caster is given two souls so the soul check passes on both casts; the second cast must be
     * rejected by the already-owns-a-horcrux gate, leaving exactly one horcrux and the second soul
     * unspent.
     * </p>
     */
    @Test
    void existingHorcruxTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Existing");
        Location location = getNextLocation(testWorld);
        Location firstTarget = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        Location secondTarget = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ() + 10);
        PlayerMock caster = mockServer.addPlayer();

        O2Player o2Player = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        assertNotNull(o2Player);
        o2Player.addSoul();
        o2Player.addSoul();

        // first cast creates a horcrux and spends one soul
        TestCommon.createBlockBase(new Location(testWorld, firstTarget.getX(), firstTarget.getY() - 1, firstTarget.getZ()), 3);
        testWorld.dropItem(firstTarget, new ItemStack(Material.DIAMOND));
        castSpell(caster, location, firstTarget);
        mockServer.getScheduler().performTicks(20);
        assertEquals(1, horcruxCountFor(caster), "first horcrux not created");
        assertEquals(1, o2Player.getSouls(), "first cast did not spend exactly one soul");

        // second cast is rejected because the caster already owns a horcrux
        TestCommon.createBlockBase(new Location(testWorld, secondTarget.getX(), secondTarget.getY() - 1, secondTarget.getZ()), 3);
        testWorld.dropItem(secondTarget, new ItemStack(Material.DIAMOND));
        ET_INTERFICIAM_ANIMAM_LIGAVERIS spell = (ET_INTERFICIAM_ANIMAM_LIGAVERIS) castSpell(caster, location, secondTarget);
        mockServer.getScheduler().performTicks(20);

        assertTrue(spell.isKilled(), "second spell not killed when caster already owned a horcrux");
        assertEquals(1, horcruxCountFor(caster), "second horcrux created when caster already owned one");
        assertEquals(1, o2Player.getSouls(), "soul spent on a rejected second horcrux");
    }

    /**
     * Verify the spell refuses to halve health below the survivable floor.
     * <p>
     * With the caster's maximum health set to 2, the post-halving value (1) is below the spell's
     * minimum, so the cast must be rejected without spending the soul or changing the health.
     * </p>
     */
    @Test
    void insufficientHealthTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "LowHealth");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        O2Player o2Player = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        assertNotNull(o2Player);
        o2Player.addSoul();

        AttributeInstance health = caster.getAttribute(Attribute.MAX_HEALTH);
        assertNotNull(health);
        health.setBaseValue(2); // halving this yields 1, below the spell's minimum of 2

        TestCommon.createBlockBase(new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        testWorld.dropItem(targetLocation, new ItemStack(Material.DIAMOND));

        ET_INTERFICIAM_ANIMAM_LIGAVERIS spell = (ET_INTERFICIAM_ANIMAM_LIGAVERIS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(spell.isKilled(), "spell not killed when caster had too little health");
        assertEquals(0, horcruxCountFor(caster), "horcrux created when caster had too little health");
        assertEquals(1, o2Player.getSouls(), "soul spent when horcrux was rejected for low health");
        assertEquals(2, health.getBaseValue(), "max health changed when horcrux was rejected");
    }

    /**
     * Verify wands and enchanted items cannot be made into horcruxes.
     * <p>
     * The caster has a soul and full health, but the only items in range are a wand and an enchanted
     * broomstick. Both must be skipped, so the spell finds no usable item, ends on the block hit, and
     * leaves the soul unspent.
     * </p>
     */
    @Test
    void invalidItemSkippedTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "InvalidItems");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        O2Player o2Player = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        assertNotNull(o2Player);
        o2Player.addSoul();

        TestCommon.createBlockBase(new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        ItemStack wandStack = Ollivanders2API.getItems().getWands().createRandomWand();
        assertNotNull(wandStack);
        testWorld.dropItem(targetLocation, wandStack);
        ItemStack broomStack = Ollivanders2API.getItems().getItemByType(O2ItemType.BROOMSTICK, 1);
        assertNotNull(broomStack);
        testWorld.dropItem(targetLocation, broomStack);

        ET_INTERFICIAM_ANIMAM_LIGAVERIS spell = (ET_INTERFICIAM_ANIMAM_LIGAVERIS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(spell.isKilled(), "spell not killed when only wand/enchanted items were present");
        assertEquals(0, horcruxCountFor(caster), "horcrux created from a wand or enchanted item");
        assertEquals(1, o2Player.getSouls(), "soul spent when no valid item was present");
    }

    /**
     * No-op: ET_INTERFICIAM_ANIMAM_LIGAVERIS has no revert behavior — spending a soul, halving
     * health, and anchoring a horcrux are all permanent.
     */
    @Override
    @Test
    void revertTest() {
        // spell has no revert actions
    }

    /**
     * Count the active horcrux stationary spells owned by the given caster.
     *
     * @param caster the player whose horcruxes to count
     * @return the number of active {@link O2StationarySpellType#HORCRUX} spells cast by this player
     */
    private int horcruxCountFor(@NotNull PlayerMock caster) {
        int count = 0;
        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getActiveStationarySpells()) {
            if (stationarySpell.getSpellType() == O2StationarySpellType.HORCRUX
                    && stationarySpell.getCasterID().equals(caster.getUniqueId()))
                count = count + 1;
        }
        return count;
    }
}