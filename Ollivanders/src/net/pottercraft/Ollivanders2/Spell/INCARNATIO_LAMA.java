package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.LlamaWatcher;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created by Azami7 on 6/28/17.
 *
 * Turn target player in to a llama.
 *
 * @since 2.2.3
 * @author Azami7
 */
public final class INCARNATIO_LAMA extends PlayerDisguiseSuper
{
   public O2SpellType spellType = O2SpellType.INCARNATIO_LAMA;

   protected String text = "Turns target player in to a llama.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCARNATIO_LAMA () { }
   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public INCARNATIO_LAMA(Ollivanders2 plugin, Player player, O2SpellType type, Double rightWand)
   {
      super(plugin, player, rightWand);

      targetType = EntityType.LLAMA;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      LlamaWatcher watcher = (LlamaWatcher)disguise.getWatcher();
      watcher.setAdult();

      Ollivanders2Common common = new Ollivanders2Common(p);
      watcher.setColor(common.randomLlamaColor());
   }
}
