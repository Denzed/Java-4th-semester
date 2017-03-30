import hashers.SingleThreadedMD5Hasher;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A test set for {@link SingleThreadedMD5Hasher}
 */
public class SingleThreadedMD5HasherTest extends HasherTest {
    @Override
    byte[] getHashFromPath(Path path) throws IOException {
        return SingleThreadedMD5Hasher.getHashFromPath(path);
    }
}
