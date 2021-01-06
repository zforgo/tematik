package io.github.zforgo.tematik.lucene;

import io.github.zforgo.tematik.lucene.model.FacetedProduct;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.range.LongRange;
import org.apache.lucene.facet.range.LongRangeFacetCounts;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TermQuery;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.github.zforgo.tematik.lucene.model.FacetedProduct.of;
import static io.github.zforgo.tematik.lucene.model.Product.Color.*;
import static io.github.zforgo.tematik.lucene.support.Utils.dumpResult;
import static io.github.zforgo.tematik.lucene.support.Utils.stripToken;

public class FacetTest extends FacetedTestBase<FacetedProduct> {
    @Override
    public List<FacetedProduct> items() {
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
    void simpleFacet() throws IOException {
        var searcher = new IndexSearcher(reader);
        var q = new MatchAllDocsQuery();
//        var q = new TermQuery(new Term("szin", stripToken(RED.name(), analyzer)));

        FacetsCollector fc = new FacetsCollector();
        var result = FacetsCollector.search(searcher, q, 10, fc);

        Facets facets = new FastTaxonomyFacetCounts(taxoReader, facetsConfig, fc);
        var facetResult = facets.getAllDims(10);

        dumpResult(result, reader, "model", "szin", "ar", "felbontas", "vizallosag");
        System.out.println(facetResult);
    }

    @Test
    void rangeFacet() throws IOException {
        final var occso = new LongRange("Olcsóért", 0, true, 15_000, true);
        final var koztes = new LongRange("Köztes", 15_000, false, 50_000, true);
        final var draga = new LongRange("Drága", 50_000, false, 130_000, true);
        final var nema = new LongRange("Hülyének nézel?", 130_000, false, Long.MAX_VALUE, true);

        var searcher = new IndexSearcher(reader);
//        var q = new MatchAllDocsQuery();
        var q = new TermQuery(new Term("szin", stripToken(RED.name(), analyzer)));

        FacetsCollector fc = new FacetsCollector();
        var result = FacetsCollector.search(searcher, q, 10, fc);
        Facets facets = new LongRangeFacetCounts("ar", fc, occso, koztes, draga, nema);
        var facetResult = facets.getAllDims(10);

        dumpResult(result, reader, "model", "szin", "ar", "felbontas", "vizallosag");
        System.out.println(facetResult);

    }
}
