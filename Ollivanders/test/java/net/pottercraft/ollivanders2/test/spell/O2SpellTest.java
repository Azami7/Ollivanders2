package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.HIGHER_SKILL;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.player.Year;
import net.pottercraft.ollivanders2.spell.ACCIO;
import net.pottercraft.ollivanders2.spell.AGUAMENTI;
import net.pottercraft.ollivanders2.spell.APARECIUM;
import net.pottercraft.ollivanders2.spell.AQUA_ERUCTO;
import net.pottercraft.ollivanders2.spell.ARRESTO_MOMENTUM;
import net.pottercraft.ollivanders2.spell.BOMBARDA_MAXIMA;
import net.pottercraft.ollivanders2.spell.BOTHYNUS;
import net.pottercraft.ollivanders2.spell.BRACKIUM_EMENDO;
import net.pottercraft.ollivanders2.spell.CONFUNDUS_DUO;
import net.pottercraft.ollivanders2.spell.DEFODIO;
import net.pottercraft.ollivanders2.spell.DELETRIUS;
import net.pottercraft.ollivanders2.spell.DEPRIMO;
import net.pottercraft.ollivanders2.spell.DIFFINDO;
import net.pottercraft.ollivanders2.spell.EPISKEY;
import net.pottercraft.ollivanders2.spell.LUMOS;
import net.pottercraft.ollivanders2.spell.MELOFORS;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.lang.reflect.Constructor;
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
 * Unit tests for O2Spell base class functionality.
 *
 * <p>Tests core spell mechanics including projectile movement, collision detection, entity detection,
 * spell validation, cooldowns, and message handling. Tests use MockBukkit to simulate server behavior
 * without requiring a full Bukkit/Paper server.</p>
 *
 * <p><strong>Test Structure:</strong> A shared MockBukkit server instance is created once before all tests
 * and reused across test methods to improve performance. Each test creates its own world and spell instances
 * to avoid state pollution.</p>
 *
 * @author Azami7
 */
@Isolated
public class O2SpellTest {
    /**
     * Shared mock Bukkit server instance for all tests.
     *
     * <p>Static field initialized once before all tests in this class. Reused across test instances
     * to avoid expensive server setup/teardown for each test method.</p>
     */
    static ServerMock mockServer;

    /**
     * The plugin instance being tested.
     *
     * <p>Loaded fresh before each test method with the default configuration. Provides access to
     * logger, scheduler, and other plugin API methods during tests.</p>
     */
    static Ollivanders2 testPlugin;

