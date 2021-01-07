package io.github.zforgo.tematik.lucene;

import io.github.zforgo.tematik.lucene.model.StringModel;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.github.zforgo.tematik.lucene.support.Utils.dumpResult;
import static io.github.zforgo.tematik.lucene.support.Utils.stripTokens;

public class AdvancedStringQueryTest extends TestBase<StringModel> {
    @Override
    public List<StringModel> items() {
        return List.of(
                new StringModel(
                        "The Lucene was made by Doug Cutting, " +
                                "the great Hadoop was made by Mike Cafarella " +
                                "and Spark was made by Matei Zaharia"),

                new StringModel(
                        "The Lucene search engine originally was made by Doug Cutting, " +
                                "the great Hadoop was made by Mike Cafarella " +
                                "and Spark was made by Matei Zaharia"),

                new StringModel(
                        "Doug Cutting wrote Lucene in 1999.")

        );
    }

    @Test
    void spanQuery() throws IOException {
        var searcher = new IndexSearcher(reader);
        var input = "lucene doug the";
        var maximumSlop = 6;
        // uncomment to see only id:0 and id:2 appears in result set
//        var maximumSlop = 4;
        var inOrder = false;
        // uncomment set strict order mode
//        var inOrder = true;

        var q = new SpanNearQuery(
                stripTokens(input, analyzer).stream()
                        .map(s -> new Term("content", s))
                        .map(SpanTermQuery::new)
                        .toArray(SpanQuery[]::new),
                maximumSlop,
                inOrder
        );

/*
        var q = new SpanNearQuery(new SpanQuery[]{
                    new SpanTermQuery(new Term("content", "lucene")),
                    new SpanTermQuery(new Term("content", "doug"))},
                6,
                true);
*/
        var result = searcher.search(q, 20);
        dumpResult(result);
    }

    @Test
    void phraseQuery() throws IOException {
        var input = "lucene doug the";
        var searcher = new IndexSearcher(reader);
        var maximumSlop = 6;
        var qb = new PhraseQuery.Builder()
                .setSlop(maximumSlop);
        stripTokens(input, analyzer).stream()
                .map(s -> new Term("content", s))
                .forEach(qb::add);
        var result = searcher.search(qb.build(), 20);
        dumpResult(result);
    }
}
