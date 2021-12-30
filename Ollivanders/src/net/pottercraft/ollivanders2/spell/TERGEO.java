package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The siphoning spell.
 *
 * @author Azami7
 * @version Ollivanders2
 */
public final class TERGEO extends BlockTransfiguration
{
   static final private int maxRadius = 20;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public TERGEO(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.TERGEO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
      {{
         add("The Siphoning Spell");
         add("The wand siphoned off most of the grease. Looking rather pleased with himself, Ron handed the slightly smoking handkerchief to Hermione.");
      }};

      text = "Tergeo will siphon water where it hits.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public TERGEO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.TERGEO;
      branch = O2MagicBranch.CHARMS;

      transfigureType = Material.AIR;
      permanent = false;

      // set materials that can be transfigured by this spell
      materialWhitelist.add(Material.WATER);

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.BUILD);

      // pass-through
      projectilePassThrough.remove(Material.WATER);

      initSpell();
   }

   @Override
   void doInitSpell()
   {
      radius = 1 + ((int) usesModifier / 20);
      if (radius > maxRadius)
      {
         radius = maxRadius;
      }
   }
}