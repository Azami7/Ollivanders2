package ollivanders;

import java.io.File;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HelloWorldTest {

    World world;

    @BeforeEach
    void setUp() {
        ServerMock mock = MockBukkit.mock();

        File pluginYml = new File("Ollivanders/test/resources/plugin-test.yml");
        Ollivanders2 plugin = MockBukkit.loadWith(Ollivanders2.class, pluginYml);

        this.world = mock.addSimpleWorld("world");
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void test() {
        world.createExplosion(new Location(world, 0, 0, 0), 0.2f);
    }
}