package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.effect.SLEEPING;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The Draught of Living Death is an extremely powerful sleeping draught, sending the drinker into a deathlike slumber.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class DRAUGHT_OF_LIVING_DEATH extends O2Potion
{
   /**
    * Constructor
    *
    * @param plugin a callback to the plugin
    */
   public DRAUGHT_OF_LIVING_DEATH(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      potionType = O2PotionType.DRAUGHT_OF_LIVING_DEATH;

      ingredients.put(O2ItemType.POWDERED_ASHPODEL_ROOT, 1);
      ingredients.put(O2ItemType.INFUSION_OF_WORMWOOD, 1);
      ingredients.put(O2ItemType.VALERIAN_ROOT, 1);
      ingredients.put(O2ItemType.SOPOPHORUS_BEAN_JUICE, 1);
      ingredients.put(O2ItemType.SLOTH_BRAIN, 1);
      ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

      text = "Puts the drinker in a permanent magical sleep.";
      flavorText.add("The Draught of Living Death brings upon its drinker a very powerful sleep that can last indefinitely. This is an extremely dangerous potion. Execute with maximum caution.");
      flavorText.add("By mixing an infusion of wormwood with powdered root of asphodel you can make a sleeping potion so powerful it is known as the Draught of Living Death.");
      flavorText.add("\"Mr. Potter. Our new celebrity. Tell me, what would I get if I added powdered root of asphodel to an infusion of wormwood?\" -Severus Snape");

      potionColor = Color.fromRGB(200, 162, 200);
   }

   /**
    * Drink this potion and do effects
    *
    * @param player the player who drank the potion
    */
   @Override
   public void drink(@NotNull Player player)
   {
      if (Ollivanders2API.getPlayers(p).playerEffects.hasEffect(player.getUniqueId(), O2EffectType.AWAKE))
      {
         player.sendMessage(Ollivanders2.chatColor + "You yawn, close your eyes for a moment, then feel fine.");
      }
      else
      {
         SLEEPING effect = new SLEEPING(p, 5, player.getUniqueId());
         effect.setPermanent(true);
         Ollivanders2API.getPlayers(p).playerEffects.addEffect(effect);

         player.sendMessage(Ollivanders2.chatColor + "You fall in to a powerful magic sleep.");
      }
   }
}
