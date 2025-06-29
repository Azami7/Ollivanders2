package net.pottercraft.ollivanders2.stationaryspell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Player will spawn here when killed, with all of their spell levels intact. Only fiendfyre can destroy it.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Horcrux-making_spell">https://harrypotter.fandom.com/wiki/Horcrux-making_spell</a>
 * {@link net.pottercraft.ollivanders2.spell.ET_INTERFICIAM_ANIMAM_LIGAVERIS}
 */
public class HORCRUX extends O2StationarySpell {
    // todo make this an item enchantment and not a stationary spell
    /**
     * the min radius for this spell
     */
    public static final int minRadiusConfig = 3;

    /**
     * the max radius for this spell
     */
    public static final int maxRadiusConfig = 3;

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public HORCRUX(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.HORCRUX;
        permanent = true;
        this.radius = minRadius = maxRadius = minRadiusConfig;
    }

    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
     */
    public HORCRUX(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location) {
        super(plugin, pid, location);

        spellType = O2StationarySpellType.HORCRUX;
        permanent = true;

        radius = minRadius = maxRadius = minRadiusConfig;
        duration = 10;

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Harm players who get too close to the Horcrux
     */
    @Override
    public void checkEffect() {
        List<LivingEntity> entities = getEntitiesInsideSpellRadius();
        for (LivingEntity entity : entities) {
            if (entity instanceof Player) {
                if (entity.getUniqueId() != getCasterID()) {
                    PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 200, 2);
                    PotionEffect wither = new PotionEffect(PotionEffectType.WITHER, 200, 3);
                    entity.addPotionEffect(blindness);
                    entity.addPotionEffect(wither);
                }
            }
        }
    }

    /**
     * Serialize all data specific to this spell so it can be saved.
     *
     * @return a map of the serialized data
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    /**
     * Deserialize the data for this spell and load the data to this spell.
     *
     * @param spellData a map of the saved spell data
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Restore a player back to full health if a damage event would kill them.
     *
     * @param event the event
     */
    @Override
    void doOnEntityDamageEvent(@NotNull EntityDamageEvent event) {
        Entity entity = event.getEntity(); // will never be null
        double damage = event.getDamage();

        if (!(entity instanceof Player) || damage <= 0 || entity.getUniqueId() != getCasterID())
            return;

        // we only want to consume 1 horcrux for this player
        if (!isFirstHorcrux(entity.getUniqueId()))
            return;

        // will this damage kill the player
        Player player = (Player) entity;
        if (damage < player.getHealth())
            return;

        // reset them to full health
        O2PlayerCommon.restoreFullHealth(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                playerFeedback(player);
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Is this horcrux the first instance for this player?
     *
     * @param playerID the player to check
     * @return true if this is the first instance of a horcrux for this player, false otherwise
     */
    private boolean isFirstHorcrux(UUID playerID) {
        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getActiveStationarySpells()) {
            if (stationarySpell.getSpellType() != O2StationarySpellType.HORCRUX)
                continue;

            if (stationarySpell.getCasterID() == playerID) {
                return stationarySpell == this;
            }
        }

        return true;
    }

    /**
     * Feedback to the player when they try to apparate.
     *
     * @param player the player
     */
    private void playerFeedback(@NotNull Player player) {
        player.sendMessage(Ollivanders2.chatColor + "Your Horcrux has been used to restore your health.");
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        flair(5);
    }

    @Override
    void doCleanUp() {}
}