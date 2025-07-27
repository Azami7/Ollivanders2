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
 * The Fire-Protection Potion - aka the Ice Potion - is used to move through flames unscathed.
 *
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Fire_Protection_Potion">https://harrypotter.fandom.com/wiki/Fire_Protection_Potion</a>
 * @since 2.21
 */
public class ICE_POTION extends O2Potion {
    /**
     * Constructor.
     *
     * @param plugin the callback to the MC plugin
     */
    public ICE_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.ICE_POTION;

        ingredients.put(O2ItemType.SALAMANDER_BLOOD, 2);
        ingredients.put(O2ItemType.WARTCAP_POWDER, 1);
        ingredients.put(O2ItemType.BURSTING_MUSHROOM, 4);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 1);

        text = "The Fire-Protection, or Ice Potion, allows the drinker to move through flames unscathed.";
        flavorText.add("\"Danger lies before you, while safety lies behind, Two of us will help you, whichever you would find, One among us seven will let you move ahead, Another will transport the drinker back instead.\"");
        flavorText.add("\"It was indeed as though ice was flooding his body.\"");

        // potion color
        potionColor = Color.fromRGB(176, 224, 230); // powder blue

        // last 5 minutes, which is longer than night vision II lasts
        duration = Ollivanders2Common.ticksPerMinute * 5;

        minecraftPotionEffect = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, 1);
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
