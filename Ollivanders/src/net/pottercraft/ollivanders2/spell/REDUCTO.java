package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Creates an explosion of magnitude depending on the spell level which destroys blocks and sets fires.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Reductor_Curse">https://harrypotter.fandom.com/wiki/Reductor_Curse</a>
 */
public final class REDUCTO extends O2Spell {
    // todo rework what type of blocks this can target, make work with bombarda (prob can be a child class), 5th year dark arts spell
    /**
     * The maximum possible strength, 4f is TNT
     */
    private final static float maxPower = 4f;

    /**
     * The minimum strength for this spell
     */
    private final static float minPower = 0.25f;

    /**
     * The power of the explosion - 4f is TNT
     */
    private float power;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public REDUCTO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.REDUCTO;
        branch = O2MagicBranch.DARK_ARTS;

        flavorText = new ArrayList<>() {{
            add("The Reductor Curse");
            add("With this powerful curse, skilled wizards can easily reduce obstacles to pieces. For obvious reasons great care must be exercised when learning and practising this spell, lest you find yourself sweeping up in detention for it is all too easy to bring your classroom ceiling crashing down, or to reduce your teacher's desk to a fine mist.");
        }};

        text = "Reducto creates an explosion which will damage the terrain.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public REDUCTO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.REDUCTO;
        branch = O2MagicBranch.DARK_ARTS;

        power = minPower;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.OTHER_EXPLOSION);

        initSpell();
    }

    /**
     * Set the power for the explosion based on the caster's skill.
     */
    @Override
    void doInitSpell() {
        power = (float) (usesModifier / 4);
        if (power < minPower)
            power = minPower;
        else if (power > maxPower)
            power = maxPower;
    }

    /**
     * Cause an explosion in the location 1 before the target location.
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitTarget())
            return;

        Location backLoc = location.clone().subtract(vector);
        World world = backLoc.getWorld();
        if (world == null)
            common.printDebugMessage("REDUCTO.doCheckEffect: world is null", null, null, true);
        else
            world.createExplosion(backLoc, power);

        kill();
    }
}