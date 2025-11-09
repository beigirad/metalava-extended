package ir.beigirad.metalavaextended

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class MetalavaExtendedTest {

    @Test
    fun `filter a single line method`() {
        val input = """
        package ir.tapsell.core {
            public final class LogTag {
                ctor public LogTag();
                method public static String T_TAPSELL();
                field public static final String tpslc;
                field public static final String tpsld;
                field public static final String tpsle;
            }
        }""".trimIndent()
        val output = """
        package ir.tapsell.core {
            public final class LogTag {
                ctor public LogTag();
                method public static String T_TAPSELL();
            }
        }""".trimIndent()

        assertEquals(input.filterReport("tpsl"), output)
    }

    @Test
    fun `filter a class`() {
        val input = """
        package ir.tapsell.core {
            public final class LogTag {
                ctor public LogTag();
                method public static String T_TAPSELL();
                field public static final String tpslc;
                field public static final String tpsld;
                field public static final String tpsle;
            }
            
            public final class LogTag1.tpsl {
                field public static final String sda;
                field public static final String tpsle;
            }
        }""".trimIndent()
        val output = """
        package ir.tapsell.core {
            public final class LogTag {
                ctor public LogTag();
                method public static String T_TAPSELL();
            }
        }""".trimIndent()

        assertEquals(input.filterReport("tpsl"), output)
    }

    @Test
    fun `filter with multiple ignore phrases`() {
        val input = """
        package ir.tapsell.core {
            public final class LogTag {
                ctor public LogTag();
                method public static String T_TAPSELL();
                field public static final String tpslc;
                field public static final String tpsld;
                field public static final String tpsle;
            }
        }""".trimIndent()
        val output = """
        package ir.tapsell.core {
            public final class LogTag {
                ctor public LogTag();
                method public static String T_TAPSELL();
                field public static final String tpsle;
            }
        }""".trimIndent()

        assertEquals(input.filterReport("tpslc", "tpsld"), output)
    }

    @Test
    fun `filter with multiple empty phrases`() {
        val input = """
        package ir.tapsell.core {
            public final class LogTag {
                ctor public LogTag();
                method public static String T_TAPSELL();
                field public static final String tpslc;
                field public static final String tpsld;
                field public static final String tpsle;
            }
        }""".trimIndent()

        assertEquals(input.filterReport(), input)
    }

    @Test
    fun `filter an empty block`() {
        val input = """
        package ir.sample.internal {
            public final class sensitive {
                ctor public hello();
                method public static String hi();
            }
        }
        """.trimIndent()

        assertEquals(input.filterReport("sensitive"), "")
    }
}