package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * The Cure for Boils (also known as simply Boil Cure) is a potion which cures boils, even those produced by the Pimple Jinx.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class CURE_FOR_BOILS extends O2Potion
{
   /**
    * Constructor.
    *
    * @param plugin the callback to the MC plugin
    */
   public CURE_FOR_BOILS (Ollivanders2 plugin)
   {
      super(plugin);

      potionType = O2PotionType.CURE_FOR_BOILS;
      potionLevel = PotionLevel.BEGINNER;
      name = "Cure For Boils";

      ingredients.put(IngredientType.GROUND_SNAKE_FANGS, 3);
      ingredients.put(IngredientType.DRIED_NETTLES, 1);
      ingredients.put(IngredientType.GROUND_PORCUPINE_QUILLS, 1);
      ingredients.put(IngredientType.HORNED_SLUG_MUCUS, 4);
      ingredients.put(IngredientType.STANDARD_POTION_INGREDIENT, 4);

      text = "The Cure for Boils (also known as simply Boil Cure) is a potion which cures boils, even those produced by the Pimple Jinx.";
      flavorText.add("\"Being an effective remedie against pustules, hives, boils and many other scrofulous conditions. This is a robust potion of powerful character. Care should be taken when brewing. Prepared incorrectly this potion has been known to cause boils, rather than cure them...\" -Zygmunt Budge");

      effect = new PotionEffect(PotionEffectType.HEAL, duration, 1);
      potionColor = Color.fromRGB(65, 105, 225);
   }
}
