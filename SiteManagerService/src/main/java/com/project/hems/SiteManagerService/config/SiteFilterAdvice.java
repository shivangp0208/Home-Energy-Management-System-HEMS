package com.project.hems.SiteManagerService.config;

import java.util.Collection;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
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
import com.project.hems.SiteManagerService.dto.CursorSiteResponse;
import com.project.hems.hems_api_contracts.contract.program.Program;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class SiteFilterAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    @Nullable
    public Object beforeBodyWrite(
            @Nullable Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        boolean includeProgram = false;

        if (request instanceof ServletServerHttpRequest servletRequest) {
            includeProgram = Boolean.parseBoolean(
                    servletRequest.getServletRequest().getParameter("includeProgram"));
        }

        SimpleFilterProvider filters = new SimpleFilterProvider()
                .addFilter(
                        "siteFilter",
                        includeProgram
                                ? SimpleBeanPropertyFilter.serializeAll()
                                : SimpleBeanPropertyFilter.serializeAllExcept("enrollProgramIds"))
                .addFilter(
                        "programFilter",
                        SimpleBeanPropertyFilter.serializeAllExcept("sites"));

        if (body == null) {
            return null;
        }

        MappingJacksonValue wrapper = new MappingJacksonValue(body);
        wrapper.setFilters(filters);

        return wrapper;
    }

}
