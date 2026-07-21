package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.SHRINKING;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Shrinking Solution — applies the temporary SHRINKING effect, making the drinker smaller.
 *
 * @author Azami7
 */
public class SHRINKING_SOLUTION extends O2Potion {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin instance
     */
    public SHRINKING_SOLUTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        ingredients.put(O2ItemType.RAT_SPLEEN, 1);
        ingredients.put(O2ItemType.SLICED_CATERPILLARS, 2);
        ingredients.put(O2ItemType.SHRIVELIG, 2);
        ingredients.put(O2ItemType.LEECH_JUICE, 1);
        ingredients.put(O2ItemType.DAISY_ROOTS, 2);
        ingredients.put(O2ItemType.INFUSION_OF_COWBANE, 1);
        ingredients.put(O2ItemType.INFUSION_OF_WORMWOOD, 2);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

        potionType = O2PotionType.SHRINKING_SOLUTION;

        potionColor = Color.fromRGB(218, 165, 32); // goldenrod
        duration = Ollivanders2Common.ticksPerMinute * 5;
        potionSuccessMessage = "You feel yourself compressing.";

        text = "The Shrinking Solution is a potion that shrinks a player in size.";
        flavorText.add("\"A more subtle potion than many appreciate at first savour, the Shrinking Solution causes creatures to shrink to a younger form.\" -Zygmunt Budge");
        flavorText.add("\"Everyone gather 'round, and watch what happens to Longbottom's toad. If he has managed to produce a Shrinking Solution, it will shrink to a tadpole. If, as I don't doubt, he has done it wrong, his toad is likely to be poisoned.\" -Severus Snape");
    }

    /**
     * Apply the SHRINKING effect to the drinker and send the success message.
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        SHRINKING effect = new SHRINKING(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}
