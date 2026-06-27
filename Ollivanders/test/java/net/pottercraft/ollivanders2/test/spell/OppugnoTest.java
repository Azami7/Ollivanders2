package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.OPPUGNO;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link OPPUGNO} spell, which makes a nearby living entity attack the caster's target.
 *
 * <p>OPPUGNO finds the first living entity (other than the caster) near the projectile as the target,
 * then a second living entity within its attacker radius as the attacker. The attacker is launched at
 * the target and the target takes skill-scaled damage credited to the attacker.</p>
 *
 * <p>Tests verify:
 * <ul>
 * <li>Construction sets the correct spell type and magic branch</li>
 * <li>A cast with a valid target and a distinct attacker damages the target, launches the attacker,
 * and kills the spell</li>
 * <li>A cast with a target but no available attacker kills the spell without dealing damage</li>
 * <li>Damage scales with caster skill and is clamped to the spell's min/max bounds</li>
 * </ul>
 *
 * <p>To keep target/attacker selection deterministic, the target is spawned at the projectile's
 * arrival point (within {@code defaultRadius}) and the attacker is spawned farther away — outside
 * {@code defaultRadius} but inside the attacker radius — so the target loop can only pick the target
 * and the attacker loop can only pick the attacker. Most tests use a non-monster entity (cow) as the
 * attacker; {@link #monsterAttackerTargetsVictimTest()} uses a monster (zombie) to cover the spell's
 * {@code Monster}-only targeting branch.</p>
 *
 * @author Azami7
 */
public class OppugnoTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.OPPUGNO;
    }

    /**
     * Tests that the casting constructor sets the spell type and magic branch.
     */
    @Override
    @Test
    void spellConstructionTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Construction");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        OPPUGNO oppugno = (OPPUGNO) castSpell(caster, location, targetLocation);

        assertEquals(O2SpellType.OPPUGNO, oppugno.getSpellType(), "spell type is not OPPUGNO");
        assertEquals(O2MagicBranch.DARK_ARTS, oppugno.getMagicBranch(), "magic branch is not DARK_ARTS");
    }

    /**
     * Tests that the spell damages the target, launches the attacker, and kills itself.
     *
     * <p>Casts at {@code spellMasteryLevel} skill with a target at the projectile arrival point and a
     * distinct attacker 4 blocks away. Verifies the target lost health, the attacker received a non-zero
     * velocity (it was launched toward the target), and the spell killed itself after resolving.</p>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        Location attackerLocation = new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() + 4);
        PlayerMock caster = mockServer.addPlayer();

        LivingEntity target = (LivingEntity) testWorld.spawnEntity(targetLocation, EntityType.COW);
        LivingEntity attacker = (LivingEntity) testWorld.spawnEntity(attackerLocation, EntityType.COW);

        AttributeInstance maxHealthAttribute = target.getAttribute(Attribute.MAX_HEALTH);
        assertNotNull(maxHealthAttribute);
        double maxHealth = maxHealthAttribute.getValue();

        OPPUGNO oppugno = (OPPUGNO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20); // ticks per second - ample time for the projectile to reach the target

        assertTrue(oppugno.isKilled(), "spell did not kill itself after resolving");
        assertTrue(target.getHealth() < maxHealth, "target was not damaged");
        assertTrue(attacker.getVelocity().lengthSquared() > 0, "attacker was not launched toward the target");
    }

    /**
     * Tests that the spell deals no damage and kills itself when no attacker is available.
     *
     * <p>Spawns only the target near the projectile, so the attacker search finds no eligible entity
     * (the caster and the target are both excluded). Verifies the spell killed itself and the target's
     * health is unchanged.</p>
     */
    @Test
    void noAttackerTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "NoAttacker");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        LivingEntity target = (LivingEntity) testWorld.spawnEntity(targetLocation, EntityType.COW);

        AttributeInstance maxHealthAttribute = target.getAttribute(Attribute.MAX_HEALTH);
        assertNotNull(maxHealthAttribute);
        double maxHealth = maxHealthAttribute.getValue();

        OPPUGNO oppugno = (OPPUGNO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(oppugno.isKilled(), "spell did not kill itself when no attacker was available");
        assertEquals(maxHealth, target.getHealth(), "target was damaged despite there being no attacker");
    }

    /**
     * Tests that an entity beyond the attacker radius is not used as an attacker.
     *
     * <p>Spawns the target near the projectile and a second eligible entity 15 blocks away — beyond the
     * spell's attacker radius (10). The attacker search should reject the out-of-range entity, leaving no
     * attacker, so the spell kills itself and the target takes no damage. This verifies the radius bound
     * actually filters candidates rather than the spell finding any entity anywhere.</p>
     */
    @Test
    void attackerOutOfRangeTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "OutOfRange");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        // candidate attacker sits 15 blocks away, outside the attacker radius (10), so it must be ignored
        Location farLocation = new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() + 15);
        PlayerMock caster = mockServer.addPlayer();

        LivingEntity target = (LivingEntity) testWorld.spawnEntity(targetLocation, EntityType.COW);
        testWorld.spawnEntity(farLocation, EntityType.COW);

        AttributeInstance maxHealthAttribute = target.getAttribute(Attribute.MAX_HEALTH);
        assertNotNull(maxHealthAttribute);
        double maxHealth = maxHealthAttribute.getValue();

        OPPUGNO oppugno = (OPPUGNO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(oppugno.isKilled(), "spell did not kill itself when the only candidate attacker was out of range");
        assertEquals(maxHealth, target.getHealth(), "target was damaged by an out-of-range attacker");
    }

    /**
     * Tests that a monster attacker is set to actively target the victim.
     *
     * <p>Uses a zombie (a {@link org.bukkit.entity.Monster}) as the attacker so the spell's
     * monster-only branch runs, setting the zombie's target to the victim. Verifies the zombie's target
     * is the spell's target, the target took damage, and the spell killed itself.</p>
     */
    @Test
    void monsterAttackerTargetsVictimTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "MonsterAttacker");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        Location attackerLocation = new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() + 4);
        PlayerMock caster = mockServer.addPlayer();

        LivingEntity target = (LivingEntity) testWorld.spawnEntity(targetLocation, EntityType.COW);
        Mob attacker = (Mob) testWorld.spawnEntity(attackerLocation, EntityType.ZOMBIE);

        AttributeInstance maxHealthAttribute = target.getAttribute(Attribute.MAX_HEALTH);
        assertNotNull(maxHealthAttribute);
        double maxHealth = maxHealthAttribute.getValue();

        OPPUGNO oppugno = (OPPUGNO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(oppugno.isKilled(), "spell did not kill itself after resolving");
        assertNotNull(attacker.getTarget(), "monster attacker was not given a target");
        assertEquals(target.getUniqueId(), attacker.getTarget().getUniqueId(), "monster attacker was not set to target the victim");
        assertTrue(target.getHealth() < maxHealth, "target was not damaged");
    }

    /**
     * Tests that exactly one attacker is launched when several eligible candidates are in range.
     *
     * <p>Spawns the target plus two eligible attacker candidates, both within the attacker radius and
     * outside {@code defaultRadius}. The spell should pick a single attacker (the loop breaks on the
     * first match), so exactly one of the two candidates is launched (has a non-zero velocity) and the
     * target is damaged once. Which specific candidate is chosen depends on iteration order, so the test
     * only asserts that precisely one was launched.</p>
     */
    @Test
    void singleAttackerTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "MultiAttacker");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        // two candidate attackers at distinct spots, both outside defaultRadius (1.5) and inside the attacker radius (10)
        Location candidateLocationA = new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() + 4);
        Location candidateLocationB = new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() + 6);
        PlayerMock caster = mockServer.addPlayer();

        LivingEntity target = (LivingEntity) testWorld.spawnEntity(targetLocation, EntityType.COW);
        LivingEntity candidateA = (LivingEntity) testWorld.spawnEntity(candidateLocationA, EntityType.COW);
        LivingEntity candidateB = (LivingEntity) testWorld.spawnEntity(candidateLocationB, EntityType.COW);

        AttributeInstance maxHealthAttribute = target.getAttribute(Attribute.MAX_HEALTH);
        assertNotNull(maxHealthAttribute);
        double maxHealth = maxHealthAttribute.getValue();

        OPPUGNO oppugno = (OPPUGNO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(oppugno.isKilled(), "spell did not kill itself after resolving");
        assertTrue(target.getHealth() < maxHealth, "target was not damaged");

        int launched = 0;
        if (candidateA.getVelocity().lengthSquared() > 0)
            launched++;
        if (candidateB.getVelocity().lengthSquared() > 0)
            launched++;
        assertEquals(1, launched, "expected exactly one candidate to be launched as the attacker");
    }

    /**
     * Tests that the damage scales with caster skill and is clamped to the spell's min/max bounds.
     *
     * <p>With the destined wand the {@code usesModifier} equals the spell experience, driving the
     * damage formula in {@code doInitSpell()}. Verifies all three branches:
     * <ul>
     * <li>Very low skill clamps up to {@link OPPUGNO#getMinDamage()}</li>
     * <li>Very high skill clamps down to {@link OPPUGNO#getMaxDamage()}</li>
     * <li>Mid skill produces an un-clamped value strictly between the two bounds</li>
     * </ul>
     *
     * <p>The casts only read each freshly-constructed spell's computed damage with no scheduler
     * advancement, so they are kept in a single method per the parallel-execution model.</p>
     */
    @Test
    void damageScalingTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Scaling");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // experience 1 -> usesModifier 1 -> 0.05 damage, clamped up to the minimum
        OPPUGNO lowSkill = (OPPUGNO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 1);
        assertEquals(lowSkill.getMinDamage(), lowSkill.getDamage(), "low skill damage not clamped to minimum");

        // experience mastery * 2 -> usesModifier 200 -> 10 damage, clamped down to the maximum
        OPPUGNO highSkill = (OPPUGNO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        assertEquals(highSkill.getMaxDamage(), highSkill.getDamage(), "high skill damage not clamped to maximum");

        // mastery skill -> usesModifier 100 -> 5 damage, between the bounds and not clamped
        OPPUGNO midSkill = (OPPUGNO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        assertTrue(midSkill.getDamage() > midSkill.getMinDamage(), "mid skill damage not above minimum");
        assertTrue(midSkill.getDamage() < midSkill.getMaxDamage(), "mid skill damage not below maximum");
    }

    /**
     * OPPUGNO has no revert action - the damage and launch are applied instantly on cast.
     */
    @Override
    @Test
    void revertTest() {
        // OPPUGNO does not override revert; its effect is instantaneous
    }
}
