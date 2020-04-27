package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * The Wiggenweld Potion is a healing potion with the power to awaken a person from a magically-induced sleep, which
 * gives it the ability to reverse the effects of potions like the Sleeping Draught and the Draught of Living Death.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class WIGGENWELD_POTION extends O2Potion implements O2SplashPotion
{
   /**
    * Constructor
    *
    * @param plugin a callback to the plugin
    */
   public WIGGENWELD_POTION (Ollivanders2 plugin)
   {
      super(plugin);

      potionType = O2PotionType.WIGGENWELD_POTION;
      potionLevel = PotionLevel.OWL;

      potionMaterialType = Material.SPLASH_POTION;

      ingredients.put(O2ItemType.HORKLUMP_JUICE, 1);
      ingredients.put(O2ItemType.FLOBBERWORM_MUCUS, 2);
      ingredients.put(O2ItemType.CHIZPURFLE_FANGS, 1);
      ingredients.put(O2ItemType.BILLYWIG_STING_SLIME, 2);
      ingredients.put(O2ItemType.MINT_SPRIG, 1);
      ingredients.put(O2ItemType.BOOM_BERRY_JUICE, 1);
      ingredients.put(O2ItemType.MANDRAKE_LEAF, 2);
      ingredients.put(O2ItemType.HONEYWATER, 1);
      ingredients.put(O2ItemType.SLOTH_BRAIN_MUCUS, 1);
      ingredients.put(O2ItemType.MOONDEW_DROP, 3);
      ingredients.put(O2ItemType.SALAMANDER_BLOOD, 1);
      ingredients.put(O2ItemType.LIONFISH_SPINES, 10);
      ingredients.put(O2ItemType.UNICORN_HORN, 1);
      ingredients.put(O2ItemType.WOLFSBANE, 2);
      ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 1);

      text = "The Wiggenweld Potion is a healing potion which can also be used to wake the drinker from magically-induced sleep.";
      flavorText.add("\"Today you will learn to brew the Wiggenweld Potion. It is a powerful healing potion that can be used to heal injuries, or reverse the effects of a Sleeping Draught.\" -Severus Snape");

      potionColor = Color.fromRGB(0, 206, 209);
      effect = new PotionEffect(PotionEffectType.HEAL, duration, 1);
   }

   @Override
   public void drink (O2Player o2p, Player player) { }

   @Override
   public void thrownEffect (PotionSplashEvent event)
   {
      for (LivingEntity e : event.getAffectedEntities())
      {
         if (e instanceof Player)
         {
            Player player = (Player)e;
            Ollivanders2API.getPlayers().playerEffects.removeEffect(player.getUniqueId(), O2EffectType.SLEEPING);
         }
      }
   }
}
