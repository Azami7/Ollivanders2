package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.effect.ANIMAGUS_EFFECT;
import net.pottercraft.ollivanders2.effect.ANIMAGUS_INCANTATION;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Amato Animo Animato Animagus is an incantation used in the process of becoming an Animagus. The spell alone is
 * not enough to change a player in to an Animagus. The spell has to be recited either at sunrise or sunset
 * and the player must drink the Animagus potion after saying the incantation. The potion must be consumed during
 * a lightning storm. Once the player has successfully become an Animagus it will require considerable practice
 * before they can consistently take the form.
 *
 * @since 2.2.6
 * @author Azami7
 */
public class AMATO_ANIMO_ANIMATO_ANIMAGUS extends O2Spell
{
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AMATO_ANIMO_ANIMATO_ANIMAGUS(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>()
        {{
            add("An Animagus is a wizard who elects to turn into an animal.");
            add("\"You know that I can disguise myself most effectively.\" -Peter Pettigrew");
        }};

        text = "Becoming an Animagus takes practice, skill, and patience. The animagus incantation is the one of the "
                + "most difficult Transfiguration spells. The spell alone is not sufficient to transform the caster the "
                + "first time. You must drink the Animagus potion immediately after saying the incantation. Both the "
                + "incantation and the potion also have specific environmental requirements. The incantation must be said "
                + "at either sunrise or sunset. The potion must be consumed during a thunderstorm. One you have successfully "
                + "transformed, you no longer need the potion and can use the spell at any time, however it will take "
                + "considerable practice before you will be able to consistently change form.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public AMATO_ANIMO_ANIMATO_ANIMAGUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS;
        branch = O2MagicBranch.TRANSFIGURATION;

        initSpell();
    }

    /**
     * Perform the Animagus spell
     */
    @Override
    public void checkEffect()
    {
        if (!isSpellAllowed())
        {
            kill();
            return;
        }

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());

        if (o2p == null)
        {
            kill();
            return;
        }

        if (o2p.isAnimagus())
        {
            // If the player is already an animagus, the incantation changes them to and from their animal form.
            common.printDebugMessage(player.getDisplayName() + " is an Animagus.", null, null, false);
            transform(o2p);
        }
        else
        {
            common.printDebugMessage(player.getDisplayName() + " is not an Animagus.", null, null, false);
            setAnimagusIncantation();
        }

        kill();
    }

    /**
     * If the player is not an animagus, saying the incantation at the correct time of day is the first
     * step to becoming one. Once said, the player must drink the Animagus potion within 15 seconds.
     */
    private void setAnimagusIncantation()
    {
        long curTime = player.getWorld().getTime();

        if ((curTime >= 23000 && curTime <= 24000) || (curTime >= 12000 && curTime <= 13000))
        {
            ANIMAGUS_INCANTATION effect = new ANIMAGUS_INCANTATION(p, 300, player.getUniqueId());
            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

            player.sendMessage(Ollivanders2.chatColor + "You feel slightly different.");
        }
        else
        {
            player.sendMessage(Ollivanders2.chatColor + "Nothing seems to happen.");
        }
    }

    /**
     * If the player is an animagus, saying the incantation will toggle them to and from their
     * animal form. Changing to animal form takes practice to do consistently. Players can always
     * return to their human form once transformed.
     *
     * @param o2p the o2player casting this spell
     */
    private void transform(@NotNull O2Player o2p)
    {
        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(o2p.getID(), O2EffectType.ANIMAGUS_EFFECT))
        {
            // change them back to human form
            Ollivanders2API.getPlayers().playerEffects.removeEffect(o2p.getID(), O2EffectType.ANIMAGUS_EFFECT);
        }
        else
        {
            transformToAnimalForm(o2p);
        }
    }

    private void transformToAnimalForm(@NotNull O2Player o2p)
    {
        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);
        int uses = (int) (usesModifier * 10);

        // set success rate based on their experience
        int successRate;
        if (uses < 25)
            successRate = 5;
        else if (uses < 100)
            successRate = 10;
        else if (uses < 200)
            successRate = uses / 2;
        else
            successRate = 100;

        if (rand < successRate)
        {
            ANIMAGUS_EFFECT animagusEffect = new ANIMAGUS_EFFECT(p, 5, player.getUniqueId());
            Ollivanders2API.getPlayers().playerEffects.addEffect(animagusEffect);

            player.sendMessage(Ollivanders2.chatColor + "You feel very different.");
        }
        else
        {
            player.sendMessage(Ollivanders2.chatColor + "You feel a momentary change but it quickly fades.");
        }

        p.setO2Player(player, o2p);
    }

    /**
     * Override setUsesModifier because this spell does not require holding a wand.
     */
    @Override
    protected void setUsesModifier()
    {
        usesModifier = p.getSpellCount(player, spellType);

        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL))
        {
            usesModifier *= 2;
        }
    }

    @Override
    protected void doCheckEffect() {}
}