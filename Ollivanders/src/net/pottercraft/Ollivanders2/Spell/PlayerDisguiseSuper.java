package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Super class for all transfigurations of players.
 *
 * @since 2.2.6
 * @author Azami7
 */
public class PlayerDisguiseSuper extends EntityDisguiseSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PlayerDisguiseSuper () {}

   /**
    * Constructor.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public PlayerDisguiseSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      entityWhitelist.add(EntityType.PLAYER);

      if (usesModifier < 10)
         successRate = 10;
      else if (usesModifier < 100)
         successRate = (int)usesModifier;
      else
         successRate = 100;
   }

   /**
    * Check whether player has permissions to affect other players in their current location or in the
    * target entity's location.
    *
    * @param e the target entity
    * @return true if the player has the right permissions, false otherwise
    */
   @Override
   protected boolean wgPermissionsCheck(Entity e)
   {
      if (worldGuard.checkWGPVP(player, player.getLocation()))
      {
         return true;
      }
      else if (worldGuard.checkWGPVP(player, e.getLocation()))
      {
         return true;
      }
      else
      {
         return false;
      }
   }
}
