package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Spell shoots a block of water at a target, extinguishing fire.
 *
 * @author Azami7
 * @version Ollivanders2
 */
public final class AQUA_ERUCTO extends BlockTransfiguration
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public AQUA_ERUCTO(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.AQUA_ERUCTO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
      {{
         add("The Aqua Eructo Charm");
         add("\"Very good. You'll need to use Aqua Eructo to put out the fires.\" -Bartemius Crouch Jr (disguised as Alastor Moody)");
      }};

      text = "Shoots a jet of water from your wand tip.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public AQUA_ERUCTO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.AQUA_ERUCTO;
      branch = O2MagicBranch.CHARMS;

      permanent = true;
      radius = 1;
      successMessage = "A fire is doused by the water.";

      initSpell();

      transfigurationMap.put(Material.LAVA, Material.OBSIDIAN);
      transfigurationMap.put(Material.FIRE, Material.AIR);

      // materials that can be transfigured by this spell
      materialWhitelist.add(Material.LAVA);
      materialWhitelist.add(Material.FIRE);

      moveEffectData = Material.BLUE_ICE;

      // pass-through materials
      projectilePassThrough.remove(Material.WATER);
      projectilePassThrough.remove(Material.FIRE);
   }
}