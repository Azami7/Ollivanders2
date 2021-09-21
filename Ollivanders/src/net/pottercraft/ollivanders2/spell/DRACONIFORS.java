package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Turn target entity in to a Dragon.
 *
 * @author lownes
 * @author Azami7
 * @since 1.0
 */
public final class DRACONIFORS extends FriendlyMobDisguise
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public DRACONIFORS(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.DRACONIFORS;
      branch = O2MagicBranch.TRANSFIGURATION;

      flavorText = new ArrayList<>()
      {{
         add("The Draconifors Transfiguration");
         add("\"It was great! Now I can turn anything into dragons!\" -Hermione Granger");
      }};

      text = "The Draconifors spell turns an entity in to a dragon. It is one of the most challenging transfigurations owing to the size and power of the dragon form.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public DRACONIFORS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.DRACONIFORS;
      branch = O2MagicBranch.TRANSFIGURATION;

      initSpell();

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