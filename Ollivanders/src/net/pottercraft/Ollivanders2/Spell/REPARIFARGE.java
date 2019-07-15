package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Untransfiguration
 *
 * @author cakenggt
 * @author Azami7
 */
public final class REPARIFARGE extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public REPARIFARGE ()
   {
      super();

      spellType = O2SpellType.REPARIFARGE;

      flavorText = new ArrayList<String>() {{
         add("Incomplete Transfigurations are difficult to put right, but you must attempt to do so. Leaving the head of a rabbit on a footstool is irresponsible and dangerous. Say 'Reparifarge!' and the object or creature should return to its natural state.");
      }};

      text = "Reparifarge will cause the duration of the transfiguration on the targeted entity to decrease.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public REPARIFARGE (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.REPARIFARGE;
      setUsesModifier();
   }

   @Override
   public void doCheckEffect ()
   {
      for (Entity entity : getCloseEntities(1.5))
      {
         if (entity instanceof EnderDragonPart)
         {
            entity = ((EnderDragonPart) entity).getParent();
         }

         // determine if the entity is temporarily transfigured
         for (O2Spell spell : p.getProjectiles())
         {
            if (spell instanceof Transfiguration)
            {
               if (entity.getUniqueId() == ((Transfiguration) spell).getToID())
               {
                  int percent = (int) usesModifier / 20;

                  ((Transfiguration) spell).reparifarge(percent);
               }
            }
         }
      }
   }
}