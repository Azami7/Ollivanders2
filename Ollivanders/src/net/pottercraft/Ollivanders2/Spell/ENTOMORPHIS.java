package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.SpiderWatcher;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Turn target player in to a spider.
 *
 * @since 2.2.6
 * @link https://github.com/Azami7/Ollivanders2/issues/30
 * @author Azami7
 */
public final class ENTOMORPHIS extends PlayerDisguiseSuper
{
   public ENTOMORPHIS ()
   {
      super();

      flavorText.add("What wouldn't he give to strike now, to jinx Dudley so thoroughly he'd have to crawl home like an insect, struck dumb, sprouting feelers...");
      flavorText.add("The Insect Jinx");
      text = "Entomorphis will transfigure an entity into a spider a duration dependent on your experience.";
   }

   public ENTOMORPHIS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      targetType = EntityType.SPIDER;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      SpiderWatcher watcher = (SpiderWatcher)disguise.getWatcher();
   }
}