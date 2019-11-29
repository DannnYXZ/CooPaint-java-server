package com.epam.coopaint.dao.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

class RSMapper<T> {
    private List<RsToObjectMapper<T>> mappings;

    RSMapper(List<RsToObjectMapper<T>> mappings) {
        // mapping functions
        this.mappings = mappings;
    }

    List<T> mapToList(ResultSet resultSet, Supplier<T> supplier) throws Exception {
        List<T> boards = new ArrayList<>();
        while (resultSet.next()) {
            var target = supplier.get();
            for (var column : mappings) {
                column.apply(resultSet, target);
            }
            boards.add(target);
        }
        return boards;
    }
}