package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * Shoots the hit entity away from you.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class DEPULSO extends Knockback
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DEPULSO ()
   {
      super();

      spellType = O2SpellType.DEPULSO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>() {{
         add("They were supposed to be practising the opposite of the Summoning Charm today — the Banishing Charm. Owing to the potential for nasty accidents when objects kept flying across the room. Professor Flitwick had given each student a stack of cushions on which to practise, the theory being that these wouldn’t hurt anyone if they went off target.");
         add("The Banishing Charm");
      }};

      text = "Depulso will repel any entity you hit with it.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public DEPULSO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.DEPULSO;
      branch = O2MagicBranch.CHARMS;

      strengthReducer = 20;

      // world guard flags
      worldGuardFlags.add(Flags.PVP);
      worldGuardFlags.add(Flags.DAMAGE_ANIMALS);

      initSpell();
   }
}