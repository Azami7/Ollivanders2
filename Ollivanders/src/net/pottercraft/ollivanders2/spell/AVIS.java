package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.EntityCommon;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Parrot;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Bird-Conjuring Charm - https://harrypotter.fandom.com/wiki/Bird-Conjuring_Charm - conjures a flock of birds from the
 * tip of the wand.
 */
public final class AVIS extends O2Spell {
    /**
     * The number of birds remaining to be spawned
     */
    private int birdsRemaining = 1;

    /**
     * The max number of birds this spell can spawn
     */
    private static final int maxBirds = 10;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AVIS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.AVIS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Bird-Conjuring Charm");
            add("Most of the class had already left, although several twittering yellow birds were still zooming around the room, all of Hermione's creation; nobody else had succeeded in conjuring so much as a feather from thin air.");
            add("\"Oh, hello, Harry ... I was just practicing.\" -Hermione Granger conjuring small golden birds just before sending them to attack Ron");
        }};

        text = "Causes one or more birds to fly out of the tip of your wand.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public AVIS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.AVIS;
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.MOB_SPAWNING);

        initSpell();
    }

    /**
     * Sets the number of birds that can be summoned based on the caster's experience
     */
    @Override
    void doInitSpell() {
        birdsRemaining = (int) usesModifier / 4;

        if (birdsRemaining > maxBirds)
            birdsRemaining = maxBirds;
        else if (birdsRemaining < 1)
            birdsRemaining = 1;
    }

    /**
     * Shoot a stream of birds from the caster's wand
     */
    @Override
    public void checkEffect() {
        if (!isSpellAllowed()) {
            kill();
            return;
        }

        if (birdsRemaining > 0) {
            World world = location.getWorld();
            if (world == null) {
                common.printDebugMessage("AVIS.checkEffect: world is null", null, null, true);
                kill();
                return;
            }

            Parrot bird = (Parrot) (world.spawnEntity(location, EntityType.PARROT));
            bird.setVariant(EntityCommon.getRandomParrotColor());

            birdsRemaining = birdsRemaining - 1;
        }
        else
            kill();
    }

    /**
     * Nothing to do since we overrode checkEffect() itself
     */
    @Override
    protected void doCheckEffect() {
    }
}