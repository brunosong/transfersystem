package com.brunosong.deployment.system.deployment.service.dto.query;

import java.util.List;
import java.util.function.Function;

/**
 * 화면에 내려 주는 페이지 응답. Spring 의 Page 를 그대로 직렬화하지 않고
 * 필요한 값만 담아 API 모양을 고정한다.
 */
public record PageView<T>(List<T> items, int page, int size, long totalItems, int totalPages) {

    public <R> PageView<R> map(Function<T, R> mapper) {
        return new PageView<>(items.stream().map(mapper).toList(), page, size, totalItems, totalPages);
    }
}
