package io.github.zforgo.tematik.lucene.model;

import io.github.zforgo.tematik.lucene.support.FacetAware;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.index.IndexableField;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
@Value
public class FacetedProduct extends Product implements FacetAware {

    private FacetedProduct(String gyarto, String model, int felbontas, int ar, int tomeg, int vizallosag, Color szin) {
        super(gyarto, model, felbontas, ar, tomeg, vizallosag, szin);
    }

    @Override
    public List<IndexableField> getDocumentFields() {
        return Stream.concat(
                super.getDocumentFields().stream(),
                Stream.of(
                        new FacetField("gyarto", getGyarto()),
                        new FacetField("ar", String.valueOf(getAr())),
                        new FacetField("felbontas", String.valueOf(getFelbontas()))
                )
        ).collect(Collectors.toList());
    }

    public static FacetedProduct of(String gyarto, String model, int felbontas, int ar, int tomeg, int vizallosag, Color szin) {
        return new FacetedProduct(gyarto, model, felbontas, ar, tomeg, vizallosag, szin);
    }

    @Override
    public FacetsConfig configFacets(FacetsConfig base) {
        base.setMultiValued("gyarto", true);
        return base;
    }
}