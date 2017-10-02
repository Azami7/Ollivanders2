package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Gives a target blindness
 *
 * @author lownes
 * @author Azami7
 */
public final class OBSCURO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public OBSCURO ()
   {
      super();

      flavorText.add("A black blindfold appeared over Phineas Nigellus' clever, dark eyes, causing him to bump into the frame and shriek with pain.");
      text = "Obscuro will blind the target.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public OBSCURO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(1))
      {
         PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, (int) (usesModifier * 1200), 0);
         live.addPotionEffect(blind);
         kill();
         return;
      }
   }
}
