package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Spell shoots a block of water at a target, extinguishing fire.
 *
 * @version Ollivanders2
 * @author Azami7
 */
public final class AQUA_ERUCTO extends BlockTransfigurationSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public AQUA_ERUCTO ()
   {
      super();

      spellType = O2SpellType.AQUA_ERUCTO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>() {{
         add("The Aqua Eructo Charm");
         add("\"Very good. You'll need to use Aqua Eructo to put out the fires.\" -Bartemius Crouch Jr (disguised as Alastor Moody)");
      }};

      text = "Shoots a jet of water from your wand tip.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public AQUA_ERUCTO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.AQUA_ERUCTO;
      branch = O2MagicBranch.CHARMS;
      setUsesModifier();

      spellDuration = (int)(1200 * usesModifier);
      permanent = false;
      radius = 1;

      transfigurationMap.put(Material.LAVA, Material.OBSIDIAN);
      transfigurationMap.put(Material.STATIONARY_LAVA, Material.OBSIDIAN);
      transfigurationMap.put(Material.FIRE, Material.AIR);

      materialWhitelist.add(Material.LAVA);
      materialWhitelist.add(Material.STATIONARY_LAVA);
      materialWhitelist.add(Material.FIRE);

      moveEffectData = Material.WATER;
   }
}