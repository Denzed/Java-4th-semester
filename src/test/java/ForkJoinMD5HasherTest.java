import hashers.ForkJoinMD5Hasher;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A test set for {@link ForkJoinMD5Hasher}
 */
public class ForkJoinMD5HasherTest extends HasherTest {
    @Override
    byte[] getHashFromPath(Path path) throws IOException {
        return ForkJoinMD5Hasher.getHashFromPath(path);
    }
}
