package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.RabbitWatcher;
import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Transfigures entity into a rabbit.
 *
 * @author Azami7
 * @link https://github.com/Azami7/Ollivanders2/issues/51
 * @since 2.2.3
 */
public final class LAPIFORS extends FriendlyMobDisguise
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LAPIFORS()
   {
      super();

      spellType = O2SpellType.LAPIFORS;
      branch = O2MagicBranch.TRANSFIGURATION;

      flavorText = new ArrayList<String>()
      {{
         add("\"Lapifors, the transformation of a small object into a rabbit\" -Hermione Granger");
      }};

      text = "The transfiguration spell Lapifors will transfigure an entity into a rabbit.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public LAPIFORS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.LAPIFORS;
      branch = O2MagicBranch.TRANSFIGURATION;

      initSpell();

      targetType = EntityType.RABBIT;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      RabbitWatcher watcher = (RabbitWatcher)disguise.getWatcher();
      watcher.setAdult();

      watcher.setType(Ollivanders2API.common.randomRabbitType());
   }
}
