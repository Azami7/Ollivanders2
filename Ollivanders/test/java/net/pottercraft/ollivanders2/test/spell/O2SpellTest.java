package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.HIGHER_SKILL;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.player.Year;
import net.pottercraft.ollivanders2.spell.ACCIO;
import net.pottercraft.ollivanders2.spell.AGUAMENTI;
import net.pottercraft.ollivanders2.spell.APARECIUM;
import net.pottercraft.ollivanders2.spell.ARRESTO_MOMENTUM;
import net.pottercraft.ollivanders2.spell.BOMBARDA_MAXIMA;
import net.pottercraft.ollivanders2.spell.BOTHYNUS;
import net.pottercraft.ollivanders2.spell.BRACKIUM_EMENDO;
import net.pottercraft.ollivanders2.spell.CONFUNDUS_DUO;
import net.pottercraft.ollivanders2.spell.DEFODIO;
import net.pottercraft.ollivanders2.spell.DEPRIMO;
import net.pottercraft.ollivanders2.spell.EPISKEY;
import net.pottercraft.ollivanders2.spell.FIANTO_DURI;
import net.pottercraft.ollivanders2.spell.LUMOS;
import net.pottercraft.ollivanders2.spell.MELOFORS;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.PYROSVESTIRAS;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockbukkit.mockbukkit.matcher.plugin.PluginManagerFiredEventClassMatcher.hasFiredEventInstance;

/**
 * Unit tests for the {@link O2Spell} base class: projectile movement, collision, entity detection, zone validation,
 * and message handling.
 *
 * @author Azami7
 */
@Isolated
public class O2SpellTest {
    /**
     * Shared MockBukkit server, created once for all tests in the class.
     */
    static ServerMock mockServer;

    /**
     * The plugin under test, loaded once with the default config.
     */
    static Ollivanders2 testPlugin;

