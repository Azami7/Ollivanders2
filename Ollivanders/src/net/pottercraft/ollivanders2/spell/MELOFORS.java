package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Encases target's head in a melon
 *
 * @author lownes
 */
public final class MELOFORS extends GaleatiSuper
{
   static Material[] melons = {
           Material.MELON,
           Material.JACK_O_LANTERN,
           Material.PUMPKIN,
   };

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MELOFORS()
   {
      super();

      spellType = O2SpellType.MELOFORS;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("Harry overheard one second-year girl assuring another that Fudge was now lying in St Mungo’s with a pumpkin for a head.\"");
         add("The Melon-Head Spell");
      }};

      text = "Melofors places a melon on the target player's head.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public MELOFORS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.MELOFORS;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      materialType = melons[Math.abs(Ollivanders2Common.random.nextInt() % melons.length)];
   }
}