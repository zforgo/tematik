package io.github.zforgo.tematik;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class BasicStoreTest {
    private static final Directory directory = new ByteBuffersDirectory();

    public static final String RESET = "\033[0m";  // Text Reset
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";

    @Test
    void indexDemo() throws IOException {
        var writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
        {
            var doc = new Document();
            doc.add(new TermVectorTextField("title", "Kutyák és macskák 2.", Field.Store.NO));
            doc.add(new TermVectorTextField("subtitle", "A rusnya macska bosszúja", Field.Store.NO));
//            doc.add(new SortedDocValuesField("title", new BytesRef("Kutyák és macskák 2.")));
            writer.addDocument(doc);
            writer.commit();
        }
        {
            var doc = new Document();
            doc.add(new TermVectorTextField("title", "Kutyák és macskák 3.", Field.Store.NO));
            doc.add(new TermVectorTextField("subtitle", "A mancs parancs", Field.Store.NO));
//            doc.add(new SortedDocValuesField("title", new BytesRef("Kutyák és macskák 3.")));
            writer.commit();
            writer.addDocument(doc);
        }
        {
            var doc = new Document();
            doc.add(new TermVectorTextField("title", "Terminátor 2.", Field.Store.NO));
            doc.add(new TermVectorTextField("subtitle", "Az ítélet napja", Field.Store.NO));
//            doc.add(new SortedDocValuesField("title", new BytesRef("Terminátor 2.")));
            writer.commit();
            writer.addDocument(doc);
        }

        var stats = writer.getDocStats();
        writer.close();

        System.out.printf("Number of documents: %32d%n", stats.numDocs);
        var reader = DirectoryReader.open(directory);
        var vector = reader.getTermVector(reader.maxDoc() - 1, "subtitle");
        System.out.printf("Vector bytes: %s%n", ANSI_YELLOW + vector.getStats() + RESET);


        final var queryString = "ítélet";
        var term = new Term("subtitle", queryString);

        var result = reader.docFreq(term);
        System.out.printf("Document frequency for %s in field %s is: %4d %n", ANSI_GREEN + term.text() + RESET, ANSI_YELLOW + term.field() + RESET, result);

        reader.close();
    }
}