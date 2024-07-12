package arquitectureTest;

import com.common.lib.utils.test.ArchitectureTest;
import org.junit.Test;

public class HexagonalTest {

    private String packageName = "com.juliaosystem";
    @Test
    public void testArchitectureAndNaming() {
        ArchitectureTest.checkHexagonalArchitecture(packageName);
    }

    @Test
   public void methodsTest()
    {
        ArchitectureTest.checkMethodSizeAndParameters("juliaosystem.infrastructure.adapters.secondary");
    }
}
