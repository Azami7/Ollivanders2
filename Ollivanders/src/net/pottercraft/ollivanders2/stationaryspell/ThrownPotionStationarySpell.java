package net.pottercraft.ollivanders2.stationaryspell;

import com.sk89q.worldguard.protection.flags.StateFlag;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.block.BlockCommon;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Base class for stationary spells created by thrown potions, representing a lingering potion effect left in the world.
 * <p>
 * Unlike other stationary spells, these run their own WorldGuard permission check ({@link #checkWorldGuard()}), because
 * the thrown potion that spawns them does not perform that check the way a normal cast O2Spell would.
 *
 * @author Azami7
 */
public abstract class ThrownPotionStationarySpell extends O2StationarySpell {
    /**
     * The WorldGuard state flags this spell requires at every block in its radius; checked by {@link #checkWorldGuard()}.
     */
    List<StateFlag> worldGuardFlags = new ArrayList<>();

    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public ThrownPotionStationarySpell(@NotNull Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor for casting a new thrown-potion stationary spell.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
     */
    public ThrownPotionStationarySpell(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location) {
        super(plugin, pid, location);
    }

    /**
     * Kill this spell if WorldGuard is enabled and the caster lacks one of {@link #worldGuardFlags} anywhere within the
     * spell's radius. No-op when WorldGuard is disabled.
     */
    protected void checkWorldGuard() {
        if (!Ollivanders2.worldGuardEnabled)
            return;

        Player caster = p.getServer().getPlayer(playerUUID);
        if (caster == null) {
            kill();
            return;
        }

        for (StateFlag flag : worldGuardFlags) {
            for (Block block : BlockCommon.getBlocksInRadius(location, radius)) {
                if (!Ollivanders2.worldGuardO2.checkWGFlag(caster, block.getLocation(), flag)) {
                    common.printDebugMessage(spellType.toString() + " cannot be cast because of WorldGuard flag " + flag, null, null, false);

                    kill();
                    return;
                }
            }
        }
    }
}
