package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Mines a line of blocks of length depending on the player's level in this spell.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class DEFODIO extends O2Spell
{
   private int depth;

   private Block curBlock = null;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DEFODIO()
   {
      super();

      spellType = O2SpellType.DEFODIO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("The Gouging Spell enables a witch or wizard to carve through earth and stone with ease. From budding Herbologists digging for Snargaluff seedlings to treasure-hunting curse breakers uncovering ancient wizard tombs, the Gouging Spell makes all manner of heavy labour a matter of pointing a wand.");
         add("The Gouging Charm");
      }};

      text = "Mines a line of blocks.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public DEFODIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.DEFODIO;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      depth = (int) usesModifier;

      // world-guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.BUILD);

      // material black list
      materialBlackList.add(Material.WATER);
      materialBlackList.add(Material.LAVA);
      materialBlackList.add(Material.FIRE);

      for (Material material : Ollivanders2Common.unbreakableMaterials)
      {
         if (!materialBlackList.contains(material))
            materialBlackList.add(material);
      }
   }

   /**
    * Break a row of blocks
    */
   @Override
   protected void doCheckEffect()
   {
      if (!hasHitTarget())
         return;

      curBlock = getTargetBlock();
      if (curBlock == null)
      {
         common.printDebugMessage("DEFODIO.doCheckEffect: target block is null", null, null, true);
         kill();
         return;
      }

      // stop the spell if we hit a blacklisted block type or when the max depth is reached
      if (materialBlackList.contains(curBlock.getType()) || depth <= 0)
      {
         kill();
         return;
      }

      Location curLoc = curBlock.getLocation();

      // stop the spell if we hit a colloportus stationary spell
      if (Ollivanders2API.getStationarySpells(p).checkLocationForSpell(curLoc, O2StationarySpellType.COLLOPORTUS))
      {
         kill();
         return;
      }

      // stop the spell if something prevented the current block breaking naturally
      if (curBlock.breakNaturally())
      {
         depth--;

         Location nextLoc = curLoc.add(vector);
         curBlock = nextLoc.getBlock();
      }
      else
      {
         kill();
      }
   }
}