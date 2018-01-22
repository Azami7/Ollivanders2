package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.HorseWatcher;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Transfigures an entity into a horse.
 *
 * @since 1.0
 * @author lownes
 * @author Azami7
 */
public final class EQUUSIFORS extends FriendlyMobDisguiseSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EQUUSIFORS ()
   {
      super();

      text = "Turns target entity in to a horse.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public EQUUSIFORS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      targetType = EntityType.HORSE;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      HorseWatcher watcher = (HorseWatcher)disguise.getWatcher();
      watcher.setAdult();

      // randomize appearance
      watcher.setStyle(INCARNATIO_EQUUS.getRandomHorseStyle());
      watcher.setColor(INCARNATIO_EQUUS.getRandomHorseColor());
   }
}