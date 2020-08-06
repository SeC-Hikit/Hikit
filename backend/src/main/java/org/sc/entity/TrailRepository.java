package org.sc.entity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "trail", path = "trail")
public interface TrailRepository extends MongoRepository<Trail, String> {
    List<Trail> findAllTrails();
    List<Trail> findTrailsByStartOrDestinationPoint(@Param("place") String place);
    List<Trail> findTrailsByCode(@Param("trailCode") String trailCode);
}
