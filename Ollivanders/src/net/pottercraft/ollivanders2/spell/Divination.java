package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
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
 * Base class for all divination spells, which produce a prophecy about a target player rather than a projectile effect.
 * <p>
 * Divination spells do not travel or strike a target like other spells, so {@code noProjectile} is set and the spell
 * resolves on the first tick in {@link #doCheckEffect()}: it enforces any required {@link #facingBlock} (such as a
 * crystal ball) and {@link #itemHeld} (such as an egg), confirms the {@link #target} is online, then reflectively
 * constructs the prophecy for the configured {@link O2DivinationType} and divines it. The held item is consumed when
 * {@link #consumeHeld} is set.
 * </p>
 * <p>
 * Concrete subclasses set their {@link #spellType} and {@link #divinationType} in both constructors and configure any
 * facing-block or held-item requirements in the casting constructor. The magic branch is set to
 * {@link O2MagicBranch#DIVINATION}, and the casting constructor sets {@code noProjectile}, here for all subclasses.
 * </p>
 *
 * @author Azami7
 */
public abstract class Divination extends O2Spell {
    /**
     * The type of divination
     */
    O2DivinationType divinationType = null;

    /**
     * The target of this divination's prophecy
     */
    Player target = null;

    /**
     * (optional) Item type that needs to be held to perform this divination (such as an egg for Ovognosis)
     */
    O2ItemType itemHeld = null;

    /**
     * (optional) The description string for the item held, used in spell book texts
     * <p>
     * Needs to be set if itemHeld is set or the book text cannot include this information.
     */
    String itemHeldString = "";

    /**
     * Is the item held consumed as a part of the divination
     */
    boolean consumeHeld = false;

    /**
     * (optional) The block a player must be facing to do this divination (such as a crystal ball for Intueor)
     */
    Material facingBlock = null;

    /**
     * (optional) The description string for the block to be faced, used in spell book texts
     * <p>
     * Needs to be set if facingBlock is set or the book text cannot include this information.
     */
    String facingBlockString = "";

    /**
     * All divination spell types
     */
    public static final List<O2SpellType> divinationSpells = new ArrayList<>() {{
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
     *
     * @param plugin the Ollivanders2 plugin
     */
    public Divination(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.DIVINATION;
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public Divination(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.DIVINATION;

        noProjectile = true;
    }

    /**
     * Calculate the uses modifier for this divination spell.
     * <p>
     * Overrides the base behavior because divination does not require holding a wand, so the modifier is based purely
     * on the caster's experience with this spell, doubled when the caster is under the
     * {@link O2EffectType#HIGHER_SKILL} effect.
     * </p>
     */
    @Override
    protected void setUsesModifier() {
        usesModifier = p.getSpellCount(caster, spellType);

        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(caster.getUniqueId(), O2EffectType.HIGHER_SKILL))
            usesModifier *= 2;
    }

    /**
     * Set the target for this divination. This must be done when the spell is created.
     *
     * @param t the target player
     */
    public void setTarget(@NotNull Player t) {
        if (caster != null)
            target = t;
    }

    /**
     * Resolve the divination.
     * <p>
     * Because divination spells set {@code noProjectile}, the base {@link #checkEffect()} validates the spell is
     * allowed and then calls this on the first tick. This enforces any {@link #facingBlock} and {@link #itemHeld}
     * requirements, confirms the {@link #target} is online, then constructs and runs the prophecy for this spell's
     * {@link #divinationType}. The held item is consumed when {@link #consumeHeld} is set. The spell is killed once
     * resolved or whenever a requirement is not met.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        kill();

        // if this divination type requires the player be facing a block, like a crystal ball, check for the block
        if (facingBlock != null) {
            Block facing = Ollivanders2Common.playerFacingBlockType(caster, facingBlock);
            if (facing == null) {
                caster.sendMessage(Ollivanders2.chatColor + "You must be facing " + facingBlockString + " to do that.");
                return;
            }
        }

        // if this divination type requires the player hold an item, like an egg, check for the item
        if (itemHeld != null) {
            ItemStack held = caster.getInventory().getItemInMainHand();

            // if the item has a display name, it is a custom item
            ItemMeta meta = held.getItemMeta();
            if (meta == null || !meta.getDisplayName().equalsIgnoreCase(itemHeld.getName().toLowerCase())) {
                wrongItemHeld();
                return;
            }
        }

        // target must be logged in to make prophecy about them
        if (target == null || !target.isOnline()) {
            caster.sendMessage(Ollivanders2.chatColor + "Unable to find that player online.");
            return;
        }

        int experience = p.getO2Player(caster).getSpellCount(spellType);

        // create a prophecy of the correct type
        O2Divination divination;
        Class<?> divinationClass = divinationType.getClassName();

        try {
            divination = (O2Divination) divinationClass.getConstructor(Ollivanders2.class, Player.class, Player.class, int.class).newInstance(p, caster, target, experience);
        }
        catch (Exception e) {
            common.printDebugMessage("Exception creating divination", e, null, true);
            return;
        }

        divination.divine();

        // if the spell requires consuming the item held, consume it
        if (itemHeld != null && consumeHeld) {
            int amount = caster.getInventory().getItemInMainHand().getAmount();
            caster.getInventory().getItemInMainHand().setAmount(amount - 1);
        }
    }

    /**
     * Notify the caster that they are holding the wrong item for this divination, and kill the spell.
     */
    private void wrongItemHeld() {
        caster.sendMessage(Ollivanders2.chatColor + "You must hold " + itemHeldString + " to do that.");
        kill();
    }

    /**
     * Get the block type the caster must be facing to perform this divination, if any.
     *
     * @return the required facing block type, or null if this divination has no facing requirement
     */
    public Material getFacingBlock() {
        return facingBlock;
    }

    /**
     * Get the item type the caster must be holding to perform this divination, if any.
     *
     * @return the required held item type, or null if this divination has no held-item requirement
     */
    public O2ItemType getItemHeld() {
        return itemHeld;
    }

    /**
     * Whether the held item is consumed when this divination is performed.
     *
     * @return true if the held item is consumed, false otherwise
     */
    public boolean isConsumeHeld() {
        return consumeHeld;
    }
}
