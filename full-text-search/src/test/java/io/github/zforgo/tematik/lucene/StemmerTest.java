package io.github.zforgo.tematik.lucene;

import io.github.zforgo.tematik.lucene.model.IMDBItem;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.hunspell.HunspellStemFilterFactory;
import org.apache.lucene.analysis.miscellaneous.CapitalizationFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.zforgo.tematik.lucene.model.IMDBItem.ofMovie;
import static io.github.zforgo.tematik.lucene.support.Utils.dumpResult;
import static io.github.zforgo.tematik.lucene.support.Utils.stripToken;


public class StemmerTest extends TestBase<IMDBItem> {

    @Override
    public List<IMDBItem> items() {
        // @formatter:off
        return List.of(
                ofMovie("Kutyák és macskák 2.",     "A rusnya macska bosszúja", 2010),
                ofMovie("Kutyák és macskák 3.",     "A mancs parancs",          2020),
                ofMovie("Terminátor 2.",             "Az ítélet napja",         1991),
                ofMovie("Melegedő helyzet",          "Nincs neki",              2012)
        );
        // @formatter:on
    }

    private static final String PARAM_DICTIONARY = "dictionary";
    private static final String PARAM_AFFIX = "affix";
    private static final String PARAM_IGNORE_CASE = "ignoreCase";
    private static final String PARAM_LONGEST_ONLY = "longestOnly";
    private static final TokenFilterFactory HUNSPELL_STEMMER_FACTORY;


    static {
        final var args = Stream.of(new String[][]{
                {PARAM_DICTIONARY, "hu/index.dic"},
                {PARAM_AFFIX, "hu/index.aff"},
                {PARAM_IGNORE_CASE, Boolean.TRUE.toString()}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        var stemmer = new HunspellStemFilterFactory(args);
        var loader = new ClasspathResourceLoader(StemmerTest.class.getClassLoader());
        try {
            stemmer.inform(loader);
            HUNSPELL_STEMMER_FACTORY = stemmer;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private static final class MyCustomAnalyzer extends Analyzer {

        @Override
        protected TokenStreamComponents createComponents(String fieldName) {
            final StandardTokenizer src = new StandardTokenizer();
            TokenStream result = new LowerCaseFilter(src);
            result = new CapitalizationFilter(result);
            result = HUNSPELL_STEMMER_FACTORY.create(result);
            result = new LowerCaseFilter(result);
            return new TokenStreamComponents(src, result);
        }

    }

    StemmerTest() {
        super();
        analyzer = new MyCustomAnalyzer();
    }

    @Test
    void stemmedTermQuery() throws IOException {
        var searcher = new IndexSearcher(reader);

        final var queryString = "felmelegedett";
        var term = new Term("cim", stripToken(queryString, analyzer));
        var q = new TermQuery(term);

        var result = searcher.search(q, 20);
        dumpResult(result, reader, "cim");
    }
}
