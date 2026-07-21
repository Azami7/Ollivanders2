package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.MagicLevel;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for spells that disguise the caster as a friendly mob, with larger mobs unlocked at higher skill.
 *
 * @see <a href="https://minecraft.fandom.com/wiki/Mob">Minecraft Wiki - Mob</a>
 * @author Azami7
 */
public abstract class FriendlyMobDisguise extends EntityDisguise
{
    /**
     * Small size friendly and neutral mobs (mostly birds and fish).
     */
    public static final List<EntityType> smallFriendlyMobs = new ArrayList<>()
    {{
        add(EntityType.BAT);
        add(EntityType.BEE);
        add(EntityType.CHICKEN);
        add(EntityType.COD);
        add(EntityType.PARROT);
        add(EntityType.PUFFERFISH);
        add(EntityType.RABBIT);
        add(EntityType.SALMON);
        add(EntityType.TROPICAL_FISH);
    }};

    /**
     * Medium size friendly mobs.
     */
    public static final List<EntityType> mediumFriendlyMobs = new ArrayList<>()
    {{
        add(EntityType.AXOLOTL);
        add(EntityType.CAT);
        add(EntityType.FOX);
        add(EntityType.OCELOT);
        add(EntityType.PIG);
        add(EntityType.SHEEP);
        add(EntityType.TURTLE);
        add(EntityType.WOLF);
    }};

    /**
     * Large size friendly mobs. Ender Dragon is explicitly excluded as it is too big/strong to target.
     */
    public static final List<EntityType> largeFriendlyMobs = new ArrayList<>()
    {{
        add(EntityType.COW);
        add(EntityType.DOLPHIN);
        add(EntityType.DONKEY);
        add(EntityType.GLOW_SQUID);
        add(EntityType.GOAT);
        add(EntityType.HORSE);
        add(EntityType.IRON_GOLEM);
        add(EntityType.LLAMA);
        add(EntityType.MOOSHROOM);
        add(EntityType.MULE);
        add(EntityType.PANDA);
        add(EntityType.POLAR_BEAR);
        add(EntityType.SHULKER); // not large in size but complex
        add(EntityType.SKELETON_HORSE);
        add(EntityType.SNOW_GOLEM);
        add(EntityType.SQUID);
        add(EntityType.STRIDER);
        add(EntityType.TRADER_LLAMA);
        add(EntityType.VILLAGER);
        add(EntityType.WANDERING_TRADER);
    }};

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public FriendlyMobDisguise(Ollivanders2 plugin)
    {
        super(plugin);
    }

    /**
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public FriendlyMobDisguise(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.DAMAGE_ANIMALS);

        failureMessage = "Your transfiguration failed.";
    }

    /**
     * Add the mob types this caster may disguise as to the allowed list: small mobs always, medium above skill 50,
     * large above skill 100 (each also gated by the caster's year when years are enabled). Must be called after
     * {@link #initSpell()} populates {@code usesModifier}.
     */
    void populateEntityAllowedList()
    {
        // add all small mobs as allowed targets by default
        entityAllowedList.addAll(smallFriendlyMobs);

        if (usesModifier > 50 || Ollivanders2.maxSpellLevel)
        {
            if (!Ollivanders2.useYears || casterO2P.getYear().getHighestLevelForYear().ordinal() >= MagicLevel.OWL.ordinal())
                entityAllowedList.addAll(mediumFriendlyMobs);
        }

        if (usesModifier > 100 || Ollivanders2.maxSpellLevel)
        {
            // if years are off or
            // if years are on and this player's year's max level is >= NEWT level
            if (!Ollivanders2.useYears || casterO2P.getYear().getHighestLevelForYear().ordinal() >= MagicLevel.NEWT.ordinal())
                entityAllowedList.addAll(largeFriendlyMobs);
        }
    }
}
