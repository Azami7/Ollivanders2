package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.block.BlockCommon;
import net.pottercraft.ollivanders2.house.O2HouseType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.stationaryspell.PROTEGO_DIABOLICA;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link PROTEGO_DIABOLICA}. Extends {@link O2StationarySpellTest} for the shared stationary-spell
 * tests.
 *
 * @author Azami7
 */
@Isolated
public class ProtegoDiabolicaTest extends O2StationarySpellTest {
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.PROTEGO_DIABOLICA;
    }

    PROTEGO_DIABOLICA createStationarySpell(Player caster, Location location) {
        return new PROTEGO_DIABOLICA(testPlugin, caster.getUniqueId(), location, PROTEGO_DIABOLICA.minRadiusConfig, PROTEGO_DIABOLICA.minDurationConfig);
    }

    /**
     * The first upkeep builds a ring of soul sand and soul fire blocks at the spell radius, all registered as temporary
     * blocks.
     */
    @Override
    @Test
    void upkeepTest() {
        Ollivanders2.debug = true;

        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);

        PROTEGO_DIABOLICA protegoDiabolica = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoDiabolica);
        mockServer.getScheduler().performTicks(5);

        List<Block> fireBaseBlocks = BlockCommon.getBlocksInRadiusByType(location, protegoDiabolica.getRadius(), Material.SOUL_SAND);
        assertFalse(fireBaseBlocks.isEmpty(), "spell did not create a ring of soul sand blocks");

        for (Block block : fireBaseBlocks) {
            assertTrue(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(block), "firebase block not added to tracking");
            assertTrue(location.distance(block.getLocation()) >= (protegoDiabolica.getRadius() - 1), "");
            assertTrue(location.distance(block.getLocation()) <= (protegoDiabolica.getRadius() + 1), "");
        }

        List<Block> fireBlocks = BlockCommon.getBlocksInRadiusByType(location, protegoDiabolica.getRadius(), Material.SOUL_FIRE);
        assertFalse(fireBlocks.isEmpty(), "spell did not create a ring of soul fire blocks");
        for (Block block : fireBlocks) {
            assertTrue(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(block), "fire block not added to tracking");
            assertTrue(location.distance(block.getLocation()) >= (protegoDiabolica.getRadius() - 1), "");
            assertTrue(location.distance(block.getLocation()) <= (protegoDiabolica.getRadius() + 1), "");
        }
    }

    /**
     * The radius can be changed before the fire ring is built but is locked afterward.
     */
    @Override
    @Test
    void radiusTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        PROTEGO_DIABOLICA protegoDiabolica = createStationarySpell(caster, location);
        int radius = protegoDiabolica.getRadius();
        assertTrue(radius <= protegoDiabolica.getMaxRadius(), "Spell radius exceeds maximum");
        assertTrue(radius >= protegoDiabolica.getMinRadius(), "Spell radius below minimum");

        protegoDiabolica.increaseRadius(5);
        assertEquals(radius + 5, protegoDiabolica.getRadius(), "protego diabolica radius did not increase before fire ring created");

        radius = protegoDiabolica.getRadius();
        protegoDiabolica.decreaseRadius(2);
        assertEquals(radius - 2, protegoDiabolica.getRadius(), "protego diabolica radius did not decrease before fire ring created");

        Ollivanders2API.getStationarySpells().addStationarySpell(protegoDiabolica);
        mockServer.getScheduler().performTicks(2);

        radius = protegoDiabolica.getRadius();
        protegoDiabolica.increaseRadius(5);
        assertEquals(radius, protegoDiabolica.getRadius(), "protego diabolica radius increased after fire ring created");

        protegoDiabolica.decreaseRadius(2);
        assertEquals(radius, protegoDiabolica.getRadius(), "protego diabolica radius decreased after fire ring created");
    }

    /**
     * A muggle or a wizard from another house is burned moving into the area, while the caster's housemates and anyone
     * moving out are unharmed.
     */
    @Test
    void doOnPlayerMoveEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 300, 40, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + PROTEGO_DIABOLICA.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock player = mockServer.addPlayer();

        caster.setLocation(location);
        player.setLocation(outsideLocation);

        O2Player casterO2P = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        assertNotNull(casterO2P);
        O2Player playerO2P = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        assertNotNull(playerO2P);

        casterO2P.setMuggle(false);
        O2HouseType casterHouse = O2HouseType.SLYTHERIN;
        O2HouseType playerHouse = O2HouseType.RAVENCLAW;
        Ollivanders2API.getHouses().sort(caster, casterHouse);
        playerO2P.setMuggle(true);

        PROTEGO_DIABOLICA protegoDiabolica = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoDiabolica);

        double health = player.getHealth();

        PlayerMoveEvent event = new PlayerMoveEvent(player, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(player.getHealth() < health, "muggle not damaged by fire");

        player.setHealth(health);
        playerO2P.setMuggle(false);
        event = new PlayerMoveEvent(player, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(player.getHealth() < health, "wizard not sorted not damaged by fire");

        player.setHealth(health);
        Ollivanders2API.getHouses().sort(player, playerHouse);
        event = new PlayerMoveEvent(player, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(player.getHealth() < health, "wizard in different house not damaged by fire");

        player.setHealth(health);
        event = new PlayerMoveEvent(player, location, outsideLocation);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertEquals(health, player.getHealth(), "player damaged when to location is outside the spell area");

        player.setHealth(health);
        Ollivanders2API.getHouses().forceSetHouse(player, casterHouse);
        event = new PlayerMoveEvent(player, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertEquals(health, player.getHealth(), "wizard in same house was damaged by fire");
    }

    /**
     * A wizard from another house is burned teleporting into the area, while the caster's housemates and anyone
     * teleporting out are unharmed.
     */
    @Test
    void doOnPlayerTeleportEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 400, 40, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + PROTEGO_DIABOLICA.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock player = mockServer.addPlayer();

        caster.setLocation(location);
        player.setLocation(outsideLocation);

        O2Player casterO2P = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        assertNotNull(casterO2P);
        O2Player playerO2P = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        assertNotNull(playerO2P);

        O2HouseType casterHouse = O2HouseType.SLYTHERIN;
        O2HouseType playerHouse = O2HouseType.RAVENCLAW;
        Ollivanders2API.getHouses().sort(caster, casterHouse);
        Ollivanders2API.getHouses().sort(player, playerHouse);

        double health = player.getHealth();
        PROTEGO_DIABOLICA protegoDiabolica = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoDiabolica);
        mockServer.getScheduler().performTicks(5);

        PlayerTeleportEvent event = new PlayerTeleportEvent(player, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(player.getHealth() < health, "wizard in different house not damaged by fire on teleport in to spell area");

        player.setHealth(health);
        event = new PlayerTeleportEvent(player, location, outsideLocation);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertEquals(health, player.getHealth(), "player damaged when teleporting to location outside the spell area");

        player.setHealth(health);
        Ollivanders2API.getHouses().forceSetHouse(player, casterHouse);
        event = new PlayerTeleportEvent(player, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertEquals(health, player.getHealth(), "wizard in same house was damaged by fire on teleporting in to spell area");
    }

    /**
     * Combustion is cancelled for the caster and their housemates inside the area, but not for disloyal players or
     * anyone outside it.
     */
    @Test
    void doOnEntityCombustEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 500, 40, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + PROTEGO_DIABOLICA.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock player = mockServer.addPlayer();

        caster.setLocation(location);
        player.setLocation(location);

        O2Player casterO2P = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        assertNotNull(casterO2P);
        O2Player playerO2P = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        assertNotNull(playerO2P);

        O2HouseType casterHouse = O2HouseType.SLYTHERIN;
        O2HouseType playerHouse = O2HouseType.RAVENCLAW;
        Ollivanders2API.getHouses().sort(caster, casterHouse);
        Ollivanders2API.getHouses().sort(player, playerHouse);

        PROTEGO_DIABOLICA protegoDiabolica = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoDiabolica);
        mockServer.getScheduler().performTicks(5);

        EntityCombustEvent event = new EntityCombustEvent(player, 5f);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "combust event canceled when player is not loyal");

        Ollivanders2API.getHouses().forceSetHouse(player, casterHouse);
        event = new EntityCombustEvent(player, 5f);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "combust event not canceled when player is loyal");

        event = new EntityCombustEvent(caster, 5f);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "combust event not canceled when entity is caster");

        player.setLocation(outsideLocation);
        event = new EntityCombustEvent(player, 5f);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "combust event canceled when player is outside of spell area");
    }

    /**
     * Fire damage is cancelled for the caster and their housemates inside the area, but not for disloyal players,
     * non-fire damage, or anyone outside it.
     */
    @Test
    void doOnEntityDamageEvent() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 600, 40, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + PROTEGO_DIABOLICA.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock player = mockServer.addPlayer();

        caster.setLocation(location);
        player.setLocation(location);

        O2Player casterO2P = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        assertNotNull(casterO2P);
        O2Player playerO2P = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        assertNotNull(playerO2P);

        O2HouseType casterHouse = O2HouseType.SLYTHERIN;
        O2HouseType playerHouse = O2HouseType.RAVENCLAW;
        Ollivanders2API.getHouses().sort(caster, casterHouse);
        Ollivanders2API.getHouses().sort(player, playerHouse);

        PROTEGO_DIABOLICA protegoDiabolica = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(protegoDiabolica);
        mockServer.getScheduler().performTicks(5);

        DamageSource damageSource = DamageSource.builder(DamageType.IN_FIRE)
                .withDamageLocation(player.getLocation())
                .build();
        EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.FIRE, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "canceled fire damage event when player is not loyal");

        Ollivanders2API.getHouses().forceSetHouse(player, casterHouse);
        event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.FIRE, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "fire damage event not canceled when player is loyal");

        damageSource = DamageSource.builder(DamageType.ARROW)
                .withDamageLocation(player.getLocation())
                .build();
        event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "canceled non-fire damage event");

        player.setLocation(outsideLocation);
        damageSource = DamageSource.builder(DamageType.IN_FIRE)
                .withDamageLocation(player.getLocation())
                .build();
        event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.FIRE, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "canceled fire damage event when player not in spell area");

        event = new EntityDamageEvent(caster, EntityDamageEvent.DamageCause.FIRE, damageSource, 1.0);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "fire damage event not canceled for caster");
    }
}
