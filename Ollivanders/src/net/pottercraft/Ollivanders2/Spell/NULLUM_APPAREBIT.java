package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2API;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;

/**
 * Creates an Anti-apparition spell object.
 *
 * @author lownes
 * @author Azami7
 */
public final class NULLUM_APPAREBIT extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public NULLUM_APPAREBIT ()
   {
      super();

      spellType = O2SpellType.NULLUM_APPAREBIT;
      text = "Nullum apparebit creates a stationary spell which will not allow apparition into it.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public NULLUM_APPAREBIT (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.NULLUM_APPAREBIT;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      Material targetBlockType = getBlock().getType();
      if (targetBlockType != Material.AIR && targetBlockType != Material.FIRE && targetBlockType != Material.WATER)
      {
         int duration = (int) (usesModifier * 1200);
         net.pottercraft.Ollivanders2.StationarySpell.NULLUM_APPAREBIT nullum = new net.pottercraft.Ollivanders2.StationarySpell.NULLUM_APPAREBIT(p, player.getUniqueId(), location, O2StationarySpellType.NULLUM_APPAREBIT, 5, duration);
         nullum.flair(10);
         Ollivanders2API.getStationarySpells().addStationarySpell(nullum);
         kill();
      }
   }
}