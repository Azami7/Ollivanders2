package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO;
import net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS;
import net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS;
import net.pottercraft.ollivanders2.stationaryspell.HORCRUX;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.jetbrains.annotations.NotNull;

/**
 * Gives information on LivingEntity (health) and StationarySpellObj (duration) and weather (duration) and Player (spell effects).
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Informous_Spell">https://harrypotter.fandom.com/wiki/Informous_Spell</a>
 */
public final class INFORMOUS extends O2Spell {
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
        if (!hasHitTarget())
            return;

        boolean gaveInfo = false;

        for (LivingEntity entity : getNearbyLivingEntities(1.5)) {
            if (entity.getUniqueId() == player.getUniqueId())
                continue;

            entityInfo(entity);
            gaveInfo = true;
        }

        if (!gaveInfo) {
            for (O2StationarySpell spell : Ollivanders2API.getStationarySpells().getActiveStationarySpells()) {
                if (spell.isLocationInside(location)) {
                    stationarySpellInfo(spell);
                    gaveInfo = true;
                }
            }
        }

        if (!gaveInfo) {
            Location playerLocation = player.getLocation();

            if (playerLocation.getY() > 256) {
                String weather;
                World world = playerLocation.getWorld();
                if (world == null) {
                    common.printDebugMessage("INFORMOUS.doCheckEffect: world is null", null, null, true);
                    kill();
                    return;
                }

                boolean thunder = world.isThundering();

                if (world.hasStorm()) {
                    weather = "rain";
                }
                else {
                    weather = "clear skies";
                }
                int weatherTime = world.getWeatherDuration();
                int thunderTime = world.getThunderDuration();

                player.sendMessage(Ollivanders2.chatColor + "There will be " + weather + " for " + weatherTime / Ollivanders2Common.ticksPerSecond + " more seconds.");
                if (thunder) {
                    player.sendMessage(Ollivanders2.chatColor + "There will be thunder for " + thunderTime / Ollivanders2Common.ticksPerSecond + " more seconds.");
                }
            }
        }

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
        player.sendMessage(Ollivanders2.chatColor + entityName + " has " + entity.getHealth() + " health.");

        if (entity instanceof Player) {
            Player target = (Player) entity;

            // food level
            player.sendMessage(Ollivanders2.chatColor + " has " + target.getFoodLevel() + " food level.");

            // exhaustion level
            player.sendMessage(Ollivanders2.chatColor + " has " + target.getExhaustion() + " exhaustion level.");

            // detectable effects
            String infoText = Ollivanders2API.getPlayers().playerEffects.detectEffectWithInformous(entity.getUniqueId());
            if (infoText != null)
                player.sendMessage(Ollivanders2.chatColor + " " + infoText + ".");

            // line of sight
            if (target.canSee(player))
                player.sendMessage(Ollivanders2.chatColor + " can see you.");
            else
                player.sendMessage(Ollivanders2.chatColor + " cannot see you.");
        }
    }

    /**
     * Information about a stationary spell.
     *
     * @param spell the stationary spell
     */
    private void stationarySpellInfo(@NotNull O2StationarySpell spell) {
        if (spell instanceof COLLOPORTUS) {
            int power;
            if (spell.getDuration() >= 1200)
                power = spell.getDuration() / 1200;
            else
                power = 1;

            player.sendMessage(Ollivanders2.chatColor + spell.getSpellType().toString() + " of radius " + spell.getRadius() + " has " + power + " power left.");
        }
        else if (spell instanceof HORCRUX) {
            Player caster = Bukkit.getPlayer(spell.getCasterID());
            String casterString;

            if (caster != null)
                casterString = " of player " + caster.getName();
            else
                casterString = " cast by persons unknown ";

            player.sendMessage(Ollivanders2.chatColor + spell.getSpellType().toString() + casterString + " of radius " + spell.getRadius());
        }
        else if (spell instanceof ALIQUAM_FLOO) {
            player.sendMessage(Ollivanders2.chatColor + "Floo registration of " + ((ALIQUAM_FLOO) spell).getFlooName());
        }
        else if (spell instanceof HARMONIA_NECTERE_PASSUS) {
            player.sendMessage(Ollivanders2.chatColor + "Vanishing Cabinet");
        }
        else {
            player.sendMessage(Ollivanders2.chatColor + spell.getSpellType().toString() + " of radius " + spell.getLocation() + " has " + spell.getDuration() / Ollivanders2Common.ticksPerSecond + " seconds left.");
        }
    }
}