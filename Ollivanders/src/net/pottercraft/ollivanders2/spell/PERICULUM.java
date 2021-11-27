package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Shoots red burst fireworks in to the air.
 *
 * @author lownes
 * @author Azami7
 * @see Pyrotechnia
 */
public final class PERICULUM extends Pyrotechnia
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public PERICULUM(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.PERICULUM;
      branch = O2MagicBranch.CHARMS;

      text = "Conjures red burst fireworks in the air.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PERICULUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PERICULUM;
      branch = O2MagicBranch.CHARMS;

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.RED);
      fireworkType = Type.BURST;

      initSpell();
   }

   @Override
   void doInitSpell()
   {
      setMaxFireworks(10);
   }
}