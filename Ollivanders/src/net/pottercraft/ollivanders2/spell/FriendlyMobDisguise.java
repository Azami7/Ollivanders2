package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Super class for transfiguring friendly mobs.
 *
 * @see <a href = "https://minecraft.fandom.com/wiki/Mob">https://minecraft.fandom.com/wiki/Mob</a>
 */
public abstract class FriendlyMobDisguise extends EntityDisguise
{
    /**
     * Small size friendly and neutral mobs (mostly birds and fish).
     */
    public static final List<EntityType> smallFriendlyMobs = new ArrayList<>()
    {{
        add(EntityType.BAT);
        add(EntityType.CHICKEN);
        add(EntityType.RABBIT);
        add(EntityType.PARROT);
        add(EntityType.COD);
        add(EntityType.SALMON);
        add(EntityType.TROPICAL_FISH);
        add(EntityType.PUFFERFISH);
        add(EntityType.BEE);
    }};

    /**
     * Medium size friendly mobs.
     */
    public static final List<EntityType> mediumFriendlyMobs = new ArrayList<>()
    {{
        add(EntityType.SHEEP);
        add(EntityType.PIG);
        add(EntityType.OCELOT);
        add(EntityType.WOLF);
        add(EntityType.CAT);
        add(EntityType.FOX);
        add(EntityType.TURTLE);
        add(EntityType.AXOLOTL);
    }};

    /**
     * Large size friendly mobs. Ender Dragon is explicitly excluded as it is too big/strong to target.
     */
    public static final List<EntityType> largeFriendlyMobs = new ArrayList<>()
    {{
        add(EntityType.COW);
        add(EntityType.DONKEY);
        add(EntityType.HORSE);
        add(EntityType.MOOSHROOM);
        add(EntityType.IRON_GOLEM);
        add(EntityType.SNOW_GOLEM);
        add(EntityType.MULE);
        add(EntityType.SQUID);
        add(EntityType.GLOW_SQUID);
        add(EntityType.POLAR_BEAR);
        add(EntityType.LLAMA);
        add(EntityType.SHULKER); // not large in size but complex
        add(EntityType.PANDA);
        add(EntityType.DOLPHIN);
        add(EntityType.TRADER_LLAMA);
        add(EntityType.SKELETON_HORSE);
        add(EntityType.STRIDER);
        add(EntityType.VILLAGER);
        add(EntityType.WANDERING_TRADER);
        add(EntityType.DOLPHIN);
        add(EntityType.GOAT);
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
     * Constructor.
     *
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
     * Populate the entity allowed list for this spell based on the caster's skill and level.
     *
     * <p>This must be called after initSpell() so that usesModifier is populated.</p>
     */
    void populateEntityAllowedList()
    {
        // add all small mobs as allowed targets by default
        entityAllowedList.addAll(smallFriendlyMobs);

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        if (o2p == null)
        {
            common.printDebugMessage("Null o2player in FriendlyMobDisguise.doInitSpell()", null, null, true);
            return;
        }

        if (usesModifier > 50 || Ollivanders2.maxSpellLevel)
        {
            if (!Ollivanders2.useYears || o2p.getYear().getHighestLevelForYear().ordinal() >= MagicLevel.OWL.ordinal())
                entityAllowedList.addAll(mediumFriendlyMobs);
        }

        if (usesModifier > 100 || Ollivanders2.maxSpellLevel)
        {
            // if years are off or
            // if years are on and this player's year's max level is >= NEWT level
            if (!Ollivanders2.useYears || o2p.getYear().getHighestLevelForYear().ordinal() >= MagicLevel.NEWT.ordinal())
                entityAllowedList.addAll(largeFriendlyMobs);
        }
    }
}