    /**
     * Start the shared MockBukkit server and load the plugin before any tests run.
     */
    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Verify usesModifier reflects spell experience, wand correctness, player year, the HIGHER_SKILL effect, and the
     * maxSpellLevel setting. Kept in one method because it toggles global settings (useYears, maxSpellLevel) that
     * would interfere with parallel tests.
     */
    @Test
    void setUsesModifierTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);

        // player has no experience in the spell
        ACCIO accio = new ACCIO(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(0.0, accio.getUsesModifier(), "not expected uses modifier for no experience");
        accio.kill();

        // player has some experience
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        assertNotNull(o2p);
        double spellcount = 30;
        o2p.setSpellCount(O2SpellType.ACCIO, (int)spellcount);
        accio = new ACCIO(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(spellcount / O2PlayerCommon.rightWand, accio.getUsesModifier(), "not expected uses modifier with correct wand");
        accio.kill();

        // player using wrong wand
        accio = new ACCIO(testPlugin, caster, O2PlayerCommon.wrongWand);
        assertEquals(spellcount / O2PlayerCommon.wrongWand, accio.getUsesModifier(), "not expected uses modifier for wrong wand");
        accio.kill();

        accio = new ACCIO(testPlugin, caster, O2PlayerCommon.elderWand);
        assertEquals(spellcount / O2PlayerCommon.elderWand, accio.getUsesModifier(), "not expected uses modifier for elder wand");
        accio.kill();

        Ollivanders2.useYears = true;

        // if the spell is the same level as the player, usesModifier is unchanged
        o2p.setSpellCount(O2SpellType.LUMOS, (int)spellcount);
        LUMOS lumos = new LUMOS(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(spellcount / O2PlayerCommon.rightWand, lumos.getUsesModifier(), "not expected uses modifier with correct wand and same level");
        lumos.kill();

        // uses modifier is half for 1 level above
        accio = new ACCIO(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(spellcount / 2, accio.getUsesModifier(), "not expected uses modifier with correct wand and 1 level higher");
        accio.kill();

        // uses modifier is quartered for 2 or more levels above
        o2p.setSpellCount(O2SpellType.AGUAMENTI, (int)spellcount);
        AGUAMENTI aguamenti = new AGUAMENTI(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(spellcount / 4, aguamenti.getUsesModifier(), "not expected uses modifier with correct wand and 2 levels higher");
        aguamenti.kill();

        // uses modifier 50% higher when spell is 1 level below
        o2p.setYear(Year.YEAR_7);
        aguamenti = new AGUAMENTI(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(spellcount * 1.5, aguamenti.getUsesModifier(), "not expected uses modifier with correct wand and 1 level lower");
        aguamenti.kill();

        accio = new ACCIO(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(spellcount * 2, accio.getUsesModifier(), "not expected uses modifier with correct wand and 2 levels lower");
        accio.kill();
        Ollivanders2.useYears = false;

        // uses modifier is set to 200 when maxSpellLevel set
        Ollivanders2.maxSpellLevel = true;
        accio = new ACCIO(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(200.0, accio.getUsesModifier(), "uses modifier not set to 200 when maxSpellLevel enabled");
        accio.kill();
        Ollivanders2.maxSpellLevel = false;

        // uses modifier is doubled if the player is affected by HIGHER_SKILL
        HIGHER_SKILL higherSkill = new HIGHER_SKILL(testPlugin, 200, false, caster.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(higherSkill);
        accio = new ACCIO(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(spellcount * 2.0, accio.getUsesModifier(), "uses modifier not doubled by HIGHER_SKILL");
        accio.kill();
        higherSkill.kill();
    }

    /**
     * Verify each {@link O2Spell#checkEffect()} tick moves the projectile and ages it, that the spell ages out after
     * max lifetime without marking a block hit, and that hitting a block stops the projectile and kills the spell.
     */
    @Test
    void checkEffectTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location castLocation = new Location(testWorld, 300, 40, 100);
        Location targetLocation = new Location(testWorld, 300, 40, 110);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(TestCommon.faceTarget(castLocation, targetLocation));

        MELOFORS melofors = new MELOFORS(testPlugin, caster, O2PlayerCommon.rightWand);

        // call check effect should move the spell and increase its age by 1
        melofors.checkEffect();
        assertNotEquals(castLocation, melofors.getLocation(), "Melofors did not move");
        assertEquals(1, melofors.getProjectileAge(), "Melofors did not age by 1");

        // call check effect until the spell should age out
        for (int i = 0; i < O2Spell.maxSpellLifetime; i++) {
            melofors.checkEffect();
        }
        assertTrue(melofors.isKilled(), "melofors did not age out");
        assertFalse(melofors.hasHitBlock(), "melofors set has hit target when spell aged out");

        // hitting a target stops the projectile
        melofors = new MELOFORS(testPlugin, caster, O2PlayerCommon.rightWand);
        testWorld.getBlockAt(targetLocation).setType(Material.DIRT);
        Ollivanders2API.getSpells().addSpell(caster, melofors);
        mockServer.getScheduler().performTicks(20);
        assertTrue(melofors.hasHitBlock(), "melofors did not hit target");
        assertTrue(melofors.isKilled(), "melofors projectile not killed when target hit");
    }

    /**
     * Verify a spell is killed when it hits a block off its allow list, a block on its blocked list, or an unbreakable
     * block.
     */
    @Test
    void checkEffectAllowBlockTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location castLocation = new Location(testWorld, 400, 40, 100);
        Location targetLocation = new Location(testWorld, 400, 40, 110);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(TestCommon.faceTarget(castLocation, targetLocation));

        // test allow list only allows targeting that block type, hitting wrong block type kills the spell
        testWorld.getBlockAt(targetLocation).setType(Material.DIRT);
        PYROSVESTIRAS pyrosvestiras = new PYROSVESTIRAS(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, pyrosvestiras);
        mockServer.getScheduler().performTicks(20);
        assertTrue(pyrosvestiras.isKilled(), "pyrosvestiras not killed when it hit a block off its allow list");

        // test blocked list, spell is killed if it hits a blocked type
        testWorld.getBlockAt(targetLocation).setType(Material.LAVA);
        testWorld.getBlockAt(targetLocation).getRelative(BlockFace.DOWN).setType(Material.DIRT); // give the water something to sit on
        DEPRIMO deprimo = new DEPRIMO(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, deprimo);
        mockServer.getScheduler().performTicks(20);
        assertTrue(deprimo.isKilled(), "deprimo not killed when it hit a blocked type");

        // test no spell can target an unbreakable
        testWorld.getBlockAt(targetLocation).setType(Ollivanders2Common.getUnbreakableMaterials().getFirst());
        DEFODIO defodio = new DEFODIO(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, defodio);
        mockServer.getScheduler().performTicks(20);
        assertTrue(defodio.isKilled(), "defodio not killed when it hit an unbreakable block");
    }

    /**
     * Verify zone config governs casting: a spell on a cuboid or world zone's disallowed list is killed when cast and
     * the caster gets the failure message, while other spells in the same zone are unaffected.
     */
    @Test
    void checkEffectIsAllowedTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location castLocation = new Location(testWorld, 600, 40, 100);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(castLocation);

        // -- Cuboid zone with disallowed spell --
        testPlugin.getConfig().set("zones.test-cuboid.type", "Cuboid");
        testPlugin.getConfig().set("zones.test-cuboid.world", testWorld.getName());
        testPlugin.getConfig().set("zones.test-cuboid.area", "550 0 50 650 100 150");
        testPlugin.getConfig().set("zones.test-cuboid.disallowed-spells", List.of("BOMBARDA_MAXIMA"));
        Ollivanders2API.getSpells().loadZoneConfig();

        // disallowed spell in the cuboid is killed via isSpellTypeAllowed
        assertFalse(Ollivanders2API.getSpells().isSpellTypeAllowed(castLocation, O2SpellType.BOMBARDA_MAXIMA),
                "disallowed spell in cuboid zone was allowed");

        // other spell in the cuboid is not blocked
        assertTrue(Ollivanders2API.getSpells().isSpellTypeAllowed(castLocation, O2SpellType.CONFUNDUS_DUO),
                "non-disallowed spell in cuboid zone was blocked");

        // disallowed spell is killed when cast via isSpellAllowed and player gets failure message
        BOMBARDA_MAXIMA bombardaMaxima = new BOMBARDA_MAXIMA(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, bombardaMaxima);
        mockServer.getScheduler().performTicks(20);
        assertTrue(bombardaMaxima.isKilled(), "disallowed spell not killed when cast in cuboid zone");
        String message = caster.nextMessage();
        assertNotNull(message, "player did not receive failure message for disallowed spell");
        assertTrue(TestCommon.messageStartsWith("A powerful protective magic", message),
                "unexpected failure message for disallowed spell");

        // allowed spell is not killed when cast
        CONFUNDUS_DUO confundusDuo = new CONFUNDUS_DUO(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, confundusDuo);
        mockServer.getScheduler().performTicks(20);
        assertFalse(confundusDuo.isKilled(), "allowed spell killed when cast in cuboid zone");
        confundusDuo.kill();

        // clean up cuboid zone
        testPlugin.getConfig().set("zones.test-cuboid", null);

        // -- World zone with disallowed spell --
        testPlugin.getConfig().set("zones.test-world.type", "World");
        testPlugin.getConfig().set("zones.test-world.world", testWorld.getName());
        testPlugin.getConfig().set("zones.test-world.disallowed-spells", List.of("BOMBARDA_MAXIMA"));
        Ollivanders2API.getSpells().loadZoneConfig();

        // disallowed spell in the world is killed via isSpellTypeAllowed
        assertFalse(Ollivanders2API.getSpells().isSpellTypeAllowed(castLocation, O2SpellType.BOMBARDA_MAXIMA),
                "disallowed spell in world zone was allowed");

        // other spell in the world is not blocked
        assertTrue(Ollivanders2API.getSpells().isSpellTypeAllowed(castLocation, O2SpellType.CONFUNDUS_DUO),
                "non-disallowed spell in world zone was blocked");

        // disallowed spell is killed when cast via isSpellAllowed and player gets failure message
        bombardaMaxima = new BOMBARDA_MAXIMA(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, bombardaMaxima);
        mockServer.getScheduler().performTicks(20);
        assertTrue(bombardaMaxima.isKilled(), "disallowed spell not killed when cast in world zone");
        message = caster.nextMessage();
        assertNotNull(message, "player did not receive failure message for disallowed spell in world zone");
        assertTrue(TestCommon.messageStartsWith("A powerful protective magic", message),
                "unexpected failure message for disallowed spell in world zone");

        // allowed spell is not killed when cast in world zone
        confundusDuo = new CONFUNDUS_DUO(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, confundusDuo);
        mockServer.getScheduler().performTicks(20);
        assertFalse(confundusDuo.isKilled(), "allowed spell killed when cast in world zone");
        confundusDuo.kill();

        // clean up world zone and reload to reset
        testPlugin.getConfig().set("zones.test-world", null);
        Ollivanders2API.getSpells().loadZoneConfig();
    }

    /**
     * Verify {@link O2Spell#move()} advances the projectile one block, fires an
     * {@link OllivandersSpellProjectileMoveEvent}, kills the spell (without marking a block hit) at max distance,
     * no-ops once the spell is killed, and kills the spell when a move enters a disallowed location.
     */
    @Test
    void moveTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location castLocation = new Location(testWorld, 500, 40, 100);
        Location targetLocation = new Location(testWorld, 500, 40, 110);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(TestCommon.faceTarget(castLocation, targetLocation));

        ACCIO accio = new ACCIO(testPlugin, caster, O2PlayerCommon.rightWand);

        accio.move();
        assertNotEquals(castLocation, accio.getLocation(), "Projectile did not move");

        // move until the spell should be 1 move from expiring
        for (int i = 0; i < O2Spell.maxProjectileDistance; i++) {
            accio.move();
        }
        assertFalse(accio.isKilled(), "Accio killed before hitting max distance");

        // move one more to hit max distance and kill the spell without hitting a target
        accio.move();
        assertTrue(accio.isAtMaxDistance());
        assertTrue(accio.isKilled(), "Accio not killed when hitting max projectile distance.");
        assertFalse(accio.hasHitBlock(), "Accio has hit target set when projectile hit max distance");

        // a move event is fire when projectiles move
        assertThat(mockServer.getPluginManager(), hasFiredEventInstance(OllivandersSpellProjectileMoveEvent.class));

        // move does not run for killed spells
        Location current = accio.getLocation().clone();
        accio.kill();
        accio.move();
        assertEquals(current, accio.getLocation(), "Accio moved after killed");

        // move will kill a spell if it is in a location it is not allowed
        testPlugin.getConfig().set("zones.test-world.type", "World");
        testPlugin.getConfig().set("zones.test-world.world", testWorld.getName());
        testPlugin.getConfig().set("zones.test-world.disallowed-spells", List.of("EPISKEY"));
        Ollivanders2API.getSpells().loadZoneConfig();
        EPISKEY episkey = new EPISKEY(testPlugin, caster, O2PlayerCommon.rightWand);
        episkey.move();
        assertTrue(episkey.isKilled(), "Episkey spell not killed by move when not allowed in area");
    }

    /**
     * Verify {@link O2Spell#getNearbyEntities(double)} excludes the caster at projectile spawn, detects living and
     * non-living entities in range as the projectile moves, and does not target an entity sharing the caster's
     * location on the first tick.
     */
    @Test
    void getNearbyEntitiesTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location castLocation = new Location(testWorld, 700, 40, 100);
        Location targetLocation = new Location(testWorld, 700, 40, 110);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(TestCommon.faceTarget(castLocation, targetLocation));

        Entity entity = testWorld.spawnEntity(targetLocation, EntityType.RABBIT);
        ARRESTO_MOMENTUM arrestoMomentum = new ARRESTO_MOMENTUM(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, arrestoMomentum);

        // at time 0, the player is the close entity but should be excluded from the list
        assertTrue(arrestoMomentum.getNearbyEntities(O2Spell.defaultRadius).isEmpty(), "getNearbyEntities() included the caster at projectile spawn");

        // run ahead 10 ticks and check for the rabbit entity
        mockServer.getScheduler().performTicks(10);
        assertFalse(arrestoMomentum.getNearbyEntities(O2Spell.defaultRadius).isEmpty(), "getNearbyEntities() did not find the rabbit");
        arrestoMomentum.kill();
        entity.remove();

        // an entity right in the player's location is not targeted at time 0 for the spell
        entity = testWorld.spawnEntity(targetLocation, EntityType.RABBIT);
        arrestoMomentum = new ARRESTO_MOMENTUM(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, arrestoMomentum);
        assertTrue(arrestoMomentum.getNearbyEntities(O2Spell.defaultRadius).isEmpty(), "getNearbyEntities() targeted an entity at the caster's location on the first tick");
        arrestoMomentum.kill();
        entity.remove();

        // non-living entities
        entity = testWorld.spawnEntity(targetLocation, EntityType.MINECART);
        arrestoMomentum = new ARRESTO_MOMENTUM(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, arrestoMomentum);
        mockServer.getScheduler().performTicks(10);
        assertFalse(arrestoMomentum.getNearbyEntities(O2Spell.defaultRadius).isEmpty(), "getNearbyEntities() did not find the minecart");
        arrestoMomentum.kill();
        entity.remove();
    }

    /**
     * Verify {@link O2Spell#getNearbyItems(double)} finds a dropped item once the projectile is within range, but not
     * at the projectile's spawn location.
     */
    @Test
    void getNearbyItemsTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location castLocation = new Location(testWorld, 800, 40, 100);
        Location targetLocation = new Location(testWorld, 800, 40, 110);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(TestCommon.faceTarget(castLocation, targetLocation));

        testWorld.dropItem(targetLocation, new ItemStack(Material.APPLE, 1));

        APARECIUM aparecium = new APARECIUM(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, aparecium);

        assertTrue(aparecium.getNearbyItems(O2Spell.defaultRadius).isEmpty(), "getNearbyItems() found an item at the projectile spawn location");

        mockServer.getScheduler().performTicks(10);
        assertFalse(aparecium.getNearbyItems(O2Spell.defaultRadius).isEmpty(), "Did not find nearby item");
    }

    /**
     * Verify {@link O2Spell#getNearbyLivingEntities(double)} and {@link O2Spell#getNearbyDamageableEntities(double)}
     * include the caster and other living entities in range as the projectile moves, but never non-living entities.
     */
    @Test
    void getNearbyLivingAndDamageableEntitiesTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location castLocation = new Location(testWorld, 900, 40, 100);
        Location targetLocation = new Location(testWorld, 900, 40, 110);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(TestCommon.faceTarget(castLocation, targetLocation));

        Entity entity = testWorld.spawnEntity(targetLocation, EntityType.COW);
        BRACKIUM_EMENDO brackiumEmendo = new BRACKIUM_EMENDO(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, brackiumEmendo);

        // no entities at time 0
        assertTrue(brackiumEmendo.getNearbyLivingEntities(O2Spell.defaultRadius).isEmpty(), "getNearbyLivingEntities() included an entity at projectile spawn");
        assertTrue(brackiumEmendo.getNearbyDamageableEntities(O2Spell.defaultRadius).isEmpty(), "getNearbyDamageableEntities() included an entity at projectile spawn");

        // get player at time 2
        mockServer.getScheduler().performTicks(2);
        assertFalse(brackiumEmendo.getNearbyLivingEntities(4).isEmpty(), "Did not add player at time 2");
        assertFalse(brackiumEmendo.getNearbyDamageableEntities(4).isEmpty(), "Did not add damageable player at time 2");

        // get Cow at time 10
        mockServer.getScheduler().performTicks(8);
        assertFalse(brackiumEmendo.getNearbyLivingEntities(2).isEmpty(), "Did not find cow at time 10");
        assertFalse(brackiumEmendo.getNearbyDamageableEntities(2).isEmpty(), "Did not find damageable at time 10");
        brackiumEmendo.kill();
        entity.remove();

        // do not detect a non-living entity
        testWorld.spawnEntity(targetLocation, EntityType.MINECART);
        brackiumEmendo = new BRACKIUM_EMENDO(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, brackiumEmendo);
        mockServer.getScheduler().performTicks(10);
        assertTrue(brackiumEmendo.getNearbyLivingEntities(O2Spell.defaultRadius).isEmpty(), "non-living entity added");
        assertTrue(brackiumEmendo.getNearbyDamageableEntities(O2Spell.defaultRadius).isEmpty(), "non-damageable entity added");
    }

    /**
     * Verify {@link O2Spell#getNearbyPlayers(double)} includes the caster and other players in range as the projectile
     * moves, but never non-player entities.
     */
    @Test
    void getNearbyPlayersTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location castLocation = new Location(testWorld, 1000, 40, 100);
        Location targetLocation = new Location(testWorld, 1000, 40, 110);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(TestCommon.faceTarget(castLocation, targetLocation));

        Player player = mockServer.addPlayer();
        player.teleport(targetLocation);

        ACCIO accio = new ACCIO(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, accio);

        // no players at time 0
        assertTrue(accio.getNearbyPlayers(3).isEmpty(), "Added players at time 0");

        // detect the caster at time 2
        mockServer.getScheduler().performTicks(2);
        assertFalse(accio.getNearbyPlayers(4).isEmpty(), "Did not add player at time 2");

        // detect the other player at time 10
        mockServer.getScheduler().performTicks(8);
        assertFalse(accio.getNearbyPlayers(3).isEmpty(), "Did not find player at time 10");
        player.teleport(castLocation);
        accio.kill();
        mockServer.getScheduler().performTicks(10);

        // do not detect a cow
        testWorld.spawnEntity(targetLocation, EntityType.COW);
        accio = new ACCIO(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, accio);
        mockServer.getScheduler().performTicks(10);
        assertTrue(accio.getNearbyPlayers(3).isEmpty(), "Added non-player entity");
    }

    /**
     * Verify {@link O2Spell#getTargetBlock()} returns null while the projectile is moving and the hit block once it
     * stops on a target.
     */
    @Test
    void getTargetBlockTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location castLocation = new Location(testWorld, 1000, 40, 100);
        Location targetLocation = new Location(testWorld, 1000, 40, 110);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(TestCommon.faceTarget(castLocation, targetLocation));
        testWorld.getBlockAt(targetLocation).setType(Material.DIRT);

        DEFODIO defodio = new DEFODIO(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, defodio);

        assertNull(defodio.getTargetBlock(), "get target block returned block when target has not been hit");

        mockServer.getScheduler().performTicks(10);
        assertNotNull(defodio.getTargetBlock(), "get target block did not return target when hit");
    }

    /**
     * Verify {@link O2Spell#getText()} returns non-empty book text for a spell that has a description.
     */
    @Test
    void getTextTest() {
        ACCIO accio = new ACCIO(testPlugin);
        assertFalse(accio.getText().isEmpty());
    }

    /**
     * Verify {@link O2Spell#getFlavorText()} returns text for a spell that has flavor text and null for one that does
     * not.
     */
    @Test
    void getFlavorTextTest() {
        ACCIO accio = new ACCIO(testPlugin);
        assertNotNull(accio.getFlavorText());

        BOTHYNUS bothynus = new BOTHYNUS(testPlugin);
        assertNull(bothynus.getFlavorText());
    }

    /**
     * Verify {@link O2Spell#sendSuccessMessage()} sends the success message to the caster, both directly and when the
     * spell actually affects its target.
     */
    @Test
    void sendSuccessMessageTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location castLocation = new Location(testWorld, 1100, 40, 100);
        Location targetLocation = new Location(testWorld, 1100, 39, 110);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(TestCommon.faceTarget(castLocation, targetLocation));

        TestCommon.createBlockBase(targetLocation, 5);

        AGUAMENTI aguamenti = new AGUAMENTI(testPlugin, caster, O2PlayerCommon.rightWand);
        aguamenti.sendSuccessMessage();
        mockServer.getScheduler().performTicks(10);

        String message = caster.nextMessage();
        assertNotNull(message, "player did not get spell success message");
        assertEquals(aguamenti.getSuccessMessage(), TestCommon.cleanChatMessage(message), "player did not get expected success message");

        // verify success message sends when spell actually works
        Ollivanders2API.getSpells().addSpell(caster, aguamenti);
        mockServer.getScheduler().performTicks(20);
        assertTrue(aguamenti.isTransfigured());

        message = caster.nextMessage();
        assertNotNull(message, "player did not get spell success message when spell succeeded");
        assertEquals(aguamenti.getSuccessMessage(), TestCommon.cleanChatMessage(message), "player did not get expected success message  when spell succeeded");
    }

    /**
     * Verify {@link O2Spell#sendFailureMessage()} sends the failure message to the caster, both directly and when the
     * spell fails against its target.
     */
    @Test
    void sendFailureMessageTest() {
        World testWorld = mockServer.addSimpleWorld("O2Spell");
        Location location = new Location (testWorld, 1200, 40, 100);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        FIANTO_DURI fiantoDuri = new FIANTO_DURI(testPlugin, caster, O2PlayerCommon.rightWand);
        fiantoDuri.sendFailureMessage();

        String message = caster.nextMessage();
        assertNotNull(message, "player did not get spell failure message");
        assertEquals(fiantoDuri.getFailureMessage(), TestCommon.cleanChatMessage(message), "player did not get expected failure message");

        // verify failure message sends when spell actually fails
        TestCommon.createNorthSouthBlockWall(targetLocation, 5);
        Ollivanders2API.getSpells().addSpell(caster, fiantoDuri);
        mockServer.getScheduler().performTicks(20);

        message = caster.nextMessage();
        assertNotNull(message, "player did not get spell failure message when spell failed");
        assertEquals(fiantoDuri.getFailureMessage(), TestCommon.cleanChatMessage(message), "player did not get expected failure message when spell failed");
    }

    /**
     * Stop the MockBukkit server after all tests in the class complete.
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
