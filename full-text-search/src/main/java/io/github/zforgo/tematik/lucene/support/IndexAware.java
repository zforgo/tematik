package io.github.zforgo.tematik.lucene.support;

import org.apache.lucene.index.IndexableField;

import java.util.List;

public interface IndexAware {
    List<IndexableField> getDocumentFields();
}
