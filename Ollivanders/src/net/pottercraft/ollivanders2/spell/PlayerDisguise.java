package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Super class for all transfigurations of players.
 */
public abstract class PlayerDisguise extends EntityDisguise {
    //todo make player transfiguration an effect so it persists over log out/restarts

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PlayerDisguise(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PlayerDisguise(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        entityAllowedList.add(EntityType.PLAYER);

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.PVP);
    }

    /**
     * Calculate the success rate for this spell. This has to be run from the spell itself after setting
     * usesModifier since it is based on specific spell usage.
     */
    void calculateSuccessRate() {
        if (usesModifier < 10)
            successRate = 10;
        else if (usesModifier < 100)
            successRate = (int) usesModifier;
        else
            successRate = 100;
    }
}
