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
 * Protego Diabolica: a stationary ring of soul fire around the caster that burns disloyal players (muggles and members
 * of other houses) who move or teleport into it. The caster and members of the caster's house are immune to the fire.
 * <p>
 * The ring is soul sand at ground level with soul fire above, built on the first upkeep; the radius is locked once the
 * ring exists. A short cooldown keeps a player from taking repeated fire ticks.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Protego_Diabolica">Harry Potter Wiki - Protego Diabolica</a>
 */
public class PROTEGO_DIABOLICA extends O2StationarySpell {
    /**
     * Minimum spell radius, in blocks.
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius, in blocks.
     */
    public static final int maxRadiusConfig = 10;

    /**
     * Minimum spell duration: 5 minutes.
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Maximum spell duration: 30 minutes.
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Whether the fire ring has been built yet; set on the first {@link #upkeep()} and locks the radius afterward.
     */
    boolean createdFireRing = false;

    /**
     * Minimum ticks between fire damage ticks on the same player.
     */
    final static int maxCooldown = Ollivanders2Common.ticksPerSecond / 2;

    /**
     * Remaining damage cooldown per player, keyed by UUID.
     */
    final HashMap<UUID, Integer> damageCooldowns = new HashMap<>();

    /**
     * Constructor for loading a saved spell from disk or generating spell text; do not use to cast a new spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PROTEGO_DIABOLICA(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.PROTEGO_DIABOLICA;
    }

    /**
     * Constructor for casting a new Protego Diabolica spell.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell, clamped to the min/max bounds
     * @param duration the duration in ticks, clamped to the min/max bounds
     */
    public PROTEGO_DIABOLICA(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.PROTEGO_DIABOLICA;

        setRadius(radius);
        setDuration(duration, false);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Age the spell, build the fire ring on the first call, and count down each player's damage cooldown.
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
     * Build the ring of soul sand and soul fire at the spell radius, skipping positions where the fire block is not air
     * or the base block is unbreakable. Changed blocks are registered as temporary so {@link #doCleanUp()} can revert
     * them.
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
     * Set the radius, unless the fire ring already exists, in which case the radius is locked.
     *
     * @param radius the new radius, clamped to the min/max bounds
     */
    @Override
    void setRadius(int radius) {
        if (createdFireRing)
            return;

        super.setRadius(radius);
    }

    /**
     * Increase the radius, unless the fire ring already exists, in which case the radius is locked.
     *
     * @param increase the amount to increase the radius by
     */
    @Override
    public void increaseRadius(int increase) {
        if (createdFireRing)
            return;

        super.increaseRadius(increase);
    }

    /**
     * Decrease the radius, unless the fire ring already exists, in which case the radius is locked.
     *
     * @param decrease the amount to decrease the radius by
     */
    @Override
    public void decreaseRadius(int decrease) {
        if (createdFireRing)
            return;

        super.decreaseRadius(decrease);
    }

    /**
     * Burn a disloyal player who moves into the area.
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
     * Burn a disloyal player who teleports into the area.
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
     * Deal fire damage to a player unless they are the caster, loyal to the caster, or still on cooldown. Damaged
     * players are put on the damage cooldown.
     *
     * @param player the player to damage
     */
    void damageUnloyalPlayer(Player player) {
        if (player.getUniqueId().equals(getCasterID()) || damageCooldowns.containsKey(player.getUniqueId()))
            return;

        if (!isLoyal(player)) {
            DamageSource damageSource = DamageSource.builder(DamageType.IN_FIRE)
                    .withDamageLocation(player.getLocation())
                    .build();
            player.damage(10, damageSource);
            damageCooldowns.put(player.getUniqueId(), maxCooldown);
        }
    }

    /**
     * Whether a player is loyal to the caster, i.e. in the same house (two unsorted players count as the same house).
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
     * Prevent the caster or a loyal player inside the area from catching fire.
     *
     * @param event the entity combust event
     */
    @Override
    void doOnEntityCombustEvent(@NotNull EntityCombustEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Location entityLocation = entity.getLocation();

            if (isLocationInside(entityLocation)) {
                if (entity.getUniqueId().equals(getCasterID()) || isLoyal((Player) entity)) {
                    event.setCancelled(true);
                    common.printDebugMessage("PROTEGO_DIABOLICA: canceled EntityCombustEvent", null, null, false);
                }
            }
        }
    }

    /**
     * Prevent fire damage to the caster or a loyal player inside the area. Other damage is unaffected.
     *
     * @param event the entity damage event
     */
    @Override
    void doOnEntityDamageEvent(@NotNull EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Location entityLocation = entity.getLocation();

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

    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Revert the soul sand and soul fire blocks this spell placed.
     */
    @Override
    void doCleanUp() {
        Ollivanders2API.getBlocks().revertTemporarilyChangedBlocksBy(this);
    }
}
