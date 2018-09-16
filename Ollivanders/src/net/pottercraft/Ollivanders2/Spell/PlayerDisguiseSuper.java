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
public abstract class PlayerDisguiseSuper extends EntityDisguiseSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PlayerDisguiseSuper () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PlayerDisguiseSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      entityWhitelist.add(EntityType.PLAYER);

      int uses = (int)(usesModifier * 5);

      if (uses < 10)
         successRate = 10;
      else if (uses < 100)
         successRate = uses;
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
