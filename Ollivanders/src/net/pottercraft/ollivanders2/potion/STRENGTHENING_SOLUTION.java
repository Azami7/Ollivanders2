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
 * Strengthening Solution — applies the STRENGTH effect for 5 minutes, longer than a Minecraft Strength II potion.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Strengthening_Solution">Strengthening Solution</a>
 */
public class STRENGTHENING_SOLUTION extends O2Potion {
    /**
     * Constructor
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

        potionColor = Color.fromRGB(54, 224, 208); // turquoise
        // longer than a Minecraft Strength II potion
        duration = Ollivanders2Common.ticksPerMinute * 5;
        potionSuccessMessage = "You feel incredibly strong.";

        text = "A Strengthening Solution gives the drinker immense strength.";
        flavorText.add("\"Well, the class seems fairly advanced for their level. Though I would question whether it is advisable to teach them a potion like the Strengthening Solution. I think the Ministry would prefer it if that was removed from the syllabus.\" -Dolores Umbridge");
        flavorText.add("\"We are continuing with our Strengthening Solutions today, you will find your mixtures as you left them last lesson, if correctly made they should have matured well over the weekend — instructions — on the board. Carry on.\" -Severus Snape");
    }

    /**
     * Apply the STRENGTH effect to the player for 5 minutes.
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
