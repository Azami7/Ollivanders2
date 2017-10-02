package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Gives an entity a healing effect for usesModifier seconds
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class EPISKEY extends Healing
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EPISKEY ()
   {
      super();

      flavorText.add("\"Episkey,\" said Tonks. Harry's nose felt very hot, then very cold. He raised a hand and felt it gingerly. It seemed to be mended.");
      flavorText.add("A minor healing spell.");
      text = "Episkey will heal minor injuries.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public EPISKEY (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(1))
      {
         live.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (int) (usesModifier * 20), 0), true);
         kill();
         return;
      }
   }
}