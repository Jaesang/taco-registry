package com.registry.util;

import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by boozer on 2019. 6. 18
 */
public class PageableUtil {

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /**
     * Pageable에 해당하는 목록으로 Array를 반환한다.
     * @param target
     * @param pageable
     * @return
     */
    public static <T> List<T> subList(List<T> target, Pageable pageable) {

        // From Index
        int from = pageable.getPageSize() * pageable.getPageNumber();

        // To Index
        int to = (pageable.getPageSize() * (pageable.getPageNumber() + 1) > target.size()) ?
                target.size() :
                pageable.getPageSize() * (pageable.getPageNumber() + 1);

        return target.subList(from, to);
    }
}
