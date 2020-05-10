package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.HorseWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Transfigures an entity into a horse.
 *
 * @author lownes
 * @author Azami7
 * @since 1.0
 */
public final class EQUUSIFORS extends FriendlyMobDisguise
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EQUUSIFORS()
   {
      super();

      spellType = O2SpellType.EQUUSIFORS;
      branch = O2MagicBranch.TRANSFIGURATION;

      text = "Turns target entity in to a horse.";
   }

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
      spellType = O2SpellType.EQUUSIFORS;
      branch = O2MagicBranch.TRANSFIGURATION;

      initSpell();

      targetType = EntityType.HORSE;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      HorseWatcher watcher = (HorseWatcher) disguise.getWatcher();
      watcher.setAdult();

      // randomize appearance
      Ollivanders2Common common = new Ollivanders2Common(p);
      watcher.setStyle(common.getRandomHorseStyle());
      watcher.setColor(common.getRandomHorseColor());
   }
}