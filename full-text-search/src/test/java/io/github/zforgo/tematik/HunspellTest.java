package io.github.zforgo.tematik;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.hunspell.HunspellStemFilterFactory;
import org.apache.lucene.analysis.miscellaneous.CapitalizationFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.apache.lucene.analysis.util.TokenFilterFactory;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.zforgo.tematik.BasicStoreTest.*;

public class HunspellTest {
    private static final String PARAM_DICTIONARY = "dictionary";
    private static final String PARAM_AFFIX = "affix";
    private static final String PARAM_IGNORE_CASE = "ignoreCase";
    private static final String PARAM_LONGEST_ONLY = "longestOnly";
    private static final TokenFilterFactory HUNSPELL_STEMMER_FACTORY;

    private static final Directory directory = new ByteBuffersDirectory();

    static {
        final var args = Stream.of(new String[][]{
                {PARAM_DICTIONARY, "hu/index.dic"},
                {PARAM_AFFIX, "hu/index.aff"},
                {PARAM_IGNORE_CASE, Boolean.TRUE.toString()}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        var stemmer = new HunspellStemFilterFactory(args);
        var loader = new ClasspathResourceLoader(HunspellTest.class.getClassLoader());
        try {
            stemmer.inform(loader);
            HUNSPELL_STEMMER_FACTORY = stemmer;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private static final class MyCustomAnalyzer extends Analyzer {

        @Override
        protected TokenStreamComponents createComponents(String fieldName) {
            final StandardTokenizer src = new StandardTokenizer();
            TokenStream result = new LowerCaseFilter(src);
            result = new CapitalizationFilter(result);
            result = HUNSPELL_STEMMER_FACTORY.create(result);
            return new TokenStreamComponents(src, result);
        }

    }

    @Test
    void foo() throws IOException {
        var writer = new IndexWriter(directory, new IndexWriterConfig(new MyCustomAnalyzer()));
        {
            var doc = new Document();
            doc.add(new TermVectorTextField("title", "Kutyák és macskák 2.", Field.Store.NO));
            doc.add(new TermVectorTextField("subtitle", "A rusnya macska bosszúja", Field.Store.NO));
            writer.addDocument(doc);
        }
        {
            var doc = new Document();
            doc.add(new TermVectorTextField("title", "Kutyák és macskák 3.", Field.Store.NO));
            doc.add(new TermVectorTextField("subtitle", "A mancs parancs", Field.Store.NO));
            writer.addDocument(doc);
        }
        {
            var doc = new Document();
            doc.add(new TermVectorTextField("title", "Terminátor 2.", Field.Store.NO));
            doc.add(new TermVectorTextField("subtitle", "Az ítélet napja", Field.Store.NO));
            writer.addDocument(doc);
        }

        var stats = writer.getDocStats();
        writer.close();

        System.out.printf("Number of documents: %31d%n", stats.numDocs);
        var reader = DirectoryReader.open(directory);
        var vector = reader.getTermVector(reader.maxDoc() - 1, "subtitle");
        System.out.printf("Vector bytes: %s%n", ANSI_YELLOW + vector.getStats() + RESET);


        final var queryString = "nap";
        var term = new Term("subtitle", queryString);

        var result = reader.docFreq(term);
        System.out.printf("Document frequency for %s in field %s is: %4d %n", ANSI_YELLOW + term.text() + RESET,ANSI_GREEN + term.field() + RESET, result);
        reader.close();

    }
}
