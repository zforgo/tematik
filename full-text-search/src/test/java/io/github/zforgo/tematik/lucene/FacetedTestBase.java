package io.github.zforgo.tematik.lucene;

import io.github.zforgo.tematik.lucene.support.FacetAware;
import io.github.zforgo.tematik.lucene.support.IndexAware;
import io.github.zforgo.tematik.lucene.support.Utils;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;


public abstract class FacetedTestBase<T extends IndexAware & FacetAware> extends TestBase<T> {
    protected DirectoryTaxonomyWriter taxoWriter;
    protected DirectoryTaxonomyReader taxoReader;
    protected FacetsConfig facetsConfig;

    @BeforeEach
    void init() throws IOException {
        var items = items();
        facetsConfig = items.get(0).configFacets(new FacetsConfig());
        writer = new IndexWriter(Utils.newDirectory(), new IndexWriterConfig(analyzer));
        taxoWriter = new DirectoryTaxonomyWriter(Utils.newDirectory());

        ObjectIndexer.index(writer, taxoWriter, items(), facetsConfig);
        reader = DirectoryReader.open(writer);
        taxoReader = new DirectoryTaxonomyReader(taxoWriter);
    }


    @AfterEach
    void tearDown() throws IOException {
        super.tearDown();
        if (null != taxoWriter) {
            taxoWriter.close();
        }
        if (null != taxoReader) {
            taxoReader.close();
        }
    }
}
