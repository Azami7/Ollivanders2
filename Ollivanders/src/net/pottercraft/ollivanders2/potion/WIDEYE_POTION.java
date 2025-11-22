package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.effect.AWAKE;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wideye potion prevents the drinker from falling asleep.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class WIDEYE_POTION extends O2Potion {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public WIDEYE_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.WIDEYE_POTION;

        ingredients.put(O2ItemType.GROUND_SNAKE_FANGS, 3);
        ingredients.put(O2ItemType.BILLYWIG_STING_SLIME, 6);
        ingredients.put(O2ItemType.WOLFSBANE, 2);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 4);

        text = "The Wideye Potion, also known as the Awakening Potion, is used to prevent the drinker from falling asleep.";

        potionColor = Color.fromRGB(13, 55, 13);
    }

    /**
     * Drink this potion and do effects
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        AWAKE effect = new AWAKE(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + "You feel alert.");
    }
}
