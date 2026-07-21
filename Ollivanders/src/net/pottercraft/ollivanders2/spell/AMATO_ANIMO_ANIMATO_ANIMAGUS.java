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
 * Amato Animo Animato Animagus — the incantation used to become, and later transform as, an Animagus. Its effect
 * depends on the caster's status:
 * <ul>
 * <li>Not yet an Animagus: applies the {@link ANIMAGUS_INCANTATION} effect (the first step); the caster must then
 * drink the Animagus potion within 5 minutes to complete the process.</li>
 * <li>Already an Animagus: toggles between human and animal form, at a success rate that scales with experience.</li>
 * </ul>
 * <p>
 * Under strict conditions the incantation must be said at dawn or sunset and the potion drunk during a thunderstorm.
 * </p>
 *
 * @author Azami7
 * @see ANIMAGUS_INCANTATION
 * @see ANIMAGUS_EFFECT
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
     * Toggle an existing Animagus between human and animal form, or apply the {@link ANIMAGUS_INCANTATION} to a
     * non-Animagus as the first step of the process.
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
     * Apply the {@link ANIMAGUS_INCANTATION} effect, opening the 5-minute window during which the caster must drink
     * the Animagus potion to complete the transformation. When strict conditions are enabled this only succeeds near
     * dawn or sunset; otherwise it always succeeds. Messages the caster either way.
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
            ANIMAGUS_INCANTATION effect = new ANIMAGUS_INCANTATION(p, 5 * Ollivanders2Common.ticksPerMinute, false, caster.getUniqueId());
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
     * Toggle the caster between forms: remove the {@link ANIMAGUS_EFFECT} to return to human form, or attempt to
     * transform to animal form if not currently in it.
     *
     * @param o2p the player casting this spell
     */
    private void transform(@NotNull O2Player o2p) {
        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(o2p.getID(), O2EffectType.ANIMAGUS_EFFECT)) {
            // change them back to human form
            Ollivanders2API.getPlayers().playerEffects.removeEffect(o2p.getID(), O2EffectType.ANIMAGUS_EFFECT);
            successMessage = "You return to human form.";
            sendSuccessMessage();
        }
        else {
            transformToAnimalForm();
        }
    }

    /**
     * Attempt to transform the caster into their animal form. Success rate is 10% while {@code usesModifier} is below
     * a fifth of spell mastery (20), otherwise {@code usesModifier / 2}. Messages the caster on success or failure.
     */
    private void transformToAnimalForm() {
        int rand = Ollivanders2Common.random.nextInt(100);

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
    }

    /**
     * Set {@link #usesModifier} from the raw spell count (doubled with {@link O2EffectType#HIGHER_SKILL}); this
     * incantation is cast without a wand, so it ignores the wand-based calculation.
     */
    @Override
    protected void setUsesModifier() {
        usesModifier = p.getSpellCount(caster, spellType);

        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(caster.getUniqueId(), O2EffectType.HIGHER_SKILL)) {
            usesModifier *= 2;
        }
    }
}