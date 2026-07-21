package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Reveal the last spell cast by a nearby target player's wand — the "Reverse Spell".
 * <p>
 * On a skill-based success roll the echoed spell is announced to the target and every player within
 * {@link #visibleRadius} blocks of them; on failure only the caster is told the wand resisted. If the target's wand
 * has never cast a spell, only the caster is notified.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Reverse_Spell">Harry Potter Wiki - Reverse Spell (Prior Incantato)</a>
 */
public class PRIOR_INCANTATO extends O2Spell {
    /**
     * The radius, in blocks, around the target within which other players witness the echoed spell.
     */
    private static final int visibleRadius = 10;

    /**
     * Leading fragment of the echo message naming the revealed spell, e.g. "The shadowy echo of the spell Lumos".
     */
    public static final String echoMessagePrefix = "The shadowy echo of the spell ";

    /**
     * Trailing fragment of the echo message shown to the target, completing "...Lumos emits from your wand."
     */
    public static final String echoEmitsFromYourWand = " emits from your wand.";

    /**
     * Middle fragment of the echo message shown to witnesses, between the spell name and the target's name,
     * completing "...Lumos emits from Harry's wand." together with {@link #wandPossessiveSuffix}.
     */
    public static final String echoEmitsFrom = " emits from ";

    /**
     * Possessive wand suffix appended after a player's name in the witness echo message ("Harry's wand.").
     */
    public static final String wandPossessiveSuffix = "'s wand.";

    /**
     * Message fragment, appended after the target's name, telling the caster the target's wand resisted the spell.
     */
    public static final String wandResistsMessage = "'s wand resists your spell.";

    /**
     * Message fragment, appended after the target's name, telling the caster the target's wand has cast no spell.
     */
    public static final String wandNoPriorSpellMessage = "'s wand has not cast a spell.";

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PRIOR_INCANTATO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PRIOR_INCANTATO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Reverse Spell");
            add("\"Placing his wand tip to tip against Harry's wand and saying the spell, Amos causes a shadow of the Dark Mark to erupt from where the two wands meet, showing that this was the last spell cast with Harry's wand.\"");
        }};

        text = "Force a player's wand to reveal the last spell cast. Your success depends on your experience with this spell.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PRIOR_INCANTATO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PRIOR_INCANTATO;
        branch = O2MagicBranch.CHARMS;

        initSpell();
    }

    /**
     * Reveal the prior incantation of the first nearby player other than the caster, on a skill-based success roll,
     * then end the spell.
     * <p>
     * On a failed roll the caster is told the wand resisted. A block hit still scans for a nearby target so that
     * slightly-off aim can succeed.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitBlock())
            kill();

        for (Player target : getNearbyPlayers(defaultRadius)) {
            if (target.getUniqueId().equals(caster.getUniqueId()))
                continue;

            int rand = Ollivanders2Common.random.nextInt(100);

            if (usesModifier > rand)
                doPriorIncantato(target);
            else
                caster.sendMessage(Ollivanders2.chatColor + target.getName() + wandResistsMessage);

            kill();
            return;
        }
    }

    /**
     * Reveal the target wand's prior incantation to the target and nearby witnesses.
     * <p>
     * Looks up the target's last-cast spell via {@link O2Player#getPriorIncantatem()}. If the wand has never cast
     * a spell, only the caster is notified. Otherwise the target and every other player within
     * {@link #visibleRadius} blocks of the target are sent a message naming the echoed spell.
     * </p>
     *
     * @param target the player whose wand's prior incantation is revealed
     */
    private void doPriorIncantato(@NotNull Player target) {
        O2Player o2p = p.getO2Player(target);
        O2SpellType prior = o2p.getPriorIncantatem();

        if (prior == null) {
            caster.sendMessage(Ollivanders2.chatColor + target.getName() + wandNoPriorSpellMessage);

            return;
        }

        List<Entity> nearbyPlayers = EntityCommon.getNearbyEntitiesByType(target.getLocation(), visibleRadius, EntityType.PLAYER);

        for (Entity entity : nearbyPlayers) {
            if (!(entity instanceof Player) || entity.getUniqueId().equals(target.getUniqueId()))
                continue;

            entity.sendMessage(Ollivanders2.chatColor + echoMessagePrefix + prior.getSpellName() + echoEmitsFrom + target.getName() + wandPossessiveSuffix);
        }

        target.sendMessage(Ollivanders2.chatColor + echoMessagePrefix + prior.getSpellName() + echoEmitsFromYourWand);
    }
}
