package Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Spell shoots a block of water at a target, extinguishing fire
 *
 * @author lownes
 */
public class AQUA_ERUCTO extends SpellProjectile implements Spell
{

   private boolean move;
   private double lifeTime;

   @SuppressWarnings("deprecation")
   public AQUA_ERUCTO (Ollivanders2 p, Player player, Spells name, Double rightWand)
   {
      super(p, player, name, rightWand);
      lifeTime = usesModifier * 16;
      move = true;
      moveEffectData = Material.WATER.getId();
   }

   public void checkEffect ()
   {
      if (move)
      {
         move();
         //Check if the blocks set on fire are still on fire
         Block block = getBlock();
         Material type = block.getType();
         if (type == Material.FIRE)
         {
            block.setType(Material.AIR);
            changed.add(block);
         }
         if (type == Material.LAVA || type == Material.STATIONARY_LAVA)
         {
            block.setType(Material.OBSIDIAN);
            changed.add(block);
         }
         List<LivingEntity> living = getLivingEntities(1);
         for (LivingEntity live : living)
         {
            live.setFireTicks(0);
            kill();
         }
         if (lifeTicks > lifeTime)
         {
            kill = false;
            move = false;
            lifeTicks = (int) (-(usesModifier * 1200));
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

   public void revert ()
   {
      for (Block block : changed)
      {
         Material mat = block.getType();
         if (mat == Material.AIR)
         {
            block.setType(Material.FIRE);
         }
         if (mat == Material.OBSIDIAN)
         {
            block.setType(Material.STATIONARY_LAVA);
         }
      }
   }
}