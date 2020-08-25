import java.io.IOException;
import java.nio.file.*;

public class NIOSummary {
    public static void main(String[] args) throws IOException {
        // String path
        // Path path
        Path path = Paths.get("nio/dir/1.txt");
//        path.iterator()
//                .forEachRemaining(p -> System.out.println(p.getFileName()));
//        path.toAbsolutePath()
//                .iterator()
//                .forEachRemaining(p -> System.out.println(p.getFileName()));
        for(String line : Files.readAllLines(path)) {
            System.out.println(line);
        }
        Files.copy(path, Paths.get("nio/dir/1_copy.txt"), StandardCopyOption.REPLACE_EXISTING);
        Files.write(path, "Hello World!".getBytes(), StandardOpenOption.APPEND);
    }
}
