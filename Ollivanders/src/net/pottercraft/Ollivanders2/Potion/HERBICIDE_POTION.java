package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.LivingEntity;

/**
 * Herbicide Potion (or simply Herbicide) is a potion that kills or damages creepers. It will also harm other
 * entities, including players, though not as much as Creepers.
 *
 * @since 2.2.7
 * @author Azami7
 */
public final class HERBICIDE_POTION extends O2Potion implements O2SplashPotion
{
   private final double minimumEffect = 0.1;

   public HERBICIDE_POTION (Ollivanders2 plugin)
   {
      super (plugin);

      potionMaterialType = Material.SPLASH_POTION;

      ingredients.put(Material.RAW_FISH, 4);
      ingredients.put(Material.SUGAR, 2);
      ingredients.put(Material.MAGMA_CREAM, 2);
      ingredients.put(Material.SLIME_BALL, 2);

      name = "Herbicide Potion";
      text = "The Herbicide Potion is damages or kills Creepers. It is also harmful to other living creatures." + getIngredientsText();

      // set duration of potion effect to 30 seconds
      duration = 600;
      effect = new PotionEffect(PotionEffectType.HARM, duration, 1);
      potionColor = Color.OLIVE;
   }

   public void drink (O2Player o2p, Player player) { }

   /**
    * Reduce intensity of this potion if the affected entity is not a Creeper.
    *
    * @param event the splash potion thrown event
    */
   public void thrownEffect (PotionSplashEvent event)
   {
      for (LivingEntity e : event.getAffectedEntities())
      {
         if (e.getType() != EntityType.CREEPER)
         {
            event.setIntensity(e, minimumEffect);
         }
      }
   }
}
