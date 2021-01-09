package org.sc.data.dto;


public interface Mapper<K, T> {
    T toDto(K entity);
    K toEntity(T dto);
}
