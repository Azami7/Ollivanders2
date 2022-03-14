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
 * The Snufflifors Spell (Snufflifors) is a spell used to transfigure books into mice.
 * <p>
 * Baby foxes are used as the MC approximation of mice (since rabbits, which are probably closer, have their own
 * spell already - lapifors).
 * <p>
 * https://harrypotter.fandom.com/wiki/Snufflifors_Spell
 */
public class SNUFFLIFORS extends BlockToEntityTransfiguration
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

        spellType = O2SpellType.PIERTOTUM_LOCOMOTOR;
        branch = O2MagicBranch.TRANSFIGURATION;

        consumeOriginal = true;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 1.0;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.MOB_SPAWNING);

        // materials this transfiguration works on
        materialAllowList.add(Material.BOOK);
        materialAllowList.add(Material.WRITTEN_BOOK);
        materialAllowList.add(Material.WRITABLE_BOOK);
        materialAllowList.add(Material.ENCHANTED_BOOK);
        materialAllowList.add(Material.KNOWLEDGE_BOOK);

        // the target entity type
        entityType = EntityType.FOX;

        initSpell();

        // put this after initSpell to ensure it is always 100
        successRate = 100;
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
