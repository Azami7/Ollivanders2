package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Super class for all Entity to Object transfigurations. This cannot be used for player entities.
 *
 * @since 2.2.5
 * @author Azami7
 */
public abstract class EntityToObjectTransfigurationSuper extends SpellProjectile implements O2Spell
{
   // these should generally not be changed
   boolean isTransfigured = false;
   EntityType originalEntityType = null;
   protected O2MagicBranch branch = O2MagicBranch.TRANSFIGURATION;

   // these should be set by each spell
   Material transfigureType = Material.AIR;
   List<EntityType> entityBlacklist = new ArrayList<>();
   List<EntityType> entityWhitelist = new ArrayList<>();
   protected ArrayList<String> flavorText = new ArrayList<>();
   protected String text = "";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EntityToObjectTransfigurationSuper () { }

   /**
    * Constructor for casting a transfiguration spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public EntityToObjectTransfigurationSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      entityBlacklist.add(EntityType.PAINTING);
      entityBlacklist.add(EntityType.ITEM_FRAME);
      entityBlacklist.add(EntityType.AREA_EFFECT_CLOUD);
   }

   @Override
   public void checkEffect ()
   {
      // if the entity has not transfigured, transfigure it
      if (!isTransfigured)
      {
         // move the projectile
         move();
         Block center = getBlock();
         if (center.getType() != Material.AIR)
         {
            if (center.getState() instanceof Entity)
            {
               List<Entity> closeEntities = getCloseEntities(1);
               // do transfiguration
               Entity target = closeEntities.get(0);

               if (!checkWorldGuard(target))
               {
                  kill();
                  p.spellCannotBeCastMessage(player);
                  return;
               }

               transfigure(target);
            }
         }
      }
      // if the object has transfigured
      else
      {
         kill();
      }
   }

   protected boolean checkWorldGuard (Entity entity)
   {
      boolean wgOK = true;

      // build permissions

      // if target is creature, can friendly mobs be damaged

      // if target is vehicle, vehicle-destroy
      return wgOK;
   }

   protected void transfigure (Entity target)
   {

   }
}
