package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.WEAKNESS;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Weakness Potion — when drunk, applies the WEAKNESS effect to the drinker for 5 minutes.
 *
 * @author Azami7
 */
public class WEAKNESS_POTION extends O2Potion {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin instance
     */
    public WEAKNESS_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.WEAKNESS_POTION;

        potionColor = Color.fromRGB(244, 164, 96); // light brown
        duration = Ollivanders2Common.ticksPerMinute * 5;
        potionSuccessMessage = "You feel drained of strength.";

        ingredients.put(O2ItemType.FLOBBERWORM_MUCUS, 1);
        ingredients.put(O2ItemType.VALERIAN_ROOT, 2);
        ingredients.put(O2ItemType.LEECH_JUICE, 1);
        ingredients.put(O2ItemType.DOXY_VENOM, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

        text = "The weakness potion will drain the drinker of strength.";
    }

    /**
     * On drink, apply the WEAKNESS effect to the player for this potion's duration.
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        WEAKNESS effect = new WEAKNESS(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}
