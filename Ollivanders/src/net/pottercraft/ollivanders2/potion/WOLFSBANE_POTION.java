package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.effect.LYCANTHROPY_RELIEF;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wolfsbane Potion — applies the LYCANTHROPY_RELIEF effect, temporarily easing (but not curing) the symptoms of
 * Lycanthropy.
 *
 * @author Azami7
 * @author cakenggt
 */
public final class WOLFSBANE_POTION extends O2Potion {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin instance
     */
    public WOLFSBANE_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.WOLFSBANE_POTION;

        ingredients.put(O2ItemType.WOLFSBANE, 2);
        ingredients.put(O2ItemType.MANDRAKE_LEAF, 3);
        ingredients.put(O2ItemType.POISONOUS_POTATO, 1);
        ingredients.put(O2ItemType.DITTANY, 2);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

        potionColor = Color.fromRGB(51, 0, 102);
        potionSuccessMessage = "You feel a sense of relief.";

        text = "This potion will relieve, though not cure, the symptoms of Lycanthropy. It is a complex potion and requires "
                + "the most advanced potion-making skills.";

        flavorText.add("\"There is no known cure, although recent developments in potion-making have to a great extent alleviated the worst symptoms.\" —Newton Scamander");
    }

    /**
     * Apply the LYCANTHROPY_RELIEF effect to the player for the default potion duration.
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        LYCANTHROPY_RELIEF effect = new LYCANTHROPY_RELIEF(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}