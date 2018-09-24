package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;

import java.util.ArrayList;

/**
 * Eliminates all fall damage.
 *
 * @author lownes
 * @author Azami7
 */
public final class MOLLIARE extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MOLLIARE ()
   {
      super();

      spellType = O2SpellType.MOLLIARE;

      flavorText = new ArrayList<String>() {{
         add("The Cushioning Charm.");
         add("Harry felt himself glide back toward the ground as though weightless, landing painlessly on the rocky passage floor.");
      }};

      text = "Molliare softens the ground in a radius around the site.  All fall damage will be negated in this radius.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public MOLLIARE (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.MOLLIARE;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         int duration = (int) (usesModifier * 1200);
         net.pottercraft.Ollivanders2.StationarySpell.MOLLIARE molliare = new net.pottercraft.Ollivanders2.StationarySpell.MOLLIARE(p, player.getUniqueId(), location, O2StationarySpellType.MOLLIARE, 5, duration);
         molliare.flair(10);
         p.stationarySpells.addStationarySpell(molliare);
         kill();
      }
   }
}