package net.pottercraft.ollivanders2.stationaryspell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.util.Vector;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * The basic protection spell
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Shield_Charm">https://harrypotter.fandom.com/wiki/Shield_Charm</a>
 * {@link net.pottercraft.ollivanders2.spell.PROTEGO}
 */
public class PROTEGO extends ShieldSpell {
    /**
     * min radius for this spell
     */
    public static final int minRadiusConfig = 5;
    /**
     * max radius for this spell
     */
    public static final int maxRadiusConfig = 20;
    /**
     * min duration for this spell - 15 seconds
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 15;
    /**
     * max duration for this spell - 1 min
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute;

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public PROTEGO(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.PROTEGO;
    }

    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell
     * @param duration the duration of the spell
     */
    public PROTEGO(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.PROTEGO;

        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;

        setRadius(radius);
        setDuration(duration);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Age the spell by 1 tick,
     */
    @Override
    public void checkEffect() {
        age();

        Player ply = Bukkit.getPlayer(getCasterID());
        if (ply == null) {
            kill();
            return;
        }

        double rightWand = Ollivanders2API.playerCommon.wandCheck(ply);
        if (ply.isSneaking() && rightWand != -1) {
            location = ply.getEyeLocation();
            flair(1);

            List<O2Spell> projectiles = p.getProjectiles();

            for (O2Spell proj : projectiles) {
                if (isLocationInside(proj.location) && proj.spellType.getLevel().ordinal() <= this.spellType.getLevel().ordinal()) {
                    if (location.distance(proj.location) > radius - 1) {
                        Vector N = proj.location.toVector().subtract(location.toVector()).normalize();
                        double b = p.getSpellCount(ply, O2SpellType.PROTEGO) / rightWand / 10.0;
                        b += 1;
                        Vector V = proj.vector.clone();
                        proj.vector = N.multiply((V.dot(N))).multiply(-2).add(V).multiply(b);
                        flair(10);
                    }
                }
            }
        }
    }

    /**
     * Handle entity combust by block events
     *
     * @param event the event
     */
    @Override
    void doOnEntityCombustEvent(@NotNull EntityCombustEvent event) {
        Entity entity = event.getEntity(); // will never be null
        Location entityLocation = entity.getLocation();

        if (isLocationInside(entityLocation)) {
            event.setCancelled(true);
            common.printDebugMessage("PROTEGO: canceled PlayerInteractEvent", null, null, false);
        }
    }

    /**
     * Handle spell projectile move events
     *
     * @param event the spell projectile move event
     */
    void doOnSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
        // is the spell inside this protego?
        Location to = event.getTo(); // will never be null
        if (!isLocationInside(to))
            return;

        // did it originate within this protego?
        Location from = event.getFrom(); // will never be null
        if (isLocationInside(from))
            return;

        // is this spell higher level than Protego?
        O2Spell spell = event.getSpell(); // will never be null
        if (spell.spellType.getLevel().ordinal() > this.spellType.getLevel().ordinal())
            return;

        event.setCancelled(true);
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
    void doCleanUp() {}
}