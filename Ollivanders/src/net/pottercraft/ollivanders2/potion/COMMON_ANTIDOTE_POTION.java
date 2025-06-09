package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * The Antidote for Common Poisons is a potion which counteracts ordinary poisons, such as creature bites and stings.
 *
 * http://harrypotter.wikia.com/wiki/Antidote_to_Common_Poisons
 *
 * @since 2.2.7
 * @author Azami7
 */
public final class COMMON_ANTIDOTE_POTION extends O2Potion
{
   // todo lower strength so that we can also have the stronger antidote to uncommon poisons for longer duration poison
   /**
    * Constructor
    *
    * @param plugin a callback to the plugin
    */
   public COMMON_ANTIDOTE_POTION(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      potionType = O2PotionType.COMMON_ANTIDOTE_POTION;

      ingredients.put(O2ItemType.MISTLETOE_BERRIES, 2);
      ingredients.put(O2ItemType.BEZOAR, 1);
      ingredients.put(O2ItemType.UNICORN_HAIR, 1);
      ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

      text = "Counteracts ordinary poisons, such as creature bites and stings.";

      potionColor = Color.TEAL;
   }

   /**
    * Drink this potion and do effects
    *
    * @param player the player who drank the potion
    */
   @Override
   public void drink(@NotNull Player player)
   {
      if (player.hasPotionEffect(PotionEffectType.POISON))
      {
         player.removePotionEffect(PotionEffectType.POISON);

         player.sendMessage(Ollivanders2.chatColor + "A cool feeling flows through your body as the poison is removed.");
      }
      else
      {
         player.sendMessage(Ollivanders2.chatColor + "You smell a faint odor that reminds you of winter, then it is gone.");
      }
   }
}