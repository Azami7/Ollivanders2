package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.ItemToEntityTransfiguration;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract test suite for {@link ItemToEntityTransfiguration} spells.
 *
 * <p>Tests item-to-entity transfiguration behavior: material type validation, enchanted item handling,
 * transfiguration map allowlisting, and entity death event handling. Subclasses provide the valid and
 * invalid material types for the specific spell being tested.</p>
 *
 * @see ItemToEntityTransfiguration
 * @see EntityTransfigurationTest
 */
abstract public class ItemToEntityTransfigurationTest extends EntityTransfigurationTest {
    /**
     * Returns {@link EntityType#ITEM} since all item-to-entity spells target dropped items.
     *
     * @return ITEM entity type
     */
    @Override
    @NotNull
    EntityType getValidEntityType() {
        return EntityType.ITEM;
    }

    /**
     * Returns a non-item entity type that should not be targetable.
     *
     * @return ACACIA_BOAT entity type
     */
    @Override
    @Nullable
    EntityType getInvalidEntityType() {
        return EntityType.ACACIA_BOAT;
    }

    /**
     * Returns null since items cannot already be the target entity type.
     *
     * @return null
     */
    @Override
    @Nullable
    EntityType getSameEntityType() {
        return null;
    }

    /**
     * Returns true since these spells only target items
     *
     * @return true
     */
    @Override
    boolean transfiguresItems() {
        return true;
    }

    /**
     * Test that a valid item is transfigured into the expected entity type.
     */
    @Override
    @Test
    void transfigureTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);

        ItemToEntityTransfiguration itemToEntityTransfiguration = (ItemToEntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        Map<Material, EntityType> transfigurationMap = itemToEntityTransfiguration.getTransfigurationMap();

        Material originalMaterial;

        if (transfigurationMap.isEmpty())
            originalMaterial = getValidMaterialType();
        else {
            originalMaterial = transfigurationMap.keySet().iterator().next();
        }
        testWorld.dropItem(targetLocation, new ItemStack(originalMaterial, 1));

        mockServer.getScheduler().performTicks(20);
        assertTrue(itemToEntityTransfiguration.isTransfigured(), "Item was not transfigured");
        assertFalse(EntityCommon.getEntitiesInRadius(targetLocation, 2).isEmpty(), "Did not find spawned entity");

        EntityType expectedType = transfigurationMap.get(originalMaterial);
        if (expectedType == null)
            expectedType = itemToEntityTransfiguration.getTargetType();

        assertEquals(expectedType, itemToEntityTransfiguration.getTransfiguredEntity().getType(), "Item was not transfigured to expected entity type");
    }

    /**
     * Test that items with material types not in the transfiguration map are rejected.
     * Skipped if the spell accepts all material types (no invalid material defined).
     */
    @Test
    void canTransfigureMaterialAllowlistTest() {
        Material invalidMaterial = getInvalidMaterialType();

        if (invalidMaterial != null) {
            World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
            Location location = getNextLocation(testWorld);
            Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
            PlayerMock caster = mockServer.addPlayer();

            TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
            testWorld.dropItem(targetLocation, new ItemStack(invalidMaterial, 1));

            ItemToEntityTransfiguration itemToEntityTransfiguration = (ItemToEntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
            mockServer.getScheduler().performTicks(20);

            assertTrue(itemToEntityTransfiguration.isKilled(), "spell not stopped when invalid item type targeted");
            assertFalse(itemToEntityTransfiguration.isTransfigured(), "invalid item type transfigured");
        }
    }

    /**
     * We can't test this yet because there is no transfiguration spell that results in an Item entity type and is not permanent.
     */
    @Override
    @Test
    void canTransfigureAlreadyTransfiguredTest() {
        /*
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop
        testWorld.dropItem(targetLocation, new ItemStack(Material.STICK, 1));

        CALAMUS calamus = (CALAMUS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel + 2, O2SpellType.CALAMUS);
        mockServer.getScheduler().performTicks(20);
        assertTrue(calamus.isTransfigured());
        Item transfiguredItem = EntityCommon.getItemAtLocation(targetLocation);
        assertNotNull(transfiguredItem);

        ItemToEntityTransfiguration itemToEntityTransfiguration = (ItemToEntityTransfiguration) castSpell(caster, location, transfiguredItem.getLocation(), O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        mockServer.getScheduler().performTicks(20);
        assertFalse(itemToEntityTransfiguration.isTransfigured(), "Item was transformed when it is already transformed");
        assertTrue(itemToEntityTransfiguration.isKilled(), "spell not killed when hitting invalid target");
        */
    }

    /**
     * This cannot be tested since items are not InventoryHolders
     */
    @Test
    void inventoryHolderRevertTest() {
    }

    /**
     * Test that killing the transfigured entity causes the spell to be killed.
     * Skipped for permanent spells since they do not register as event listeners.
     */
    @Test
    void onEntityDeathTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        testWorld.dropItem(targetLocation, new ItemStack(getValidMaterialType(), 1));

        ItemToEntityTransfiguration itemToEntityTransfiguration = (ItemToEntityTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);

        if (!itemToEntityTransfiguration.isPermanent()) { // permanent spell will immediately exit, it is not an event listener
            mockServer.getScheduler().performTicks(20);

            Entity transfigured = itemToEntityTransfiguration.getTransfiguredEntity();

            if (transfigured instanceof LivingEntity) {
                DamageSource damageSource = DamageSource.builder(DamageType.IN_FIRE)
                        .withDamageLocation(transfigured.getLocation())  // location of the fire block
                        .build();

                EntityDeathEvent event = new EntityDeathEvent((LivingEntity) transfigured, damageSource, new ArrayList<>());
                mockServer.getPluginManager().callEvent(event);
                mockServer.getScheduler().performTicks(20);
                assertTrue(itemToEntityTransfiguration.isKilled());
            }
        }
    }
}
