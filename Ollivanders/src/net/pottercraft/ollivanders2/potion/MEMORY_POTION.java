package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.effect.FAST_LEARNING;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Memory Potion — applies the FAST_LEARNING effect, which doubles the experience gained from casting spells.
 *
 * @author Azami7
 * @author cakenggt
 */
public final class MEMORY_POTION extends O2Potion {
    /**
     * Constructor
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
     * Apply the FAST_LEARNING effect to the player and send the success message.
     *
     * @param player the player who drank the potion
     */
    public void drink(@NotNull Player player) {
        FAST_LEARNING effect = new FAST_LEARNING(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}