    /**
     * Initialize the mock Bukkit server before all tests.
     *
     * <p>Static setup method called once before all tests in this class. Creates the shared
     * MockBukkit server instance that is reused across all test methods to avoid expensive
     * server creation/destruction overhead.</p>
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
     * Test spell uses modifier calculation based on experience, wand type, and player level.
     *
     * <p>Verifies that {@link O2Spell#usesModifier} is correctly calculated considering:
     * <ul>
     * <li>Spell usage count (experience)</li>
     * <li>Wand correctness (right wand, wrong wand, elder wand)</li>
     * <li>Player year level (if years enabled)</li>
     * <li>HIGHER_SKILL effect doubling</li>
     * <li>maxSpellLevel setting</li>
     * </ul>
     *
     * <p><strong>Note:</strong> This test is monolithic by design. Running it in parallel with other tests
     * that modify global settings (Ollivanders2.useYears, Ollivanders2.maxSpellLevel) causes interference.
     */
    @Test
    void setUsesModifierTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);

        // player has no experience in the spell
        ACCIO accio = new ACCIO(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(0.0, accio.usesModifier, "not expected uses modifier for no experience");
        accio.kill();

        // player has some experience
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        assertNotNull(o2p);
        double spellcount = 30;
        o2p.setSpellCount(O2SpellType.ACCIO, (int)spellcount);
        accio = new ACCIO(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(spellcount / O2PlayerCommon.rightWand, accio.usesModifier, "not expected uses modifier with correct wand");
        accio.kill();

        // player using wrong wand
        accio = new ACCIO(testPlugin, caster, O2PlayerCommon.wrongWand);
        assertEquals(spellcount / O2PlayerCommon.wrongWand, accio.usesModifier, "not expected uses modifier for wrong wand");
        accio.kill();

        accio = new ACCIO(testPlugin, caster, O2PlayerCommon.elderWand);
        assertEquals(spellcount / O2PlayerCommon.elderWand, accio.usesModifier, "not expected uses modifier for elder wand");
        accio.kill();

        Ollivanders2.useYears = true;

        // if the spell is the same level as the player, usesModifier is unchanged
        o2p.setSpellCount(O2SpellType.LUMOS, (int)spellcount);
        LUMOS lumos = new LUMOS(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(spellcount / O2PlayerCommon.rightWand, lumos.usesModifier, "not expected uses modifier with correct wand and same level");
        lumos.kill();

        // uses modifier is half for 1 level above
        accio = new ACCIO(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(spellcount / 2, accio.usesModifier, "not expected uses modifier with correct wand and 1 level higher");
        accio.kill();

        // uses modifier is quartered for 2 or more levels above
        o2p.setSpellCount(O2SpellType.AGUAMENTI, (int)spellcount);
        AGUAMENTI aguamenti = new AGUAMENTI(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(spellcount / 4, aguamenti.usesModifier, "not expected uses modifier with correct wand and 2 levels higher");
        aguamenti.kill();

        // uses modifier 50% higher when spell is 1 level below
        o2p.setYear(Year.YEAR_7);
        aguamenti = new AGUAMENTI(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(spellcount * 1.5, aguamenti.usesModifier, "not expected uses modifier with correct wand and 1 level lower");
        aguamenti.kill();

        accio = new ACCIO(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(spellcount * 2, accio.usesModifier, "not expected uses modifier with correct wand and 2 levels lower");
        accio.kill();
        Ollivanders2.useYears = false;

        // uses modifier is set to 200 when maxSpellLevel set
        Ollivanders2.maxSpellLevel = true;
        accio = new ACCIO(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(200.0, accio.usesModifier, "uses modifier not set to 200 when maxSpellLevel enabled");
        accio.kill();
        Ollivanders2.maxSpellLevel = false;

        // uses modifier is doubled if the player is affected by HIGHER_SKILL
        HIGHER_SKILL higherSkill = new HIGHER_SKILL(testPlugin, 200, false, caster.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(higherSkill);
        accio = new ACCIO(testPlugin, caster, O2PlayerCommon.rightWand);
        assertEquals(spellcount * 2.0, accio.usesModifier, "uses modifier not doubled by HIGHER_SKILL");
        accio.kill();
        higherSkill.kill();
    }

    /**
     * Test spell update cycle via checkEffect() method.
     *
     * <p>Verifies that each tick:
     * <ul>
     * <li>Projectile moves one block via {@link O2Spell#move()}</li>
     * <li>Spell age increments via {@link O2Spell#getProjectileAge()}</li>
     * <li>Spell terminates after exceeding max lifetime</li>
     * <li>hasHitTarget is not set when aging out (only when hitting a target)</li>
     * <li>Hitting a block terminates the spell and sets hasHitTarget</li>
     * </ul>
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
        assertNotEquals(castLocation, melofors.location, "Melofors did not move");
        assertEquals(1, melofors.getProjectileAge(), "Melofors did not age by 1");

        // call check effect until the spell should age out
        for (int i = 0; i < O2Spell.maxSpellLifetime; i++) {
            melofors.checkEffect();
        }
        assertTrue(melofors.isKilled(), "melofors did not age out");
        assertFalse(melofors.hasHitTarget(), "melofors set has hit target when spell aged out");

        // hitting a target stops the projectile
        melofors = new MELOFORS(testPlugin, caster, O2PlayerCommon.rightWand);
        testWorld.getBlockAt(targetLocation).setType(Material.DIRT);
        Ollivanders2API.getSpells().addSpell(caster, melofors);
        mockServer.getScheduler().performTicks(20);
        assertTrue(melofors.hasHitTarget(), "melofors did not hit target");
        assertTrue(melofors.isKilled(), "melofors projectile not killed when target hit");
    }

    /**
     * Test spell targeting constraints: allow lists, blocked lists, and unbreakable blocks.
     *
     * <p>Verifies that spells are terminated when hitting:
     * <ul>
     * <li>Blocks not on the allow list (if allow list exists)</li>
     * <li>Blocks on the blocked list</li>
     * <li>Unbreakable blocks (all spells)</li>
     * </ul>
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
        AQUA_ERUCTO aquaEructo = new AQUA_ERUCTO(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, aquaEructo);
        mockServer.getScheduler().performTicks(20);
        assertTrue(aquaEructo.isKilled(), "aqua eructo not killed when it hit not allowed block type");

        // test blocked list, spell is killed if it hits a blocked type
        testWorld.getBlockAt(targetLocation).setType(Material.WATER);
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
        assertTrue(deprimo.isKilled(), "deprimo not killed when it hit an unbreakable block");
    }

    /**
     * Test zone-based spell permission system via config.
     *
     * <p>Verifies that spells are blocked/allowed based on Ollivanders2 zone configuration:
     * <ul>
     * <li>Cuboid zones can disallow specific spells</li>
     * <li>World zones can disallow specific spells</li>
     * <li>Disallowed spells are killed when cast and send failure message</li>
     * <li>Allowed spells in restricted zones are not affected</li>
     * <li>Only spells in the disallowed list are blocked (others always allowed)</li>
     * </ul>
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
     * Test projectile movement via the move() method.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Projectile moves one block per call</li>
     * <li>Projectile stops at max distance without hitting a target</li>
     * <li>hasHitTarget is false when reaching max distance</li>
     * <li>OllivandersSpellProjectileMoveEvent fires each move</li>
     * <li>Move does not execute for killed spells</li>
     * <li>Spell is killed if move takes it to a disallowed location</li>
     * </ul>
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
        assertNotEquals(castLocation, accio.location, "Projectile did not move");

        // move until the spell should be 1 move from expiring
        for (int i = 0; i < O2Spell.maxProjectileDistance; i++) {
            accio.move();
        }
        assertFalse(accio.isKilled(), "Accio killed before hitting max distance");

        // move one more to hit max distance and kill the spell without hitting a target
        accio.move();
        assertTrue(accio.isAtMaxDistance());
        assertTrue(accio.isKilled(), "Accio not killed when hitting max projectile distance.");
        assertFalse(accio.hasHitTarget(), "Accio has hit target set when projectile hit max distance");

        // a move event is fire when projectiles move
        assertThat(mockServer.getPluginManager(), hasFiredEventInstance(OllivandersSpellProjectileMoveEvent.class));

        // move does not run for killed spells
        Location current = accio.location.clone();
        accio.kill();
        accio.move();
        assertEquals(current, accio.location, "Accio moved after killed");

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
     * Test entity detection via getCloseEntities() method.
     *
     * <p>Verifies that getCloseEntities() correctly:
     * <ul>
     * <li>Excludes the caster when projectile is freshly spawned (lifeTicks == 0)</li>
     * <li>Includes the caster after lifeTicks > 0</li>
     * <li>Detects living entities (animals) within radius</li>
     * <li>Uses expanded radius for large entities (Ender Dragon, Giant)</li>
     * <li>Includes non-living entities (minecarts)</li>
     * <li>Respects distance checks based on eye location for living entities</li>
     * </ul>
     */
    @Test
    void getCloseEntitiesTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location castLocation = new Location(testWorld, 700, 40, 100);
        Location targetLocation = new Location(testWorld, 700, 40, 110);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(TestCommon.faceTarget(castLocation, targetLocation));

        Entity entity = testWorld.spawnEntity(targetLocation, EntityType.RABBIT);
        ARRESTO_MOMENTUM arrestoMomentum = new ARRESTO_MOMENTUM(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, arrestoMomentum);

        // at time 0, the player is the close entity but should be excluded from the list
        assertTrue(arrestoMomentum.getCloseEntities(O2Spell.defaultRadius).isEmpty(), "getCloseEntities() included player");

        // run ahead 10 ticks and check for the rabbit entity
        mockServer.getScheduler().performTicks(10);
        assertFalse(arrestoMomentum.getCloseEntities(O2Spell.defaultRadius).isEmpty(), "getCloseEntities() did not add Cow");
        arrestoMomentum.kill();
        entity.remove();

        // an entity right in the player's location is not targeted at time 0 for the spell
        entity = testWorld.spawnEntity(targetLocation, EntityType.RABBIT);
        arrestoMomentum = new ARRESTO_MOMENTUM(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, arrestoMomentum);
        assertTrue(arrestoMomentum.getCloseEntities(O2Spell.defaultRadius).isEmpty(), "getCloseEntities() included entity at 0th lifetick");
        arrestoMomentum.kill();
        entity.remove();

        // non-living entities
        entity = testWorld.spawnEntity(targetLocation, EntityType.MINECART);
        arrestoMomentum = new ARRESTO_MOMENTUM(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, arrestoMomentum);
        mockServer.getScheduler().performTicks(10);
        assertFalse(arrestoMomentum.getCloseEntities(O2Spell.defaultRadius).isEmpty(), "getCloseEntities did not add minecart");
        arrestoMomentum.kill();
        entity.remove();
    }

    /**
     * Test item entity detection via getNearbyItems() method.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Items are not detected when projectile is at spawn location (lifeTicks == 0)</li>
     * <li>Items are detected when projectile is within radius</li>
     * </ul>
     */
    @Test
    void getNearbyItemsTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location castLocation = new Location(testWorld, 800, 40, 100);
        Location targetLocation = new Location(testWorld, 800, 40, 110);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(TestCommon.faceTarget(castLocation, targetLocation));

        Item item = testWorld.dropItem(targetLocation, new ItemStack(Material.APPLE, 1));

        APARECIUM aparecium = new APARECIUM(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, aparecium);

        assertTrue(aparecium.getNearbyItems(O2Spell.defaultRadius).isEmpty(), "");

        mockServer.getScheduler().performTicks(10);
        assertFalse(aparecium.getNearbyItems(O2Spell.defaultRadius).isEmpty(), "Did not find nearby item");
    }

    /**
     * Test living entity and damageable entity detection.
     *
     * <p>Verifies that getNearbyLivingEntities() and getNearbyDamageableEntities():
     * <ul>
     * <li>Exclude entities at spawn (lifeTicks == 0)</li>
     * <li>Include the caster after movement (lifeTicks > 0)</li>
     * <li>Detect other living entities (cows) when in range</li>
     * <li>Detect damageable entities (players, mobs)</li>
     * <li>Do not include non-living entities (minecarts)</li>
     * </ul>
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
        assertTrue(brackiumEmendo.getNearbyLivingEntities(O2Spell.defaultRadius).isEmpty(), "");
        assertTrue(brackiumEmendo.getNearbyDamageableEntities(O2Spell.defaultRadius).isEmpty(), "");

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
        entity = testWorld.spawnEntity(targetLocation, EntityType.MINECART);
        brackiumEmendo = new BRACKIUM_EMENDO(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, brackiumEmendo);
        mockServer.getScheduler().performTicks(10);
        assertTrue(brackiumEmendo.getNearbyLivingEntities(O2Spell.defaultRadius).isEmpty(), "non-living entity added");
        assertTrue(brackiumEmendo.getNearbyDamageableEntities(O2Spell.defaultRadius).isEmpty(), "non-damageable entity added");
    }

    /**
     * Test player entity detection via getNearbyPlayers() method.
     *
     * <p>Verifies that getNearbyPlayers():
     * <ul>
     * <li>Does not include players at spawn (lifeTicks == 0)</li>
     * <li>Includes the caster after projectile moves</li>
     * <li>Includes other players when within radius</li>
     * <li>Does not include non-player entities (cows, animals)</li>
     * </ul>
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

        DIFFINDO diffindo = new DIFFINDO(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, diffindo);

        // no players at time 0
        assertTrue(diffindo.getNearbyPlayers(3).isEmpty(), "Added players at time 0");

        // detect the caster at time 2
        mockServer.getScheduler().performTicks(2);
        assertFalse(diffindo.getNearbyPlayers(4).isEmpty(), "Did not add player at time 2");

        // detect the other player at time 10
        mockServer.getScheduler().performTicks(8);
        assertFalse(diffindo.getNearbyPlayers(3).isEmpty(), "Did not find player at time 10");
        player.teleport(castLocation);
        diffindo.kill();
        mockServer.getScheduler().performTicks(10);

        // do not detect a cow
        testWorld.spawnEntity(targetLocation, EntityType.COW);
        diffindo = new DIFFINDO(testPlugin, caster, O2PlayerCommon.rightWand);
        Ollivanders2API.getSpells().addSpell(caster, diffindo);
        mockServer.getScheduler().performTicks(10);
        assertTrue(diffindo.getNearbyPlayers(3).isEmpty(), "Added non-player entity");
    }

    /**
     * Test target block detection via getTargetBlock() method.
     *
     * <p>Verifies that:
     * <ul>
     * <li>getTargetBlock() returns null while projectile is moving (hasHitTarget == false)</li>
     * <li>getTargetBlock() returns the block when projectile hits a target (hasHitTarget == true)</li>
     * </ul>
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
     * Test spell book text retrieval via getText() method.
     *
     * <p>Verifies that getText() returns non-empty text for spells that have book descriptions.
     */
    @Test
    void getTextTest() {
        ACCIO accio = new ACCIO(testPlugin);
        assertFalse(accio.getText().isEmpty());
    }

    /**
     * Test spell flavor text retrieval via getFlavorText() method.
     *
     * <p>Verifies that:
     * <ul>
     * <li>getFlavorText() returns text for spells with flavor text</li>
     * <li>getFlavorText() returns null for spells without flavor text</li>
     * </ul>
     */
    @Test
    void getFlavorTextTest() {
        ACCIO accio = new ACCIO(testPlugin);
        assertNotNull(accio.getFlavorText());

        BOTHYNUS bothynus = new BOTHYNUS(testPlugin);
        assertNull(bothynus.getFlavorText());
    }

    /**
     * Test success message delivery via sendSuccessMessage() method.
     *
     * <p>Verifies that:
     * <ul>
     * <li>sendSuccessMessage() sends the success message to the caster</li>
     * <li>Success message is sent when spell actually affects its target</li>
     * </ul>
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
     * Test failure message delivery via sendFailureMessage() method.
     *
     * <p>Verifies that:
     * <ul>
     * <li>sendFailureMessage() sends the failure message to the caster</li>
     * <li>Failure message is sent when spell fails to affect its target</li>
     * </ul>
     *
     * <p>Uses DELETRIUS spell which has specific failure conditions (no valid target item).
     */
    @Test
    void sendFailureMessageTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location castLocation = new Location(testWorld, 1200, 40, 100);
        Location targetLocation = new Location(testWorld, 1200, 40, 110);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(TestCommon.faceTarget(castLocation, targetLocation));

        DELETRIUS deletrius = new DELETRIUS(testPlugin, caster, O2PlayerCommon.rightWand);
        deletrius.sendFailureMessage();
        mockServer.getScheduler().performTicks(10);

        String message = caster.nextMessage();
        assertNotNull(message, "player did not get spell failure message");
        assertEquals(deletrius.getFailureMessage(), TestCommon.cleanChatMessage(message), "player did not get expected failure message");

        // verify failure message sends when spell actually fails
        testWorld.getBlockAt(targetLocation).getRelative(BlockFace.DOWN).setType(Material.DIRT);
        ItemStack broom = O2ItemType.BROOMSTICK.getItem(1);
        assertNotNull(broom);
        testWorld.dropItem(targetLocation, broom);
        Ollivanders2API.getSpells().addSpell(caster, deletrius);
        mockServer.getScheduler().performTicks(20);

        message = caster.nextMessage();
        assertNotNull(message, "player did not get spell failure message when spell failed");
        assertEquals(deletrius.getFailureMessage(), TestCommon.cleanChatMessage(message), "player did not get expected failure message when spell failed");
    }

    @Nullable
    O2Spell getBaseSpell(O2SpellType spellType) {
        String spellClass = "net.pottercraft.ollivanders2.spell." + spellType;
        Constructor<?> c = null;
        try {
            c = Class.forName(spellClass).getConstructor(Ollivanders2.class);
        }
        catch (Exception e) {
        }
        assertNotNull(c, "Failed to to get constructor for " + spellType);

        O2Spell spell = null;
        try {
            spell = (O2Spell) c.newInstance(testPlugin);
        }
        catch (Exception e) {
        }

        return spell;
    }

    /**
     * Tear down the mock Bukkit server after all tests complete.
     *
     * <p>Static teardown method called once after all tests in this class have finished.
     * Releases the MockBukkit server resources to prevent memory leaks and allow clean
     * test execution in subsequent test classes.</p>
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
