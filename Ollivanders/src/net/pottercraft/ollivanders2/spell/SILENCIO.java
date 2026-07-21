package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Silences a target player so they can only cast nonverbal spells.
 * <p>
 * Applies the {@link O2EffectType#MUTED_SPEECH} effect to the first nearby player the projectile reaches, for a
 * duration that scales with the caster's skill (limited to the bounds configured on {@link AddO2Effect}).
 * </p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Silencing_Charm">Harry Potter Wiki - Silencing Charm</a>
 */
public final class SILENCIO extends AddO2Effect {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public SILENCIO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.SILENCIO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The raven continued to open and close its sharp beak, but no sound came out.");
            add("The Silencing Charm");
        }};

        text = "Mutes the target for a time.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public SILENCIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.SILENCIO;
        branch = O2MagicBranch.CHARMS;

        effectsToAdd.add(O2EffectType.MUTED_SPEECH);
        minDurationInSeconds = 15;
        strengthModifier = 1;

        initSpell();
    }
 }