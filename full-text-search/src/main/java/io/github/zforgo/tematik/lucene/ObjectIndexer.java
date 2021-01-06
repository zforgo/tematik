package io.github.zforgo.tematik.lucene;

import io.github.zforgo.tematik.lucene.support.IndexAware;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;
import java.util.Collection;

public class ObjectIndexer {

    public static void index(IndexWriter writer, Collection<? extends IndexAware> objects) throws IOException {
        objects.stream()
                .map(IndexAware::getDocumentFields)
                .forEach(fields -> {
                    try {
                        writer.addDocument(fields);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        writer.commit();
    }

    public static void index(IndexWriter writer, TaxonomyWriter taxoWriter, Collection<? extends IndexAware> objects, FacetsConfig facetsConfig) throws IOException {
        objects.stream()
                .map(IndexAware::getDocumentFields)
                .forEach(fields -> {
                    try {
                        var d = new Document();
                        fields.forEach(d::add);
                        writer.addDocument(facetsConfig.build(taxoWriter, d));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        writer.commit();
    }
}
