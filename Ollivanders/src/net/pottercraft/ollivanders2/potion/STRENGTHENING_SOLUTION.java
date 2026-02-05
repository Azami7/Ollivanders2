package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.STRENGTH;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Strengthening Solution - enhances the drinker's physical strength significantly.
 *
 * <p>When consumed, this potion applies the STRENGTH effect to the player for 5 minutes,
 * greatly increasing their physical strength and power. The player will deal significantly more
 * damage with melee attacks and be able to perform stronger physical actions during the effect
 * duration.</p>
 *
 * <p>The 5-minute duration is longer than the standard Minecraft Strength II potion, providing
 * extended combat advantage. This potion is useful for offensive gameplay, combat scenarios, and
 * situations requiring physical power.</p>
 *
 * @author Azami7
 * @since 2.21
 * @see <a href="https://harrypotter.fandom.com/wiki/Strengthening_Solution">Strengthening Solution</a>
 */
public class STRENGTHENING_SOLUTION extends O2Potion {
    /**
     * Constructor for Strengthening Solution potion.
     *
     * <p>Initializes the potion with its ingredients (Salamander Blood, Powdered Griffin Claw,
     * Ground Snake Fangs, and Standard Potion Ingredients), description text, flavor text, potion
     * color, and the Strength effect. Sets up the 5-minute duration for the strength
     * enhancement effect.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public STRENGTHENING_SOLUTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.STRENGTHENING_SOLUTION;

        ingredients.put(O2ItemType.SALAMANDER_BLOOD, 2);
        ingredients.put(O2ItemType.POWDERED_GRIFFIN_CLAW, 1);
        ingredients.put(O2ItemType.GROUND_SNAKE_FANGS, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

        // potion color
        potionColor = Color.fromRGB(54, 224, 208); // turquoise
        // last 5 minutes, which is longer than strength II lasts
        duration = Ollivanders2Common.ticksPerMinute * 5;
        potionSuccessMessage = "You feel incredibly strong.";

        text = "A Strengthening Solution gives the drinker immense strength.";
        flavorText.add("\"Well, the class seems fairly advanced for their level. Though I would question whether it is advisable to teach them a potion like the Strengthening Solution. I think the Ministry would prefer it if that was removed from the syllabus.\" -Dolores Umbridge");
        flavorText.add("\"We are continuing with our Strengthening Solutions today, you will find your mixtures as you left them last lesson, if correctly made they should have matured well over the weekend — instructions — on the board. Carry on.\" -Severus Snape");
    }

    /**
     * Drink the Strengthening Solution and gain immense physical strength.
     *
     * <p>Applies the STRENGTH effect to the player for 5 minutes. This effect significantly
     * increases the player's melee damage output and physical power, allowing them to deal much
     * more damage with attacks. The strength bonus is particularly useful for combat scenarios and
     * defeating enemies.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        STRENGTH effect = new STRENGTH(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}
