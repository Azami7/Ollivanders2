package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Pulls a living entity towards the caster.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class CARPE_RETRACTUM extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public CARPE_RETRACTUM ()
   {
      super();

      flavorText.add("\"...which is why the Carpe Retractum spell is useful. It allows you to seize and pull objects within your direct line of sight towards you...\" -Professor Flitwick");
      flavorText.add("Seize and Pull Charm");
      text = "Pulls a living entity towards you.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public CARPE_RETRACTUM (Ollivanders2 plugin, Player player, O2SpellType name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      List<LivingEntity> living = getLivingEntities(2);
      for (LivingEntity live : living)
      {
         live.setVelocity(player.getEyeLocation().toVector().subtract(live.getLocation().toVector()).normalize().multiply(usesModifier / 10));
         kill();
         return;
      }
   }
}