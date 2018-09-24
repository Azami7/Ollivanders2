package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;

import java.util.ArrayList;

/**
 * Shield spell
 *
 * @author cakenggt
 * @author Azami7
 */
public final class PROTEGO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PROTEGO ()
   {
      super();

      spellType = O2SpellType.PROTEGO;

      flavorText = new ArrayList<String>() {{
         add("\"I don't remember telling you to use a Shield Charm... but there is no doubt that it was effective...\" -Severus Snape");
         add("The Shield Charm");
      }};

      text = "Protego is a shield spell which, while you are crouching, will cause any spells cast at it to bounce off.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PROTEGO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PROTEGO;
      setUsesModifier();
   }

   public void checkEffect ()
   {
      net.pottercraft.Ollivanders2.StationarySpell.PROTEGO protego =
            new net.pottercraft.Ollivanders2.StationarySpell.PROTEGO(p, player.getUniqueId(), location, O2StationarySpellType.PROTEGO, 5, 12000);
      protego.flair(2);
      p.stationarySpells.addStationarySpell(protego);
      kill();
   }
}