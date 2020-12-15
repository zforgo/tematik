package io.github.zforgo.tematik;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.apache.lucene.search.BooleanClause.Occur.MUST;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

public class AdvancedQueryTest {
    private static final Directory directory = new ByteBuffersDirectory();

    @BeforeAll
    static void beforeAll() throws IOException {
        var writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
        {
            var doc = new Document();
            doc.add(new TermVectorTextField("title", "New Amsterdam", Field.Store.YES));
            doc.add(new TermVectorTextField("type", "sorozat", Field.Store.YES));
            doc.add(new TextField("formats", "DVD VHS", Field.Store.NO));
            doc.add(new IntPoint("year", 2008));
            doc.add(new IntPoint("seasons", 1));
            writer.addDocument(doc);
            writer.commit();
        }
        {
            var doc = new Document();
            doc.add(new TermVectorTextField("title", "New Amsterdam", Field.Store.YES));
            doc.add(new TermVectorTextField("type", "sorozat", Field.Store.YES));
            doc.add(new TextField("formats", "DVD", Field.Store.NO));
            doc.add(new IntPoint("year", 2018));
            doc.add(new IntPoint("seasons", 2));
            writer.addDocument(doc);
            writer.commit();
        }
        {
            var doc = new Document();
            doc.add(new TermVectorTextField("title", "New Amsterdam", Field.Store.YES));
            doc.add(new TermVectorTextField("type", "film", Field.Store.YES));
            doc.add(new TextField("formats", "DVD VHS", Field.Store.NO));
            doc.add(new IntPoint("year", 1998));
            writer.addDocument(doc);
            writer.commit();
        }
        {
            var doc = new Document();
            doc.add(new TermVectorTextField("title", "Terminator", Field.Store.YES));
            doc.add(new TermVectorTextField("type", "film", Field.Store.YES));
            doc.add(new TextField("formats", "VHS DVD", Field.Store.NO));
            doc.add(new IntPoint("year", 1984));
            writer.addDocument(doc);
            writer.commit();
        }
        {
            var doc = new Document();
            doc.add(new TermVectorTextField("title", "Terminator", Field.Store.YES));
            doc.add(new TermVectorTextField("type", "sorozat", Field.Store.YES));
            doc.add(new TextField("formats", "VHS DVD", Field.Store.NO));
            doc.add(new IntPoint("year", 2008));
            doc.add(new IntPoint("seasons", 2));
            writer.addDocument(doc);
            writer.commit();
        }
        writer.close();

    }

    @Test
    void simpleTermQuery() throws IOException {
        var reader = DirectoryReader.open(directory);
        var searcher = new IndexSearcher(reader);
        var term = new Term("title", "amsterdam");
        var q = new TermQuery(term);
        var result = searcher.search(q, 20);
        dumpResult(result, reader);
        reader.close();
    }

    /*
    MUST, MUST
    MUST, SHOULD
     */
    @Test
    void simpleBooleanQuery() throws IOException, ParseException {
        var reader = DirectoryReader.open(directory);
        var searcher = new IndexSearcher(reader);

        var titleTerm = new Term("title", "amsterdam");
        var typeTerm = new Term("type", "film");

        final var analyzer = new StandardAnalyzer();
        var qp0 = new QueryParser("title", analyzer);
        var qp1 = new QueryParser("type", analyzer);
        {
            var q = new BooleanQuery.Builder()
                    .add(qp0.parse(titleTerm.text()), MUST)
                    .add(qp1.parse(typeTerm.text()), SHOULD)
                    .build();
            var result = searcher.search(q, 20);
            dumpResult(result, reader);

        }
        System.out.printf("-----%n");
        {
            var q = new BooleanQuery.Builder()
                    .add(new TermQuery(titleTerm), MUST)
                    .add(new TermQuery(typeTerm), SHOULD)
                    .build();
            var result = searcher.search(q, 20);
            dumpResult(result, reader);
        }
        reader.close();
    }

    /*
        title && (type || format || year)
     */
    @Test
    void minShouldMatchBooleanQuery() throws IOException, ParseException {
        var reader = DirectoryReader.open(directory);
        var searcher = new IndexSearcher(reader);
        var titleTerm = new Term("title", "amsterdam");
        var typeTerm = new Term("type", "sorozat");
        var formatTerm = new Term("formats", "DVD");
        final var analyzer = new StandardAnalyzer();
        var qp0 = new QueryParser("title", analyzer);
        var qp1 = new QueryParser("type", analyzer);
        var qp2 = new QueryParser("formats", analyzer);
        var q = new BooleanQuery.Builder()
                .add(qp0.parse(titleTerm.text()), MUST)
                .add(new BooleanQuery.Builder()
                        .add(qp1.parse(typeTerm.text()), SHOULD)
                        .add(qp2.parse(formatTerm.text()), SHOULD)
                        .add(IntPoint.newRangeQuery("year", 2010, 2020), SHOULD)
//                        .setMinimumNumberShouldMatch(3)
                        .build(), MUST

                )
                .build();
        var result = searcher.search(q, 20);
        dumpResult(result, reader);

        reader.close();
    }

    private void dumpResult(TopDocs result, DirectoryReader reader) throws IOException {
        for (final ScoreDoc scoreDoc : result.scoreDocs) {
            final int docId = scoreDoc.doc;
            final Document d = reader.document(docId);
            System.out.printf("%5s %-40s - %-10s score: %f%n",
                    scoreDoc.doc,
                    d.getField("title").stringValue(),
                    d.getField("type").stringValue(),
                    scoreDoc.score);
        }

    }

}
