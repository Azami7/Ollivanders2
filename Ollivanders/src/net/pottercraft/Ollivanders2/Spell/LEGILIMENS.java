package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Open the target LivingEntity's inventory
 *
 * @author lownes
 * @author Azami7
 */
public final class LEGILIMENS extends DarkArts
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LEGILIMENS ()
   {
      super();

      flavorText.add("\"The mind is not a book, to be opened at will and examined at leisure. Thoughts are not etched on the inside of skulls, to be perused by any invader. The mind is a complex and many-layered thing, Potter. Or at least most minds are... It is true, however, that those who have mastered Legilimency are able, under certain conditions, to delve into the minds of their victims and to interpret their findings correctly.\" -Severus Snape");
      flavorText.add("The Legilimency Spell");

      text = "Legilimens, when cast at a player, will allow you to open their inventory if your level in legilimens is higher than theirs.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public LEGILIMENS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(1))
      {
         if (live instanceof Player)
         {
            Player target = (Player) live;
            double experience = p.getO2Player(target).getSpellCount(Spells.LEGILIMENS);
            if (usesModifier > experience)
            {
               player.openInventory(target.getInventory());
            }
            kill();
            return;
         }
      }
   }
}
