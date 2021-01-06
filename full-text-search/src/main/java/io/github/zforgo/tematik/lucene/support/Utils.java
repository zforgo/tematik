package io.github.zforgo.tematik.lucene.support;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Utils {
    public static final String ANSI_RESET = "\033[0m";  // Text Reset
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static Directory newDirectory() {
        return new ByteBuffersDirectory();
    }

    public static void dumpResult(TopDocs result) {
        for (final ScoreDoc scoreDoc : result.scoreDocs) {
            System.out.printf(ANSI_GREEN + "%5s" + ANSI_RESET + " score: %f%n",
                    scoreDoc.doc,
                    scoreDoc.score);
        }
    }

    public static void dumpResult(TopDocs result, IndexReader reader, String... fields) throws IOException {
        System.out.printf(ANSI_YELLOW + "Összes találat:" + ANSI_GREEN + "%5d %n" + ANSI_RESET, result.totalHits.value);

        final var strPatternPart = "%-" + (100 / fields.length) + "s";
        final var strPattern = String.join("", Collections.nCopies(fields.length, strPatternPart));
        final var pattern = ANSI_GREEN + "%5d." + ANSI_RESET + "  - %-12.6f " + strPattern + "%n";
        System.out.printf(ANSI_YELLOW + "\t\t\t\t\t   " + strPattern + "%n", (Object[]) fields);
        System.out.println("------------------------------------------------------------------------------------------------------------------------------" + ANSI_RESET);

        for (final ScoreDoc scoreDoc : result.scoreDocs) {
            final int docId = scoreDoc.doc;
            final Document d = reader.document(docId);
            var params = new ArrayList<Object>(Arrays.asList(scoreDoc.doc, scoreDoc.score));
            Arrays.stream(fields)
                    .map(d::getField)
                    .map(IndexableField::stringValue)
                    .forEachOrdered(params::add);
            System.out.printf(pattern, params.toArray());

        }

    }

    public static List<String> stripTokens(String input, Analyzer analyzer) {
        List<String> result = new ArrayList<>();
        var ts = analyzer.tokenStream("noop", new StringReader(input));

        try (ts) {
            CharTermAttribute cattr = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                result.add(cattr.toString());
            }
            ts.end();
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String stripToken(String input, Analyzer analyzer) {
        return stripTokens(input, analyzer).get(0);
    }

}
