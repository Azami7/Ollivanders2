package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Turns cobblestone in to stone. Originally imported from iarepandemonium/Ollivanders.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 * @since 2.5.3
 */
public final class LAPIDO extends BlockTransfiguration
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LAPIDO()
   {
      super();

      spellType = O2SpellType.LAPIDO;
      branch = O2MagicBranch.TRANSFIGURATION;

      flavorText = new ArrayList<String>()
      {{
         add("Cobblestone to Stone Spell");
      }};

      text = "Turns cobblestone in to stone.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public LAPIDO(Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.LAPIDO;
      branch = O2MagicBranch.TRANSFIGURATION;

      transfigureType = Material.STONE;
      materialWhitelist.add(Material.COBBLESTONE);

      initSpell();
   }
}
