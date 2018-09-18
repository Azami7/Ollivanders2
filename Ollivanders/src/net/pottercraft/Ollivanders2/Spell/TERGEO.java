package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * The siphoning spell.
 *
 * @author cakenggt
 * @author Azami7
 */
public final class TERGEO extends Charms
{
   boolean move;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public TERGEO ()
   {
      super();

      spellType = O2SpellType.TERGEO;

      flavorText = new ArrayList<String>() {{
         add("The Siphoning Spell");
         add("The wand siphoned off most of the grease. Looking rather pleased with himself, Ron handed the slightly smoking handkerchief to Hermione.");
      }};

      text = "Tergeo will siphon off a block of water where it hits. It will also disable any Aguamenti-placed water blocks nearby.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public TERGEO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.TERGEO;
      move = true;
   }

   @Override
   public void checkEffect ()
   {
      if (move)
      {
         move();
         if (getBlock().getType() == Material.WATER || getBlock().getType() == Material.STATIONARY_WATER)
         {
            Block block = location.getBlock();
            block.setType(Material.AIR);
            changed.add(block);
            kill = false;
            move = false;
            lifeTicks = (int) (-(usesModifier * 1200));
         }
         for (O2Spell proj : p.getProjectiles())
         {
            if (proj.spellType == O2SpellType.AGUAMENTI && proj.location.getWorld() == location.getWorld())
            {
               if (proj.location.distance(location) < 1)
               {
                  proj.revert();
                  proj.kill();
               }
            }
         }
      }
      else
      {
         lifeTicks++;
      }
      if (lifeTicks >= 159)
      {
         revert();
         kill();
      }
   }

   @Override
   public void revert ()
   {
      for (Block block : changed)
      {
         Material mat = block.getType();
         if (mat == Material.AIR)
         {
            block.setType(Material.STATIONARY_WATER);
         }
      }
   }
}