package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.LUMOS_FERVENS;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link LUMOS_FERVENS}. Extends {@link O2StationarySpellTest} for the shared stationary-spell tests.
 *
 * @author Azami7
 */
public class LumosFervensTest extends O2StationarySpellTest {
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.LUMOS_FERVENS;
    }

    @Override
    LUMOS_FERVENS createStationarySpell(Player caster, Location location) {
        return new LUMOS_FERVENS(testPlugin, caster.getUniqueId(), location, LUMOS_FERVENS.minRadiusConfig, LUMOS_FERVENS.minDurationConfig);
    }

    @Test
    void getBaseBlockTest() {
        // simple getter, skipping tests
    }

    @Test
    void getFireBlockTest() {
        // simple getter, skipping tests
    }

    /**
     * Casting builds the soul sand base and soul fire above it, cleanup reverts both blocks, and the spell kills itself
     * if the block above the target is not air.
     */
    @Override
    @Test
    void upkeepTest() {
        // age is covered by ageAndKillTest(), we'll use this test to check createSoulFire()
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        PlayerMock caster = mockServer.addPlayer();

        Block baseBlock = location.getBlock();
        Material baseBlockMaterial = Material.DIRT;
        baseBlock.setType(baseBlockMaterial);
        assertEquals(baseBlockMaterial, location.getBlock().getType());

        Block fireBlock = baseBlock.getRelative(BlockFace.UP);
        Material air = Material.AIR;
        fireBlock.setType(air);
        assertEquals(air, baseBlock.getRelative(BlockFace.UP).getType());

        LUMOS_FERVENS lumosFervens = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(lumosFervens);
        mockServer.getScheduler().performTicks(20);
        assertFalse(lumosFervens.isKilled(), "Failed to create lumos fervens stationary spell");
        assertEquals(LUMOS_FERVENS.baseBlockMaterial, baseBlock.getType(), "base block is not expected type");
        assertEquals(LUMOS_FERVENS.fireBlockMaterial, fireBlock.getType(), "fire block is not expected type");

        // base block reverts to original type and fire block reverts to air
        Ollivanders2API.getStationarySpells().removeStationarySpell(lumosFervens);
        mockServer.getScheduler().performTicks(20);
        assertEquals(baseBlockMaterial, baseBlock.getType(), "base block did not revert to original material");
        assertEquals(air, fireBlock.getType(), "fire block did not revert to air");

        // test the spell exits if the block above the target block is not air
        fireBlock.setType(baseBlockMaterial);
        lumosFervens = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(lumosFervens);
        mockServer.getScheduler().performTicks(20);
        assertTrue(lumosFervens.isKilled(), "spell not killed when fire block did not start as air");
    }

    /**
     * Fire damage is cancelled for an entity inside the area but non-fire damage inside and fire damage outside are
     * not.
     */
    @Test
    void doOnEntityDamageEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX() + LUMOS_FERVENS.maxRadiusConfig + 1, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        LUMOS_FERVENS lumosFervens = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(lumosFervens);
        mockServer.getScheduler().performTicks(20);

        // player in this area does not take fire damage
        caster.setLocation(location);
        assertTrue(lumosFervens.isLocationInside(caster.getLocation()));
        DamageSource damageSource = DamageSource.builder(DamageType.IN_FIRE)
                .withDamageLocation(caster.getLocation())
                .build();
        EntityDamageEvent event = new EntityDamageEvent(caster, EntityDamageEvent.DamageCause.FIRE, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "Lumos fervens did not cancel fire damage event");

        // non-fire damage event is not cancelled
        damageSource = DamageSource.builder(DamageType.FALL)
                .withDamageLocation(caster.getLocation())
                .build();
        event = new EntityDamageEvent(caster, EntityDamageEvent.DamageCause.FALL, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "non-fire damage event cancelled");

        // fire damage event outside spell area is not cancelled
        caster.setLocation(outsideLocation);
        damageSource = DamageSource.builder(DamageType.IN_FIRE)
                .withDamageLocation(caster.getLocation())
                .build();
        event = new EntityDamageEvent(caster, EntityDamageEvent.DamageCause.FIRE, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "fire damage event cancelled outside of spell area");
    }

    /**
     * Combustion is cancelled for an entity inside the area and allowed outside.
     */
    @Test
    void doOnEntityCombustEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 300, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX() + LUMOS_FERVENS.maxRadiusConfig + 1, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        LUMOS_FERVENS lumosFervens = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(lumosFervens);
        mockServer.getScheduler().performTicks(20);

        // player inside spell area cannot combust
        caster.setLocation(location);
        EntityCombustEvent event = new EntityCombustEvent(caster, 5f);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "combust event not cancelled in spell area");

        // player outside spell area can combust
        caster.setLocation(outsideLocation);
        event = new EntityCombustEvent(caster, 5f);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "combust event outside spell area cancelled");
    }

    /**
     * Water flowing onto the fire block is prevented but lava is allowed.
     */
    @Test
    void doOnBlockFromToEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 400, 4, 100);
        PlayerMock caster = mockServer.addPlayer();

        LUMOS_FERVENS lumosFervens = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(lumosFervens);
        mockServer.getScheduler().performTicks(20);

        // prevent water flowing on to our fire block
        Block fireBlock = lumosFervens.getFireBlock();
        Block fireNeighbor = fireBlock.getRelative(BlockFace.EAST);
        fireNeighbor.setType(Material.WATER);
        BlockFromToEvent event = new BlockFromToEvent(fireNeighbor, fireBlock);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "water flow not prevented");

        // allow lava flowing
        fireNeighbor.setType(Material.LAVA);
        event = new BlockFromToEvent(fireNeighbor, fireBlock);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "lava flow was prevented");
    }

    /**
     * Emptying a water bucket onto the fire block or an adjacent block is prevented.
     */
    @Test
    void doOnPlayerBucketEmptyEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 500, 4, 100);
        PlayerMock caster = mockServer.addPlayer();

        ItemStack bucket = new ItemStack(Material.WATER_BUCKET);
        caster.getInventory().setItemInMainHand(bucket);

        LUMOS_FERVENS lumosFervens = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(lumosFervens);
        mockServer.getScheduler().performTicks(20);

        // emptying bucket on fire block is prevented
        Block fireBlock = lumosFervens.getFireBlock();
        PlayerBucketEmptyEvent event = new PlayerBucketEmptyEvent(caster, fireBlock, fireBlock, BlockFace.UP, Material.WATER_BUCKET, bucket, EquipmentSlot.HAND);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "failed to prevent bucket empty event on fire block");

        // emptying bucket next to fire block is prevented
        Block fireNeighbor = fireBlock.getRelative(BlockFace.EAST);
        event = new PlayerBucketEmptyEvent(caster, fireNeighbor, fireNeighbor, BlockFace.UP, Material.WATER_BUCKET, bucket, EquipmentSlot.HAND);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "failed to prevent bucket empty event next to fire block");
    }

    /**
     * The spell is killed when its fire block or its base block is broken.
     */
    @Test
    void doOnBlockBreakEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 600, 4, 100);
        PlayerMock caster = mockServer.addPlayer();

        LUMOS_FERVENS lumosFervens = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(lumosFervens);
        mockServer.getScheduler().performTicks(20);

        // spell is killed when fire block is broken
        BlockBreakEvent event = new BlockBreakEvent(lumosFervens.getFireBlock(), caster);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(lumosFervens.isKilled(), "lumos fervens not killed when fire block broken");

        // spell is killed when base block is broken
        lumosFervens = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(lumosFervens);
        mockServer.getScheduler().performTicks(20);

        event = new BlockBreakEvent(lumosFervens.getBaseBlock(), caster);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(lumosFervens.isKilled(), "lumos fervens not killed when base block broken");
    }

    /**
     * An entity changing the fire block is cancelled, protecting the spell structure.
     */
    @Test
    void doOnEntityChangeBlockEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 700, 4, 100);
        PlayerMock caster = mockServer.addPlayer();

        LUMOS_FERVENS lumosFervens = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(lumosFervens);
        mockServer.getScheduler().performTicks(20);

        EntityChangeBlockEvent event = new EntityChangeBlockEvent(caster, lumosFervens.getFireBlock(), lumosFervens.getFireBlock().getBlockData());
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "did not cancel block change event");
    }
}
