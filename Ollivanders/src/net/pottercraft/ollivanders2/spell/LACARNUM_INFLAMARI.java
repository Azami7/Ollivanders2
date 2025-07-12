package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Shoots out a small fireball projectile.
 *
 * @author lownes
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Lacarnum_Inflamari">https://harrypotter.fandom.com/wiki/Lacarnum_Inflamari</a>
 */
public final class LACARNUM_INFLAMARI extends O2Spell {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public LACARNUM_INFLAMARI(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.LACARNUM_INFLAMARI;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("Some of the new incantations, such as ‘lacarnum inflamari’ must have sounded more dramatic onscreen – although by the time you’ve managed to say ‘lacarnum inflamari’, you’ve surely lost precious seconds in which the Devil’s Snare might have throttled you. But that’s showbiz.");
            add("She whipped out her wand, waved it, muttered something, and sent a jet of the same bluebell flames she had used on Snape at the plant. In a matter of seconds, the two boys felt it loosening its grip as it cringed away from the light and warmth.");
            add("Bluebell Flames");
            add("Cold Flames");
        }};

        text = "Lacarnum Inflamarae will shoot a fire charge out of the tip of your wand. This fire charge is not a spell, and thus can pass through normal anti-spell barriers.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LACARNUM_INFLAMARI(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.LACARNUM_INFLAMARI;
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.LIGHTER);

        initSpell();
    }

    @Override
    protected void doCheckEffect() {
        player.launchProjectile(SmallFireball.class, vector);

        kill();
    }
}
