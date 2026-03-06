package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.player.Year;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.PIERTOTUM_LOCOMOTOR;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockbukkit.mockbukkit.matcher.plugin.PluginManagerFiredEventClassMatcher.hasFiredEventInstance;

/**
 * Unit tests for the PIERTOTUM_LOCOMOTOR spell.
 *
 * <p>PIERTOTUM_LOCOMOTOR transfigures iron and snow blocks into iron and snow golems. The spell has a
 * variable duration based on caster skill, becoming permanent at mastery level ≥ 1.5 (with house/year
 * restrictions). The spawned golem is loyal to its creator—it will not attack them and will automatically
 * retaliate against attackers.</p>
 *
 * <p>Test Coverage:</p>
 * <ul>
 * <li>Spell targeting and entity transfiguration (inherited from BlockToEntityTransfigurationTest)</li>
 * <li>Success rate (always 100%)</li>
 * <li>Duration calculation and permanent transfiguration thresholds</li>
 * <li>House/year restrictions on permanent transfiguration</li>
 * <li>Event handlers for golem loyalty (no self-damage, retaliation against attackers, no targeting creator)</li>
 * </ul>
 *
 * @see net.pottercraft.ollivanders2.spell.PIERTOTUM_LOCOMOTOR for the spell implementation
 * @see BlockToEntityTransfigurationTest for inherited test framework
 */
@Isolated
public class PiertotumLocomotorTest extends BlockToEntityTransfigurationTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.PIERTOTUM_LOCOMOTOR
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PIERTOTUM_LOCOMOTOR;
    }

    /**
     * Get a valid target material for this spell (iron block).
     *
     * @return Material.IRON_BLOCK
     */
    @NotNull
    Material getValidTargetType() {
        return Material.IRON_BLOCK;
    }


    /**
     * Test spell-specific construction including success rate, duration calculation, and permanence thresholds.
     *
     * <p>Verifies:
     * <ul>
     * <li>Success rate is always 100%</li>
     * <li>Spell is temporary when caster skill &lt; spellMasteryLevel * 1.5</li>
     * <li>Duration is clamped to min/max bounds</li>
     * <li>Spell becomes permanent at skill ≥ spellMasteryLevel * 1.5 (when useYears is disabled)</li>
     * <li>Permanence is blocked when useYears is enabled and player year &lt; spell level requirement</li>
     * <li>Permanence is enabled when useYears is enabled and player year ≥ spell level requirement</li>
     * <li>Permanent transfigurations return false for isEntityTransfigured()</li>
     * </ul></p>
     */
    @Override @Test
    void spellConstructionTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        testWorld.getBlockAt(targetLocation).setType(getValidTargetType());
        PIERTOTUM_LOCOMOTOR piertotumLocomotor = (PIERTOTUM_LOCOMOTOR) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        // success rate should be 100
        assertEquals(100, piertotumLocomotor.getSuccessRate(), "success rate not set to 100");
        // permanent should be false when caster's skill is < O2Spell.spellMasteryLevel * 1.5
        assertFalse(piertotumLocomotor.isPermanent(), "spell is permanent when player skill < O2Spell.spellMasteryLevel * 1.5");
        // max duration >= duration >= min duration
        assertTrue(piertotumLocomotor.getEffectDuration() >= piertotumLocomotor.getMinDuration(), "duration < min duration");
        assertTrue(piertotumLocomotor.getEffectDuration() <= piertotumLocomotor.getMaxDuration(), "duration > max duration");
        piertotumLocomotor.kill();

        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(getValidTargetType());
        piertotumLocomotor = (PIERTOTUM_LOCOMOTOR) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 1.5);
        mockServer.getScheduler().performTicks(20);

        // spell is permanent when skill >= O2Spell.spellMasteryLevel * 1.5 and useYears is false
        assertTrue(piertotumLocomotor.isPermanent(), "spell is permanent when player skill < O2Spell.spellMasteryLevel * 1.5");
        piertotumLocomotor.kill();

        Ollivanders2.useYears = true;
        testWorld.getBlockAt(targetLocation).setType(getValidTargetType());
        piertotumLocomotor = (PIERTOTUM_LOCOMOTOR) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 1.5);
        mockServer.getScheduler().performTicks(20);

        // spell is not permanent when use years is enabled, skill level >= O2Spell.spellMasteryLevel * 1.5, but player year < spell level
        assertFalse(piertotumLocomotor.isPermanent(), "spell is permanent when use years is on and player year is too low");
        piertotumLocomotor.kill();

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        assertNotNull(o2p);
        o2p.setYear(Year.YEAR_5);
        testWorld.getBlockAt(targetLocation).setType(getValidTargetType());
        piertotumLocomotor = (PIERTOTUM_LOCOMOTOR) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 1.5);
        mockServer.getScheduler().performTicks(20);

        // spell is permanent when use year is on, skill level >= O2Spell.spellMasteryLevel * 1.5, and player year >= OWL equiv
        assertTrue(piertotumLocomotor.isPermanent(), "spell not permanent at skill level >= O2Spell.spellMasteryLevel * 1.5 and year >= 3");
        // when spell is permanent, isEntityTransfigured() returns false because they are permanently changed
        LivingEntity golem = EntityCommon.getLivingEntityAtLocation(target.getLocation());
        assertNotNull(golem);
        assertFalse(piertotumLocomotor.isEntityTransfigured(golem), "isEntityTransfigured(golem) returned true when it is permanent");
        piertotumLocomotor.kill();
    }

    /**
     * Test the golem's event handlers for loyalty behavior.
     *
     * <p>Verifies that the spawned golem:
     * <ul>
     * <li>Does not damage its creator (attacks are cancelled)</li>
     * <li>Automatically retaliates against entities that attack its creator</li>
     * <li>Does not target its creator even when approached</li>
     * <li>Can target and be attacked by other entities normally</li>
     * </ul></p>
     *
     * <p>Note: Tests must be in a single method to avoid listener registration/cleanup conflicts
     * between test instances.</p>
     */
    @Test
    void eventHandlersTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(getValidTargetType());
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        LivingEntity golem = EntityCommon.getLivingEntityAtLocation(target.getLocation());
        assertNotNull(golem);
        DamageSource damageSource = DamageSource.builder(DamageType.GENERIC)
                .withDamageLocation(caster.getLocation())  // location of the fire block
                .build();

        // prevent the golem from damaging its creator
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(golem, caster, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "EntityDamageByEntityEvent not canceled when damager is golem and damagee is caster");

        // golem attacks entities that damage its creator
        Skeleton skeleton = (Skeleton) testWorld.spawnEntity(targetLocation, EntityType.SKELETON);
        event = new EntityDamageByEntityEvent(skeleton, caster, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(5);
        assertThat(mockServer.getPluginManager(), hasFiredEventInstance(EntityTargetEvent.class));

        EntityTargetEvent targetEvent = new EntityTargetEvent(golem, caster, EntityTargetEvent.TargetReason.CLOSEST_ENTITY);
        mockServer.getPluginManager().callEvent(targetEvent);
        mockServer.getScheduler().performTicks(20);
        assertTrue(targetEvent.isCancelled(), "EntityTargetEvent not canceled when golem targeted caster");

        targetEvent = new EntityTargetEvent(skeleton, caster, EntityTargetEvent.TargetReason.CLOSEST_ENTITY);
        mockServer.getPluginManager().callEvent(targetEvent);
        mockServer.getScheduler().performTicks(20);
        assertFalse(targetEvent.isCancelled(), "EntityTargetEvent canceled when skeleton targeted caster");
    }
}
