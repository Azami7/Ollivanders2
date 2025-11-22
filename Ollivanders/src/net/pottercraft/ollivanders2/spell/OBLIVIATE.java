package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.Map;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Decreases all target player's spell levels by the caster's level in obliviate.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Memory_Charm">https://harrypotter.fandom.com/wiki/Memory_Charm</a>
 */
public final class OBLIVIATE extends O2Spell {
    private static final int maxReduction = 200;
    private static final int maxImpact = 20;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public OBLIVIATE(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.OBLIVIATE;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Memory Charm");
            add("\"If there’s one thing I pride myself on, it’s my Memory Charms.\" -Gilderoy Lockhart");
            add("\"Miss Dursley has been punctured and her memory has been modified. She has no recollection of the incident at all. So that's that, and no harm done.\" -Cornelius Fudge");
        }};

        text = "Causes target player to lose some of their magical ability.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public OBLIVIATE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.OBLIVIATE;
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.PVP);

        initSpell();
    }

    /**
     * Target the first player we find and remove random potion and/or spell knowledge
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitTarget())
            kill();

        for (Player target : getNearbyPlayers(defaultRadius)) {
            if (target.getUniqueId().equals(player.getUniqueId()))
                continue;

            O2Player o2player = Ollivanders2API.getPlayers().getPlayer(target.getUniqueId());
            if (o2player == null) {
                common.printDebugMessage("Null O2Player in OBLIVIATE.doCheckEffect()", null, null, true);
                continue;
            }

            // start making the player forget things
            for (int i = 0; i < maxImpact; i++) {
                forgetSomething(o2player);

                if (!canContinue())
                    break;
            }

            kill();
            return;
        }
    }

    /**
     * Random chance to continue impacting player based on spell skill
     *
     * @return true if the spell continues to affect the target, flase otherwise
     */
    private boolean canContinue() {
        int chance = (Math.abs(Ollivanders2Common.random.nextInt()) % 100); // 0-99

        if (chance <= 10) // 10% chance this fails no matter what skill level
            return false;

        return (chance < (usesModifier / 2));
    }

    /**
     * Make a player lose a random amount of skill in a potion or spell randomly selected.
     *
     * @param o2player the player to affect
     */
    private void forgetSomething(O2Player o2player) {
        // determine how much the player forgets
        int reduction = Math.abs(Ollivanders2Common.random.nextInt()) % (int) usesModifier;
        if (reduction < 5)
            reduction = 5;
        else if (reduction > maxReduction)
            reduction = maxReduction;

        Map<O2PotionType, Integer> knownPotions = o2player.getKnownPotions();
        if (Ollivanders2Common.random.nextInt() > 0 && !knownPotions.isEmpty()) // forget a potion if any known and rand is positive (50% chance)
        {
            int index = Math.abs(Ollivanders2Common.random.nextInt() % knownPotions.size());

            O2PotionType[] potions = knownPotions.keySet().toArray(new O2PotionType[0]);
            o2player.setPotionCount(potions[index], knownPotions.get(potions[index]) - reduction);

            common.printDebugMessage(o2player.getPlayerName() + " loses " + reduction + " level in " + potions[index].getPotionName(), null, null, false);
        }
        else // forget a spell
        {
            Map<O2SpellType, Integer> knownSpells = o2player.getKnownSpells();
            int index = Math.abs(Ollivanders2Common.random.nextInt() % knownSpells.size());

            O2SpellType[] spells = knownSpells.keySet().toArray(new O2SpellType[0]);
            o2player.setSpellCount(spells[index], knownSpells.get(spells[index]) - reduction);

            common.printDebugMessage(o2player.getPlayerName() + " loses " + reduction + " level in " + spells[index].getSpellName(), null, null, false);
        }
    }
}