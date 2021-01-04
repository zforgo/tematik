package io.github.zforgo.tematik.lucene.model;

import io.github.zforgo.tematik.TermVectorTextField;
import io.github.zforgo.tematik.lucene.support.IndexAware;
import lombok.Value;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.IndexableField;

import java.util.List;
import java.util.Set;

import static io.github.zforgo.tematik.lucene.model.IMDBItem.Type.MOVIE;

@Value
public class IMDBItem implements IndexAware {

    String cim;
    String alcim;
    Type tipus;
    Set<Format> formatum;
    int megjelenes;
    Integer evadok;

    public static IMDBItem of(String cim, String alcim, Type tipus, Set<Format> formatum, int megjelenes, int evadok) {
        return new IMDBItem(cim, alcim, tipus, formatum, megjelenes, evadok);
    }

    public static IMDBItem ofMovie(String cim, String alcim, int megjelenes) {
        return new IMDBItem(cim, alcim, MOVIE, null, megjelenes, null);
    }

    @Override
    public List<IndexableField> getDocumentFields() {
        return List.of(
                new TermVectorTextField("cim", cim, Field.Store.NO),
                new TermVectorTextField("alcim", alcim, Field.Store.NO),
                new IntPoint("megjelenes", megjelenes)
        );
    }

    public enum Type {
        MOVIE,
        SERIES,
        TV
    }

    public enum Format {
        VHS,
        DVD,
        BLUE_RAY,
        VIDEO_DISC
    }
}
