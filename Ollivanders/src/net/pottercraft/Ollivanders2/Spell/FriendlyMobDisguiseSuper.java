package net.pottercraft.Ollivanders2.Spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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

      worldGuardFlags = new ArrayList<>();
      worldGuardFlags.add(DefaultFlag.DAMAGE_ANIMALS);
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
}
