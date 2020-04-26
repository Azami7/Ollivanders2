package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.entity.Entity;
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
   public FriendlyMobDisguiseSuper ()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FriendlyMobDisguiseSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      entityWhitelist.addAll(Ollivanders2Common.smallFriendlyAnimals);

      int uses = (int)(usesModifier * 5);

      if (uses > 100)
      {
         entityWhitelist.addAll(Ollivanders2Common.mediumFriendlyAnimals);
      }

      if (uses > 200)
      {
         entityWhitelist.addAll(Ollivanders2Common.largeFriendlyAnimals);
      }
   }

   /**
    * Check whether player has permissions to damage friendly mobs in their current location and in the
    * target entity's location.
    *
    * @param e the target entity
    * @return true if the player has the right permissions, false otherwise
    */
   @Override
   protected boolean wgPermissionsCheck(Entity e)
   {
      if (worldGuard.checkWGFriendlyMobDamage(player, player.getLocation()) && worldGuard.checkWGFriendlyMobDamage(player, e.getLocation()))
      {
         return true;
      }

      return false;
   }
}
