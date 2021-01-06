package io.github.zforgo.tematik.lucene.support;

import org.apache.lucene.facet.FacetsConfig;

public interface FacetAware {
    default FacetsConfig configFacets(FacetsConfig base) {
        return base;
    }
}
