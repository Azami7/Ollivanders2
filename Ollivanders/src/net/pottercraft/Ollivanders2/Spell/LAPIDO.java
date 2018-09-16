package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

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
   public O2SpellType spellType = O2SpellType.LAPIDO;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("Cobblestone to Stone Spell");
   }};

   protected String text = "Turns cobblestone in to stone.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LAPIDO () { }

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

      transfigureType = Material.STONE;
      materialWhitelist.add(Material.COBBLESTONE);
   }
}
