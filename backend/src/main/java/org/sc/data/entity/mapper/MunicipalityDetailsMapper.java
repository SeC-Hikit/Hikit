package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.MunicipalityDetails;
import org.springframework.stereotype.Component;

@Component
public class MunicipalityDetailsMapper implements Mapper<MunicipalityDetails>{

    @Override
    public MunicipalityDetails mapToObject(Document document) {
        return new MunicipalityDetails(
                document.getString(MunicipalityDetails.CODE),
                document.getString(MunicipalityDetails.CITY),
                document.getString(MunicipalityDetails.PROVINCE),
                document.getString(MunicipalityDetails.PROVINCE_SHORT)
        );
    }

    @Override
    public Document mapToDocument(MunicipalityDetails object) {
        return new Document(MunicipalityDetails.CODE, object.getCode())
                .append(MunicipalityDetails.CITY, object.getCity())
                .append(MunicipalityDetails.PROVINCE, object.getProvince())
                .append(MunicipalityDetails.PROVINCE_SHORT, object.getProvinceShort());
    }
}
