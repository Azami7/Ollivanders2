package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.HUNGER;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Hunger Potion — applies the HUNGER effect for 3 minutes, causing rapid food depletion.
 *
 * @author Azami7w
 */
public class HUNGER_POTION extends O2Potion {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin instance
     */
    public HUNGER_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.HUNGER_POTION;

        ingredients.put(O2ItemType.ROTTEN_FLESH, 2);
        ingredients.put(O2ItemType.GROUND_SCARAB_BEETLE, 1);
        ingredients.put(O2ItemType.CRUSHED_FIRE_SEEDS, 1);
        ingredients.put(O2ItemType.CRUSHED_GURDYROOT, 1);
        ingredients.put(O2ItemType.INFUSION_OF_WORMWOOD, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

        potionColor = Color.fromRGB(88, 118, 83); // dark green
        duration = Ollivanders2Common.ticksPerMinute * 3;
        potionSuccessMessage = "You feel ravenously hungry.";

        text = "The hunger potion will cause the drinker to feel ravenous.";
    }

    /**
     * Apply the HUNGER effect to the player for 3 minutes.
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        HUNGER effect = new HUNGER(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}