package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Nullum Evanescunt: a stationary spell that blocks apparition (by name or coordinates) and teleportation out of its
 * area, trapping those inside. It shares its radius and duration bounds with {@link NULLUM_APPAREBIT}.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Anti-Disapparition_Jinx">Harry Potter Wiki - Anti-Disapparition Jinx</a>
 */
public class NULLUM_EVANESCUNT extends O2StationarySpell {
    /**
     * The message shown to a player blocked from apparating or teleporting out of the area.
     */
    public static final String feedbackMessage = "A powerful magic protects this place.";

    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public NULLUM_EVANESCUNT(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.NULLUM_EVANESCUNT;
    }

    /**
     * Constructor for casting a new Nullum Evanescunt spell.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell, clamped to the min/max bounds
     * @param duration the duration in ticks, clamped to the min/max bounds
     */
    public NULLUM_EVANESCUNT(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.NULLUM_EVANESCUNT;

        setRadius(radius);
        setDuration(duration, false);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = NULLUM_APPAREBIT.minRadiusConfig;
        maxRadius = NULLUM_APPAREBIT.maxRadiusConfig;
        minDuration = NULLUM_APPAREBIT.minDurationConfig;
        maxDuration = NULLUM_APPAREBIT.maxDurationConfig;
    }

    @Override
    public void upkeep() {
        age();
    }

    /**
     * Cancel apparition-by-name by a player inside the area and give them feedback.
     *
     * @param event the apparate by name event
     */
    @Override
    void doOnOllivandersApparateByNameEvent(@NotNull OllivandersApparateByNameEvent event) {
        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();

        if (isLocationInside(playerLocation)) {
            event.setCancelled(true);
            common.printDebugMessage("NULLUM_EVANESCUNT: canceled OllivandersApparateByNameEvent", null, null, false);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.isCancelled())
                    playerFeedback(player);
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Cancel apparition-by-coordinates by a player inside the area and give them feedback.
     *
     * @param event the apparate by coordinates event
     */
    @Override
    void doOnOllivandersApparateByCoordinatesEvent(@NotNull OllivandersApparateByCoordinatesEvent event) {
        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();

        if (isLocationInside(playerLocation)) {
            event.setCancelled(true);
            common.printDebugMessage("NULLUM_EVANESCUNT: canceled OllivandersApparateByCoordinatesEvent", null, null, false);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.isCancelled())
                    playerFeedback(player);
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Cancel a non-player entity teleporting out of the area.
     *
     * @param event the entity teleport event
     */
    @Override
    void doOnEntityTeleportEvent(@NotNull EntityTeleportEvent event) {
        Entity entity = event.getEntity();
        Location entityLocation = entity.getLocation();

        if (isLocationInside(entityLocation)) {
            event.setCancelled(true);
            common.printDebugMessage("NULLUM_EVANESCUNT: canceled EntityTeleportEvent", null, null, false);
        }
    }

    /**
     * Cancel a player teleporting out of the area and give them feedback.
     *
     * @param event the player teleport event
     */
    @Override
    void doOnPlayerTeleportEvent(@NotNull PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();

        if (isLocationInside(playerLocation)) {
            event.setCancelled(true);
            common.printDebugMessage("NULLUM_EVANESCUNT: canceled PlayerTeleportEvent", null, null, false);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.isCancelled())
                    playerFeedback(player);
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Tell a blocked player the area is protected, with a message, sound, and flair.
     *
     * @param player the player who was blocked
     */
    private void playerFeedback(@NotNull Player player) {
        player.sendMessage(Ollivanders2.chatColor + feedbackMessage);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        flair(5);
    }

    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    @Override
    void doCleanUp() {
    }
}