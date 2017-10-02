package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;

/**
 * Shield spell
 *
 * @author cakenggt
 * @author Azami7
 */
public final class PROTEGO extends Charms
{
   public PROTEGO ()
   {
      super();

      flavorText.add("\"I don't remember telling you to use a Shield Charm… but there is no doubt that it was effective…\" -Severus Snape");
      flavorText.add("The Shield Charm");

      text = "Protego is a shield spell which, while you are crouching, will cause any spells cast at it to bounce off.";
   }

   public PROTEGO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      net.pottercraft.Ollivanders2.StationarySpell.PROTEGO protego =
            new net.pottercraft.Ollivanders2.StationarySpell.PROTEGO(player, location, StationarySpells.PROTEGO, 5, 12000);
      protego.flair(2);
      p.addStationary(protego);
      kill();
   }
}