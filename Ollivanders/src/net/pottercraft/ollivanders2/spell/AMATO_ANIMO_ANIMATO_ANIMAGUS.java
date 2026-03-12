package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.common.TimeCommon;
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
 * Amato Animo Animato Animagus is an incantation used in the process of becoming an Animagus.
 *
 * <p>This spell serves two purposes depending on the caster's Animagus status:</p>
 *
 * <ul>
 * <li>Non-Animagus: Applies the {@link ANIMAGUS_INCANTATION} effect, which is the first step in the
 * transformation process. The player must then drink the Animagus potion within 15 seconds.</li>
 * <li>Animagus: Toggles between human and animal form. Success rate scales with spell experience.</li>
 * </ul>
 *
 * <p>When strict conditions are enabled, the incantation must be said at dawn or sunset, and the potion
 * must be consumed during a thunderstorm.</p>
 *
 * @author Azami7
 * @see ANIMAGUS_INCANTATION
 * @see ANIMAGUS_EFFECT
 * @since 2.2.6
 */
public class AMATO_ANIMO_ANIMATO_ANIMAGUS extends O2Spell {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AMATO_ANIMO_ANIMATO_ANIMAGUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>() {{
            add("An Animagus is a wizard who elects to turn into an animal.");
            add("\"You know that I can disguise myself most effectively.\" -Peter Pettigrew");
        }};

        if (Ollivanders2.useStrictAnimagusConditions)
            text = "Becoming an Animagus takes practice, skill, and patience. The animagus incantation is the one of the "
                    + "most difficult Transfiguration spells. The spell alone is not sufficient to transform the caster the "
                    + "first time. You must drink the Animagus potion immediately after saying the incantation. Both the "
                    + "incantation and the potion also have specific environmental requirements. The incantation must be said "
                    + "at either sunrise or sunset. The potion must be consumed during a thunderstorm. Once you have successfully "
                    + "transformed, you no longer need the potion and can use the spell at any time, however it will take "
                    + "considerable practice before you will be able to consistently change form.";
        else
            text = "Becoming an Animagus takes practice, skill, and patience. The animagus incantation is the one of the "
                    + "most difficult Transfiguration spells. The spell alone is not sufficient to transform the caster the "
                    + "first time. You must drink the Animagus potion immediately after saying the incantation. Once you have successfully "
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
    public AMATO_ANIMO_ANIMATO_ANIMAGUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS;
        branch = O2MagicBranch.TRANSFIGURATION;

        noProjectile = true;

        initSpell();
    }

    /**
     * Checks the spell effect based on the caster's Animagus status.
     *
     * <p>If the caster is already an Animagus, toggles between human and animal form. Otherwise, applies the
     * {@link ANIMAGUS_INCANTATION} effect as the first step of the transformation process.</p>
     */
    @Override
    protected void doCheckEffect() {
        if (casterO2P.isAnimagus()) {
            // If the player is already an animagus, the incantation changes them to and from their animal form.
            common.printDebugMessage(caster.getDisplayName() + " is an Animagus.", null, null, false);
            transform(casterO2P);
        }
        else {
            common.printDebugMessage(caster.getDisplayName() + " is not an Animagus.", null, null, false);
            setAnimagusIncantation();
        }

        kill();
    }

    /**
     * Applies the {@link ANIMAGUS_INCANTATION} effect if the time-of-day conditions are met.
     *
     * <p>When strict conditions are enabled, the incantation only succeeds during dawn (dawn to sunrise)
     * or dusk (sunset to moonrise). With strict conditions disabled, it always succeeds.</p>
     *
     * <p>The effect lasts 300 ticks (15 seconds), during which the player must drink the Animagus potion
     * to complete the transformation.</p>
     */
    private void setAnimagusIncantation() {
        boolean success = false;

        if (Ollivanders2.useStrictAnimagusConditions) {
            long curTime = caster.getWorld().getTime();
            if ((curTime >= TimeCommon.DAWN.getTick() && curTime <= TimeCommon.SUNRISE.getTick())
                    || (curTime >= TimeCommon.SUNSET.getTick() && curTime <= TimeCommon.MOONRISE.getTick()))
                success = true;
        }
        else
            success = true;

        if (success) {
            ANIMAGUS_INCANTATION effect = new ANIMAGUS_INCANTATION(p, 300, false, caster.getUniqueId());
            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

            successMessage = "You feel slightly different.";
            sendSuccessMessage();
        }
        else {
            failureMessage = "Nothing seems to happen.";
            sendFailureMessage();
        }
    }

    /**
     * Toggles the caster between human and animal form.
     *
     * <p>If the caster currently has the {@link ANIMAGUS_EFFECT}, it is removed to return them to human form.
     * Otherwise, attempts to transform them to animal form with a success rate based on experience.</p>
     *
     * @param o2p the player casting this spell
     */
    private void transform(@NotNull O2Player o2p) {
        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(o2p.getID(), O2EffectType.ANIMAGUS_EFFECT)) {
            // change them back to human form
            Ollivanders2API.getPlayers().playerEffects.removeEffect(o2p.getID(), O2EffectType.ANIMAGUS_EFFECT);
        }
        else {
            transformToAnimalForm(o2p);
        }
    }

    /**
     * Attempts to transform the caster into their animal form.
     *
     * <p>Success rate scales with spell experience:</p>
     *
     * <ul>
     * <li>Under 25 uses: 10%</li>
     * <li>25 or more uses: usesModifier / 2 </li>
     * </ul>
     *
     * @param o2p the player casting this spell
     */
    private void transformToAnimalForm(@NotNull O2Player o2p) {
        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);

        // set success rate based on their experience
        int successRate;
        if (usesModifier < (O2Spell.spellMasteryLevel * 0.20)) // 20% spell mastery or lower
            successRate = 10;
        else
            successRate = (int) Math.floor(usesModifier / 2);

        if (rand < successRate) {
            ANIMAGUS_EFFECT animagusEffect = new ANIMAGUS_EFFECT(p, 5, true, caster.getUniqueId());
            Ollivanders2API.getPlayers().playerEffects.addEffect(animagusEffect);

            successMessage = "You feel very different.";
            sendSuccessMessage();
        }
        else {
            failureMessage = "You feel a momentary change but it quickly fades.";
            sendFailureMessage();
        }

        p.setO2Player(caster, o2p);
    }

    /**
     * Overrides the default uses modifier calculation since this spell is an incantation that does not
     * require holding a wand.
     *
     * <p>Uses the raw spell count rather than wand-based calculation, doubled if the player has the
     * {@link O2EffectType#HIGHER_SKILL} effect.</p>
     */
    @Override
    protected void setUsesModifier() {
        usesModifier = p.getSpellCount(caster, spellType);

        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(caster.getUniqueId(), O2EffectType.HIGHER_SKILL)) {
            usesModifier *= 2;
        }
    }
}