package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Hides all players within its area. The code to hide players is located in OllivandersSchedule.invisPlayer()
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.REPELLO_MUGGLETON}
 */
public class REPELLO_MUGGLETON extends ShieldSpell
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
    public REPELLO_MUGGLETON(@NotNull Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2StationarySpellType.REPELLO_MUGGLETON;
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
    public REPELLO_MUGGLETON(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration)
    {
        super(plugin);

        spellType = O2StationarySpellType.REPELLO_MUGGLETON;
    }

    /**
     * Upkeep
     */
    @Override
    public void checkEffect()
    {
        age();

        if (duration % Ollivanders2Common.ticksPerSecond == 0)
        {
            Material toMat = getBlock().getType();
            byte toDat = getBlock().getData();
            double viewDistance = Math.sqrt(2 * Math.pow(((Bukkit.getServer().getViewDistance() + 1) * 16), 2));
            for (Player player : getBlock().getWorld().getPlayers())
            {
                if (player.getLocation().distance(location) < viewDistance && !isLocationInside(player.getLocation()))
                {
                    for (Block block : Ollivanders2Common.getBlocksInRadius(location, radius))
                    {
                        player.sendBlockChange(block.getLocation(), toMat, toDat);
                    }
                }
                else if (isLocationInside(player.getLocation()))
                {
                    for (Block block : Ollivanders2Common.getBlocksInRadius(location, radius))
                    {
                        player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
                    }
                }
            }
        }
        if (duration <= 1)
        {
            for (Player player : getBlock().getWorld().getPlayers())
            {
                for (Block block : Ollivanders2Common.getBlocksInRadius(location, radius))
                {
                    player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
                }
            }
        }
    }

    /**
     * Do not allow targeting if the target is inside the radius and the targeter is not
     *
     * @param event the event
     */
    @Override
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event)
    {
        Entity target = event.getTarget();
        Entity entity = event.getEntity(); // will never be null

        if (target == null)
            return;

        if (isLocationInside(target.getLocation()) && !isLocationInside(entity.getLocation()))
        {
            event.setCancelled(true);
            common.printDebugMessage("REPELLO_MUGGLETON: canceled EntityTargetEvent", null, null, false);
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