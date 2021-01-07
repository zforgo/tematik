package io.github.zforgo.tematik.lucene;

import io.github.zforgo.tematik.lucene.model.StringModel;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.github.zforgo.tematik.lucene.support.Utils.dumpResult;

public class SimpleStringQueryTest extends TestBase<StringModel> {
    @Override
    public List<StringModel> items() {
        return List.of(
                new StringModel(
                        "Mint ismert, Bergendy István Liszt Ferenc díjas zenész."),

                new StringModel(
                        "Bergendy István, az együttes  zenekarvezetője szaxofonozik. A Bergendy zenekar a " +
                                "Süsü, a sárkány című bábfilm főcímdalát énekli Bodrogi Gyulával az MTVA 1-es stúdiójában")
        );
    }

    @Test
    void termQuery() throws IOException {
        var searcher = new IndexSearcher(reader);
        var q = new TermQuery(new Term("content", "bergendy"));
        var result = searcher.search(q, 20);
        // id:1 score is higher
        dumpResult(result);
    }

    @Test
    void prefixQuery() throws IOException {
        var searcher = new IndexSearcher(reader);
        // first run: all scores are 1.0
        var q = new PrefixQuery(new Term("content", "bergend"));
        // uncomment to use real scoring instead of constants
//        q.setRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_REWRITE);
        var result = searcher.search(q, 20);
        dumpResult(result);
    }

    @Test
    void wildcardQuery() throws IOException {
        var searcher = new IndexSearcher(reader);
        var q = new WildcardQuery(new Term("content", "*ergend*"));
        q.setRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_REWRITE);
        var result = searcher.search(q, 20);
        dumpResult(result);
    }
}
