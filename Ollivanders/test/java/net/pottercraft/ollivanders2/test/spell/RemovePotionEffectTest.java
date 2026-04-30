package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.RemovePotionEffect;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base test class for spells that remove potion effects from targets.
 *
 * <p>Tests the core functionality of potion effect removal spells, including self-targeting,
 * non-self-targeting projectile behavior, and multi-target radius behavior. Subclasses provide
 * the spell type and the potion effect type to verify.</p>
 */
abstract public class RemovePotionEffectTest extends O2SpellTestSuper {
    /**
     * Get the potion effect type that the spell under test removes.
     *
     * @return the potion effect type to verify removal of
     */
    abstract PotionEffectType getValidEffectType();

    /**
     * Test that the spell correctly removes potion effects from targets.
     *
     * <p>Verifies three behaviors based on spell configuration:</p>
     *
     * <ul>
     * <li>Self-targeting: if targetsSelf, caster's effect is removed and spell must be noProjectile</li>
     * <li>Non-self-targeting: effect is removed from a target entity but not from the caster</li>
     * <li>Multi-target: if affectsMultiple, effects are removed from all entities within the
     *     effect radius but not from entities outside it</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);

        caster.addPotionEffect(new PotionEffect(getValidEffectType(), 1000, 1));
        assertTrue(caster.hasPotionEffect(getValidEffectType()));

        RemovePotionEffect removePotionEffect = (RemovePotionEffect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        // make sure the effect is targetsSelf it is also noProjectile
        if (removePotionEffect.doesTargetSelf())
            assertTrue(removePotionEffect.isNoProjectile(), "spell is targets self but also does a projectile, this is an invalid combination");
        mockServer.getScheduler().performTicks(20);
        assertTrue(removePotionEffect.isKilled(), "spell not killed");
        if (removePotionEffect.doesTargetSelf())
            assertFalse(caster.hasPotionEffect(getValidEffectType()), getValidEffectType().toString() + " not removed from caster when spell targets self");
        else
            assertTrue(caster.hasPotionEffect(getValidEffectType()), getValidEffectType().toString() + " removed from caster when spell does not target self");

        if (!removePotionEffect.doesTargetSelf()) {
            PlayerMock target = mockServer.addPlayer();
            target.setLocation(targetLocation);
            target.addPotionEffect(new PotionEffect(getValidEffectType(), 1000, 1));
            assertTrue(target.hasPotionEffect(getValidEffectType()));

            mockServer.getScheduler().performTicks(20);
            assertTrue(removePotionEffect.isKilled(), "spell not killed when target hit");
            assertFalse(target.hasPotionEffect(getValidEffectType()), getValidEffectType().toString() + " not removed from target");
        }

        if (removePotionEffect.doesAffectMultiple()) {
            removePotionEffect = (RemovePotionEffect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);

            PlayerMock player1 = mockServer.addPlayer();
            PlayerMock player2 = mockServer.addPlayer();
            PlayerMock player3 = mockServer.addPlayer();

            Location baseLocation;
            if (removePotionEffect.isNoProjectile())
                baseLocation = location;
            else
                baseLocation = targetLocation;

            player1.setLocation(baseLocation.getBlock().getRelative(BlockFace.EAST).getLocation());
            player2.setLocation(baseLocation.getBlock().getRelative(BlockFace.WEST).getLocation());
            // set player 3 outside the effect radius
            player3.setLocation(new Location(testWorld, baseLocation.getX() + removePotionEffect.getEffectRadius() + 3, baseLocation.getY(), baseLocation.getZ()));

            player1.addPotionEffect(new PotionEffect(getValidEffectType(), 1000, 1));
            assertTrue(player1.hasPotionEffect(getValidEffectType()));
            player2.addPotionEffect(new PotionEffect(getValidEffectType(), 1000, 1));
            assertTrue(player2.hasPotionEffect(getValidEffectType()));
            player3.addPotionEffect(new PotionEffect(getValidEffectType(), 1000, 1));
            assertTrue(player3.hasPotionEffect(getValidEffectType()));

            mockServer.getScheduler().performTicks(20);
            assertTrue(removePotionEffect.isKilled());
            assertFalse(player1.hasPotionEffect(getValidEffectType()), getValidEffectType().toString() + " not removed from player1");
            assertFalse(player2.hasPotionEffect(getValidEffectType()), getValidEffectType().toString() + " not removed from player2");
            assertTrue(player3.hasPotionEffect(getValidEffectType()), getValidEffectType().toString() + " removed from player3");
        }
    }

    /**
     * Revert test (empty for potion effect removal spells).
     *
     * <p>Potion effect removal spells do not have revert actions.</p>
     */
    @Override
    @Test
    void revertTest() {
        // no revert actions
    }
}
