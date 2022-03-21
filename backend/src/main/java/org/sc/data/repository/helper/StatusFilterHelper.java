package org.sc.data.repository.helper;

import org.bson.Document;
import org.sc.data.model.TrailStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.sc.data.repository.MongoUtils.$_IN;

@Component
public class StatusFilterHelper {

    public Document getInFilterBson(boolean isDraftTrailVisible){
        return new Document($_IN, getInFilter(isDraftTrailVisible));
    }

    public List<String> getInFilter(boolean isDraftTrailVisible) {
        return isDraftTrailVisible ?
                Arrays.asList(TrailStatus.PUBLIC.name(),
                        TrailStatus.DRAFT.name()) :
                Collections.singletonList(
                        TrailStatus.PUBLIC.name());
    }
}
