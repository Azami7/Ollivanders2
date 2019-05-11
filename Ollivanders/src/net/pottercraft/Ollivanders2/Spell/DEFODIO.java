package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Mines a line of blocks of length depending on the player's level in this spell.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class DEFODIO extends Charms
{
   private int depth;

   private Block curBlock = null;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DEFODIO ()
   {
      super();

      spellType = O2SpellType.DEFODIO;

      flavorText = new ArrayList<String>() {{
         add("The Gouging Spell enables a witch or wizard to carve through earth and stone with ease. From budding Herbologists digging for Snargaluff seedlings to treasure-hunting curse breakers uncovering ancient wizard tombs, the Gouging Spell makes all manner of heavy labour a matter of pointing a wand.");
         add("The Gouging Charm");
      }};

      text = "Mines a line of blocks.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public DEFODIO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.DEFODIO;

      // set up usage modifier, has to be done here to get the uses for this specific spell
      setUsesModifier();

      depth = (int) usesModifier;

      // world-guard flags
      worldGuardFlags.add(DefaultFlag.BUILD);

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
   protected void doCheckEffect ()
   {
      if (!hasHitTarget())
         return;

      curBlock = getTargetBlock();

      // stop the spell if we hit a blacklisted block type or when the max depth is reached
      if (materialBlackList.contains(curBlock.getType()) || depth <= 0)
      {
         kill();
         return;
      }

      Location curLoc = curBlock.getLocation();

      // stop the spell if we hit a colloportus stationary spell
      if (Ollivanders2API.getStationarySpells().checkLocationForSpell(curLoc, O2StationarySpellType.COLLOPORTUS))
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