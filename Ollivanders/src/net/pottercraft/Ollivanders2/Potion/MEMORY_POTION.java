package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Doubles spell experience gained when casting spells.
 *
 * @author Azami7
 * @author cakenggt
 */
public final class MEMORY_POTION extends O2Potion
{
   public MEMORY_POTION (Ollivanders2 plugin, O2PotionType potionType)
   {
      super(plugin, potionType);

      ingredients.put(IngredientType.MANDRAKE_LEAF, 3);
      ingredients.put(IngredientType.JOBBERKNOLL_FEATHER, 2);
      ingredients.put(IngredientType.GALANTHUS_NIVALIS, 2);
      ingredients.put(IngredientType.POWDERED_SAGE, 1);
      ingredients.put(IngredientType.STANDARD_POTION_INGREDIENT, 2);


      name = "Memory Potion";
      text = "This potion improves the drinker's memory. All spell experience is doubled." + getIngredientsText();
      potionColor = Color.fromRGB(255, 128, 0);
   }

   public void drink (O2Player o2p, Player player)
   {
      if (!extendEffect(o2p))
      {
         o2p.addEffect(new net.pottercraft.Ollivanders2.Effect.MEMORY_POTION(p, O2EffectType.MEMORY_POTION, duration, player.getUniqueId()));
      }

      player.sendMessage(Ollivanders2.chatColor + "You feel more alert.");
   }
}