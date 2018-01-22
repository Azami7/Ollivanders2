package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.OcelotWatcher;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;

/**
 * Created by Azami7 on 6/28/17.
 *
 * Turn target player in to an ocelot.
 *
 * @since 2.2.3
 * @author lownes
 * @author Azami7
 */
public final class INCARNATIO_FELIS extends PlayerDisguiseSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCARNATIO_FELIS ()
   {
      super();

      text = "Turns target player in to an ocelot or cat.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public INCARNATIO_FELIS(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      targetType = EntityType.OCELOT;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      OcelotWatcher watcher = (OcelotWatcher)disguise.getWatcher();
      watcher.setAdult();

      int rand = Math.abs(Ollivanders2.random.nextInt() % 3);
      if (rand == 0)
         watcher.setType(Ocelot.Type.BLACK_CAT);
      else if (rand == 1)
         watcher.setType(Ocelot.Type.RED_CAT);
      else
         watcher.setType(Ocelot.Type.SIAMESE_CAT);

      rand = Ollivanders2.random.nextInt() % 10;
      if (rand == 0)
         watcher.isTamed();
   }
}
