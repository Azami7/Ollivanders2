package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Creates a larger incendio that strafes and has 4x the radius and duration.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class INCENDIO_TRIA extends IncendioSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public INCENDIO_TRIA(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.INCENDIO_TRIA;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>() {{
         add("\"Incendio!\" said Mr Weasley, pointing his wand at the hole in the wall behind him. Flames rose at once in the fireplace, crackling merrily as though they had been burning for hours.");
         add("The Strongest Fire-Making Charm");
      }};

      text = "Incendio Tria will burn blocks and entities it passes by. It's radius is four times that of Incendio and it's duration one quarter.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public INCENDIO_TRIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.INCENDIO_TRIA;
      branch = O2MagicBranch.CHARMS;

      location.add(vector.multiply(2));
      strafe = true;
      radius = 2;
      blockRadius = 4;
      durationModifier = 4;

      initSpell();
   }
}