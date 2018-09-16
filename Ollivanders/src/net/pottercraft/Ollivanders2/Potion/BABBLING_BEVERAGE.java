package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Effect.BABBLING;
import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

/**
 * Babbling Beverage is a potion that caused the drinker to babble nonsense.
 *
 * @since 2.2.7
 * @author Azami7
 */
public class BABBLING_BEVERAGE extends O2Potion
{
   /**
    * Constructor
    *
    * @param plugin a callback to the plugin
    */
   public BABBLING_BEVERAGE (Ollivanders2 plugin)
   {
      super(plugin);

      potionType = O2PotionType.BABBLING_BEVERAGE;

      ingredients.put(IngredientType.VALERIAN_SPRIGS, 2);
      ingredients.put(IngredientType.DITTANY, 1);
      ingredients.put(IngredientType.WOLFSBANE, 1);
      ingredients.put(IngredientType.STANDARD_POTION_INGREDIENT, 2);

      potionColor = Color.FUCHSIA;

      name = "Babbling Beverage";
      text = "Babbling Beverage is a potion that causes the drinker to babble nonsense.";
      flavorText.add("\"Potter, when I want nonsense shouted at me I shall give you a Babbling Beverage.\" -Severus Snape");
   }

   public void drink (O2Player o2p, Player player)
   {
      BABBLING effect = new BABBLING(p, duration, player.getUniqueId());
      p.players.playerEffects.addEffect(effect);

      player.sendMessage(Ollivanders2.chatColor + "You tongue feels fuzzy.");
   }
}