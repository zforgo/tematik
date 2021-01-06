package io.github.zforgo.tematik.lucene.model;

import io.github.zforgo.tematik.lucene.TermVectorTextField;
import io.github.zforgo.tematik.lucene.support.IndexAware;
import lombok.Value;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static IMDBItem ofMovie(String cim, Type tipus, Set<Format> formatum, int megjelenes) {
        return new IMDBItem(cim, null, tipus, formatum, megjelenes, null);
    }

    @Override
    public List<IndexableField> getDocumentFields() {
        return Stream.concat(
                Stream.of(
                        new TermVectorTextField("cim", cim, Field.Store.YES),
                        null == alcim ? null : new TermVectorTextField("alcim", alcim, Field.Store.NO),
                        new TermVectorTextField("tipus", tipus.name(), Field.Store.YES),
                        new StoredField("megjelenes", megjelenes),
                        null == evadok ? null : new IntPoint("evadok", evadok)
                ),
                Optional.ofNullable(formatum)
                        .map(
                                fs -> fs.stream()
                                        .map(f -> new TermVectorTextField("formatum", f.name(), Field.Store.NO))
                        ).orElse(Stream.empty()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
