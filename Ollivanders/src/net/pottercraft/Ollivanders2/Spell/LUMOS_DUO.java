package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Creates a line of glowstone that goes away after a time.
 *
 * @author lownes
 * @author Azami7
 */
public final class LUMOS_DUO extends Charms
{
   public O2SpellType spellType = O2SpellType.LUMOS_DUO;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("A variation of the Wand-Lighting Charm.");
   }};

   protected String text = "Creates a stream of flowstone to light your way.";

   private List<Block> line = new ArrayList();

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LUMOS_DUO () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public LUMOS_DUO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      lifeTicks = (int) (-(usesModifier * 20));
   }

   public void checkEffect ()
   {
      move();
      if (getBlock().getType() != Material.AIR)
      {
         kill = false;
      }
      else
      {
         location.getBlock().setType(Material.GLOWSTONE);
         p.getTempBlocks().add(getBlock());
         line.add(getBlock());
      }
      if (lifeTicks >= 159)
      {
         for (Block block : line)
         {
            if (block.getType() == Material.GLOWSTONE)
            {
               block.setType(Material.AIR);
            }
            p.getTempBlocks().remove(block);
         }
         kill = true;
      }
   }
}