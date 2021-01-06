package org.sc.data.repository;

import org.sc.common.rest.PoiMacroType;
import org.sc.data.entity.Poi;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PoiRepository extends MongoRepository<Poi, String> {
    Optional<Poi> findById(String id);
    List<Poi> findByCode(String code);
    List<Poi> findByMacro(PoiMacroType macroType);
    List<Poi> findByName(String name);

    @Query(value="{}", fields="{'name': /?0/}")
    List<Poi> findPoiByNameAndTags(String name, String tags);
}
