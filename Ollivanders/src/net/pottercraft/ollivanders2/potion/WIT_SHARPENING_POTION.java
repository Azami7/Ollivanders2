package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.effect.IMPROVED_BOOK_LEARNING;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The Wit-Sharpening Potion is a potion which allows the drinker to think more clearly. Due to this, it acts a a
 * counteragent to the Confundus Charm.
 *
 * @author Azami7
 */
public final class WIT_SHARPENING_POTION extends O2Potion
{
   /**
    * Constructor
    *
    * @param plugin a callback to the plugin
    */
   public WIT_SHARPENING_POTION(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      potionType = O2PotionType.WIT_SHARPENING_POTION;
      potionLevel = PotionLevel.OWL;

      ingredients.put(O2ItemType.GINGER_ROOT, 2);
      ingredients.put(O2ItemType.GROUND_SCARAB_BEETLE, 3);
      ingredients.put(O2ItemType.ARMADILLO_BILE, 2);
      ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

      text = "The Wit-Sharpening Potion is a potion which allows the drinker to think more clearly. Due to this, it acts"
            + "as a counteragent to the Confundus Charm.";
      flavorText.add("\"Some of you will benefit from today's assignment: Wit-Sharpening Potion. Perhaps you should begin immediately.\" -Severus Snape");

      potionColor = Color.fromRGB(204, 102, 0);
   }

   @Override
   public void drink(@NotNull O2Player o2p, @NotNull Player player)
   {
      IMPROVED_BOOK_LEARNING effect = new IMPROVED_BOOK_LEARNING(p, duration, player.getUniqueId());
      Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

      player.sendMessage(Ollivanders2.chatColor + "You feel ready to learn.");
   }
}