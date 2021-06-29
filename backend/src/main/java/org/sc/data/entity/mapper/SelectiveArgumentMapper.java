package org.sc.data.entity.mapper;


import org.bson.Document;
import org.sc.processor.TrailSimplifierLevel;

public interface SelectiveArgumentMapper<T> {
    T mapToObject(Document document, TrailSimplifierLevel trailSimplifierLevel);
}
