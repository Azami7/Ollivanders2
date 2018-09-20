package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Effect.LYCANTHROPY_RELIEF;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;

/**
 * Relieve the symptoms of Lycanthropy
 *
 * @author Azami7
 * @author cakenggt
 */
public final class WOLFSBANE_POTION extends O2Potion
{
   /**
    * Constructor
    *
    * @param plugin a callback to the plugin
    */
   public WOLFSBANE_POTION (Ollivanders2 plugin)
   {
      super(plugin);

      potionType = O2PotionType.WOLFSBANE_POTION;
      potionLevel = PotionLevel.EXPERT;

      ingredients.put(IngredientType.WOLFSBANE, 2);
      ingredients.put(IngredientType.MANDRAKE_LEAF, 3);
      ingredients.put(IngredientType.POISONOUS_POTATO, 1);
      ingredients.put(IngredientType.DITTANY, 2);
      ingredients.put(IngredientType.STANDARD_POTION_INGREDIENT, 3);

      text = "This potion will relieve, though not cure, the symptoms of Lycanthropy. It is a complex potion and requires"
            + "the most advanced potion-making skills.";

      flavorText.add("\"There is no known cure, although recent developments in potion-making have to a great extent alleviated the worst symptoms.\" —Newton Scamander");
      potionColor = Color.fromRGB(51, 0, 102);
   }

   @Override
   public void drink (O2Player o2p, Player player)
   {
      LYCANTHROPY_RELIEF effect = new LYCANTHROPY_RELIEF(p, duration, player.getUniqueId());
      p.players.playerEffects.addEffect(effect);

      player.sendMessage(Ollivanders2.chatColor + "You feel a sense of relief.");
   }
}