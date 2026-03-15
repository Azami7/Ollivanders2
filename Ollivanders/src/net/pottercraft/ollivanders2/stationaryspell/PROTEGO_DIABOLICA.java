package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.block.BlockCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Protego Diabolica creates a protective ring of soul fire around the caster that harms players
 * outside the caster's house who cross the boundary.
 *
 * <p>The spell creates a ring of soul sand at ground level with soul fire above it. Once the
 * ring is created, the radius cannot be modified. The spell damages disloyal players (Muggles and
 * students from other houses) when they move or teleport into the protected area, with a cooldown
 * to prevent rapid damage ticks. Loyal players (the caster and students from the same house) are
 * immune to fire damage from the spell.</p>
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Creates a ring of soul sand and soul fire at the spell's radius on first upkeep</li>
 * <li>Deals fire damage to Muggles and students from other houses who enter the protected area</li>
 * <li>Does not harm the caster or students from the same house</li>
 * <li>Radius modification locked after fire ring creation</li>
 * <li>Damage cooldown of 0.5 seconds prevents rapid fire ticks on the same player</li>
 * <li>Protects from fire damage from the created soul fire blocks</li>
 * <li>Radius range: 5-10 blocks</li>
 * <li>Duration range: 5-30 minutes</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Protego_Diabolica">https://harrypotter.fandom.com/wiki/Protego_Diabolica</a>
 */
public class PROTEGO_DIABOLICA extends O2StationarySpell {
    /**
     * Minimum spell radius
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius
     */
    public static final int maxRadiusConfig = 10;

    /**
     * Minimum spell duration
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Maximum spell duration
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Flag to track if fire ring has been created on first upkeep
     */
    boolean createdFireRing = false;

    /**
     * Cooldown between damage ticks for players (0.5 seconds in ticks)
     */
    final static int maxCooldown = Ollivanders2Common.ticksPerSecond / 2;

    /**
     * Tracks damage cooldown for each player by UUID
     */
    final HashMap<UUID, Integer> damageCooldowns = new HashMap<>();

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PROTEGO_DIABOLICA(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.PROTEGO_DIABOLICA;
    }

