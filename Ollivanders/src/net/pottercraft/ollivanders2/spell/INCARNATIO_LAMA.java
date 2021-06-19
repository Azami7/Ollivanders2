package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.LlamaWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Azami7 on 6/28/17.
 * <p>
 * Turn target player in to a llama.
 *
 * @author Azami7
 * @since 2.2.3
 */
public final class INCARNATIO_LAMA extends PlayerDisguise
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCARNATIO_LAMA()
   {
      super();

      spellType = O2SpellType.INCARNATIO_LAMA;
      branch = O2MagicBranch.TRANSFIGURATION;

      text = "Turns target player in to a llama.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public INCARNATIO_LAMA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.INCARNATIO_LAMA;
      branch = O2MagicBranch.TRANSFIGURATION;

      initSpell();
      calculateSuccessRate();

      targetType = EntityType.LLAMA;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      LlamaWatcher watcher = (LlamaWatcher) disguise.getWatcher();
      watcher.setAdult();

      Ollivanders2Common common = new Ollivanders2Common(p);
      watcher.setColor(common.getRandomLlamaColor());
   }
}
