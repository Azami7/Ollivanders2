package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Causes blindness in a radius larger than fumos.
 *
 * @see FumosSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class FUMOS_DUO extends FumosSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public FUMOS_DUO(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.FUMOS_DUO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>() {{
         add("A Stronger Smoke-Screen Spell");
      }};

      text = "Fumos Duo will cause those in an area to be blinded by a smoke cloud. The blindness lasts for a time twice as long as that created by Fumos";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FUMOS_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.FUMOS_DUO;
      branch = O2MagicBranch.CHARMS;

      strengthModifier = 2;
      minDurationInSeconds = 30;
      maxDurationInSeconds = 240;

      initSpell();
   }
}
