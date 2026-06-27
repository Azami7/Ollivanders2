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
 * Reduces a target player's known spell and potion levels — Ollivanders2's adaptation of the canon
 * Memory Charm.
 * <p>
 * On hitting another player, OBLIVIATE iterates up to {@link #maxImpact} times, each iteration
 * randomly removing skill from one of the target's known spells or potions. The reduction amount
 * per iteration scales with the caster's {@code usesModifier} (capped at {@link #maxReduction}),
 * and {@code canContinue()} probabilistically ends the loop early so low-skill casters typically
 * forget fewer things than high-skill casters.
 * </p>
 * <p>
 * The spell is killed on first impact (one target per cast), and is also killed if the projectile
 * hits a block — though the loop still runs once after a block hit so a player standing at the
 * impact point can still be affected. The caster is always skipped.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Memory_Charm">Harry Potter Wiki - Memory Charm</a>
 */
public final class OBLIVIATE extends O2Spell {
    /**
     * Hard cap on how many skill levels a single iteration of {@link #forgetSomething(O2Player)}
     * can subtract. Even at extreme {@code usesModifier} values, no single forget event removes
     * more than this.
     */
    private static final int maxReduction = O2Spell.spellMasteryLevel * 2;

    /**
     * Maximum number of forget iterations per cast. The loop in {@link #doCheckEffect()} will run
     * at most this many times against the impacted player; {@link #canContinue()} may terminate
     * earlier based on caster skill.
     */
    private static final int maxImpact = 20;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     * <p>
     * Populates {@link #flavorText} with the canon Memory Charm excerpts and sets the descriptive
     * {@link #text}. Does not configure cast-time state — the casting constructor handles that.
     * </p>
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
     * Constructor for casting Obliviate.
     * <p>
     * Adds the {@link Flags#PVP} WorldGuard flag if WorldGuard is enabled (since OBLIVIATE
     * affects another player), then invokes {@link #initSpell()}.
     * </p>
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
     * Tick handler: locate a non-caster player within range and reduce their spell/potion knowledge.
     * <p>
     * If the projectile has hit a block, kills the spell — but does not return immediately, so a
     * player at the impact point still gets a chance to be hit on this final tick. Iterates the
     * caster's nearby-player list, skips the caster themselves, and on the first valid target runs
     * up to {@link #maxImpact} iterations of {@link #forgetSomething(O2Player)}, breaking early
     * when {@link #canContinue()} rolls false. Always kills the spell after impacting one target.
     * </p>
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
     * Decide whether to run another forget iteration against the current target.
     * <p>
     * Higher caster skill (larger {@code usesModifier}) increases the chance of continuing. There
     * is also a base 10% short-circuit failure regardless of skill, suppressed under
     * {@link Ollivanders2#testMode} so unit tests can rely on deterministic continuation when
     * skill warrants it.
     * </p>
     *
     * @return true if the spell should run another forget iteration, false to stop
     */
    private boolean canContinue() {
        int chance = Ollivanders2Common.random.nextInt(100); // 0-99

        if (chance < 10 && !Ollivanders2.testMode) // 10% chance this fails no matter what skill level except in testmode where we need it to consistently succeed
            return false;

        return (chance < (usesModifier / 2));
    }

    /**
     * Reduce the target's skill in one randomly chosen known spell or potion.
     * <p>
     * Computes a per-iteration reduction amount: 1 if {@code usesModifier < 1}, otherwise a
     * uniform random value in {@code [0, floor(usesModifier))}, capped at {@link #maxReduction}.
     * Under {@link Ollivanders2#testMode} a cast at or above {@link O2Spell#spellMasteryLevel} instead
     * forces the maximum reduction, so the random roll cannot come up 0 and tests are deterministic.
     * Then a 50/50 coin flip decides whether to attempt to forget a spell or a potion first; if
     * the chosen category is empty for this target, falls back to the other category so a single
     * iteration always reduces something when at least one of the two maps is non-empty.
     * </p>
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
     * Reduce the level of one randomly selected known spell on the target by the given amount.
     * <p>
     * If the resulting level falls below 1, the spell is removed from the target's known-spells
     * map by {@link O2Player#setSpellCount(O2SpellType, int)}. No-op if the target has no known
     * spells.
     * </p>
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
     * Reduce the level of one randomly selected known potion on the target by the given amount.
     * <p>
     * If the resulting level falls below 1, the potion is removed from the target's known-potions
     * map by {@link O2Player#setPotionCount(O2PotionType, int)}. No-op if the target has no known
     * potions.
     * </p>
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