package net.pottercraft.Ollivanders2.Spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;

import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * The siphoning spell.
 *
 * @version Ollivanders2
 * @author Azami7
 */
public final class TERGEO extends BlockTransfigurationSuper
{
   static private int maxRadius = 20;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public TERGEO ()
   {
      super();

      spellType = O2SpellType.TERGEO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>() {{
         add("The Siphoning Spell");
         add("The wand siphoned off most of the grease. Looking rather pleased with himself, Ron handed the slightly smoking handkerchief to Hermione.");
      }};

      text = "Tergeo will siphon water where it hits.";
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
      branch = O2MagicBranch.CHARMS;

      transfigureType = Material.AIR;
      permanent = false;

      initSpell();

      radius = 1 + ((int) usesModifier / 20);
      if (radius > maxRadius)
      {
         radius = maxRadius;
      }

      // set materials that can be transfigured by this spell
      materialWhitelist.add(Material.WATER);

      // world guard flags
      worldGuardFlags.add(DefaultFlag.BUILD);

      // pass-through
      projectilePassThrough.remove(Material.WATER);
   }
}