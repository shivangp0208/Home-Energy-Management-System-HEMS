package com.hems.project.Virtual_Power_Plant.Config;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class VppFilterAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return MappingJackson2CborHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    @Nullable
    public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response) {
        log.debug("beforeBodyWrite: applying vpp/site group filters");

        boolean includeSite = false;

        if (request instanceof ServletServerHttpRequest servletRequest) {
            includeSite = Boolean.parseBoolean(
                    servletRequest.getServletRequest().getParameter("includeSites"));
        }

        SimpleFilterProvider filters = new SimpleFilterProvider()
                .addFilter(
                        "siteGroupFilter",
                        includeSite
                                ? SimpleBeanPropertyFilter.serializeAll()
                                : SimpleBeanPropertyFilter.serializeAllExcept("siteInGroup"));
                // .addFilter(
                //         "siteFilter",
                //         SimpleBeanPropertyFilter.serializeAllExcept("enrollProgramIds"));

        if (body == null) {
            return null;
        }

        MappingJacksonValue wrapper = new MappingJacksonValue(body);
        wrapper.setFilters(filters);

        return wrapper;
    }

}
