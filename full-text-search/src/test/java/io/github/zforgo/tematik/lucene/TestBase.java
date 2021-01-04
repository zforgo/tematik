package io.github.zforgo.tematik.lucene;

import io.github.zforgo.tematik.lucene.support.IndexAware;
import io.github.zforgo.tematik.lucene.support.Utils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.List;

public abstract class TestBase<T extends IndexAware> {
    protected IndexWriter writer;
    protected IndexReader reader;
    protected Analyzer analyzer = new StandardAnalyzer();

    public abstract List<T> items();

    @BeforeEach
    void init() throws IOException {
        writer = new IndexWriter(Utils.newDirectory(), new IndexWriterConfig(analyzer));
        ObjectIndexer.index(writer, items());
        reader = DirectoryReader.open(writer);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (null != writer && writer.isOpen()) {
            writer.close();
        }
        if (null != reader) {
            reader.close();
        }
    }

}
