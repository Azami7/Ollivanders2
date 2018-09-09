package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Aazmi7 on 6/29/17. Imported from iarepandemonium/Ollivanders.
 *
 * @version Ollivanders2
 * @since 2.5.3
 * @author lownes
 * @author Azami7
 */
public final class LAPIDO extends BlockTransfigurationSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LAPIDO (O2SpellType type)
   {
      super(type);

      flavorText.add("Cobblestone to Stone Spell");
      text = "Turns cobblestone in to stone.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param type
    * @param rightWand
    */
   public LAPIDO(Ollivanders2 plugin, Player player, O2SpellType type, Double rightWand)
   {
      super(plugin, player, type, rightWand);

      transfigureType = Material.STONE;
      materialWhitelist.add(Material.COBBLESTONE);
   }
}
