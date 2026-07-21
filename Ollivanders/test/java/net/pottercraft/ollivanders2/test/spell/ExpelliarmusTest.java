package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.EXPELLIARMUS;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link EXPELLIARMUS} spell, which disarms a nearby entity, flinging its held item away.
 *
 * <p>Tests verify:
 * <ul>
 * <li>Construction sets the correct spell type and magic branch</li>
 * <li>A cast at an entity holding a main-hand item removes it and drops it, and kills the spell</li>
 * <li>A cast falls back to the off-hand when the main hand is empty</li>
 * <li>An empty-handed entity is left alone</li>
 * <li>The launch velocity scales with caster skill and is limited to the spell's min/max bounds</li>
 * </ul>
 *
 * <p>A block base is placed under the target so the projectile resolves on a block when no entity is
 * disarmed. The flung item is located via a world-wide item scan because its velocity carries it off the
 * drop point.</p>
 *
 * @author Azami7
 */
public class ExpelliarmusTest extends O2SpellTestSuper {
    /**
     * The item used to arm the target in disarm tests.
     */
    private static final Material heldItemType = Material.DIAMOND_SWORD;

    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.EXPELLIARMUS;
    }

    /**
     * Whether the world contains a dropped item of the given type.
     *
     * @param world the world to scan
     * @param type  the item material to look for
     * @return true if at least one dropped item of that type exists
     */
    private boolean worldHasDroppedItem(@NotNull World world, @NotNull Material type) {
        for (Item item : world.getEntitiesByClass(Item.class)) {
            if (item.getItemStack().getType() == type)
                return true;
        }
        return false;
    }

    /**
     * Tests that the casting constructor sets the spell type and magic branch.
     */
    @Override
    @Test
    void spellConstructionTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Construction");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        EXPELLIARMUS spell = (EXPELLIARMUS) castSpell(caster, location, targetLocation);

        assertEquals(O2SpellType.EXPELLIARMUS, spell.getSpellType(), "spell type is not EXPELLIARMUS");
        assertEquals(O2MagicBranch.CHARMS, spell.getMagicBranch(), "magic branch is not CHARMS");
    }

    /**
     * Tests that a main-hand item is removed from the target and dropped, and the spell kills itself.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();
        TestCommon.createBlockBase(new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);

        PlayerMock target = mockServer.addPlayer();
        target.setLocation(targetLocation);
        EntityEquipment equipment = target.getEquipment();
        assertNotNull(equipment, "target has no equipment");
        equipment.setItemInMainHand(new ItemStack(heldItemType));

        EXPELLIARMUS spell = (EXPELLIARMUS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(spell.isKilled(), "spell did not kill itself after disarming the target");
        assertEquals(Material.AIR, target.getEquipment().getItemInMainHand().getType(), "target's main hand was not emptied");
        assertTrue(worldHasDroppedItem(testWorld, heldItemType), "disarmed item was not dropped into the world");
    }

    /**
     * Tests that the off-hand is disarmed when the main hand is empty.
     */
    @Test
    void offHandDisarmTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "OffHand");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();
        TestCommon.createBlockBase(new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);

        PlayerMock target = mockServer.addPlayer();
        target.setLocation(targetLocation);
        EntityEquipment equipment = target.getEquipment();
        assertNotNull(equipment, "target has no equipment");
        // main hand is empty by default; arm only the off-hand
        equipment.setItemInOffHand(new ItemStack(heldItemType));

        EXPELLIARMUS spell = (EXPELLIARMUS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(spell.isKilled(), "spell did not kill itself after disarming the target");
        assertEquals(Material.AIR, target.getEquipment().getItemInOffHand().getType(), "target's off hand was not emptied");
        assertTrue(worldHasDroppedItem(testWorld, heldItemType), "disarmed off-hand item was not dropped into the world");
    }

    /**
     * Tests that an empty-handed entity is not disarmed and no item is dropped.
     *
     * <p>The target holds nothing, so the spell should pass it over, resolve on the block base, and kill
     * itself without dropping any item.</p>
     */
    @Test
    void emptyHandedEntityIgnoredTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "EmptyHanded");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();
        TestCommon.createBlockBase(new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);

        PlayerMock target = mockServer.addPlayer();
        target.setLocation(targetLocation);

        EXPELLIARMUS spell = (EXPELLIARMUS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(spell.isKilled(), "spell did not kill itself after hitting the block base");
        assertFalse(worldHasDroppedItem(testWorld, heldItemType), "an item was dropped for an empty-handed entity");
    }

    /**
     * Tests that the launch velocity scales with caster skill and is limited to the spell's min/max bounds.
     *
     * <p>With the destined wand the {@code usesModifier} equals the spell experience, driving the velocity
     * formula in {@code doInitSpell()}. Verifies all three branches: very low skill limits up to
     * {@link EXPELLIARMUS#getMinVelocity()}, very high skill limits down to {@link EXPELLIARMUS#getMaxVelocity()},
     * and mid-skill is strictly between the two bounds.</p>
     */
    @Test
    void velocityScalingTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Scaling");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // experience 1 -> usesModifier 1 -> 0.1, limited up to the minimum
        EXPELLIARMUS lowSkill = (EXPELLIARMUS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 1);
        assertEquals(lowSkill.getMinVelocity(), lowSkill.getLaunchVelocity(), "low skill velocity not limited to minimum");

        // experience mastery -> usesModifier 100 -> 10, limited down to the maximum
        EXPELLIARMUS highSkill = (EXPELLIARMUS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        assertEquals(highSkill.getMaxVelocity(), highSkill.getLaunchVelocity(), "high skill velocity not limited to maximum");

        // experience 10 -> usesModifier 10 -> 1.0, between the bounds and not limited
        EXPELLIARMUS midSkill = (EXPELLIARMUS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 10);
        assertTrue(midSkill.getLaunchVelocity() > midSkill.getMinVelocity(), "mid skill velocity not above minimum");
        assertTrue(midSkill.getLaunchVelocity() < midSkill.getMaxVelocity(), "mid skill velocity not below maximum");
    }

    /**
     * EXPELLIARMUS has no revert action - disarm is applied instantly on impact.
     */
    @Override
    @Test
    void revertTest() {
        // EXPELLIARMUS does not override revert; its effect is instantaneous
    }
}