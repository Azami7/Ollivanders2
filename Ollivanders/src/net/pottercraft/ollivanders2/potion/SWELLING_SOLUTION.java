package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.SWELLING;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Swelling Solution — applies the SWELLING effect, causing the drinker to grow in size for 5 minutes.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Swelling_Solution">Swelling Solution</a>
 */
public class SWELLING_SOLUTION extends O2Potion {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin instance
     */
    public SWELLING_SOLUTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.SWELLING_SOLUTION;

        ingredients.put(O2ItemType.DRIED_NETTLES, 3);
        ingredients.put(O2ItemType.PUFFERFISH_EYE, 3);
        ingredients.put(O2ItemType.BAT_SPLEEN, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

        potionColor = Color.fromRGB(218, 165, 32); // goldenrod
        duration = Ollivanders2Common.ticksPerMinute * 5;
        potionSuccessMessage = "You feel yourself expanding.";

        text = "The Swelling Solution will cause the drinker to grow in size.";
        flavorText.add("\"Second-year Hogwarts students brew Swelling Solution in Potions class. This potion causes things to grow and expand. Its effects can be reversed with a Deflating Draught.\" -Severus Snape");
        flavorText.add("\"They had Potions that morning. The class had just started making a Swelling Solution — a potion that made things swell up, and was proving to be a tedious, difficult job.\"");
        flavorText.add("\"Harry caught sight of Malfoy, Crabbe, and Goyle engaged in a furious argument. Crabbe had spilled his Swelling Solution all over his own feet, which were now the size of tree trunks...\"");
    }

    /**
     * Apply the SWELLING effect to the drinker and send the success message.
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        SWELLING effect = new SWELLING(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}
