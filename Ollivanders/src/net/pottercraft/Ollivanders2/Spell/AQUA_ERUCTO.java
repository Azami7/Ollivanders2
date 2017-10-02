package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Spell shoots a block of water at a target, extinguishing fire.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class AQUA_ERUCTO extends Charms
{
   private boolean move;
   private double lifeTime;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public AQUA_ERUCTO ()
   {
      super();

      flavorText.add("The Aqua Eructo Charm");
      flavorText.add("\"Very good. You'll need to use Aqua Eructo to put out the fires.\" -Bartemius Crouch Jr (disguised as Alastor Moody)");
      text = "Shoots a jet of water from your wand tip.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public AQUA_ERUCTO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      lifeTime = usesModifier * 16;
      move = true;
      moveEffectData = Material.WATER;
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

   @Override
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