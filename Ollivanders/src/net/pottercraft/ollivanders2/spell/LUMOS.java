package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Wand-Lighting Charm: grants Night Vision to the caster and every player in a skill-scaled radius.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Wand-Lighting_Charm">Harry Potter Wiki - Wand-Lighting Charm</a>
 */
public class LUMOS extends AddPotionEffectInRadius {
    public final static int minEffectRadiusConfig = 5;
    public final static int maxEffectRadiusConfig = 20;
    private final static int minDurationInSecondsConfig = 30;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
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
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LUMOS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.LUMOS;
        branch = O2MagicBranch.CHARMS;

        potionEffectTypes.add(PotionEffectType.NIGHT_VISION);
        amplifier = 0; // Night Vision I

        minDurationInSeconds = minDurationInSecondsConfig;
        minEffectRadius = minEffectRadiusConfig;
        maxEffectRadius = maxEffectRadiusConfig;
        targetSelf = true;

        initSpell();
    }
}