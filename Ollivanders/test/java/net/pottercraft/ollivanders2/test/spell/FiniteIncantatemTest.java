package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.book.O2BookType;
import net.pottercraft.ollivanders2.effect.BABBLING;
import net.pottercraft.ollivanders2.effect.LYCANTHROPY;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.FINITE_INCANTATEM;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the {@link FINITE_INCANTATEM} spell.
 * <p>
 * Verifies FINITE_INCANTATEM's general counter-spell behavior across its three target families:
 * player {@link net.pottercraft.ollivanders2.effect.O2Effect O2Effects}, vanilla Minecraft potion
 * effects, and enchanted items. Confirms the level gate — anything at or below the spell's
 * {@code BEGINNER} level is removed while higher-level effects and enchantments (Lycanthropy,
 * Volatus) are left intact — and the {@code effectsRemaining} budget, which scales with caster
 * skill and caps how many effects a single cast can dispel from one target.
 * </p>
 *
 * @author Azami7
 */
public class FiniteIncantatemTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.FINITE_INCANTATEM;
    }

    /**
     * Verify the {@code effectsRemaining} budget: 1 at zero experience, 2 at level 40 ({@code floor(usesModifier/20)}),
     * and {@link FINITE_INCANTATEM#maxEffects} at three times mastery.
     */
    @Override
    @Test
    void spellConstructionTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        FINITE_INCANTATEM finiteIncantatem = (FINITE_INCANTATEM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);
        assertEquals(1, finiteIncantatem.getEffectsRemaining(), "unexpected number of targets at spell level 0");
        finiteIncantatem.kill();

        finiteIncantatem = (FINITE_INCANTATEM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 40);
        assertEquals(2, finiteIncantatem.getEffectsRemaining(), "unexpected number of targets at spell level 40");

        finiteIncantatem = (FINITE_INCANTATEM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 3);
        assertEquals(FINITE_INCANTATEM.maxEffects, finiteIncantatem.getEffectsRemaining(), "number of targets > max targets at 3x mastery");
    }

    /**
     * Cover the player-target paths: a block hit kills the spell; an O2Effect at or below the spell's level (Babbling)
     * is removed while one above it (Lycanthropy) is left intact; a vanilla potion effect at or below the level
     * (Hunger) is removed. Item targets are covered by {@link #itemEffectsTest()}.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);

        FINITE_INCANTATEM finiteIncantatem = (FINITE_INCANTATEM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(finiteIncantatem.isKilled(), "spell not killed when it hit a solid block");

        // O2Effect that is <= FINITE_INCANTATEM spell level
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(targetLocation);
        Ollivanders2API.getPlayers().playerEffects.addEffect(new BABBLING(testPlugin, 100, false, target.getUniqueId()));
        mockServer.getScheduler().performTicks(5);
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffects(target.getUniqueId()));

        finiteIncantatem = (FINITE_INCANTATEM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(finiteIncantatem.isKilled(), "spell not killed when it hit a target player");
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffects(target.getUniqueId()), "Babbling effect not removed from player");

        // O2Effect that is > FINITE_INCANTATEM spell level
        Ollivanders2API.getPlayers().playerEffects.addEffect(new LYCANTHROPY(testPlugin, 100, true, target.getUniqueId()));
        mockServer.getScheduler().performTicks(5);
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffects(target.getUniqueId()));

        finiteIncantatem = (FINITE_INCANTATEM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(finiteIncantatem.isKilled(), "spell not killed when it hit a target player");
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffects(target.getUniqueId()), "Lycanthropy effect removed by finite incantatum");

        // Potion effects
        target.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.HUNGER, 100, 1));
        mockServer.getScheduler().performTicks(5);
        assertTrue(target.hasPotionEffect(PotionEffectType.HUNGER));

        finiteIncantatem = (FINITE_INCANTATEM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(finiteIncantatem.isKilled(), "spell not killed when it hit a target player");
        assertFalse(target.hasPotionEffect(PotionEffectType.HUNGER), "Hunger potion effect not removed from target player");
    }

    /**
     * Verify a Celatum enchantment (at or below the spell's level) is stripped from a book while a Volatus enchantment
     * (EXPERT, above the spell's BEGINNER level) is left intact. No cursed-enchantment case yet — no curse enchantment
     * is low enough level for the spell to target.
     */
    @Test
    void itemEffectsTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        ItemStack writtenBookStack = Ollivanders2API.getBooks().getBookByType(O2BookType.HARMONIOUS_CONNECTIONS);
        assertNotNull(writtenBookStack);
        Item book = testWorld.dropItem(targetLocation, writtenBookStack);
        castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel, O2SpellType.CELATUM);
        mockServer.getScheduler().performTicks(20);
        assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(book));

        FINITE_INCANTATEM finiteIncantatem = (FINITE_INCANTATEM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(finiteIncantatem.isKilled(), "spell not killed when it hit item target");
        assertFalse(Ollivanders2API.getItems().enchantedItems.isEnchanted(book), "Celatum enchantment not removed");
        book.remove();

        ItemStack broomStack = Ollivanders2API.getItems().getItemByType(O2ItemType.BROOMSTICK, 1);
        assertNotNull(broomStack);
        Item broom = testWorld.dropItem(targetLocation, broomStack);
        assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(broom));

        finiteIncantatem = (FINITE_INCANTATEM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(broom), "Volatus enchantment removed");
        broom.remove();
    }

    /**
     * Verify the {@code effectsRemaining} budget caps how many effects a single cast removes.
     * <p>
     * A player is given three same-level potion effects and hit at level 40 (budget of 2): exactly
     * two are removed. A second cast at mastery (budget of {@link FINITE_INCANTATEM#maxEffects})
     * removes all three.
     * </p>
     */
    @Test
    void multipleEffectsTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);

        PlayerMock target = mockServer.addPlayer();
        target.setLocation(targetLocation);

        target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 100, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 1));

        FINITE_INCANTATEM finiteIncantatem = (FINITE_INCANTATEM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 40);
        assertEquals(2, finiteIncantatem.getEffectsRemaining(), "unexpected targets remaining for spell level 40");

        mockServer.getScheduler().performTicks(20);
        int removed = 0;
        if (!target.hasPotionEffect(PotionEffectType.HUNGER))
            removed = removed + 1;
        if (!target.hasPotionEffect(PotionEffectType.LUCK))
            removed = removed + 1;
        if (!target.hasPotionEffect(PotionEffectType.NIGHT_VISION))
            removed = removed + 1;

        assertEquals(2, removed, "unexpected number of effects removed");

        target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 100, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 1));

        finiteIncantatem = (FINITE_INCANTATEM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertFalse(target.hasPotionEffect(PotionEffectType.HUNGER), "Hunger not removed from target player");
        assertFalse(target.hasPotionEffect(PotionEffectType.LUCK), "Luck not removed from target player");
        assertFalse(target.hasPotionEffect(PotionEffectType.NIGHT_VISION), "Night vision not removed from target player");
    }

    /**
     * No-op: FINITE_INCANTATEM has no revert behavior — dispelling effects is permanent.
     */
    @Override
    @Test
    void revertTest() {

    }
}
