package org.tinylog.benchmarks.file;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * State for writing strings by using {@link OutputStreamWriter}.
 */
@State(Scope.Thread)
public class WriterState extends AbstractState<Writer> {

    /**
     * The buffer sizes to benchmark.
     */
    @Param({ "0", "1024", "2048", "4096", "8192", "16384", "32768", "65536", "131072" })
    private int bufferSize;

    /** */
    public WriterState() {
    }

    /**
     * @param bufferSize The buffer size in bytes
     */
    public WriterState(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public void write(String content) throws IOException {
        instance.write(content);
    }

    @Override
    protected Writer create(Path path) throws IOException {
        Writer writer = new OutputStreamWriter(new FileOutputStream(path.toFile()), CHARSET);

        if (bufferSize > 0) {
            return new BufferedWriter(writer, bufferSize);
        }

        return writer;
    }

}
