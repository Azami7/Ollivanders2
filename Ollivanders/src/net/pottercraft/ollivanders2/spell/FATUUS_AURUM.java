package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Created by Azami7 on 6/29/17.
 *
 * @author Azami7
 */
public final class FATUUS_AURUM extends BlockTransfigurationSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FATUUS_AURUM ()
   {
      super();

      spellType = O2SpellType.FATUUS_AURUM;
      branch = O2MagicBranch.TRANSFIGURATION;

      flavorText = new ArrayList<String>() {{
         add("What glitters may not be gold; and even wolves may smile; and fools will be led by promises to their deaths.");
         add("There is thy gold, worse poison to men's souls.");
         add("Stone to Gold Spell");
      }};

      text = "Turns a stone block in to gold.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FATUUS_AURUM (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.FATUUS_AURUM;
      branch = O2MagicBranch.TRANSFIGURATION;
      setUsesModifier();

      materialWhitelist.add(Material.STONE);
      transfigureType = Material.GOLD_BLOCK;
      permanent = false;
      spellDuration = (int)(2400 * usesModifier);
      radius = 1;
   }
}
