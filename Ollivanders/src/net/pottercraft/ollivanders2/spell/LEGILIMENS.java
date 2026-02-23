package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.house.O2HouseType;
import net.pottercraft.ollivanders2.house.O2Houses;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Mind-reading spell that reveals information about a target player.
 *
 * <p>The spell attempts to read the mind of a nearby player and reveal various information
 * depending on the caster's experience level and the target's resistance. Success depends on comparing
 * the caster's Legilimens skill against the target's skill.</p>
 *
 * <p><strong>Information Revealed (by Legilimens Level):</strong></p>
 * <ul>
 * <li><strong>Base (any level):</strong> Whether target is a muggle or witch/wizard, house and year (if sorted)</li>
 * <li><strong>Level 2+:</strong> Target's wand type (50% chance)</li>
 * <li><strong>Level 3+:</strong> Last spell cast by target (50% chance)</li>
 * <li><strong>Level 4+:</strong> Target's mastered spell for non-verbal casting (33% chance, if enabled)</li>
 * <li><strong>Level 6+:</strong> Active magical effects on target (40% chance)</li>
 * <li><strong>Level 10+:</strong> Whether target is an animagus and their form (10% chance)</li>
 * </ul>
 *
 * <p><strong>Success Rate by Skill Comparison:</strong></p>
 * <ul>
 * <li>80% success when caster's skill > target's skill</li>
 * <li>66% success when skills are equal</li>
 * <li>10% success when caster's skill < target's skill</li>
 * </ul>
 *
 * <p><strong>Animagus Form:</strong> When target is in animagus form, only level 10+ casters can attempt
 * mind reading, with a 10% success rate. Lower level casters cannot read an animagus's mind.</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Legilimency_Spell">Legilimency Spell</a>
 */
public final class LEGILIMENS extends O2Spell {
    public static final int radius = 3;
    public static final String mindReadFailureMessage = " resists your mind.";

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public LEGILIMENS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.LEGILIMENS;
        branch = O2MagicBranch.DARK_ARTS;

        flavorText = new ArrayList<>() {{
            add("\"The mind is not a book, to be opened at will and examined at leisure. Thoughts are not etched on the inside of skulls, to be perused by any invader. The mind is a complex and many-layered thing, Potter. Or at least most minds are... It is true, however, that those who have mastered Legilimency are able, under certain conditions, to delve into the minds of their victims and to interpret their findings correctly.\" -Severus Snape");
            add("The Legilimency Spell");
        }};

        text = "Legilimens, when cast at a player, will reveal certain information about the player. Your success depends both on your level of experience with Legilimens and the target player's experience.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LEGILIMENS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.LEGILIMENS;
        branch = O2MagicBranch.DARK_ARTS;

        noProjectile = true; // legilimens works in a radius from the caster

