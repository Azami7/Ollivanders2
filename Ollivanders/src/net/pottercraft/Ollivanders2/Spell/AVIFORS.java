package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.ParrotWatcher;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;

import java.util.ArrayList;


/**
 * Transfigures entity into a parrot
 *
 * @since 1.0
 * @author Azami7
 */
public final class AVIFORS extends FriendlyMobDisguiseSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public AVIFORS ()
   {
      super();

      spellType = O2SpellType.AVIFORS;

      flavorText = new ArrayList<String>() {{
         add("However, mastering a Transfiguration spell such as \"Avifors\" can be both rewarding and useful.");
      }};

      text = "Turns target entity in to a bird.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public AVIFORS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.AVIFORS;

      // set up usage modifier, has to be done here to get the uses for this specific spell
      setUsesModifier();

      targetType = EntityType.PARROT;

      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      ParrotWatcher watcher = (ParrotWatcher)disguise.getWatcher();
      int rand = Math.abs(Ollivanders2Common.random.nextInt() % 20);
      if (rand > 18)
      {
         watcher.setVariant(Parrot.Variant.GRAY);
      }
      else if (rand > 14)
      {
         watcher.setVariant(Parrot.Variant.GREEN);
      }
      else if (rand > 11)
      {
         watcher.setVariant(Parrot.Variant.CYAN);
      }
      else if (rand > 6)
      {
         watcher.setVariant(Parrot.Variant.BLUE);
      }
      else
      {
         watcher.setVariant(Parrot.Variant.RED);
      }

      watcher.setFlyingWithElytra(true);
   }
}