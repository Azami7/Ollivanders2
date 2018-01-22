package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.AgeableWatcher;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Transfigures entity into a chicken.
 *
 * @since 1.0
 * @author Azami7
 */
public final class DUCKLIFORS extends FriendlyMobDisguiseSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DUCKLIFORS ()
   {
      super();

      text = "The transfiguration spell Ducklifors will transfigure an entity into a chicken.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public DUCKLIFORS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      targetType = EntityType.CHICKEN;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      AgeableWatcher watcher = (AgeableWatcher)disguise.getWatcher();
      watcher.setAdult();
   }
}