package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Only players within this can hear other conversation from other players within. Duration depending on spell's level.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Muffliato_Charm
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.MUFFLIATO}
 */
public class MUFFLIATO extends ShieldSpell
{
    public static final int minRadiusConfig = 5;
    public static final int maxRadiusConfig = 20;
    public static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 30;
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public MUFFLIATO(@NotNull Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2StationarySpellType.MUFFLIATO;
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
    public MUFFLIATO(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration)
    {
        super(plugin);
        spellType = O2StationarySpellType.MUFFLIATO;
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;

        setPlayerID(pid);
        setLocation(location);
        setRadius(radius);
        setDuration(duration);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Age the spell by 1 tick
     */
    @Override
    public void checkEffect()
    {
        age();
    }

    /**
     * Handle player chat
     *
     * @param event the event
     */
    @Override
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event)
    {
        Player speaker = event.getPlayer();

        if (!isLocationInside(speaker.getLocation()))
            return;

        Set<Player> recipients = new HashSet<>(event.getRecipients());

        for (Player player : recipients)
        {
            if (!isLocationInside(player.getLocation()))
                event.getRecipients().remove(player);
        }
    }

    @Override
    @NotNull
    public Map<String, String> serializeSpellData()
    {
        return new HashMap<>();
    }

    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData)
    {
    }
}