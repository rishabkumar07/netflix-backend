package com.netflix.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    /**
     * Slices an already-fetched, already-cached full list into one page.
     * Centralizes the pagination math here instead of repeating it in MovieService per category.
     */
    public static <T> PageResponse<T> of(List<T> fullList, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);

        int totalElements = fullList.size();
        int totalPages = (int) Math.ceil((double) totalElements / safeSize);

        int fromIndex = Math.min(safePage * safeSize, totalElements);
        int toIndex = Math.min(fromIndex + safeSize, totalElements);

        List<T> content = fullList.subList(fromIndex, toIndex);

        return PageResponse.<T>builder()
                .content(content)
                .page(safePage)
                .size(safeSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }
}
