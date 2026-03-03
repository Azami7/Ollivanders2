package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Wand-Lighting Charm that grants night vision to the caster and nearby allies.
 *
 * <p>Lumos is an instant-radius charm that applies Night Vision to the caster and all targets
 * within the calculated effect radius. The effect duration depends on the caster's spell level,
 * with a minimum duration of 30 seconds. The effect radius scales with spell experience and
 * ranges from 5 to 20 blocks.</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Wand-Lighting_Charm">Wand-Lighting Charm</a>
 */
public class LUMOS extends AddPotionEffectInRadius {
    final static int minEffectRadiusConfig = 5;
    final static int maxEffectRadiusConfig = 20;
    private final static int minDurationInSecondsConfig = 30;

    /**
     * Default constructor for use in generating spell text.
     *
     * <p>Do not use this constructor to cast the spell. Use the three-parameter constructor instead.</p>
     *
     * @param plugin the Ollivanders2 plugin
     */
    public LUMOS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.LUMOS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("If in any doubt about your abilities you would do better to buy yourself a magic lantern.");
            add("The Wand-Lighting Charm");
            add("\"Ron, where are you? Oh this is stupid - lumos!\" She illuminated her wand and directed its narrow beam across the path. Ron was lying sprawled on the ground.");
            add("The Wand-Lighting Charm is simple, but requires concentration. Take care not to accidentally set your wand alight as damage of this kind can be permanent.");
        }};

        text = "Gives night vision to all the players in a radius.";
    }

    /**
     * Constructor for casting the spell.
     *
     * <p>Applies Night Vision effect to the caster and nearby entities within the effect radius.
     * Configures the spell with radius of 5-20 blocks and minimum duration of 30 seconds.</p>
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LUMOS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.LUMOS;
        branch = O2MagicBranch.CHARMS;

        effectTypes.add(PotionEffectType.NIGHT_VISION);
        amplifier = 0; // Night Vision I

        minDurationInSeconds = minDurationInSecondsConfig;
        minEffectRadius = minEffectRadiusConfig;
        maxEffectRadius = maxEffectRadiusConfig;
        targetSelf = true;

        initSpell();
    }
}