package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Transfigures books into baby foxes (mice approximation).
 *
 * <p>Baby foxes are used as the Minecraft approximation of mice since rabbits already have their own
 * spell ({@link LAPIFORS}). At higher skill levels ({@code usesModifier > 100}), the transfiguration
 * becomes permanent and the original item is consumed.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Snufflifors_Spell">https://harrypotter.fandom.com/wiki/Snufflifors_Spell</a>
 */
public class SNUFFLIFORS extends ItemToEntityTransfiguration
{
    private static final int minDurationConfig = 30 * Ollivanders2Common.ticksPerSecond;
    private static final int maxDurationConfig = 10 * Ollivanders2Common.ticksPerMinute;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public SNUFFLIFORS(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.SNUFFLIFORS;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>()
        {{
            add("\"You're going to have a lot of fun with the Snufflifors Spell, Hermione! It's particularly useful at turning books into foxes! How cool is that?\" -Fred Weasley");
        }};

        text = "The Snufflifors Spell is a spell used to transfigure books into baby foxes.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public SNUFFLIFORS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.SNUFFLIFORS;
        branch = O2MagicBranch.TRANSFIGURATION;

        consumeOriginal = false;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 1.0;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.MOB_SPAWNING);

        transfigurationMap.put(Material.BOOK, EntityType.FOX);
        transfigurationMap.put(Material.WRITTEN_BOOK, EntityType.FOX);
        transfigurationMap.put(Material.WRITABLE_BOOK, EntityType.FOX);
        transfigurationMap.put(Material.ENCHANTED_BOOK, EntityType.FOX);
        transfigurationMap.put(Material.KNOWLEDGE_BOOK, EntityType.FOX);

        // the target entity type
        targetType = EntityType.FOX;

        initSpell();
    }

    /**
     * Determine success rate and whether this spell is permanent based on player skill level
     */
    @Override
    void doInitSpell()
    {
        successRate = (int)(usesModifier);

        if (usesModifier > 100)
        {
            consumeOriginal = true;
            permanent = true;
        }
    }

    /**
     * Make the fox a baby
     */
    @Override
    void customizeEntity()
    {
        if (transfiguredEntity instanceof Ageable)
            ((Ageable) transfiguredEntity).setBaby();
        else
            common.printDebugMessage("transfigured entity is not ageable in SNUFFLIFORS.customizeEntity()", null, null, false);
    }
}
