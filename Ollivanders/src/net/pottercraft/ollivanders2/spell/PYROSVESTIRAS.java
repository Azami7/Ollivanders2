package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Extinguishing Charm: permanently extinguishes fire in a radius scaled by the caster's skill. Fire and soul
 * fire become air; campfires and soul campfires become oak logs.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Extinguishing_Spell">Harry Potter Wiki - Extinguishing Spell</a>
 */
public class PYROSVESTIRAS extends BlockTransfiguration {
    private static final int minRadiusConfig = 1;

    private static final int maxRadiusConfig = 10;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PYROSVESTIRAS(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.CHARMS;
        spellType = O2SpellType.PYROSVESTIRAS;

        flavorText = new ArrayList<>() {{
            add("A charm that extinguishes fires. Most commonly employed by Dragonologists.");
            add("The Extinguishing Charm");
        }};

        text = "A spell that extinguishes fire and soul fire.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PYROSVESTIRAS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.PYROSVESTIRAS;
        branch = O2MagicBranch.CHARMS;

        successMessage = "A fire is doused by the water.";
        failureMessage = "Nothing seems to happen.";

        permanent = true;
        minEffectRadius = minRadiusConfig;
        maxEffectRadius = maxRadiusConfig;
        effectRadiusModifier = 0.1;

        materialAllowList.add(Material.FIRE);
        materialAllowList.add(Material.SOUL_FIRE);
        materialAllowList.add(Material.CAMPFIRE);
        materialAllowList.add(Material.SOUL_CAMPFIRE);

        // stop the projectile at fire blocks so they can be targeted
        projectilePassThrough.removeAll(materialAllowList);

        transfigurationMap.put(Material.FIRE, Material.AIR);
        transfigurationMap.put(Material.SOUL_FIRE, Material.AIR);
        transfigurationMap.put(Material.CAMPFIRE, Material.OAK_LOG);
        transfigurationMap.put(Material.SOUL_CAMPFIRE, Material.OAK_LOG);

        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }

    /**
     * Set {@link #effectRadius} to {@code usesModifier * effectRadiusModifier}, limited to
     * [{@link #minEffectRadius}, {@link #maxEffectRadius}]. Scales radius faster with caster skill than the base
     * calculation.
     */
    @Override
    protected void setEffectRadius() {
        effectRadius = (int) (usesModifier * effectRadiusModifier);

        if (effectRadius < minEffectRadius)
            effectRadius = minEffectRadius;
        else if (effectRadius > maxEffectRadius)
            effectRadius = maxEffectRadius;
    }
}
