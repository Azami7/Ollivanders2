package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Effect.SLEEPING;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Player.O2Player;
import org.bukkit.Color;
import org.bukkit.entity.Player;

/**
 * Puts the drinker in to a deep but temporary sleep.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class SLEEPING_DRAUGHT extends O2Potion
{
   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    */
   public SLEEPING_DRAUGHT (Ollivanders2 plugin)
   {
      super(plugin);

      potionType = O2PotionType.SLEEPING_DRAUGHT;
      potionLevel = PotionLevel.BEGINNER;

      text = "A Sleeping Draught causes the drinker to fall almost instantly into a deep, dreamless sleep.";

      ingredients.put(IngredientType.LAVENDER_SPRIG, 4);
      ingredients.put(IngredientType.FLOBBERWORM_MUCUS, 2);
      ingredients.put(IngredientType.VALERIAN_SPRIGS, 4);
      ingredients.put(IngredientType.STANDARD_POTION_INGREDIENT, 6);

      flavorText.add("\"I've got it all worked out. I've filled these with a simple Sleeping Draught.\" -Hermione Granger");
      flavorText.add("\"You'll need to drink all of this, Harry. It's a potion for dreamless sleep.\" -Madam Pomfrey");
      flavorText.add("\"Then, without the smallest change of expression, they both keeled over backwards on to the floor.\"");
      flavorText.add("\"If I thought I could help you by putting you into an enchanted sleep and allowing you to postpone the moment when you would have to think about what has happened tonight, I would do it. But I know better.\" -Albus Dumbledore");

      potionColor = Color.fromRGB(75, 0, 130);
   }

   @Override
   public void drink (O2Player o2p, Player player)
   {
      if (p.players.playerEffects.hasEffect(player.getUniqueId(), O2EffectType.AWAKE))
      {
         player.sendMessage(Ollivanders2.chatColor + "You yawn, otherwise nothing happens.");
      }
      else
      {
         // put them asleep for ~2 minutes
         SLEEPING effect = new SLEEPING(p, 2400, player.getUniqueId());
         p.players.playerEffects.addEffect(effect);

         player.sendMessage(Ollivanders2.chatColor + "You fall in to a deep, dreamless, enchanted sleep.");
      }
   }
}
