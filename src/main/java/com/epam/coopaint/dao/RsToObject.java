package com.epam.coopaint.dao;

import java.sql.ResultSet;

public interface RsToObject<T> {
    void apply(ResultSet rs, T target) throws Exception;
}