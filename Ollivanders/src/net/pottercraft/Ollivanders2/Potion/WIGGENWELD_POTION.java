package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * The Wiggenweld Potion is a healing potion with the power to awaken a person from a magically-induced sleep, which
 * gives it the ability to reverse the effects of potions like the Sleeping Draught and the Draught of Living Death.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class WIGGENWELD_POTION extends O2Potion
{
   public WIGGENWELD_POTION (Ollivanders2 plugin, O2PotionType potionType)
   {
      super(plugin, potionType);

      ingredients.put(IngredientType.HORKLUMP_JUICE, 1);
      ingredients.put(IngredientType.FLOBBERWORM_MUCUS, 2);
      ingredients.put(IngredientType.CHIZPURFLE_FANGS, 1);
      ingredients.put(IngredientType.BILLYWIG_STING_SLIME, 2);
      ingredients.put(IngredientType.MINT_SPRIG, 1);
      ingredients.put(IngredientType.BOOM_BERRY_JUICE, 1);
      ingredients.put(IngredientType.MANDRAKE_LEAF, 2);
      ingredients.put(IngredientType.HONEYWATER, 1);
      ingredients.put(IngredientType.SLOTH_BRAIN_MUCUS, 1);
      ingredients.put(IngredientType.MOONDEW_DROP, 3);
      ingredients.put(IngredientType.SALAMANDER_BLOOD, 1);
      ingredients.put(IngredientType.LIONFISH_SPINES, 10);
      ingredients.put(IngredientType.UNICORN_HORN, 1);
      ingredients.put(IngredientType.WOLFSBANE, 2);

      text = "The Wiggenweld Potion is a healing potion which can also be used to wake the drinker from magically-induced sleep.";
      flavorText.add("\"Today you will learn to brew the Wiggenweld Potion. It is a powerful healing potion that can be used to heal injuries, or reverse the effects of a Sleeping Draught.\" -Severus Snape");
   }
}
