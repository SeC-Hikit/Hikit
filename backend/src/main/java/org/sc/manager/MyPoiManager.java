package org.sc.manager;

import org.sc.data.model.Poi;
import org.sc.data.repository.PoiDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyPoiManager {

    private final PoiDAO poiDao;

    @Autowired
    public MyPoiManager(PoiDAO myPoiDao) {
        this.poiDao = myPoiDao;
    }

    public List<Poi> getPoi(){
        List<Poi> pois = poiDao.get(0, 100, "sec-bologna");
        return pois;
    }
}
