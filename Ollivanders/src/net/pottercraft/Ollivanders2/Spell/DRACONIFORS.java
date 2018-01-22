package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.EnderDragonWatcher;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Turn target entity in to a Dragon.
 *
 * @since 1.0
 * @author lownes
 * @author Azami7
 */
public final class DRACONIFORS extends FriendlyMobDisguiseSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DRACONIFORS ()
   {
      super();

      flavorText.add("The Draconifors Transfiguration");
      flavorText.add("\"It was great! Now I can turn anything into dragons!\" -Hermione Granger");
      text = "Turn an entity in to a dragon.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public DRACONIFORS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      targetType = EntityType.ENDER_DRAGON;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      EnderDragonWatcher watcher = (EnderDragonWatcher)disguise.getWatcher();

      if (usesModifier < 20)
         successRate = 5;
      else if (usesModifier < 100)
         successRate = 10;
      else
         successRate = 20;
   }
}