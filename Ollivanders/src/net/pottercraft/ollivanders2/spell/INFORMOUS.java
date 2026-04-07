package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO;
import net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS;
import net.pottercraft.ollivanders2.stationaryspell.HORCRUX;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.jetbrains.annotations.NotNull;

/**
 * Gives information on LivingEntity (health) and StationarySpellObj (duration) and weather (duration) and Player (spell effects).
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Informous_Spell">https://harrypotter.fandom.com/wiki/Informous_Spell</a>
 */
public final class INFORMOUS extends O2Spell {
    /**
     * Search radius (in blocks) around the projectile hit location used to find nearby stationary spells.
     * This is independent of each stationary spell's own effect radius — some spells like
     * {@link HARMONIA_NECTERE_PASSUS} have a radius of 1, which is too small for a player to
     * realistically hit with a projectile from outside the structure.
     */
    private static final double stationarySpellSearchRadius = 3.0;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public INFORMOUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.INFORMOUS;
        branch = O2MagicBranch.ARITHMANCY;

        flavorText = new ArrayList<>() {{
            add("Basic Arithmancy");
        }};

        text = "Gives information on a living entity, weather, player, or stationary spell.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public INFORMOUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.INFORMOUS;
        branch = O2MagicBranch.ARITHMANCY;

        initSpell();
    }

    /**
     * Give information about an entity, stationary spells at the target, or the weather at the player's location.
     */
    @Override
    protected void doCheckEffect() {
        boolean gaveInfo = false;

        // check for entities
        for (LivingEntity entity : getNearbyLivingEntities(defaultRadius)) {
            if (entity.getUniqueId().equals(caster.getUniqueId()))
                continue;

            entityInfo(entity);
            gaveInfo = true;
        }

        if (!gaveInfo && hasHitTarget()) {
            // check for stationary spells near the projectile hit location. We use our own search radius
            // rather than the stationary spell's radius because some spells (e.g., vanishing cabinets) have
            // a radius too small for a projectile to land inside from outside the structure.
            for (O2StationarySpell spell : Ollivanders2API.getStationarySpells().getActiveStationarySpells()) {
                if (spell.getLocation().getWorld() != location.getWorld())
                    continue;
                if (spell.getLocation().distance(location) < stationarySpellSearchRadius) {
                    stationarySpellInfo(spell);
                    gaveInfo = true;
                }
            }

            // if we didn't find a stationary spell, give information about the location
            if (!gaveInfo) {
                String weather;
                boolean thunder = world.isThundering();

                if (world.hasStorm()) {
                    weather = "rain";
                }
                else {
                    weather = "clear skies";
                }
                int weatherTime = world.getWeatherDuration();
                int thunderTime = world.getThunderDuration();

                caster.sendMessage(Ollivanders2.chatColor + "There will be " + weather + " for " + weatherTime / Ollivanders2Common.ticksPerSecond + " more seconds.");
                if (thunder) {
                    caster.sendMessage(Ollivanders2.chatColor + "There will be thunder for " + thunderTime / Ollivanders2Common.ticksPerSecond + " more seconds.");
                }
            }
        }

        if (gaveInfo || hasHitTarget()) // stop the spell if we hit a target or gave info about an entity
            kill();
    }

    /**
     * Give information about a player.
     *
     * @param entity the entity
     */
    private void entityInfo(@NotNull LivingEntity entity) {
        String entityName;

        if (entity instanceof HumanEntity)
            entityName = entity.getName();
        else
            entityName = entity.getType().toString();

        // health level
        caster.sendMessage(Ollivanders2.chatColor + entityName + " has " + entity.getHealth() + " health.");

        if (entity instanceof Player) {
            Player target = (Player) entity;

            // food level
            caster.sendMessage(Ollivanders2.chatColor + " has " + target.getFoodLevel() + " food level.");

            // exhaustion level
            caster.sendMessage(Ollivanders2.chatColor + " has " + target.getExhaustion() + " exhaustion level.");

            // detectable effects
            String infoText = Ollivanders2API.getPlayers().playerEffects.detectEffectWithInformous(entity.getUniqueId());
            if (infoText != null)
                caster.sendMessage(Ollivanders2.chatColor + " " + infoText + ".");

            // line of sight
            if (target.canSee(caster))
                caster.sendMessage(Ollivanders2.chatColor + " can see you.");
            else
                caster.sendMessage(Ollivanders2.chatColor + " cannot see you.");
        }
    }

    /**
     * Information about a stationary spell.
     *
     * @param spell the stationary spell
     */
    private void stationarySpellInfo(@NotNull O2StationarySpell spell) {
        if (spell instanceof HORCRUX) {
            Player stationarySpellCaster = Bukkit.getPlayer(spell.getCasterID());
            if (stationarySpellCaster == null) {
                common.printDebugMessage("INFORMOUS.stationarySpellInfo: null player", null, null, false);
                return;
            }
            caster.sendMessage(Ollivanders2.chatColor + spell.getSpellType().toString() + " of player " + stationarySpellCaster.getName() + " of radius " + spell.getRadius());
        }
        else if (spell instanceof ALIQUAM_FLOO) {
            caster.sendMessage(Ollivanders2.chatColor + "Floo registration of " + ((ALIQUAM_FLOO) spell).getFlooName());
        }
        else if (spell instanceof HARMONIA_NECTERE_PASSUS) {
            caster.sendMessage(Ollivanders2.chatColor + "Vanishing Cabinet");
        }
        else if (spell.isPermanent()) {
            caster.sendMessage(Ollivanders2.chatColor + spell.getSpellType().toString() + " of radius " + spell.getRadius());
        }
        else {
            caster.sendMessage(Ollivanders2.chatColor + spell.getSpellType().toString() + " of radius " + spell.getRadius() + " has " + spell.getDuration() / Ollivanders2Common.ticksPerSecond + " seconds left.");
        }
    }
}