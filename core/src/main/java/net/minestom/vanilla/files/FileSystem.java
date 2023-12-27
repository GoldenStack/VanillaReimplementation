package net.minestom.vanilla.files;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface FileSystem<F> extends FileSystemMappers {

    /**
     * @return a map of file names (no directory prefix) to F objects
     */
    Map<String, F> readAll();

    /**
     * Queries all the folders in the given directory and returns a set of folder names.
     *
     * @return a set of folder names (no directory prefix)
     */
    Set<String> folders();

    /**
     * Queries all the files in the given directory and returns a set of file names.
     *
     * @return a set of file names (no directory prefix)
     */
    default Set<String> files() {
        return readAll().keySet();
    }

    /**
     * Navigates to the given subdirectory and returns a new FileSource for that directory.
     *
     * @param path the path to the subdirectory
     * @return a new FileSource for the subdirectory
     */
    FileSystem<F> folder(String path);

    default FileSystem<F> folder(@NotNull String... paths) {
        if (paths.length == 0)
            return this;

        Iterator<String> iter = Arrays.stream(paths).iterator();
        FileSystem<F> fs = folder(iter.next());

        while (iter.hasNext()) {
            fs = fs.folder(iter.next());
            if (fs.folders().isEmpty())
                return fs;
        }
        return fs;
    }

    default <T> FileSystem<T> folder(String path, Function<F, T> mapper) {
        FileSystem<F> fs = folder(path);
        if (fs == null)
            return null;
        return fs.map(mapper);
    }

    default FileSystem<F> folder(@NotNull String path, @NotNull String separator) {
        return folder(path.split(separator));
    }

    default F file(String path) {
        return readAll().get(path);
    }

    default <T> FileSystem<T> map(Function<F, T> mapper) {
        return map((str, file) -> mapper.apply(file));
    }

    default <T> FileSystem<T> map(BiFunction<String, F, T> mapper) {
        return new MappedFileSystem<>(this, mapper);
    }

    default FileSystem<F> cache() {
        return new CacheFileSystem<>(this);
    }

    default FileSystem<F> lazy() {
        return new LazyFileSystem<>(this);
    }

    default FileSystem<F> inMemory() {
        return DynamicFileSystem.from(this);
    }

    static <T> FileSystem<T> empty() {
        //noinspection unchecked
        return (FileSystem<T>) CacheFileSystem.EMPTY;
    }

    static FileSystem<ByteArray> fileSystem(Path path) {
        return new PathFileSystem(path);
    }

    static FileSystem<ByteArray> fromZipFile(File file) {
        return fromZipFile(file, p -> true);
    }

    static FileSystem<ByteArray> fromZipFile(File file, Predicate<String> pathFilter) {
        return FileSystemUtil.unzipIntoFileSystem(file, pathFilter);
    }

    default boolean hasFile(String path) {
        return readAll().containsKey(path);
    }
    default boolean hasFolder(String path) {
        return folders().contains(path);
    }


    static String toString(FileSystem<?> fs) {
        Stream.Builder<String> builder = Stream.builder();
        toString(fs, builder, 0);
        return builder.build().collect(Collectors.joining());
    }

    static void toString(FileSystem<?> fs, Stream.Builder<String> builder, int depth) {
        String prefix = "  ".repeat(depth);

        String folderChar = "\uD83D\uDDC0";
        String fileChar = "\uD83D\uDDCE";

        for (String folder : fs.folders()) {
            builder.add(prefix);
            builder.add(folderChar);
            builder.add(" ");
            builder.add(folder);
            builder.add("\n");
            toString(fs.folder(folder), builder, depth + 1);
        }

        for (String file : fs.files()) {
            builder.add(prefix);
            builder.add(fileChar);
            builder.add(" ");
            builder.add(file);
            builder.add("\n");
        }
    }
}