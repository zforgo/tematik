package io.github.zforgo.tematik.lucene.model;

import io.github.zforgo.tematik.lucene.TermVectorTextField;
import io.github.zforgo.tematik.lucene.support.IndexAware;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.index.IndexableField;

import java.util.List;

import static org.apache.lucene.document.Field.Store.YES;

@Value
@NonFinal
public class Product implements IndexAware {
    String gyarto;
    String model;
    int felbontas;
    int ar;
    int tomeg;
    int vizallosag;
    Color szin;

    protected Product(String gyarto, String model, int felbontas, int ar, int tomeg, int vizallosag, Color szin) {
        this.gyarto = gyarto;
        this.model = model;
        this.felbontas = felbontas;
        this.ar = ar;
        this.tomeg = tomeg;
        this.vizallosag = vizallosag;
        this.szin = szin;
    }

    public static Product of(String gyarto, String model, int felbontas, int ar, int tomeg, int vizallosag, Color szin) {
        return new Product(gyarto, model, felbontas, ar, tomeg, vizallosag, szin);
    }

    public enum Color {
        RED,
        YELLOW,
        GREEN,
        BLUE,
        PINK,
        BLACK,
        WHITE
    }


    @Override
    public List<IndexableField> getDocumentFields() {
        return List.of(
                new TermVectorTextField("gyarto", gyarto, YES),
                new FacetField("gyarto", gyarto),

                new TermVectorTextField("model", model, YES),

                new IntPoint("felbontas", felbontas),
                new NumericDocValuesField("felbontas", felbontas),
                new StoredField("felbontas", felbontas),

                new IntPoint("ar", ar),
                new StoredField("ar", ar),
                new NumericDocValuesField("ar", ar),

                new IntPoint("tomeg", tomeg),
                new NumericDocValuesField("tomeg", tomeg),

                new IntPoint("vizallosag", vizallosag),
                new NumericDocValuesField("vizallosag", vizallosag),
                new StoredField("vizallosag", vizallosag),

                new TermVectorTextField("szin", szin.name(), YES)
        );
    }
}
