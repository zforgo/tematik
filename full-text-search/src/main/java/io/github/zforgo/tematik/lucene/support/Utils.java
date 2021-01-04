package io.github.zforgo.tematik.lucene.support;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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
            System.out.printf("%5s score: %f%n",
                    scoreDoc.doc,
                    scoreDoc.score);
        }
    }

    public static List<String> stripTokens(String input, Analyzer analyzer) {
        List<String> result = new ArrayList<>();
        var ts = analyzer.tokenStream("noop", new StringReader(input));
        CharTermAttribute cattr = ts.addAttribute(CharTermAttribute.class);

        try {
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
}
