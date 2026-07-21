package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.effect.ANIMAGUS_EFFECT;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Animagus Potion — transforms the drinker into their animal form and grants the permanent ANIMAGUS_EFFECT.
 * <p>
 * Only works if LibsDisguises is enabled and the drinker has recently cast the Animagus incantation; with strict
 * conditions enabled it additionally requires a thunderstorm.
 * </p>
 *
 * @author Azami7
 */
public final class ANIMAGUS_POTION extends O2Potion {
    String potionFailureMessage = "Nothing seems to happen.";
    String alreadyAnimagusMessage = "You taste something vaguely familiar.";

    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public ANIMAGUS_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.ANIMAGUS_POTION;

        ingredients.put(O2ItemType.MANDRAKE_LEAF, 1);
        ingredients.put(O2ItemType.DEW_DROP, 2);
        ingredients.put(O2ItemType.DEATHS_HEAD_MOTH_CHRYSALIS, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

        potionColor = Color.fromRGB(102, 0, 0);
        potionSuccessMessage = "You feel transformed.";

        text = "An Animagus is a wizard who elects to turn into an animal. This potion, if brewed and consumed correctly, " +
                "will transform the drinker into their animal form. Thereafter, the Animagus can transform without the " +
                "potion, however it will take considerable practice to change forms consistently at will.";
        flavorText.add("\"You know that I can disguise myself most effectively.\" -Peter Pettigrew");
        flavorText.add("\"Normally, I have a very sweet disposition as a dog; in fact, more than once, James suggested I make the change permanent. The tail I could live with...but the fleas, they're murder.\" -Sirius Black");
    }

    /**
     * Transform the drinker into their Animagus form and grant the permanent ANIMAGUS_EFFECT, consuming the Animagus
     * incantation effect. No-ops with a failure message when the preconditions (see class doc) are not met.
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        O2Player o2p = p.getO2Player(player);

        if (o2p == null || !Ollivanders2.libsDisguisesEnabled) {
            player.sendMessage(Ollivanders2.chatColor + potionFailureMessage);
            return;
        }

        if (o2p.isAnimagus()) {
            if (Ollivanders2API.getPlayers().playerEffects.hasEffect(o2p.getID(), O2EffectType.ANIMAGUS_INCANTATION))
                Ollivanders2API.getPlayers().playerEffects.removeEffect(o2p.getID(), O2EffectType.ANIMAGUS_INCANTATION);

            player.sendMessage(Ollivanders2.chatColor + alreadyAnimagusMessage);
            return;
        }

        if (!player.getWorld().isThundering() && Ollivanders2.useStrictAnimagusConditions) {
            // potion only works in a thunderstorm
            player.sendMessage(Ollivanders2.chatColor + potionFailureMessage);
            return;
        }

        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(o2p.getID(), O2EffectType.ANIMAGUS_INCANTATION)) {
            o2p.setIsAnimagus();
            Ollivanders2API.getPlayers().playerEffects.removeEffect(o2p.getID(), O2EffectType.ANIMAGUS_INCANTATION);

            ANIMAGUS_EFFECT animagusEffect = new ANIMAGUS_EFFECT(p, 5, true, player.getUniqueId());
            Ollivanders2API.getPlayers().playerEffects.addEffect(animagusEffect);

            player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
        }
        else {
            // Player hasn't cast the Animagus incantation yet
            player.sendMessage(Ollivanders2.chatColor + potionFailureMessage);
        }
    }

    /**
     * Get the failure message to the drinker if this potion did not work.
     *
     * @return the failure message
     */
    public String getPotionFailureMessage() {
        return potionFailureMessage;
    }

    /**
     * Get the message to send to the drinker if they are already an Animagus.
     *
     * @return the already-an-Animagus message
     */
    public String getAlreadyAnimagusMessage() {
        return alreadyAnimagusMessage;
    }
}