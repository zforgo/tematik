package io.github.zforgo.tematik.lucene;

import io.github.zforgo.tematik.lucene.model.StringModel;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.zforgo.tematik.lucene.support.Utils.dumpResult;

public class PaginationTest extends TestBase<StringModel> {
    private final Path path;

    public PaginationTest() throws URISyntaxException {
        path = Path.of(getClass()
                .getResource("/telepulesek.txt").toURI());
    }

    @Override
    public List<StringModel> items() {
        try (var stream = Files.lines(path)) {
            return stream.map(StringModel::new)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    void paginationTest(int page) throws IOException {
        final var pageSize = 10;
        var searcher = new IndexSearcher(reader);
        var collector = TopScoreDocCollector.create(50_000, Integer.MAX_VALUE);
        var q = new MatchAllDocsQuery();
        searcher.search(q, collector);
        var result = collector.topDocs(page * pageSize, pageSize);
        dumpResult(result, reader, "content");
    }
}

