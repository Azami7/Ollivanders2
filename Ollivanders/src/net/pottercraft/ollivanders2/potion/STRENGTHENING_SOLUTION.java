package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Strengthening Solution is a potion that grants the drinker immense strength.
 *
 * @since 2.21
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Strengthening_Solution">https://harrypotter.fandom.com/wiki/Strengthening_Solution</a>
 */
public class STRENGTHENING_SOLUTION extends O2Potion {
    /**
     * Constructor.
     *
     * @param plugin the callback to the MC plugin
     */
    public STRENGTHENING_SOLUTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.STRENGTHENING_SOLUTION;

        ingredients.put(O2ItemType.SALAMANDER_BLOOD, 2);
        ingredients.put(O2ItemType.POWDERED_GRIFFIN_CLAW, 1);
        ingredients.put(O2ItemType.GROUND_SNAKE_FANGS, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

        text = "A Strengthening Solution gives the drinking immense strength.";
        flavorText.add("\"Well, the class seems fairly advanced for their level. Though I would question whether it is advisable to teach them a potion like the Strengthening Solution. I think the Ministry would prefer it if that was removed from the syllabus.\" -Dolores Umbridge");
        flavorText.add("\"We are continuing with our Strengthening Solutions today, you will find your mixtures as you left them last lesson, if correctly made they should have matured well over the weekend — instructions — on the board. Carry on.\" -Severus Snape");

        // potion color
        potionColor = Color.fromRGB(54, 224, 208); // turquoise

        // last 5 minutes, which is longer than strength II lasts
        duration = Ollivanders2Common.ticksPerMinute * 5;

        // Strength II
        minecraftPotionEffect = new PotionEffect(PotionEffectType.STRENGTH, duration, 1);
    }

    /**
     * Drink this potion and do effects
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        player.sendMessage(Ollivanders2.chatColor + "You feel as though ice flows through your body.");
    }
}
