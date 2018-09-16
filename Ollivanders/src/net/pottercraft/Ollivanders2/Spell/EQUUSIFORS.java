package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.HorseWatcher;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Transfigures an entity into a horse.
 *
 * @since 1.0
 * @author lownes
 * @author Azami7
 */
public final class EQUUSIFORS extends FriendlyMobDisguiseSuper
{
   public O2SpellType spellType = O2SpellType.EQUUSIFORS;

   protected String text = "Turns target entity in to a horse.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EQUUSIFORS () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public EQUUSIFORS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      targetType = EntityType.HORSE;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      HorseWatcher watcher = (HorseWatcher)disguise.getWatcher();
      watcher.setAdult();

      // randomize appearance
      Ollivanders2Common common = new Ollivanders2Common(p);
      watcher.setStyle(common.randomHorseStyle());
      watcher.setColor(common.randomHorseColor());
   }
}