package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.effect.LYCANTHROPY_RELIEF;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wolfsbane Potion - relieves the symptoms of Lycanthropy.
 *
 * <p>When consumed, this potion applies the LYCANTHROPY_RELIEF effect to the player, alleviating
 * the severe symptoms of Lycanthropy (werewolf curse). While this potion does not cure the condition
 * entirely, it greatly reduces the symptoms' severity, allowing affected players to function more
 * normally during their afflicted state. This potion is essential for managing the debilitating
 * effects of Lycanthropy and represents the most advanced potion-making techniques available.</p>
 *
 * <p>The LYCANTHROPY_RELIEF effect provides temporary respite from the worst aspects of the curse,
 * making this a critical potion for those affected by Lycanthropy. As an advanced potion with a
 * complex recipe, it requires significant potion-making skill to brew successfully.</p>
 *
 * @author Azami7
 * @author cakenggt
 */
public final class WOLFSBANE_POTION extends O2Potion {
    /**
     * Constructor for Wolfsbane Potion.
     *
     * <p>Initializes the potion with its complex recipe of rare ingredients (Wolfsbane, Mandrake
     * Leaf, Poisonous Potato, Dittany, and Standard Potion Ingredients), description text, flavor
     * text, potion color, and the LYCANTHROPY_RELIEF effect. Sets up the potion as an advanced
     * brewing recipe reflecting the complexity of managing Lycanthropy symptoms.</p>
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
     * Drink the Wolfsbane Potion and relieve Lycanthropy symptoms.
     *
     * <p>Applies the LYCANTHROPY_RELIEF effect to the player for the default potion duration.
     * This effect alleviates the severe symptoms of Lycanthropy, providing temporary relief from
     * the curse's worst manifestations. While the potion does not cure Lycanthropy entirely, it
     * greatly reduces the debilitating symptoms and allows the affected player to function more
     * normally during the effect duration. The relief is temporary and will wear off after the
     * effect expires.</p>
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