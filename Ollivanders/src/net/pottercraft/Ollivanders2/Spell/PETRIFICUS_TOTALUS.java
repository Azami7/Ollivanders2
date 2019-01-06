package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Effect.IMMOBILIZE;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Full Body-Bind Curse - Used to temporarily bind the victim's body in a position much like that of a soldier at attention.
 * http://harrypotter.wikia.com/wiki/Full_Body-Bind_Curse
 *
 * @author Azami7
 * @since 2.2.9
 */
public class PETRIFICUS_TOTALUS extends Charms
{
   public PETRIFICUS_TOTALUS ()
   {
      super();

      spellType = O2SpellType.PETRIFICUS_TOTALUS;

      flavorText = new ArrayList<String>()
      {{
         add("The Full Body-Bind Curse");
         add("\"Neville's arms snapped to his sides. His legs sprang together. His whole body rigid, he swayed where he stood and then fell flat on his face, stiff as a board. Neville's jaws were jammed together so he couldn't speak. Only his eyes were moving, looking at them in horror.\"");
         add("\"Harry's body became instantly rigid and immobile, and he felt himself fall back against the tower wall, propped like an unsteady statue.\"");
      }};

      text = "Temporarily paralyzes a person.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PETRIFICUS_TOTALUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PETRIFICUS_TOTALUS;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(1.5))
      {
         if (live.getUniqueId() == player.getUniqueId())
         {
            continue;
         }

         if (live instanceof Player)
         {
            IMMOBILIZE immobilize = new IMMOBILIZE(p, (int) (usesModifier * 1200.0), live.getUniqueId());

            Ollivanders2API.getPlayers().playerEffects.addEffect(immobilize);

            kill();
            return;
         }
      }
   }
}