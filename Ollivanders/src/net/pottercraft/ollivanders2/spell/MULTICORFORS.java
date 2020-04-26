package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.Color;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * If an entity has leather armor on, then this changes it's color.
 *
 * @author lownes
 * @author Azami7
 */
public final class MULTICORFORS extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MULTICORFORS ()
   {
      super();

      branch = O2MagicBranch.TRANSFIGURATION;
      spellType = O2SpellType.MULTICORFORS;

      text = "Multicorfors will change the color of leather armor of the target.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public MULTICORFORS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      // this is a transfiguration spell in HP but does not use the Transfiguration superclass.
      branch = O2MagicBranch.TRANSFIGURATION;
      spellType = O2SpellType.MULTICORFORS;
      setUsesModifier();
   }

   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(1.5))
      {
         if (live.getUniqueId() == player.getUniqueId())
            continue;

         EntityEquipment equipment = live.getEquipment();
         for (ItemStack armor : equipment.getArmorContents())
         {
            if (armor.getItemMeta() instanceof LeatherArmorMeta)
            {
               LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
               Color curColor = meta.getColor();
               int modifier = (int) (Math.random() * usesModifier * 40);
               modifier = modifier - modifier / 2;
               int blue = (curColor.getBlue() + modifier) % 256;
               int green = (curColor.getGreen() + modifier) % 256;
               int red = (curColor.getRed() + modifier) % 256;
               Color newColor = Color.fromRGB(red, green, blue);
               meta.setColor(newColor);
               armor.setItemMeta(meta);
               kill();
            }
         }
      }
   }
}