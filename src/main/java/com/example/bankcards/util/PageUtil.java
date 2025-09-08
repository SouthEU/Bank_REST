package com.example.bankcards.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

public final class PageUtil {

    public static Pageable setPage(int page, int size, String sortBy, String sortDir, List<String> allowedSortFields) {
        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("Sorting by field '" + sortBy + "' is not allowed");
        }

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDir.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sort direction: '" + sortDir + "'. Use 'asc' or 'desc'");
        }

        return  PageRequest.of(page, size, direction, sortBy);
    }
}
