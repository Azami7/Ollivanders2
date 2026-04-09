package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * The Killing Curse—an unforgivable dark magic spell that inflicts damage on a living entity.
 *
 * <p>Damage dealt is determined by the caster's spell level (usesModifier).
 * Subject to WorldGuard PVP and damage animal restrictions.</p>
 *
 * <p>Reference: <a href="https://harrypotter.fandom.com/wiki/Killing_Curse">Killing Curse</a></p>
 */
public final class AVADA_KEDAVRA extends O2Spell {
    /**
     * Constructor for spell info generation. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public AVADA_KEDAVRA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.AVADA_KEDAVRA;
        branch = O2MagicBranch.DARK_ARTS;

        flavorText = new ArrayList<>() {{
            add("The Killing Curse");
            add("There was a flash of blinding green light and a rushing sound, as though a vast, invisible something was soaring through the air — instantaneously the spider rolled over onto its back, unmarked, but unmistakably dead");
            add("\"Yes, the last and worst. Avada Kedavra. ...the Killing Curse.\" -Bartemius Crouch Jr (disguised as Alastor Moody)");
        }};

        text = "Cause direct damage to a living thing, possibly killing it.";
    }

    /**
     * Constructor to cast the Avada Kedavra spell.
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting the spell
     * @param rightWand the wand being used
     */
    public AVADA_KEDAVRA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.AVADA_KEDAVRA;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.PVP);
            worldGuardFlags.add(Flags.DAMAGE_ANIMALS);
        }

        moveEffectData = Material.GREEN_WOOL;

        initSpell();
    }

    /**
     * Finds and damages the nearest damageable entity within range.
     *
     * <p>Skips the caster themselves. Damage is scaled by the caster's spell level.</p>
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitBlock())
            kill();

        List<Damageable> entities = getNearbyDamageableEntities(defaultRadius);
        if (!entities.isEmpty()) {
            for (Damageable entity : entities) {
                if (entity.getUniqueId().equals(caster.getUniqueId()))
                    continue;

                entity.damage(usesModifier, caster);
                common.printDebugMessage("Targeting " + entity.getName(), null, null, false);

                kill();
                return;
            }
        }
    }
}