        initSpell();
    }

    /**
     * Attempt to read nearby (within 3 blocks) players' minds when spell is active.
     *
     * <p>Finds nearby players (excluding the caster) and determines success based on skill comparison.
     * If successful, reveals information about the target. If unsuccessful, the target resists.
     * The spell kills itself after attempting to read one target.</p>
     *
     * <p>Special handling for animagus form: only level 10+ casters can attempt reading, with 10% success.</p>
     */
    @Override
    protected void doCheckEffect() {
        kill();

        if (!isSpellAllowed()) {
            return;
        }

        for (Player target : getNearbyPlayers(radius)) {
            common.printDebugMessage("LEGILIMENS.checkEffect: checking " + target.getName(), null, null, false);
            if (target.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }

            // only someone who has mastered legilimens can even attempt to read a mind when they are in animagus form
            if (Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.ANIMAGUS_EFFECT)) {
                common.printDebugMessage("LEGILIMENS.checkEffect: target is in animagus form", null, null, false);
                common.printDebugMessage("LEGILIMENS.checkEffect: Uses modifier = " + usesModifier, null, null, false);

                if (usesModifier < O2Spell.spellMasteryLevel) {
                    return; // spell fails with no message, caster skill too low to read animagus
                }

                if (!Ollivanders2.testMode) { // always pass this in test mode so we can reliably test legilimens on animagus form
                    // 10% chance to detect animagus if spell level is at or above mastery level
                    int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 10);

                    if (rand < 9) {
                        return; // spell fails with no message, same as if they'd targeted a non-player entity
                    }
                }
            }

            // Legilimens will be successful:
            // 80% of the time if the caster's legilimens level is greater than the target's
            // 33% of the time if the caster and target have the same legilimens level
            // 10% of the time if the caster's legilimens level is less than the target
            int targetExperience = p.getO2Player(target).getSpellCount(O2SpellType.LEGILIMENS);
            int randGreater = (Math.abs(Ollivanders2Common.random.nextInt()) % 5);
            int randEqual = (Math.abs(Ollivanders2Common.random.nextInt()) % 3);
            int randLess = (Math.abs(Ollivanders2Common.random.nextInt()) % 10);

            boolean success;
            if (Ollivanders2.testMode && !Ollivanders2.maxSpellLevel) { // always fail in testmode with maxSpellLevel off so we can reliably test failure
                success = false;
            }
            else if (((usesModifier > targetExperience) && (randGreater < 4)) // success on 3, 2, 1, 0 of 0-4
                    || ((usesModifier == targetExperience) && (randEqual < 1)) // success on 0 of 0-2
                    || ((usesModifier < targetExperience) && (randLess < 1)) // success on 0 of 0-9
                    || (Ollivanders2.testMode && Ollivanders2.maxSpellLevel)) {// in testmode with maxspelllevel on so we can reliably test success
                success = true;

                readMind(target);
            }
            else
                success = false;

            if (!success) {
                failureMessage = target.getName() + mindReadFailureMessage;
                sendFailureMessage();
            }

            return;
        }
    }

    /**
     * Reveal information about a target player based on caster's Legilimens skill.
     *
     * <p>Progressively reveals more information as the caster's Legilimens level increases:
     * <ul>
     * <li>Always reveals: muggle status, house and year</li>
     * <li>Level 2+: wand type (50% chance)</li>
     * <li>Level 3+: last spell cast (50% chance)</li>
     * <li>Level 4+: mastered spell if non-verbal casting enabled (33% chance)</li>
     * <li>Level 6+: active effects on target (40% chance)</li>
     * <li>Level 10+: animagus status and form (10% chance)</li>
     * </ul>
     *
     * @param target the player whose mind is being read
     */
    private void readMind(Player target) {
        O2Player o2p = p.getO2Player(target);

        common.printDebugMessage("LEGILIMENS.readMind: usesModifier = " + usesModifier, null, null, false);
        player.sendMessage(Ollivanders2.chatColor + "You search in to " + o2p.getPlayerName() + "'s mind ...");

        // this will dictate all the information the caster can detect
        int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 100);

        // always succeed chance rolls in testmode with maxspelllevel on so we can reliably test all functionality
        if (Ollivanders2.testMode && Ollivanders2.maxSpellLevel)
            rand = 100;

        //
        // muggle
        //
        if (o2p.isMuggle())
            player.sendMessage(Ollivanders2.chatColor + target.getName() + " is a muggle.");
        //
        // basic wizard information
        //
        else {
            player.sendMessage(Ollivanders2.chatColor + target.getName() + " is a witch/wizard.");

            // detect house and year
            if (O2Houses.useHouses) {
                StringBuilder message = new StringBuilder();
                message.append(Ollivanders2.chatColor);

                if (Ollivanders2API.getHouses().isSorted(target)) {
                    message.append(" is a ");

                    if (Ollivanders2.useYears)
                        message.append(o2p.getYear().getDisplayText()).append(" year ");

                    O2HouseType house = Ollivanders2API.getHouses().getHouse(target);
                    if (house != null)
                        message.append(house.getName()).append(".");
                    else
                        common.printDebugMessage("Null house in LEGILIMENS.readMind()", null, null, false);
                }
                else
                    message.append(" has not started school yet.");

                player.sendMessage(message.toString());
            }

            // 50% chance detect destined wand
            if (rand >= 50) {
                if (o2p.foundWand())
                    player.sendMessage(Ollivanders2.chatColor + " uses a " + o2p.getDestinedWandWood() + " and " + o2p.getDestinedWandCore() + " wand.");
                else
                    player.sendMessage(Ollivanders2.chatColor + " has not gotten a wand.");
            }
        }

        // information beyond this depends on legilimens level
        if (usesModifier > (int)(O2Spell.spellMasteryLevel/5) ) { // 20% mastery level
            // 50% chance detect recent spell cast
            if (rand >= 50) {
                O2SpellType lastSpell = o2p.getLastSpell();
                if (lastSpell != null)
                    player.sendMessage(Ollivanders2.chatColor + " last cast " + lastSpell.getSpellName() + ".");
            }

            if (usesModifier > (int)(O2Spell.spellMasteryLevel/3)) { // 33% mastery level
                // 33% chance detect mastered spell
                if (rand >= 66) {
                    if (Ollivanders2.enableNonVerbalSpellCasting) {
                        O2SpellType masteredSpell = o2p.getMasterSpell();
                        if (masteredSpell != null)
                            player.sendMessage(Ollivanders2.chatColor + " can non-verbally cast the spell " + masteredSpell.getSpellName() + ".");
                    }
                }

                if (usesModifier > (int)(O2Spell.spellMasteryLevel/2)) { // 50% mastery level
                    // 40% chance detect effects
                    if (rand >= 40) {
                        String affectedBy = Ollivanders2API.getPlayers().playerEffects.detectEffectWithLegilimens(o2p.getID());
                        if (affectedBy != null)
                            player.sendMessage(Ollivanders2.chatColor + " " + affectedBy + ".");
                    }

                    if (usesModifier >= O2Spell.spellMasteryLevel) { // mastery level
                        // 10% chance detect is animagus
                        if (rand >= 90) {
                            if (o2p.isAnimagus()) {
                                player.sendMessage(Ollivanders2.chatColor + " is an animagus.");

                                EntityType animagusForm = o2p.getAnimagusForm();
                                if (animagusForm != null)
                                    player.sendMessage(Ollivanders2.chatColor + " has the animagus form of a " + Ollivanders2Common.enumRecode(animagusForm.toString()) + ".");
                            }
                        }
                    }
                }
            }
        }
    }
}
