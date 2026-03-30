package com.project.hems.site_manager_service.util;

import java.util.Set;

import org.springframework.http.converter.json.MappingJacksonValue;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public final class JsonFilterUtil {

    private JsonFilterUtil() {
    }

    public static MappingJacksonValue applyFilter(
            Object value, String filterName, Set<String> excludedFields) {

        SimpleFilterProvider filters = new SimpleFilterProvider()
                .addFilter(filterName,
                        SimpleBeanPropertyFilter.serializeAllExcept(excludedFields));

        MappingJacksonValue wrapper = new MappingJacksonValue(value);
        wrapper.setFilters(filters);
        return wrapper;
    }
}
