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
   public O2SpellType spellType = O2SpellType.REPARIFARGE;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("Incomplete Transfigurations are difficult to put right, but you must attempt to do so. Leaving the head of a rabbit on a footstool is irresponsible and dangerous. Say 'Reparifarge!' and the object or creature should return to its natural state.");
   }};

   protected String text = "Reparifarge will cause the duration of the transfiguration on the targeted entity to decrease.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public REPARIFARGE () { }

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
   }

   @Override
   public void checkEffect ()
   {
      move();
      List<Entity> entities = this.getCloseEntities(1);
      for (Entity entity : entities)
      {
         for (SpellProjectile proj : p.getProjectiles())
         {
            if (proj instanceof Transfiguration)
            {
               if (entity instanceof EnderDragonPart)
               {
                  entity = ((EnderDragonPart) entity).getParent();
               }
               if (entity.getUniqueId() == ((Transfiguration) proj).getToID())
               {
                  proj.lifeTicks = proj.lifeTicks + (int) (usesModifier * 1200) + 160;
               }
            }
         }
      }
   }
}