package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by Phevek on 10/17/18.
 * <p>
 * Pyrosvestiras is a fire extinquishing spell if you decide that you want to put out that thing you lit on fire.
 *
 * @author Phevek
 */
public class PYROSVESTIRAS extends BlockTransfiguration
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public PYROSVESTIRAS(Ollivanders2 plugin)
   {
      super(plugin);

      branch = O2MagicBranch.CHARMS;
      spellType = O2SpellType.PYROSVESTIRAS;

      flavorText = new ArrayList<>() {{
         add("A charm that extinguishes fires. Most commonly employed by Dragonologists.");
         add("The Extinguishing Charm");
      }};

      text = "A spell that turns fire into air.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PYROSVESTIRAS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PYROSVESTIRAS;
      branch = O2MagicBranch.CHARMS;

      permanent = true;
      transfigurationMap.put(Material.FIRE, Material.AIR);

      // whitelist only fire blocks
      materialWhitelist.add(Material.FIRE);

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.BUILD);

      initSpell();
   }

   @Override
   void doInitSpell()
   {
      if (usesModifier > 50)
         radius = 10;
      else if (usesModifier < 10)
         radius = 1;
      else
         radius = (int) (usesModifier / 10);
   }
}
