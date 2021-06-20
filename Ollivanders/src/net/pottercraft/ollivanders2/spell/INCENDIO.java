package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Sets fire to blocks. Also sets fire to living entities and items for an amount of time depending on the player's
 * spell level.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class INCENDIO extends IncendioSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCENDIO ()
   {
      super();

      spellType = O2SpellType.INCENDIO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>() {{
         add("The Fire-Making Charm");
         add("The ability to produce fire with the flick or a wand can be dangerous to your fellow students (and worse, your books).");
         add("From lighting a warm hearth to igniting a Christmas pudding, the Fire-Making Spell is always useful around the wizarding household.");
      }};

      text = "Will set alight blocks and entities it passes by.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public INCENDIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.INCENDIO;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      strafe = false;
      radius = 1;
      blockRadius = 1;
      durationModifier = 1;
   }
}