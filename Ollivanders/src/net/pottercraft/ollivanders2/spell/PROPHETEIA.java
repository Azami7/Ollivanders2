package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Reveal an unfulfilled prophecy made about a nearby target player.
 * <p>
 * On a skill-based success roll, an unfulfilled prophecy about the first nearby player other than the caster is
 * broadcast to everyone within {@link #messageRadius} blocks. If the roll fails or the target has no prophecy, only
 * the caster is told that nothing was discovered.
 * </p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Prophecy">Harry Potter Wiki - Prophecy</a>
 * @see net.pottercraft.ollivanders2.divination.O2Prophecy
 */
public class PROPHETEIA extends O2Spell {
    /**
     * The radius within which a revealed prophecy is broadcast.
     */
    private final static int messageRadius = 10;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PROPHETEIA(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.DIVINATION;
        spellType = O2SpellType.PROPHETEIA;

        flavorText = new ArrayList<>() {{
            add("\"But when Sybill Trelawney spoke, it was not in her usual ethereal, mystic voice, but in the hard, hoarse tones Harry had heard her use once before.\"");
        }};

        text = "Propheteia allows one to reveal an unfulfilled prophecy that has been made about a target player. Chances of success depend on experience.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PROPHETEIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.DIVINATION;
        spellType = O2SpellType.PROPHETEIA;
        initSpell();
    }

    /**
     * Reveal an unfulfilled prophecy about the first nearby player other than the caster, on a skill-based success
     * roll, then end the spell.
     * <p>
     * On a failed roll, or when the target has no prophecy, the caster is told nothing was discovered.
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

            if (usesModifier > rand || Ollivanders2.testMode) {
                String prophecy = Ollivanders2API.getProphecies().getProphecy(target.getUniqueId());

                if (prophecy != null) {
                    Ollivanders2Common.sendMessageInRadius(prophecy, location, messageRadius);

                    kill();
                    return;
                }
            }

            caster.sendMessage(Ollivanders2.chatColor + "You do not discover anything.");

            kill();
            return;
        }
    }
}
