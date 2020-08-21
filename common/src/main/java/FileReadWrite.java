import java.io.*;

public class FileReadWrite {
    // File
    // InputStream -> потоки данных
    // OutputStream -> потоки данных
    // Reader, Writer
    // буферизация

    public static void copyFromBuffered(File src, File dst) throws IOException {
        System.out.println("copy " + src.length() + " bytes");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(src))));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(dst))));
        int x;
        while ((x = reader.read()) != -1) {
            writer.write(x);
        }
        writer.close();
        reader.close();
    }

    public static void copyFrom(File src, File dst) throws IOException {
        System.out.println("copy " + src.length() + " bytes");
        InputStream is = new FileInputStream(src);
        OutputStream os = new FileOutputStream(dst);
        byte [] buffer = new byte[5]; // 8Kb
        int count = 0;
        while ((count = is.read(buffer)) != -1) {
            os.write(buffer, 0, count);
            System.out.println("read " + count + " bytes");
        }
        os.close();
        is.close();
    }


    public static void main(String[] args) throws IOException {
        File file = new File("common/src/main/resources/input.txt");
        File to = new File("common/src/main/resources/input1.txt");
        if (!to.exists()) to.createNewFile();
        copyFrom(file, to);
    }
}
