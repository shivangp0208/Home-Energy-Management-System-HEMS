package com.hems.project.Virtual_Power_Plant.Config;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
    public boolean supports(MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType) {

        final String funcName = "supports";

        boolean supported = MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType);

        log.debug("{} | converterType={} supported={}",
                funcName,
                converterType.getSimpleName(),
                supported);

        return supported;
    }

    @Override
    @Nullable
    public Object beforeBodyWrite(@Nullable Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        final String funcName = "beforeBodyWrite";

        log.debug("{} | start | contentType={} converter={}",
                funcName,
                selectedContentType,
                selectedConverterType.getSimpleName());

        boolean includeSite = false;

        if (request instanceof ServletServerHttpRequest servletRequest) {
            includeSite = Boolean.parseBoolean(
                    servletRequest.getServletRequest().getParameter("includeSites"));

            log.debug("{} | includeSites param={}", funcName, includeSite);
        } else {
            log.debug("{} | request is not ServletServerHttpRequest", funcName);
        }

        if (body == null) {
            log.debug("{} | response body is null, skipping filters", funcName);
            return null;
        }

        SimpleFilterProvider filters = new SimpleFilterProvider()
                .addFilter(
                        "siteGroupFilter",
                        includeSite
                                ? SimpleBeanPropertyFilter.serializeAll()
                                : SimpleBeanPropertyFilter.serializeAllExcept("sitesInGroup"))
                .addFilter(
                        "siteFilter",
                        SimpleBeanPropertyFilter.serializeAllExcept("enrollProgramIds"))
                .addFilter(
                        "programFilter",
                        SimpleBeanPropertyFilter.serializeAllExcept("sites"));

        log.debug("{} | filters applied | includeSite={}", funcName, includeSite);

        MappingJacksonValue wrapper = new MappingJacksonValue(body);
        wrapper.setFilters(filters);

        log.debug("{} | response wrapped with MappingJacksonValue", funcName);

        return wrapper;
    }
}