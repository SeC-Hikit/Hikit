package org.sc.data;


import org.bson.Document;

public interface Mapper<T> {
    T mapToObject(Document document);

    Document mapToDocument(T object);
}
