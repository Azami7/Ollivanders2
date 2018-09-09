package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Super class for transfiguring friendly mobs.
 *
 * @since 2.2.6
 * @author Azami7
 */
public abstract class FriendlyMobDisguiseSuper extends EntityDisguiseSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FriendlyMobDisguiseSuper (O2SpellType type)
   {
      super(type);
   }

   /**
    * Constructor.
    *
    * @param plugin
    * @param player
    * @param type
    * @param rightWand
    */
   public FriendlyMobDisguiseSuper (Ollivanders2 plugin, Player player, O2SpellType type, Double rightWand)
   {
      super(plugin, player, type, rightWand);

      for (EntityType e :p.common.smallFriendlyAnimals)
      {
         entityWhitelist.add(e);
      }

      int uses = (int)(usesModifier * 5);

      if (uses > 100)
      {
         for (EntityType e : p.common.mediumFriendlyAnimals)
         {
            entityWhitelist.add(e);
         }
      }

      if (uses > 200)
      {
         for (EntityType e : p.common.largeFriendlyAnimals)
         {
            entityWhitelist.add(e);
         }
      }
   }

   /**
    * Check whether player has permissions to damage friendly mobs in their current location or in the
    * target entity's location.
    *
    * @param e the target entity
    * @return true if the player has the right permissions, false otherwise
    */
   @Override
   protected boolean wgPermissionsCheck(Entity e)
   {
      if (worldGuard.checkWGFriendlyMobDamage(player, player.getLocation()))
      {
         return true;
      }
      else if (worldGuard.checkWGFriendlyMobDamage(player, e.getLocation()))
      {
         return true;
      }
      else
      {
         return false;
      }
   }
}
