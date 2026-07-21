package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.STANDARD_BOOK_OF_SPELLS_GRADE_1;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link STANDARD_BOOK_OF_SPELLS_GRADE_1}.
 */
public class StandardBookOfSpellsGrade1Test extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new STANDARD_BOOK_OF_SPELLS_GRADE_1(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}