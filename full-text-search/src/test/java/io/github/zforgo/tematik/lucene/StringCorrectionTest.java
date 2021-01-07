package io.github.zforgo.tematik.lucene;

import io.github.zforgo.tematik.lucene.model.IMDBItem;
import io.github.zforgo.tematik.lucene.model.StringModel;
import io.github.zforgo.tematik.lucene.support.Utils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.zforgo.tematik.lucene.model.IMDBItem.ofMovie;
import static io.github.zforgo.tematik.lucene.support.Utils.dumpResult;
import static io.github.zforgo.tematik.lucene.support.Utils.stripToken;

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

        var q = new FuzzyQuery(new Term("alcim", "naplya"));

        // see: unaccented term gives result
        // var q = new FuzzyQuery(new Term("cim", "kutyak"));

        // see: different words appeared in result
        // var q = new FuzzyQuery(new Term("alcim", "macs"));

        var result = searcher.search(q, 20);
        dumpResult(result);
    }

    @Test
    void fuzzyQuery2() throws IOException, URISyntaxException {
        writer = new IndexWriter(Utils.newDirectory(), new IndexWriterConfig(analyzer));
        var p = Path.of(getClass().getResource("/telepulesek.txt").toURI());
        {
            var telepulesek = Files.lines(p)
                    .map(StringModel::new)
                    .collect(Collectors.toList());
            ObjectIndexer.index(writer, telepulesek);
        }
        reader = DirectoryReader.open(writer);

//      Kővágótőttős vs Kővágótöttös
        var input = "Kővágótőttős";

//      Óhid vs Óhíd
//        var input = "Óhíd";

//      Bak
//        var input = "Bak";


        final var str = stripToken(input, analyzer);


        var searcher = new IndexSearcher(reader);
        var q = new FuzzyQuery(new Term("content", str));
//        var q = new FuzzyQuery(new Term("content", str), 1, 0, 100, true);
        var result = searcher.search(q, 20);
        dumpResult(result, reader, "content");
    }
}
