package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Common Antidote Potion — when drunk, removes the Minecraft POISON effect from the drinker.
 *
 * @see <a href="http://harrypotter.wikia.com/wiki/Antidote_to_Common_Poisons">Harry Potter Wiki - Antidote to Common Poisons</a>
 *
 * @author Azami7
 */
public final class COMMON_ANTIDOTE_POTION extends O2Potion {
    // todo lower strength so that we can also have the stronger antidote to uncommon poisons for longer duration poison

    /**
     * Message shown when the drinker was not poisoned, so the potion had no effect.
     */
    String potionDoNothingMessage = "You smell a faint odor that reminds you of winter, then it is gone.";
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin instance
     */
    public COMMON_ANTIDOTE_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.COMMON_ANTIDOTE_POTION;

        ingredients.put(O2ItemType.MISTLETOE_BERRIES, 2);
        ingredients.put(O2ItemType.BEZOAR, 1);
        ingredients.put(O2ItemType.UNICORN_HAIR, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

        potionColor = Color.TEAL;
        potionSuccessMessage = "A cool feeling flows through your body as the poison is removed.";

        text = "Counteracts ordinary poisons, such as creature bites and stings.";
    }

    /**
     * On drink, remove the POISON effect from the player if present; otherwise send the no-effect message.
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        if (player.hasPotionEffect(PotionEffectType.POISON)) {
            player.removePotionEffect(PotionEffectType.POISON);

            player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
        }
        else {
            player.sendMessage(Ollivanders2.chatColor + potionDoNothingMessage);
        }
    }

    /**
     * Get the message shown when the drinker was not poisoned.
     *
     * @return the no-effect message
     */
    public String getPotionDoNothingMessage() {
        return potionDoNothingMessage;
    }
}