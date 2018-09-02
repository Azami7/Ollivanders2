package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spell.Spells;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

/**
 * The Forgetfulness Potion is a potion which causes a degree of memory loss in the drinker.
 *
 * http://harrypotter.wikia.com/wiki/Forgetfulness_Potion
 *
 * @since 2.2.7
 * @author Azami7
 */
public final class FORGETFULLNESS_POTION extends O2Potion
{
   public FORGETFULLNESS_POTION (Ollivanders2 plugin)
   {
      super(plugin);

      ingredients.put(IngredientType.MISTLETOE_BERRIES, 4);
      ingredients.put(IngredientType.VALERIAN_SPRIGS, 2);
      ingredients.put(IngredientType.LETHE_RIVER_WATER, 2);
      ingredients.put(IngredientType.STANDARD_POTION_INGREDIENT, 2);

      name = "Forgetfulness Potion";
      text = "The Forgetfulness Potion is a potion which causes a degree of memory loss in the drinker." + getIngredientsText();
      flavorText.add("Hermione Granger: \"What are the three most crucial ingredients in a Forgetfulness Potion?\"\nRon Weasley: \"I forgot.\"");

      potionColor = Color.MAROON;
   }

   public void drink (O2Player o2p, Player player)
   {
      int coinToss = Math.abs(Ollivanders2.random.nextInt() % 2);

      int memLoss = Math.abs(Ollivanders2.random.nextInt() % 20) + 1;

      String lostSpell = "";

      if (coinToss > 0)
      {
         Map<Spells, Integer> knownSpells = o2p.getKnownSpells();
         if (knownSpells.size() > 0)
         {
            Set<Spells> keySet = knownSpells.keySet();
            ArrayList<Spells> listOfSpells = new ArrayList<>(keySet);
            int index = Math.abs(Ollivanders2.random.nextInt() % listOfSpells.size());

            o2p.setSpellCount(listOfSpells.get(index), memLoss);
            lostSpell = listOfSpells.get(index).toString();
         }
      }
      else
      {
         Map<String, Integer> knownPotions = o2p.getKnownPotions();
         if (knownPotions.size() > 0)
         {
            Set<String> keySet = knownPotions.keySet();
            ArrayList<String> listOfPotions = new ArrayList<>(keySet);
            int index = Math.abs(Ollivanders2.random.nextInt() % listOfPotions.size());

            o2p.setPotionCount(listOfPotions.get(index), memLoss);
            lostSpell = listOfPotions.get(index);
         }
      }

      if (Ollivanders2.debug)
         p.getLogger().info("Forgetfullness Potion: " + player.getDisplayName() + " lost " + memLoss + " experience  with " + lostSpell);

      player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor")) + "It feels like you've forgotten something.");
   }
}
