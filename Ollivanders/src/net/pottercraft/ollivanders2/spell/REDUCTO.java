package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Creates an explosion of magnitude depending on the spell level which destroys blocks and sets fires.
 *
 * @author lownes
 * @author Azami7
 */
public final class REDUCTO extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public REDUCTO()
   {
      super();

      spellType = O2SpellType.REDUCTO;
      branch = O2MagicBranch.DARK_ARTS;

      flavorText = new ArrayList<String>()
      {{
         add("The Reductor Curse");
         add("With this powerful curse, skilled wizards can easily reduce obstacles to pieces. For obvious reasons great care must be exercised when learning and practising this spell, lest you find yourself sweeping up in detention for it is all too easy to bring your classroom ceiling crashing down, or to reduce your teacher's desk to a fine mist.");
      }};

      text = "Reducto creates an explosion which will damage the terrain.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public REDUCTO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.REDUCTO;
      branch = O2MagicBranch.DARK_ARTS;

      initSpell();

      // world guard flags
      worldGuardFlags.add(DefaultFlag.OTHER_EXPLOSION);
   }

   @Override
   protected void doCheckEffect ()
   {
      if (!hasHitTarget())
      {
         return;
      }

      Location backLoc = location.clone().subtract(vector);
      backLoc.getWorld().createExplosion(backLoc, (float) (usesModifier * 0.4));
   }
}