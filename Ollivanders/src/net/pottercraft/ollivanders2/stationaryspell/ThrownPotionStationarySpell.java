package net.pottercraft.ollivanders2.stationaryspell;

import com.sk89q.worldguard.protection.flags.StateFlag;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class ThrownPotionStationarySpell extends O2StationarySpell {
    /**
     * A list of the worldguard permissions needed for this spell
     */
    List<StateFlag> worldGuardFlags = new ArrayList<>();

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public ThrownPotionStationarySpell(@NotNull Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
     */
    public ThrownPotionStationarySpell(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location) {
        super(plugin, pid, location);
    }

    /**
     * Checks world guard, if enabled, to determine if this spell can be cast here by this player. Normally stationary
     * spells don't need to check this because they are cast by O2Spells which check this for them but this stationary
     * spell is a thrown potion effect
     */
    protected void checkWorldGuard() {
        if (!Ollivanders2.worldGuardEnabled)
            return;

        Player caster = p.getServer().getPlayer(playerUUID);
        if (caster == null) {
            kill();
            return;
        }

        for (StateFlag flag : worldGuardFlags) { // check every flag relevant to this spell
            for (Block block : Ollivanders2Common.getBlocksInRadius(location, radius)) { // check at every block location in the spell's radius
                if (!Ollivanders2.worldGuardO2.checkWGFlag(caster, block.getLocation(), flag)) {
                    common.printDebugMessage(spellType.toString() + " cannot be cast because of WorldGuard flag " + flag, null, null, false);

                    kill();
                    return;
                }
            }
        }
    }
}
