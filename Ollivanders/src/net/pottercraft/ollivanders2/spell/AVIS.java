package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.EntityCommon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Parrot;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Bird-Conjuring Charm, which conjures a flock of birds from the tip of the wand.
 * <p>
 * This is a non-projectile spell: one bird is spawned at the caster's location on each tick until the number of birds
 * (determined by the caster's skill in {@link #doInitSpell()}) is exhausted, producing a stream of birds.
 * </p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Bird-Conjuring_Charm">Harry Potter Wiki - Bird-Conjuring Charm</a>
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

        noProjectile = true;

        initSpell();
    }

    /**
     * Sets the number of birds that can be summoned based on the caster's experience
     */
    @Override
    void doInitSpell() {
        birdsRemaining = (int)(usesModifier / 10); // at 50 experience this will be 5 birds

        if (birdsRemaining > maxBirds)
            birdsRemaining = maxBirds;
        else if (birdsRemaining < 1)
            birdsRemaining = 1;
    }

    /**
     * Spawn one bird at the caster's location each tick until the bird count is exhausted, then end the spell.
     */
    @Override
    protected void doCheckEffect() {
        if (birdsRemaining > 0) {
            Parrot bird = (Parrot) (world.spawnEntity(location, EntityType.PARROT));
            bird.setVariant(EntityCommon.getRandomParrotVariant());

            birdsRemaining = birdsRemaining - 1;
        }
        else
            kill();
    }

    /**
     * Get the number of birds still to be spawned by this cast.
     *
     * @return the remaining bird count
     */
    public int getBirdsRemaining() {
        return birdsRemaining;
    }

    /**
     * Get the maximum number of birds this spell can spawn.
     *
     * @return the maximum bird count
     */
    public int getMaxBirds() {
        return maxBirds;
    }
}