    /**
     * Constructs a new PROTEGO_DIABOLICA spell cast by a player.
     *
     * <p>Creates a ring of soul sand and soul fire around the caster's location with the specified radius and duration.
     * Players outside the caster's house who enter the protected area take fire damage.</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the spell =
     * @param radius   the radius for this spell =
     * @param duration the duration of the spell in ticks
     */
    public PROTEGO_DIABOLICA(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.PROTEGO_DIABOLICA;

        setRadius(radius);
        setDuration(duration);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Initializes the minimum and maximum radius and duration values.
     */
    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Handles spell upkeep on each tick.
     *
     * <p>Ages the spell (decrements duration), creates the fire ring on the first upkeep,
     * and decrements damage cooldowns for all players.</p>
     */
    @Override
    public void upkeep() {
        age();

        if (!createdFireRing)
            createFireRing();

        HashMap<UUID, Integer> cooldowns = new HashMap<>() {{
            putAll(damageCooldowns);
        }};
        for (UUID playerID : cooldowns.keySet()) {
            int cooldown = cooldowns.get(playerID);

            cooldown = cooldown - 1;
            if (cooldown <= 0)
                damageCooldowns.remove(playerID);
            else
                damageCooldowns.put(playerID, cooldown);
        }
    }

    /**
     * Creates the soul sand and soul fire ring around the spell center.
     *
     * <p>Iterates 360 degrees around the spell location at the configured radius,
     * placing soul sand at ground level and soul fire above it. Skips positions where
     * the fire block is not air or the base block is unbreakable. All changed blocks
     * are tracked for reversion when the spell expires.</p>
     */
    void createFireRing() {
        common.printDebugMessage("PROTEGO_DIABOLICA.createFireRing: creating fire ring", null, null, false);

        int baseY = location.getBlockY() - 1;

        for (int angle = 0; angle < 360; angle++) {
            double rad = Math.toRadians(angle);
            int blockX = (int) Math.floor(location.getX() + radius * Math.cos(rad));
            int blockZ = (int) Math.floor(location.getZ() + radius * Math.sin(rad));

            Block baseBlock = world.getBlockAt(blockX, baseY, blockZ);
            Block fireBlock = baseBlock.getRelative(BlockFace.UP);

            if (BlockCommon.isAirBlock(fireBlock) && !Ollivanders2Common.getUnbreakableMaterials().contains(baseBlock.getType())) {
                if (Ollivanders2API.getBlocks().addTemporarilyChangedBlock(baseBlock, this)) {
                    baseBlock.setType(Material.SOUL_SAND);
                }
                if (Ollivanders2API.getBlocks().addTemporarilyChangedBlock(fireBlock, this)) {
                    fireBlock.setType(Material.SOUL_FIRE);
                }
            }
            else
                common.printDebugMessage("PROTEGO_DIABOLICA.createFireRing: cannot create fire at " + fireBlock.getLocation().getX() + ", " + fireBlock.getLocation().getY() + ", " + fireBlock.getLocation().getZ(), null, null, false);
        }

        createdFireRing = true;
    }

    /**
     * Sets the spell radius if the fire ring has not yet been created.
     *
     * <p>Once the fire ring is created, the radius becomes locked and cannot be modified.</p>
     *
     * @param radius the new radius value (will be clamped to min/max bounds)
     */
    @Override
    void setRadius(int radius) {
        if (createdFireRing) // spell's radius cannot be changed once the fire ring is active
            return;

        super.setRadius(radius);
    }

    /**
     * Increases the spell radius if the fire ring has not yet been created.
     *
     * <p>Once the fire ring is created, the radius becomes locked and cannot be modified.</p>
     *
     * @param increase the amount to increase the radius by
     */
    @Override
    public void increaseRadius(int increase) {
        if (createdFireRing) // spell's radius cannot be changed once the fire ring is active
            return;

        super.increaseRadius(increase);
    }

    /**
     * Decreases the spell radius if the fire ring has not yet been created.
     *
     * <p>Once the fire ring is created, the radius becomes locked and cannot be modified.</p>
     *
     * @param decrease the amount to decrease the radius by
     */
    @Override
    public void decreaseRadius(int decrease) {
        if (createdFireRing) // spell's radius cannot be changed once the fire ring is active
            return;

        super.decreaseRadius(decrease);
    }

    /**
     * Handles player move events to damage players moving into the spell area.
     *
     * @param event the player move event
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        Location toLocation = event.getTo();

        if (toLocation != null && isLocationInside(toLocation)) {
            damageUnloyalPlayer(event.getPlayer());
        }
    }

    /**
     * Handles player teleport events to damage players teleporting into the spell area.
     *
     * @param event the player teleport event
     */
    @Override
    void doOnPlayerTeleportEvent(@NotNull PlayerTeleportEvent event) {
        Location toLocation = event.getTo();
        if (toLocation != null && isLocationInside(toLocation)) {
            damageUnloyalPlayer(event.getPlayer());
        }
    }

    /**
     * Damages a player if they are not in the caster's house and not on cooldown.
     *
     * <p>Checks if the player is a Muggle or in a different house than the caster.
     * If so, applies fire damage and adds them to the damage cooldown map.</p>
     *
     * @param player the player to potentially damage
     */
    void damageUnloyalPlayer(Player player) {
        if (player.getUniqueId().equals(getCasterID()) || damageCooldowns.containsKey(player.getUniqueId())) // don't damage the caster, only damage player every cooldown ticks
            return;

        if (!isLoyal(player)) {
            // add fire damage to player
            DamageSource damageSource = DamageSource.builder(DamageType.IN_FIRE)
                    .withDamageLocation(player.getLocation())
                    .build();
            player.damage(10, damageSource);
            damageCooldowns.put(player.getUniqueId(), maxCooldown);
        }
    }

    /**
     * Determines if a player is loyal to the caster (same house or both unsorted).
     *
     * <p>A player is considered loyal if they are in the same house as the caster.
     * If both the player and caster are unsorted (no house), they are also considered loyal.</p>
     *
     * @param player the player to check
     * @return true if the player is loyal, false otherwise
     */
    boolean isLoyal(Player player) {
        O2Player o2Player = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        if (o2Player == null) {
            common.printDebugMessage("PROTEGO_DIABOLICA.isLoyal: o2player is null", null, null, true);
            return false;
        }

        return Ollivanders2API.getHouses().getHouse(player) == Ollivanders2API.getHouses().getHouse(getCasterID());
    }

    /**
     * Handles entity combust events to prevent loyal players from being set on fire.
     *
     * <p>Cancels the combust event if the entity is a loyal player inside the spell area.</p>
     *
     * @param event the entity combust event
     */
    @Override
    void doOnEntityCombustEvent(@NotNull EntityCombustEvent event) {
        Entity entity = event.getEntity(); // will never be null

        if (entity instanceof Player) {
            Location entityLocation = entity.getLocation(); // will never be null

            if (isLocationInside(entityLocation)) {
                if (entity.getUniqueId().equals(getCasterID()) || isLoyal((Player) entity)) {
                    event.setCancelled(true);
                    common.printDebugMessage("PROTEGO_DIABOLICA: canceled EntityCombustEvent", null, null, false);
                }
            }
        }
    }

    /**
     * Handles entity damage events to prevent fire damage to loyal players.
     *
     * <p>Cancels fire damage events if the entity is a loyal player inside the spell area.</p>
     *
     * @param event the entity damage event
     */
    @Override
    void doOnEntityDamageEvent(@NotNull EntityDamageEvent event) {
        Entity entity = event.getEntity(); // will never be null

        if (entity instanceof Player) {
            Location entityLocation = entity.getLocation(); // will never be null

            if (event.getCause() == EntityDamageEvent.DamageCause.CAMPFIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                if (isLocationInside(entityLocation)) {
                    if (entity.getUniqueId().equals(getCasterID()) || isLoyal((Player) entity)) {
                        event.setCancelled(true);
                        common.printDebugMessage("PROTEGO_DIABOLICA: canceled EntityDamageEvent", null, null, false);
                    }
                }
            }
        }
    }

    /**
     * Serializes spell data for persistence.
     *
     * <p>PROTEGO_DIABOLICA does not have persistent data beyond the base spell properties.</p>
     *
     * @return an empty map (no custom data to serialize)
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    /**
     * Deserializes spell data from stored persistence data.
     *
     * <p>PROTEGO_DIABOLICA does not have custom data to deserialize.</p>
     *
     * @param spellData the stored spell data
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Cleans up when the spell ends.
     *
     * <p>Reverts all temporarily changed blocks (soul sand and soul fire) back to their original state.</p>
     */
    @Override
    void doCleanUp() {
        Ollivanders2API.getBlocks().revertTemporarilyChangedBlocksBy(this);
    }
}
