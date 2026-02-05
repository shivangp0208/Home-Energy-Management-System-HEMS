package com.project.hems.SiteManagerService.dto;

import java.util.List;
import java.util.UUID;

public record CursorSiteResponse<T>(
    List<T> data, //je record apde frontend ne batava na che e ..
    int pageSize,
    UUID nextCursor,
    boolean hasNext
){}
