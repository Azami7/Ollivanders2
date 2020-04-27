package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.PolarBearWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Turns target player in to a polar bear.
 *
 * @author Azami7
 * @since 2.2.6
 */
public class INCARNATIO_URSUS extends PlayerDisguise
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCARNATIO_URSUS()
   {
      super();

      spellType = O2SpellType.INCARNATIO_URSUS;
      branch = O2MagicBranch.TRANSFIGURATION;

      text = "Turns target player in to a polar bear.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public INCARNATIO_URSUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.INCARNATIO_URSUS;
      branch = O2MagicBranch.TRANSFIGURATION;

      initSpell();
      calculateSuccessRate();

      targetType = EntityType.POLAR_BEAR;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      PolarBearWatcher watcher = (PolarBearWatcher) disguise.getWatcher();
      watcher.setAdult();
      watcher.setStanding(true);
   }
}
