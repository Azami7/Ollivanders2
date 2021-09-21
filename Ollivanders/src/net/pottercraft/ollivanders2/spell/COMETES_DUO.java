package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A fancier version of COMETES.
 *
 * @author Azami7
 * @see COMETES
 * @see Pyrotechnia
 */
public final class COMETES_DUO extends Pyrotechnia
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public COMETES_DUO(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.COMETES_DUO;
      branch = O2MagicBranch.CHARMS;

      text = "Creates one or more orange burst fireworks with trails, flicker, and fades to white and yellow.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public COMETES_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.COMETES_DUO;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.ORANGE);
      fireworkType = Type.BURST;

      hasTrails = true;
      hasFlicker = true;
      hasFade = true;

      fadeColors = new ArrayList<>();
      fadeColors.add(Color.YELLOW);
      fadeColors.add(Color.WHITE);

      setMaxFireworks(10);
   }
}
