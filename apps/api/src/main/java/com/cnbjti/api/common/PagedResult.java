package com.cnbjti.api.common;

import java.util.List;

public record PagedResult<T>(List<T> items, int page, int pageSize, long total) {
}
