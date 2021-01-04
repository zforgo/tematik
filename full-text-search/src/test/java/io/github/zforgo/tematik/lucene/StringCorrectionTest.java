package io.github.zforgo.tematik.lucene;

import io.github.zforgo.tematik.lucene.model.IMDBItem;
import io.github.zforgo.tematik.lucene.model.StringModel;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.github.zforgo.tematik.lucene.model.IMDBItem.ofMovie;
import static io.github.zforgo.tematik.lucene.support.Utils.dumpResult;

public class StringCorrectionTest extends TestBase<IMDBItem> {
    @Override
    public List<IMDBItem> items() {
        // @formatter:off
        return List.of(
                ofMovie("Kutyák és macskák 2.", "A rusnya macska bosszúja", 2010),
                ofMovie("Kutyák és macskák 3.", "A mancs parancs",          2020),
                ofMovie("Terminátor 2.",        "Az ítélet napja",          1991)
        );
        // @formatter:on
    }

    @Test
    void fuzzyQuery() throws IOException {
        var searcher = new IndexSearcher(reader);
        var q = new FuzzyQuery(new Term("cim", "kutyak"));
        var result = searcher.search(q, 20);
        dumpResult(result);
    }
}
