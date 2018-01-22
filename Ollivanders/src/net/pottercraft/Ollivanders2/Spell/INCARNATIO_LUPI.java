package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/28/17.
 *
 * Turn target player in to a wolf.
 *
 * @since 2.2.3
 * @author lownes
 * @author Azami7
 */
public final class INCARNATIO_LUPI extends PlayerDisguiseSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCARNATIO_LUPI ()
   {
      super();
      text = "Turns target player in to a wolf or dog.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public INCARNATIO_LUPI(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      targetType = EntityType.WOLF;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      WolfWatcher watcher = (WolfWatcher)disguise.getWatcher();
      watcher.setAdult();

      int rand = Math.abs(Ollivanders2.random.nextInt() % 10);
      if (rand < 9)
      {
         watcher.isTamed();

         rand = Math.abs(Ollivanders2.random.nextInt() % 6);
         if (rand == 0)
            watcher.setCollarColor(DyeColor.BLUE);
         else if (rand == 1)
            watcher.setCollarColor(DyeColor.GREEN);
         else if (rand == 2)
            watcher.setCollarColor(DyeColor.ORANGE);
         else if (rand == 3)
            watcher.setCollarColor(DyeColor.PURPLE);
         else if (rand == 4)
            watcher.setCollarColor(DyeColor.YELLOW);
         else
            watcher.setCollarColor(DyeColor.RED);
      }
   }
}
