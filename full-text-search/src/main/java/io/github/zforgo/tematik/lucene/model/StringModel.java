package io.github.zforgo.tematik.lucene.model;

import io.github.zforgo.tematik.lucene.TermVectorTextField;
import io.github.zforgo.tematik.lucene.support.IndexAware;
import lombok.Value;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

import java.util.List;

@Value
public class StringModel implements IndexAware {
    String content;

    @Override
    public List<IndexableField> getDocumentFields() {
        return List.of(
                new TermVectorTextField("content", content, Field.Store.YES),
                new SortedDocValuesField("content", new BytesRef(content))
        );
    }
}
