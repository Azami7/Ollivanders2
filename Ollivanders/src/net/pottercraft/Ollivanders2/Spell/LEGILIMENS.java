package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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

      spellType = O2SpellType.LEGILIMENS;

      flavorText = new ArrayList<String>() {{
         add("\"The mind is not a book, to be opened at will and examined at leisure. Thoughts are not etched on the inside of skulls, to be perused by any invader. The mind is a complex and many-layered thing, Potter. Or at least most minds are... It is true, however, that those who have mastered Legilimency are able, under certain conditions, to delve into the minds of their victims and to interpret their findings correctly.\" -Severus Snape");
         add("The Legilimency Spell");
      }};

      text = "Legilimens, when cast at a player, will allow you to open their inventory if your level in legilimens is higher than theirs.";

   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public LEGILIMENS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.LEGILIMENS;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(2))
      {
         if (live instanceof Player)
         {
            Player target = (Player) live;
            double experience = p.players.getPlayer(target.getUniqueId()).getSpellCount(O2SpellType.LEGILIMENS);
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
