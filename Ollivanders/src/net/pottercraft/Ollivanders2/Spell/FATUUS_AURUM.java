package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/29/17.
 *
 * @author Azami7
 */
public final class FATUUS_AURUM extends BlockTransfigurationSuper
{
   protected O2MagicBranch branch = O2MagicBranch.TRANSFIGURATION;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FATUUS_AURUM ()
   {
      super();

      flavorText.add("What glitters may not be gold; and even wolves may smile; and fools will be led by promises to their deaths.");
      flavorText.add("There is thy gold, worse poison to men's souls.");
      flavorText.add("Stone to Gold Spell");
      text = "Turns a stone block in to gold.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public FATUUS_AURUM (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      materialWhitelist.add(Material.STONE);
      transfigureType = Material.GOLD_BLOCK;
      permanent = false;
      spellDuration = (int)(2400 * usesModifier);
      radius = 1;
   }
}
