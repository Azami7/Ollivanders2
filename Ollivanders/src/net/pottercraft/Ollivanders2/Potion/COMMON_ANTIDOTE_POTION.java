package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

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
   /**
    * Constructor
    *
    * @param plugin a callback to the plugin
    */
   public COMMON_ANTIDOTE_POTION (Ollivanders2 plugin)
   {
      super(plugin);

      potionType = O2PotionType.COMMON_ANTIDOTE_POTION;
      potionLevel = PotionLevel.BEGINNER;

      ingredients.put(IngredientType.MISTLETOE_BERRIES, 2);
      ingredients.put(IngredientType.BEZOAR, 1);
      ingredients.put(IngredientType.UNICORN_HAIR, 1);
      ingredients.put(IngredientType.STANDARD_POTION_INGREDIENT, 2);

      text = "Counteracts ordinary poisons, such as creature bites and stings.";

      potionColor = Color.TEAL;
   }

   @Override
   public void drink (O2Player o2p, Player player)
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