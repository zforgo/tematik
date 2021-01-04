package io.github.zforgo.tematik.lucene;

import io.github.zforgo.tematik.lucene.model.IMDBItem;
import org.apache.lucene.index.Term;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.github.zforgo.tematik.lucene.model.IMDBItem.ofMovie;
import static io.github.zforgo.tematik.lucene.support.Utils.*;

public class SimpleStoreTest extends TestBase<IMDBItem> {

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
    void statDemo() throws IOException {
        var stats = writer.getDocStats();
        System.out.printf("Number of documents: %32d%n", stats.numDocs);
        var vector = reader.getTermVector(reader.maxDoc() - 1, "alcim");
        System.out.printf("Vector bytes: %s%n", ANSI_YELLOW + vector.getStats() + ANSI_RESET);

        // docFreq: 1
        final var queryString = "ítélet";
        var term = new Term("alcim", queryString);

        // docFreq: 2
//        final var queryString = "macskák";
//        var term = new Term("cim", queryString);

        var result = reader.docFreq(term);
        System.out.printf("Document frequency for %s in field %s is: %4d %n", ANSI_GREEN + term.text() + ANSI_RESET, ANSI_YELLOW + term.field() + ANSI_RESET, result);
    }

}
