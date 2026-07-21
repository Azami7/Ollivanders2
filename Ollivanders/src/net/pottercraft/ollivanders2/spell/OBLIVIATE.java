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
 * The Memory Charm: reduces a target player's known spell and potion levels. Higher caster skill removes more — both
 * per hit and by lowering more of the target's spells/potions.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Memory_Charm">Harry Potter Wiki - Memory Charm</a>
 */
public final class OBLIVIATE extends O2Spell {
    /**
     * Hard cap on how many skill levels a single {@link #forgetSomething(O2Player)} iteration can subtract.
     */
    private static final int maxReduction = O2Spell.spellMasteryLevel * 2;

    /**
     * Maximum number of forget iterations per cast; {@link #canContinue()} may end the loop earlier.
     */
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
     * Reduce the spell/potion knowledge of the first non-caster player in range by running up to {@link #maxImpact}
     * {@link #forgetSomething(O2Player)} iterations (stopping early when {@link #canContinue()} rolls false), then end
     * the spell (one target per cast). A block hit ends the spell but still runs this once, so a player at the impact
     * point is affected.
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitBlock())
            kill();

        for (Player target : getNearbyPlayers(defaultRadius)) {
            if (target.getUniqueId().equals(caster.getUniqueId()))
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
     * Decide whether to run another forget iteration; higher caster skill makes continuing more likely. A base 10%
     * chance to stop applies regardless of skill, suppressed under {@link Ollivanders2#testMode} for deterministic
     * tests.
     *
     * @return true to run another forget iteration, false to stop
     */
    private boolean canContinue() {
        int chance = Ollivanders2Common.random.nextInt(100); // 0-99

        if (chance < 10 && !Ollivanders2.testMode) // 10% chance this fails no matter what skill level except in testmode where we need it to consistently succeed
            return false;

        return (chance < (usesModifier / 2));
    }

    /**
     * Reduce the target's skill in one randomly chosen known spell or potion by a skill-scaled amount (limited to
     * {@link #maxReduction}). A coin flip picks spell vs. potion first, falling back to the other category if the
     * chosen one is empty, so an iteration always reduces something when either map is non-empty. Under
     * {@link Ollivanders2#testMode} a cast at or above {@link O2Spell#spellMasteryLevel} forces the maximum reduction
     * so the roll cannot be 0 and tests are deterministic.
     *
     * @param target the player whose knowledge will be reduced
     */
    private void forgetSomething(O2Player target) {
        // determine how much the player forgets
        int reduction;

        if (Ollivanders2.testMode && usesModifier >= O2Spell.spellMasteryLevel)
            // at mastery under test the random roll can come up 0, leaving the target unchanged; force the maximum
            // reduction so the outcome is deterministic and always greater than 0
            reduction = maxReduction;
        else if (usesModifier < 1)
            reduction = 1;
        else {
            reduction = Ollivanders2Common.random.nextInt((int)Math.floor(usesModifier));
        }

        if (reduction > maxReduction)
            reduction = maxReduction;

        if (Ollivanders2Common.random.nextBoolean()) { // 50% chance
            if (!forgetSpell(target, reduction)) // forget a spell, if that fails because no known spells, try to forget a potion
                forgetPotion(target, reduction);
        }
        else {
            if (!forgetPotion(target, reduction)) // forget a potion, if that fails because no known potion, try to forget a spell
                forgetSpell(target, reduction);
        }
    }

    /**
     * Reduce one randomly selected known spell on the target by {@code reduction} levels (removing it if the result
     * falls below 1). No-op if the target has no known spells.
     *
     * @param target    the player whose spell knowledge will be reduced
     * @param reduction the number of skill levels to subtract from the chosen spell
     * @return true if a spell was reduced, false if the target had no known spells
     */
    boolean forgetSpell(O2Player target, int reduction) {
        Map<O2SpellType, Integer> knownSpells = target.getKnownSpells();

        if (knownSpells.isEmpty())
            return false;

        int index = Ollivanders2Common.random.nextInt(knownSpells.size());

        O2SpellType[] spells = knownSpells.keySet().toArray(new O2SpellType[0]);
        target.setSpellCount(spells[index], knownSpells.get(spells[index]) - reduction);

        common.printDebugMessage(target.getPlayerName() + " loses " + reduction + " level in " + spells[index].getSpellName(), null, null, false);
        return true;
    }

    /**
     * Reduce one randomly selected known potion on the target by {@code reduction} levels (removing it if the result
     * falls below 1). No-op if the target has no known potions.
     *
     * @param target    the player whose potion knowledge will be reduced
     * @param reduction the number of skill levels to subtract from the chosen potion
     * @return true if a potion was reduced, false if the target had no known potions
     */
    boolean forgetPotion(O2Player target, int reduction) {
        Map<O2PotionType, Integer> knownPotions = target.getKnownPotions();

        if (knownPotions.isEmpty())
            return false;

        int index = Ollivanders2Common.random.nextInt(knownPotions.size());

        O2PotionType[] potions = knownPotions.keySet().toArray(new O2PotionType[0]);
        target.setPotionCount(potions[index], knownPotions.get(potions[index]) - reduction);

        common.printDebugMessage(target.getPlayerName() + " loses " + reduction + " level in " + potions[index].getPotionName(), null, null, false);
        return true;
    }
}