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
 * Swelling Solution - causes the drinker to grow in size.
 *
 * <p>When consumed, this potion applies the SWELLING effect to the player for 5 minutes,
 * causing them to become larger in physical size. The swelling effect can impact movement,
 * interactions with the world, and overall gameplay mechanics. Players affected by this potion
 * will experience difficulty navigating tight spaces and may have altered physics.</p>
 *
 * <p>The swelling effect is temporary and will wear off after the duration expires, returning
 * the player to their normal size. This potion is primarily a transformative or debilitating
 * effect useful for puzzle-solving, exploration challenges, or roleplay scenarios.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Swelling_Solution">Swelling Solution</a>
 */
public class SWELLING_SOLUTION extends O2Potion {
    /**
     * Constructor for Swelling Solution potion.
     *
     * <p>Initializes the potion with its ingredients (Dried Nettles, Pufferfish Eye, Bat Spleen,
     * and Standard Potion Ingredients), description text, flavor text, and potion color. Sets up
     * the SWELLING effect that will be applied when the potion is consumed for 5 minutes.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public SWELLING_SOLUTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.SWELLING_SOLUTION;

        // ingredients
        ingredients.put(O2ItemType.DRIED_NETTLES, 3);
        ingredients.put(O2ItemType.PUFFERFISH_EYE, 3);
        ingredients.put(O2ItemType.BAT_SPLEEN, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

        // potion config
        potionColor = Color.fromRGB(218, 165, 32); // goldenrod
        duration = Ollivanders2Common.ticksPerMinute * 5;
        potionSuccessMessage = "You feel yourself expanding.";

        // spellbook text
        text = "The Swelling Solution will cause the drinker to grow in size.";
        flavorText.add("\"Second-year Hogwarts students brew Swelling Solution in Potions class. This potion causes things to grow and expand. Its effects can be reversed with a Deflating Draught.\" -Severus Snape");
        flavorText.add("\"They had Potions that morning. The class had just started making a Swelling Solution — a potion that made things swell up, and was proving to be a tedious, difficult job.\"");
        flavorText.add("\"Harry caught sight of Malfoy, Crabbe, and Goyle engaged in a furious argument. Crabbe had spilled his Swelling Solution all over his own feet, which were now the size of tree trunks...\"");
    }

    /**
     * Drink the Swelling Solution and grow in size.
     *
     * <p>Applies the SWELLING effect to the player for 5 minutes. This effect causes the player
     * to physically grow in size, which can affect their movement, interactions with the world,
     * and overall gameplay mechanics. The player will experience difficulty navigating tight spaces
     * and may encounter altered physics while swollen. The swelling effect is temporary and will
     * wear off after the duration expires.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        // add the swelling effect
        SWELLING effect = new SWELLING(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}
