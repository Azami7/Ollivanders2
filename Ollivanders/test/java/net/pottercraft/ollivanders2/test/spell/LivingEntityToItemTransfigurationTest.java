package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.LivingEntityToItemTransfiguration;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract public class LivingEntityToItemTransfigurationTest extends EntityTransfigurationTest {
    @Override
    @Test
    void transfigureTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);

        testWorld.spawnEntity(targetLocation, getValidEntityType());
        LivingEntityToItemTransfiguration livingEntityToItemTransfiguration = (LivingEntityToItemTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(livingEntityToItemTransfiguration.isTransfigured(), "failed to hit target");
        Item item = EntityCommon.getItemAtLocation(targetLocation);
        assertNotNull(item, "no item found at target location");
        assertTrue(livingEntityToItemTransfiguration.isTransfigured(item), "transfigured item not found");
        assertEquals(livingEntityToItemTransfiguration.getTargetMaterial(), item.getItemStack().getType(), "item not expected type");
    }
}
