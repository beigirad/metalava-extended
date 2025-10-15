import ir.beigirad.filterReport
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class MetalavaExtendedTest {

    @Test
    fun `filter a method`() {
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
}