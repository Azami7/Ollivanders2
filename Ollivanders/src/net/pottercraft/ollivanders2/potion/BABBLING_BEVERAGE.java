package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.effect.BABBLING;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Babbling Beverage - a potion that causes the drinker to babble nonsense.
 *
 * <p>When consumed, this potion applies the BABBLING effect to the player, forcing them
 * to speak randomly selected nonsense phrases for the duration of the effect. This is
 * primarily a cosmetic/humorous effect with no mechanical disadvantage.</p>
 *
 * @author Azami7
 * @since 2.2.7
 */
public class BABBLING_BEVERAGE extends O2Potion {
    /**
     * Constructor for Babbling Beverage potion.
     *
     * <p>Initializes the potion with its ingredients, description text, flavor text, and
     * potion color. Sets up the recipe and all effects that will be applied when consumed.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public BABBLING_BEVERAGE(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.BABBLING_BEVERAGE;

        ingredients.put(O2ItemType.VALERIAN_SPRIGS, 2);
        ingredients.put(O2ItemType.DITTANY, 1);
        ingredients.put(O2ItemType.WOLFSBANE, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

        potionColor = Color.FUCHSIA;
        potionSuccessMessage = "Your tongue feels fuzzy.";

        text = "Babbling Beverage is a potion that causes the drinker to babble nonsense.";
        flavorText.add("\"Potter, when I want nonsense shouted at me I shall give you a Babbling Beverage.\" -Severus Snape");
    }

    /**
     * Drink the Babbling Beverage and apply the babbling effect.
     *
     * <p>Applies the BABBLING effect to the player for the duration defined by the potion's
     * duration field. The effect causes the player to speak random nonsense phrases at regular
     * intervals while the effect is active.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        BABBLING effect = new BABBLING(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}