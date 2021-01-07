package io.github.zforgo.tematik.lucene;

import io.github.zforgo.tematik.lucene.model.Product;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.github.zforgo.tematik.lucene.model.Product.Color.*;
import static io.github.zforgo.tematik.lucene.model.Product.of;
import static io.github.zforgo.tematik.lucene.support.Utils.dumpResult;
import static org.apache.lucene.document.IntPoint.newRangeQuery;
import static org.apache.lucene.search.BooleanClause.Occur.MUST;

public class AdvancedScoringTest extends TestBase<Product> {

    @Override
    public List<Product> items() {
        //@formatter:off
        return List.of(
/*0*/           of("Panasonic", "Lumix DMC-FT30", 16, 53_900, 144,  8,  BLACK),
                of("Panasonic", "Lumix DMC-FT30", 16, 53_900, 144,  8,  BLUE),
                of("Panasonic", "Lumix DMC-FT30", 16, 53_900, 144,  8,  YELLOW),
                of("Panasonic", "Lumix DMC-FT30", 16, 53_900, 144,  8,  RED),

/*4*/           of("OLYMPUS",    "TG-6",         12, 139_999, 253, 15,  BLUE),
                of("OLYMPUS",    "TG-6",         12, 139_999, 253, 15,  BLACK),

/*6*/           of("NIKON",      "CoolPix W300", 16, 125_999, 231, 30,  BLACK),
                of("NIKON",      "CoolPix W300", 16, 125_999, 231, 30,  BLUE),
                of("NIKON",      "CoolPix W300", 16, 125_999, 231, 30,  YELLOW),

/*9*/           of("Easypix",    "W1400 Active", 14,  28_490, 440,  3,  BLACK),
                of("Easypix",    "W1400 Active", 14,  28_490, 440,  3,  RED),

/*11*/          of("CANON",      "IVY REC",      13,  45_490,  90,  2,  GREEN),
                of("CANON",      "IVY REC",      13,  45_490,  90,  2,  BLACK),
                of("CANON",      "IVY REC",      13,  45_490,  90,  2,  RED),
                of("CANON",      "IVY REC",      13,  45_490,  90,  2,  PINK),

/*15*/          of("Ricoh",      "WG-6",         20, 160_990, 246, 20,  BLACK)
        );
        // @formatter:on
    }

    @Test
    void firstTest() throws IOException {
        var searcher = new IndexSearcher(reader);
        var q = new BooleanQuery.Builder()
                .add(newRangeQuery("ar", 0, 50_000), MUST)
                .build();

        var result = searcher.search(q, 20);
        dumpResult(result, reader, "model", "szin", "ar", "felbontas", "vizallosag");
    }
}
