package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.ReparoBase;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract base test class for the Reparo family of repair charms ({@link ReparoBase} subclasses).
 *
 * <p>Provides shared coverage for repairing a damaged item and for the skip paths that protect items the
 * spell should not touch. Concrete subclasses supply the spell type (via {@link #getSpellType()}) and a
 * damageable material whose maximum durability is large enough to test that spell's repair amount
 * (via {@link #getRepairableMaterial()}).</p>
 *
 * @see ReparoBase
 * @see O2SpellTestSuper for the inherited spell testing framework
 */
abstract class ReparoBaseTest extends O2SpellTestSuper {
    /**
     * Get a damageable material this spell can repair.
     *
     * <p>Should have a maximum durability at least as large as the spell's repair amount so a fully damaged
     * item can be repaired without driving the resulting damage negative.</p>
     *
     * @return a damageable material for the repair tests
     */
    @NotNull
    abstract Material getRepairableMaterial();

    /**
     * Read the durability damage of a dropped item.
     *
     * @param item the dropped item entity to inspect
     * @return the current damage value of the item's meta
     */
    private int getDamage(@NotNull Item item) {
        return ((Damageable) item.getItemStack().getItemMeta()).getDamage();
    }

    /**
     * Create an item stack of the given material with a specific amount of durability damage.
     *
     * @param material the (damageable) material to create
     * @param damage   the durability damage to apply
     * @return the damaged item stack
     */
    @NotNull
    private ItemStack createDamagedItem(@NotNull Material material, int damage) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        ((Damageable) meta).setDamage(damage);
        stack.setItemMeta(meta);

        return stack;
    }

    /**
     * Verifies that the spell repairs a damaged item by its skill-derived repair amount.
     *
     * <p>Drops a fully damaged tool at the projectile endpoint, casts the spell, and confirms the item's
     * remaining damage dropped by exactly the spell's computed {@link ReparoBase#getRepair()} amount, and
     * that the amount stayed within the spell's [{@link ReparoBase#getMinRepair()},
     * {@link ReparoBase#getMaxRepair()}] bounds. Reading the repair amount from the spell keeps the
     * assertion tied to the production formula rather than hard-coding a skill-to-amount mapping.</p>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);

        // fully damage the item so the repair amount cannot exceed the existing damage
        int initialDamage = getRepairableMaterial().getMaxDurability();
        Item item = testWorld.dropItem(targetLocation, createDamagedItem(getRepairableMaterial(), initialDamage));

        ReparoBase reparo = (ReparoBase) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(reparo.isKilled(), "spell was not killed after repairing target");

        int repair = reparo.getRepair();
        assertTrue(repair >= reparo.getMinRepair(), "repair amount below minRepair");
        assertTrue(repair <= reparo.getMaxRepair(), "repair amount above maxRepair");
        assertEquals(initialDamage - repair, getDamage(item), "item was not repaired by the computed repair amount");
    }

    /**
     * Verifies that the spell ignores items it should not repair.
     *
     * <p>Confirms two skip paths in a single cast: an undamaged damageable item (the {@code damage == 0}
     * continue) and a non-damageable item (the {@code instanceof Damageable} guard). Neither item should be
     * altered nor removed by the spell.</p>
     */
    @Test
    void ignoresInvalidTargetsTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "InvalidTargets");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);

        // an undamaged damageable item - should hit the damage == 0 skip
        Item undamaged = testWorld.dropItem(targetLocation, new ItemStack(getRepairableMaterial()));
        // a non-damageable item - should hit the instanceof Damageable skip
        Item nonDamageable = testWorld.dropItem(targetLocation, new ItemStack(Material.DIRT));

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertEquals(0, getDamage(undamaged), "undamaged item was altered");
        assertFalse(undamaged.isDead(), "undamaged item was removed");
        assertFalse(nonDamageable.isDead(), "non-damageable item was removed");
    }

    /** {@inheritDoc} */
    @Override
    @Test
    void revertTest() {
        // Reparo spells have no revert action - the repair is permanent
    }
}