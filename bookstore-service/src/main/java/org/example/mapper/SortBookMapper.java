package org.example.mapper;

import org.example.model.SortBook;

public class SortBookMapper {

    public static String toField(SortBook sortBook) {
        return switch (sortBook) {
            case ID -> "id";
            case TITLE -> "title";
            case PRICE -> "price";
        };
    }
}
