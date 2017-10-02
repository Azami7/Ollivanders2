package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Creates a glowstone at the reticule that goes away after a time.
 *
 * @author lownes
 */
public final class LUMOS_MAXIMA extends Charms
{
   boolean lit;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LUMOS_MAXIMA ()
   {
      super();

      flavorText.add("\"Light your wands, can’t you? And hurry, we have little time!\" -Griphook");
      text = "Lumos Maxima will spawn a glowstone at the impact site.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public LUMOS_MAXIMA (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      lit = false;
   }

   @Override
   public void checkEffect ()
   {
      if (!lit)
      {
         move();
         if (getBlock().getType() != Material.AIR)
         {
            location.subtract(vector);
            location.getBlock().setType(Material.GLOWSTONE);
            p.getTempBlocks().add(getBlock());
            lit = true;
            lifeTicks = (int) (-(usesModifier * 1200));
            kill = false;
         }
      }
      else
      {
         if (getBlock().getType() == Material.GLOWSTONE)
         {
            location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
            lifeTicks++;
         }
         else
         {
            kill();
         }
      }
      if (lifeTicks >= 159)
      {
         revert();
         kill();
      }
   }

   @Override
   public void revert ()
   {
      if (getBlock().getType() == Material.GLOWSTONE)
      {
         location.getBlock().setType(Material.AIR);
      }
      p.getTempBlocks().remove(location.getBlock());
   }

}