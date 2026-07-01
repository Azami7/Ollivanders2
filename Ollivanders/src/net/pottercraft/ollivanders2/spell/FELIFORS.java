package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.O2Color;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Transfigures a dropped cauldron item into a cat.
 * <p>
 * The Harry Potter canon for the Felifors Spell is inconsistent, describing it as turning a cat into a cauldron.
 * </p>
 * <p>
 * At low caster skill the transfiguration is temporary and the cauldron is restored when the spell expires. Once
 * {@code usesModifier} exceeds 100 the transfiguration becomes permanent and the original cauldron is consumed.
 * The spawned cat is given a random type and dyed collar color outside of test mode.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Felifors_Spell">Harry Potter Wiki - Felifors Spell</a>
 */
public class FELIFORS extends ItemToEntityTransfiguration {
    private static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 15;
    private static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public FELIFORS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.FELIFORS;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>() {{
            add("\"Today in Transfiguration Class we will learn how to turn cauldrons into cats. We will begin with a practical demonstration. Watch me and watch this cauldron. You should have seen this before.\" - Professor McGonagall to third-year students");
        }};

        text = "The transfiguration spell Felifors will transfigure a cauldron into a cat.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public FELIFORS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.FELIFORS;
        branch = O2MagicBranch.TRANSFIGURATION;

        consumeOriginal = false;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 1.0;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.MOB_SPAWNING);

        transfigurationMap.put(Material.CAULDRON, EntityType.CAT);

        // the target entity type
        targetType = EntityType.CAT;

        initSpell();
    }

    /**
     * Determine success rate and whether this spell is permanent based on player skill level.
     */
    @Override
    void doInitSpell() {
        if (Ollivanders2.testMode && usesModifier == O2Spell.spellMasteryLevel)
            successRate = 100;
        else {
            successRate = (int) (usesModifier);

            if (usesModifier > 100) {
                consumeOriginal = true;
                permanent = true;
            }
        }
    }

    /**
     * Give the spawned cat a random type and dyed collar color.
     * <p>
     * Skipped in test mode because MockBukkit does not reliably support setting cat state.
     * </p>
     */
    @Override
    void customizeEntity() {
        if (Ollivanders2.testMode)
            return;

        if (transfiguredEntity instanceof Cat cat) {
            cat.setCatType(EntityCommon.getRandomCatType());
            cat.setCollarColor(O2Color.getRandomDyeableColor().getDyeColor());
        }
        else
            common.printDebugMessage("transfigured entity is not a cat in FELIFORS.customizeEntity()", null, null, false);
    }


}
