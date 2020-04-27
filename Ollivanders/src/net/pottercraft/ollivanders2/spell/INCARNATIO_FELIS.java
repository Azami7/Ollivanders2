package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.OcelotWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created by Azami7 on 6/28/17.
 * <p>
 * Turn target player in to an ocelot.
 *
 * @author lownes
 * @author Azami7
 * @since 2.2.3
 */
public final class INCARNATIO_FELIS extends PlayerDisguise
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCARNATIO_FELIS()
   {
      super();

      spellType = O2SpellType.INCARNATIO_FELIS;
      branch = O2MagicBranch.TRANSFIGURATION;

      text = "Turns target player in to an ocelot or cat.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public INCARNATIO_FELIS(Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.INCARNATIO_FELIS;
      branch = O2MagicBranch.TRANSFIGURATION;

      initSpell();
      calculateSuccessRate();

      targetType = EntityType.OCELOT;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      OcelotWatcher watcher = (OcelotWatcher) disguise.getWatcher();
      watcher.setAdult();

      Ollivanders2Common common = new Ollivanders2Common(p);
      watcher.setType(common.randomOcelotType());

      int rand = Ollivanders2Common.random.nextInt() % 10;
      if (rand == 0)
         watcher.isTamed();
   }
}
