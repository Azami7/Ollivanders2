package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.BatWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Snufflifors Spell (Snufflifors) is a spell used to transfigure objects into mice, but Flying books are
 * especially vulnerable to this spell. Bats are used as the MC approximation of mice.
 *
 * @author Azami7
 * @link https://github.com/Azami7/Ollivanders2/issues/94
 * @since 2.2.6
 */
public class SNUFFLIFORS extends FriendlyMobDisguise
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public SNUFFLIFORS()
   {
      super();

      spellType = O2SpellType.SNUFFLIFORS;
      branch = O2MagicBranch.TRANSFIGURATION;

      flavorText = new ArrayList<String>()
      {{
         add("\"You're going to have a lot of fun with the Snufflifors Spell, Hermione! It's particularly useful at turning books into bats! How cool is that?\" -Fred Weasley");
      }};

      text = "The Snufflifors Spell is a spell used to transfigure objects into bats.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public SNUFFLIFORS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.SNUFFLIFORS;
      branch = O2MagicBranch.TRANSFIGURATION;

      initSpell();

      targetType = EntityType.BAT;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      BatWatcher watcher = (BatWatcher)disguise.getWatcher();
      watcher.setFlyingWithElytra(true);
   }
}
