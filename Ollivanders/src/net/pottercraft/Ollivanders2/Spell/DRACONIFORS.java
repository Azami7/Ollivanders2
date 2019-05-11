package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.EnderDragonWatcher;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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

      spellType = O2SpellType.DRACONIFORS;

      flavorText = new ArrayList<String>() {{
         add("The Draconifors Transfiguration");
         add("\"It was great! Now I can turn anything into dragons!\" -Hermione Granger");
      }};

      text = "Turn an entity in to a dragon.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public DRACONIFORS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.DRACONIFORS;

      // set up usage modifier, has to be done here to get the uses for this specific spell
      setUsesModifier();

      targetType = EntityType.ENDER_DRAGON;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      if (usesModifier < 20)
         successRate = 5;
      else if (usesModifier < 100)
         successRate = 10;
      else
         successRate = 20;
   }
}