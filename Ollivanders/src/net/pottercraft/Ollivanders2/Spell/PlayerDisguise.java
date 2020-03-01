package net.pottercraft.Ollivanders2.Spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Super class for all transfigurations of players.
 *
 * @author Azami7
 * @since 2.2.6
 */
public abstract class PlayerDisguise extends EntityDisguise
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PlayerDisguise()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PlayerDisguise(Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      entityWhitelist.add(EntityType.PLAYER);

      // world guard flags
      worldGuardFlags.add(DefaultFlag.PVP);
   }

   /**
    * Calculate the success rate for this spell. This has to be run from the spell itself after setting
    * usesModifier since it is based on specific spell usage.
    */
   void calculateSuccessRate ()
   {
      if (usesModifier < 10)
         successRate = 10;
      else if (usesModifier < 100)
      {
         successRate = (int) usesModifier;
      }
      else
         successRate = 100;
   }
}
