package com.alpha.popularmoviesii;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MovieTrailerPagination setters and getters
 */
public class MovieTrailerPagination {

    private Integer id;
    private List<MovieTrailerItem> results = new ArrayList<>();
    private final Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }


    /**
     * @return The results
     */
    public List<MovieTrailerItem> getResults() {
        return results;
    }

    /**
     * @param results The results
     */
    public void setResults(List<MovieTrailerItem> results) {
        this.results = results;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}