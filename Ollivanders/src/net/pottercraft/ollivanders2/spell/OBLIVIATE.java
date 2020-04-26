package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Decreases all of target player's spell levels by the caster's level in obliviate.
 *
 * @author lownes
 * @author Azami7
 */
public final class OBLIVIATE extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public OBLIVIATE ()
   {
      super();

      spellType = O2SpellType.OBLIVIATE;

      flavorText = new ArrayList<String>() {{
         add("The Memory Charm");
         add("\"If there’s one thing I pride myself on, it’s my Memory Charms.\" -Gilderoy Lockhart");
         add("\"Miss Dursley has been punctured and her memory has been modified. She has no recollection of the incident at all. So that's that, and no harm done.\" -Cornelius Fudge");
      }};

      text = "Causes target player to lose some of their magical ability.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public OBLIVIATE (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.OBLIVIATE;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      int i = spellUses;
      List<LivingEntity> entities = getLivingEntities(1.5);
      for (Entity entity : entities)
      {
         if (entity.getUniqueId() == player.getUniqueId())
            continue;

         if (entity instanceof Player)
         {
            Player ply = (Player) entity;
            for (O2SpellType spellType : O2SpellType.values())
            {
               int know = p.getSpellNum(ply, spellType);
               int to = know - i;
               if (to < 0)
               {
                  to = 0;
               }
               p.setSpellNum(ply, spellType, to);
            }
            kill();
         }
      }
   }
}