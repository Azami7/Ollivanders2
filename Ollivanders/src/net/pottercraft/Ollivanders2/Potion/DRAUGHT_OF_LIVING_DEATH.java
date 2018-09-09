package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Effect.SLEEPING;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Player.O2Player;
import org.bukkit.Color;
import org.bukkit.entity.Player;

/**
 * The Draught of Living Death is an extremely powerful sleeping draught, sending the drinker into a deathlike slumber.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class DRAUGHT_OF_LIVING_DEATH extends O2Potion
{
   public DRAUGHT_OF_LIVING_DEATH (Ollivanders2 plugin, O2PotionType potionType)
   {
      super(plugin, potionType);

      name = "Draught of Living Death";

      ingredients.put(IngredientType.POWDERED_ASHPODEL, 1);
      ingredients.put(IngredientType.INFUSION_OF_WORMWOOD, 1);
      ingredients.put(IngredientType.VALERIAN_ROOT, 1);
      ingredients.put(IngredientType.SOPOPHORUS_BEAN_JUICE, 1);
      ingredients.put(IngredientType.SLOTH_BRAIN, 1);

      text = "Puts the drinker in a permanent magical sleep.";
      flavorText.add("The Draught of Living Death brings upon its drinker a very powerful sleep that can last indefinitely. This is an extremely dangerous potion. Execute with maximum caution.");
      flavorText.add("By mixing an infusion of wormwood with powdered root of asphodel you can make a sleeping potion so powerful it is known as the Draught of Living Death.");
      flavorText.add("\"Mr. Potter. Our new celebrity. Tell me, what would I get if I added powdered root of asphodel to an infusion of wormwood?\" -Severus Snape");

      potionColor = Color.fromRGB(200, 162, 200);
   }

   @Override
   public void drink (O2Player o2p, Player player)
   {
      SLEEPING effect = new SLEEPING(p, 5, player.getUniqueId());
      effect.setPermanent();
      p.players.playerEffects.addEffect(effect);

      player.sendMessage(Ollivanders2.chatColor + "You fall in to a powerful magic sleep.");
   }
}