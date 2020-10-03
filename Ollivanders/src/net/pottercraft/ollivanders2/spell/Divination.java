package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.divination.O2Divination;
import net.pottercraft.ollivanders2.divination.O2DivinationType;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Super class for all divination spells.
 *
 * @author Azami7
 * @since 2.2.9
 */
public abstract class Divination extends O2Spell
{
    O2DivinationType divinationType = null;
    Player target = null;

    O2ItemType itemHeld = null;
    String itemHeldString = "";
    boolean consumeHeld = false;

    Material facingBlock = null;
    String facingBlockString = "";

    public static final List<O2SpellType> divinationSpells = new ArrayList<O2SpellType>()
    {{
        add(O2SpellType.ASTROLOGIA);
        add(O2SpellType.BAO_ZHONG_CHA);
        add(O2SpellType.CARTOMANCIE);
        add(O2SpellType.CHARTIA);
        add(O2SpellType.INTUEOR);
        add(O2SpellType.MANTEIA_KENTAVROS);
        add(O2SpellType.OVOGNOSIS);
    }};

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     */
    public Divination()
    {
        super();

        branch = O2MagicBranch.DIVINATION;
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public Divination(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.DIVINATION;
    }

    /**
     * Override setUsesModifier because this spell does not require holding a wand.
     */
    @Override
    protected void setUsesModifier()
    {
        usesModifier = p.getSpellCount(player, spellType);

        if (Ollivanders2API.getPlayers(p).playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL))
        {
            usesModifier *= 2;
        }
    }

    /**
     * Set the target for this divination. This must be done when the spell is created.
     *
     * @param t the target player
     */
    public void setTarget(@NotNull Player t)
    {
        if (player != null)
        {
            target = t;
        }
    }

    @Override
    public void checkEffect()
    {
        if (!isSpellAllowed())
        {
            kill();
            return;
        }

        // if this divination type requires the player be facing an block, like a crystal ball, check for the block
        if (facingBlock != null)
        {
            Block facing = Ollivanders2API.common.playerFacingBlockType(player, facingBlock);
            if (facing == null)
            {
                player.sendMessage(Ollivanders2.chatColor + "You must be facing " + facingBlockString + " to do that.");
                kill();
                return;
            }
        }

        // if this divination type requires the player hold an item, like an egg, check for the item
        if (itemHeld != null)
        {
            ItemStack held = player.getInventory().getItemInMainHand();

            // if the item has a display name, it is a custom item
            ItemMeta meta = held.getItemMeta();
            if (meta == null || !meta.getDisplayName().toLowerCase().equals(itemHeld.getName().toLowerCase()))
            {
                wrongItemHeld();
                return;
            }
        }

        // target must be logged in to make prophecy about them
        if (target == null || !target.isOnline())
        {
            player.sendMessage(Ollivanders2.chatColor + "Unable to find that player online.");
            kill();
            return;
        }

        int experience = p.getO2Player(player).getSpellCount(spellType);

        // create a prophecy of the correct type
        O2Divination divination;
        Class<?> divinationClass = divinationType.getClassName();

        try
        {
            divination = (O2Divination) divinationClass.getConstructor(Ollivanders2.class, Player.class, Player.class, Integer.class).newInstance(p, player, target, experience);
        }
        catch (Exception e)
        {
            common.printDebugMessage("Exception creating divination", e, null, true);
            kill();
            return;
        }

        divination.divine();

        // if requires consume item held, consume it
        if (itemHeld != null && consumeHeld)
        {
            int amount = player.getInventory().getItemInMainHand().getAmount();
            player.getInventory().getItemInMainHand().setAmount(amount - 1);
        }

        kill();
    }

    /**
     * When the player is holding the wrong item for this divinination
     */
    private void wrongItemHeld()
    {
        player.sendMessage(Ollivanders2.chatColor + "You must hold " + itemHeldString + " to do that.");
        kill();
    }
}
