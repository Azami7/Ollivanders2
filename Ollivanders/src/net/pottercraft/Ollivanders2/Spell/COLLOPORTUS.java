package net.pottercraft.Ollivanders2.Spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;

import java.util.ArrayList;

/**
 * Locks blocks in to place.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class COLLOPORTUS extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COLLOPORTUS ()
   {
      super();

      spellType = O2SpellType.COLLOPORTUS;

      flavorText = new ArrayList<String>() {{
         add("The Locking Spell.");
      }};

      text = "Locks blocks in to place.  This spell does not age and can only be removed with the Unlocking Spell, Alohomora.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public COLLOPORTUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.COLLOPORTUS;
      setUsesModifier();

      projectilePassThrough.add(Material.FIRE);
      projectilePassThrough.add(Material.WATER);

      worldGuardFlags.add(DefaultFlag.BUILD);
   }

   /**
    * When a target is hit, create a colloportus stationary spell
    */
   @Override
   protected void doCheckEffect ()
   {
      if (hasHitTarget())
      {
         int duration = (int) (usesModifier * 1200);
         net.pottercraft.Ollivanders2.StationarySpell.COLLOPORTUS total
                 = new net.pottercraft.Ollivanders2.StationarySpell.COLLOPORTUS(p, player.getUniqueId(), location, O2StationarySpellType.COLLOPORTUS, 5, duration);
         total.flair(10);
         Ollivanders2API.getStationarySpells().addStationarySpell(total);
         kill();
      }
   }
}