package io.github.zforgo.tematik;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class StringQueryTest {
    private final Directory directory = new ByteBuffersDirectory();

    @Test
    void simpleTermQuery() throws IOException, ParseException {

        final var s = List.of(
                "Mint ismert, Bergendy István Liszt Ferenc díjas zenész.",

                "Bergendy István, az együttes  zenekarvezetője szaxofonozik. Bergendy zenekar a " +
                        "Süsü, a sárkány című bábfilm főcímdalát énekli Bodrogi Gyulával az MTVA 1-es stúdiójában"
        );
        var writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
        s.forEach(content -> {
            var doc = new Document();
            doc.add(new TermVectorTextField("content", content, Field.Store.NO));
            doc.add(new SortedDocValuesField("content", new BytesRef(content)));
            try {
                writer.addDocument(doc);
                writer.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        var reader = DirectoryReader.open(directory);
        var searcher = new IndexSearcher(reader);

//        var q = new TermQuery(new Term("content", "bergend*"));
//        var q = new PrefixQuery(new Term("content", "bergend"));
//        q.setRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_REWRITE);

        var q = new WildcardQuery(new Term("content", "*ergend*"));
        q.setRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_REWRITE);

        var result = searcher.search(q, 20);
        dumpResult(result);
        reader.close();
    }

    /*
    comment out 3rd (score changed TFiDF similarity)
    change order true / false
     */
    @Test
    void spanQuery() throws IOException {

        final var s = List.of(
                "The Lucene was made by Doug Cutting, " +
                        "the great Hadoop was made by Mike Cafarella " +
                        "and Spark was made by Matei Zaharia",

                "The Lucene search engine originally was made by Doug Cutting, " +
                        "the great Hadoop was made by Mike Cafarella " +
                        "and Spark was made by Matei Zaharia",

                "Doug Cutting wrote Lucene in 1999."


        );
        var writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
        s.forEach(content -> {
            var doc = new Document();
            doc.add(new TermVectorTextField("content", content, Field.Store.NO));
            doc.add(new SortedDocValuesField("content", new BytesRef(content)));
            try {
                writer.addDocument(doc);
                writer.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.close();

        var reader = DirectoryReader.open(directory);
        var searcher = new IndexSearcher(reader);
        var q = new SpanNearQuery(new SpanQuery[]{
                new SpanTermQuery(new Term("content", "lucene")),
                new SpanTermQuery(new Term("content", "doug"))},
                7,
                true);
        var result = searcher.search(q, 20);
        dumpResult(result);
        reader.close();
    }

    @Test
    void phraseQuery() throws IOException {
        final var str = "Van zsákomban minden jó, piros alma, mogyoró";
        var writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
        var doc = new Document();
        doc.add(new TermVectorTextField("content", str, Field.Store.NO));
        doc.add(new SortedDocValuesField("content", new BytesRef(str)));
        writer.addDocument(doc);
        writer.commit();
        writer.close();

        var reader = DirectoryReader.open(directory);
        var searcher = new IndexSearcher(reader);

        final var qStr = "Van jó mogyoró";
        var phrases = stripTokens(qStr);

        var qb = new PhraseQuery.Builder();
        for (String phrase : phrases) {
            qb = qb.add(new Term("content", phrase));
        }
        var q = qb
                .setSlop(4)
                .build();
        var result = searcher.search(q, 20);
        dumpResult(result);
        reader.close();

    }

    private List<String> stripTokens(String str) throws IOException {
        List<String> result = new ArrayList<>();
        var ts = new StandardAnalyzer().tokenStream("noop", new StringReader(str));
        CharTermAttribute cattr = ts.addAttribute(CharTermAttribute.class);

        ts.reset();
        while (ts.incrementToken()) {
            result.add(cattr.toString());
        }
        ts.end();
        return result;
    }

    private void dumpResult(TopDocs result) {
        for (final ScoreDoc scoreDoc : result.scoreDocs) {
            System.out.printf("%5s score: %f%n",
                    scoreDoc.doc,
                    scoreDoc.score);
        }

    }
}
