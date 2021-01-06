package io.github.zforgo.tematik.lucene;

import io.github.zforgo.tematik.lucene.model.IMDBItem;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.github.zforgo.tematik.lucene.model.IMDBItem.Format.*;
import static io.github.zforgo.tematik.lucene.model.IMDBItem.Type.MOVIE;
import static io.github.zforgo.tematik.lucene.model.IMDBItem.Type.SERIES;
import static io.github.zforgo.tematik.lucene.model.IMDBItem.ofMovie;
import static io.github.zforgo.tematik.lucene.support.Utils.dumpResult;
import static io.github.zforgo.tematik.lucene.support.Utils.stripToken;
import static java.util.Set.of;
import static org.apache.lucene.search.BooleanClause.Occur.MUST;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

public class SimpleBooleanQueryTest extends TestBase<IMDBItem> {
    @Override
    public List<IMDBItem> items() {
        //@formatter:off
        return List.of(
                ofMovie("New Amsterdam",    SERIES,     of(DVD, VHS),                2008),
                ofMovie("New Amsterdam",    SERIES,     of(DVD, BLUE_RAY),           2018),
                ofMovie("New Amsterdam",    MOVIE,      of(VHS, DVD),                1998),
                ofMovie("Terminátor 2.",    MOVIE,      of(VHS, DVD, VIDEO_DISC),    1984)
        );
        // @formatter:on
    }

    @Test
    void simpleBooleanQuery() throws IOException {
        var searcher = new IndexSearcher(reader);
        final var title = "amsterdam";
        final var type = SERIES;
        final var format = BLUE_RAY;
        // uncomment to see score had been changed but not filtered
//        final var format = VIDEO_DISC;

        var q = new BooleanQuery.Builder()
                .add(new TermQuery(new Term("cim", stripToken(title, analyzer))), MUST)
                .add(new TermQuery(new Term("tipus", stripToken(type.name(), analyzer))), SHOULD)
                .add(new TermQuery(new Term("formatum", stripToken(format.name(), analyzer))), SHOULD)
//                .setMinimumNumberShouldMatch(1)
                .build();
        var result = searcher.search(q, 20);
        dumpResult(result, reader, "cim","tipus","megjelenes");
    }
}
