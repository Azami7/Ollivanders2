package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.effect.FAST_LEARNING;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Memory Potion - enhances spell learning and experience gain.
 *
 * <p>When consumed, this potion applies the FAST_LEARNING effect to the player, which doubles
 * the experience gained from casting spells. This allows players to quickly improve their spell
 * skills and expertise. The effect lasts for the default potion duration, providing a significant
 * temporary boost to magical learning and development.</p>
 *
 * <p>This potion is particularly useful for players looking to advance their spell casting
 * abilities quickly and efficiently.</p>
 *
 * @author Azami7
 * @author cakenggt
 */
public final class MEMORY_POTION extends O2Potion {
    /**
     * Constructor for Memory Potion.
     *
     * <p>Initializes the potion with its ingredients (Mandrake Leaf, Jobberknoll Feather,
     * Galanthus Nivalis, Powdered Sage, and Standard Potion Ingredients), description text,
     * and potion color. Sets up the FAST_LEARNING effect that will be applied when the potion
     * is consumed to double spell experience gain.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public MEMORY_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.MEMORY_POTION;

        ingredients.put(O2ItemType.MANDRAKE_LEAF, 3);
        ingredients.put(O2ItemType.JOBBERKNOLL_FEATHER, 2);
        ingredients.put(O2ItemType.GALANTHUS_NIVALIS, 2);
        ingredients.put(O2ItemType.POWDERED_SAGE, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

        potionColor = Color.fromRGB(255, 128, 0);
        potionSuccessMessage = "You feel more alert.";

        text = "This potion improves the drinker's memory. All spell experience is doubled.";
    }

    /**
     * Drink the Memory Potion and gain temporary spell learning enhancement.
     *
     * <p>Applies the FAST_LEARNING effect to the player. While this effect is active, all spell
     * experience gained from casting spells is doubled, allowing for accelerated skill development
     * and expertise growth. The effect remains active for the default potion duration, providing a
     * temporary boost to magical learning.</p>
     *
     * @param player the player who drank the potion
     */
    public void drink(@NotNull Player player) {
        FAST_LEARNING effect = new FAST_LEARNING(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}