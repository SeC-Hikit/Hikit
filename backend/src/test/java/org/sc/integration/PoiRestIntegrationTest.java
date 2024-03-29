package org.sc.integration;

import org.hikit.common.datasource.Datasource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.response.PoiResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.auth.AuthFacade;
import org.sc.controller.POIController;
import org.sc.controller.admin.AdminPlaceController;
import org.sc.controller.admin.AdminPoiController;
import org.sc.controller.admin.AdminTrailController;
import org.sc.data.model.Poi;
import org.sc.data.model.PoiMacroType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sc.data.repository.MongoUtils.NO_FILTERING_TOKEN;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PoiRestIntegrationTest {

    public static final String EXPECTED_ID = "MY_ID";
    public static final String EXPECTED_NAME = "ANY_POI";
    public static final String EXPECTED_DESCRIPTION = "ANY_DESCRIPTION";
    public static final Date EXPECTED_DATE = new Date();
    public static final CoordinatesDto EXPECTED_COORDINATE = new CoordinatesDto(44.436084, 11.315620, 250.0);
    public static final List<String> EXPECTED_MICRO_TYPES = Arrays.asList("minorType1", "minorType2");
    public static final PoiMacroType EXPECTED_MACRO_TYPE = PoiMacroType.BELVEDERE;
    public static final List<String> EXPECTED_EXTERNAL_RESOURCES = Arrays.asList("http://externalresource.com", "http://externalresource2.com");
    public static final List<LinkedMediaDto> EXPECTED_MEDIA_IDS = Collections.emptyList();
    public static final List<String> EXPECTED_TAGS = Arrays.asList("poiType", "poiType2");
    public static final KeyValueDto EXPECTED_KEYVAL = new KeyValueDto("a", "b");
    public static final List<KeyValueDto> EXPECTED_KEY_VALS = Collections.singletonList(EXPECTED_KEYVAL);

    @Autowired
    private Datasource dataSource;
    @Autowired
    private AdminPoiController adminPoiController;
    @Autowired
    private POIController poiController;
    @Autowired
    private AdminPlaceController placeController;
    @Autowired
    private AdminTrailController adminTrailController;
    @Autowired
    private AuthFacade authHelper;

    private String importedTrailId;


    @Before
    public void setUp() {
        IntegrationUtils.clearCollections(dataSource);
        TrailImportDto trailImportDto = TrailImportRestIntegrationTest.createThreePointsTrailImport(placeController);
        TrailResponse trailResponse = adminTrailController.importTrail(trailImportDto);
        importedTrailId = trailResponse.getContent().get(0).getId();
        adminPoiController.create(new PoiDto(EXPECTED_ID, EXPECTED_NAME, EXPECTED_DESCRIPTION,
                EXPECTED_TAGS, EXPECTED_MACRO_TYPE,
                EXPECTED_MICRO_TYPES,
                EXPECTED_MEDIA_IDS, Collections.singletonList(importedTrailId),
                EXPECTED_COORDINATE,
                EXPECTED_EXTERNAL_RESOURCES,
                EXPECTED_KEY_VALS, new RecordDetailsDto(new Date(),
                "AnyUser", "SeC-Bo-123", authHelper.getAuthHelper().getRealm()),
                "", ""));
    }

    @Test
    public void getById_shouldFindOne() {
        PoiResponse getPoi = poiController.get(EXPECTED_ID);
        PoiDto firstElement = getPoi.getContent().get(0);
        assertThat(getPoi.getContent().size()).isEqualTo(1);
        assertGetFirstElement(firstElement);
    }

    @Test
    public void getByRealm_shouldFindOne() {
        PoiResponse getPoi = poiController.get(0, 1, authHelper.getAuthHelper().getRealm());
        PoiDto firstElement = getPoi.getContent().get(0);
        assertThat(getPoi.getContent().size()).isEqualTo(1);
        assertGetFirstElement(firstElement);
    }

    @Test
    public void getByAnotherRealm_shouldFindOne() {
        // when
        PoiResponse getPoi = poiController.get(0, 1, "AnotherS&CRealm");
        List<PoiDto> responseContent = getPoi.getContent();

        // then
        assertThat(responseContent).isEmpty();
    }

    @Test
    public void getByUnknownRealm_shouldReturnEmpty() {
        PoiResponse getPoi = poiController.get(0, 1, "Any other sec realm");
        assertThat(getPoi.getContent()).isEmpty();
    }

    @Test
    public void getAllPaged_shouldFindOne() {
        PoiResponse getPoi = poiController.get(0, 1, NO_FILTERING_TOKEN);
        PoiDto firstElement = getPoi.getContent().get(0);
        assertThat(getPoi.getContent().size()).isEqualTo(1);
        assertGetFirstElement(firstElement);
    }

    @Test
    public void getByMacro_shouldFindOne() {
        PoiResponse getPoi = poiController.getByMacro(EXPECTED_MACRO_TYPE.toString(), 0, 1);
        PoiDto firstElement = getPoi.getContent().get(0);
        assertThat(getPoi.getContent().size()).isEqualTo(1);
        assertGetFirstElement(firstElement);
    }

    @Test
    public void getTrailByTrailId_shouldFindOne() {
        PoiResponse getPoi = poiController.getByTrail(importedTrailId, 0, 1);
        PoiDto firstElement = getPoi.getContent().get(0);
        assertThat(getPoi.getContent().size()).isEqualTo(1);
        assertGetFirstElement(firstElement);
    }

    @Test
    public void getTrailLikeName_shouldFind() {
        PoiResponse getPoi = poiController.getByNameOrTags("poiType", 0, 1);
        PoiDto firstElement = getPoi.getContent().get(0);
        assertThat(getPoi.getContent().size()).isEqualTo(1);
        assertGetFirstElement(firstElement);
    }

    @Test
    public void getTrailByNonExistingTrailId_shouldNotFindAny() {
        PoiResponse getPoi = poiController.getByTrail("100BO_NOT_EXISTING", 0, 1);
        Assert.assertTrue(getPoi.getContent().isEmpty());
    }

    @Test
    public void getTrailByNonExistingMacro_shouldNotFindAny() {
        PoiResponse getPoi = poiController.getByMacro(PoiMacroType.CULTURAL.toString(), 0, 1);
        Assert.assertTrue(getPoi.getContent().isEmpty());
    }

    @Test
    public void getTrails_shouldFindTwo() {
        String anyOtherName = "ANY_OTHER_NAME";
        String anyOtherId = "ANY_OTHER_ID";

        adminPoiController.create(new PoiDto(anyOtherId, anyOtherName, EXPECTED_DESCRIPTION,
                EXPECTED_TAGS, EXPECTED_MACRO_TYPE,
                EXPECTED_MICRO_TYPES,
                EXPECTED_MEDIA_IDS, Collections.singletonList(importedTrailId),
                EXPECTED_COORDINATE,
                EXPECTED_EXTERNAL_RESOURCES, EXPECTED_KEY_VALS, null, "" ,""));

        PoiResponse getPoi = poiController.get(0, 3, NO_FILTERING_TOKEN);
        PoiDto firstElement = getPoi.getContent().get(0);
        assertGetFirstElement(firstElement);
        PoiDto secondElement = getPoi.getContent().get(1);
        assertThat(secondElement.getId()).isEqualTo(anyOtherId);
        assertThat(secondElement.getName()).isEqualTo(anyOtherName);
    }

    @Test
    public void shouldUpdateKeyVals() {
        String anyOtherName = "ANY_OTHER_NAME";
        String anyOtherId = "ANY_OTHER_ID";
        KeyValueDto anyOtherKeyVal = new KeyValueDto("age", "3");

        List<KeyValueDto> expectedKeyVals = Arrays.asList(EXPECTED_KEYVAL, anyOtherKeyVal);

        adminPoiController.create(new PoiDto(anyOtherId, anyOtherName, EXPECTED_DESCRIPTION,
                EXPECTED_TAGS, EXPECTED_MACRO_TYPE,
                EXPECTED_MICRO_TYPES,
                EXPECTED_MEDIA_IDS, Collections.singletonList(importedTrailId),
                EXPECTED_COORDINATE,
                EXPECTED_EXTERNAL_RESOURCES, expectedKeyVals, null, "", ""));

        PoiResponse getPoi = poiController.get(0, 3, NO_FILTERING_TOKEN);
        PoiDto firstElement = getPoi.getContent().get(0);
        assertGetFirstElement(firstElement);
        PoiDto secondElement = getPoi.getContent().get(1);
        assertThat(secondElement.getKeyVal().get(0)).isEqualTo(EXPECTED_KEYVAL);
        assertThat(secondElement.getKeyVal().get(1)).isEqualTo(anyOtherKeyVal);

        adminPoiController.create(new PoiDto(anyOtherId, anyOtherName, EXPECTED_DESCRIPTION,
                EXPECTED_TAGS, EXPECTED_MACRO_TYPE,
                EXPECTED_MICRO_TYPES,
                EXPECTED_MEDIA_IDS, Collections.singletonList(importedTrailId),
                EXPECTED_COORDINATE,
                EXPECTED_EXTERNAL_RESOURCES, EXPECTED_KEY_VALS, null, "", ""));

        PoiResponse getAgainPoi = poiController.get(0, 3, NO_FILTERING_TOKEN);

        PoiDto actual = getAgainPoi.getContent()
                .stream()
                .filter(poiDto -> poiDto.getId().equals(anyOtherId)).findFirst().get();
        assertThat(actual.getKeyVal().size()).isEqualTo(1);
        assertThat(actual.getKeyVal().get(0)).isEqualTo(EXPECTED_KEYVAL);


        PoiDto actualElement = getPoi.getContent().get(0);
        assertGetFirstElement(actualElement);

    }

    @Test
    public void afterSomeKeyValuesAreAlreadyPresent_shouldUpdateKeyValsWithOneOnly() {
        String anyOtherName = "ANY_OTHER_NAME";
        String anyOtherId = "ANY_OTHER_ID";
        KeyValueDto anyOtherKeyVal = new KeyValueDto("age", "3");

        List<KeyValueDto> expectedKeyVals = Arrays.asList(EXPECTED_KEYVAL, anyOtherKeyVal);

        adminPoiController.create(new PoiDto(anyOtherId, anyOtherName, EXPECTED_DESCRIPTION,
                EXPECTED_TAGS, EXPECTED_MACRO_TYPE,
                EXPECTED_MICRO_TYPES,
                EXPECTED_MEDIA_IDS, Collections.singletonList(importedTrailId),
                EXPECTED_COORDINATE,
                EXPECTED_EXTERNAL_RESOURCES, expectedKeyVals, null, "", ""));

        PoiResponse getPoi = poiController.get(0, 3, NO_FILTERING_TOKEN);
        PoiDto firstElement = getPoi.getContent().get(0);
        assertGetFirstElement(firstElement);
        PoiDto secondElement = getPoi.getContent().get(1);
        assertThat(secondElement.getKeyVal().get(0)).isEqualTo(EXPECTED_KEYVAL);
        assertThat(secondElement.getKeyVal().get(1)).isEqualTo(anyOtherKeyVal);
    }

    @Test
    public void delete() {
        PoiResponse getPoi = poiController.get(EXPECTED_ID);
        assertThat(getPoi.getContent().get(0).getId()).isEqualTo(EXPECTED_ID);

        adminPoiController.deletePoi(EXPECTED_ID);
        PoiResponse poiResponse = poiController.get(EXPECTED_ID);
        assertThat(poiResponse.getContent().size()).isEqualTo(0);
    }

    @Test
    public void uponTrailDeletionPoiReferencesShallBeRemoved() {
        PoiResponse getPoiFirstCall = poiController.get(EXPECTED_ID);
        assertThat(getPoiFirstCall.getContent().get(0).getTrailIds()).contains(importedTrailId);

        adminTrailController.deleteById(importedTrailId);
        PoiResponse result = poiController.get(EXPECTED_ID);
        assertThat(result.getContent().get(0).getTrailIds()).isEmpty();
    }

    @Test
    public void contextLoads() {
        assertThat(adminPoiController).isNotNull();
    }

    @After
    public void setDown() {
        IntegrationUtils.emptyCollection(dataSource, Poi.COLLECTION_NAME);
    }

    private void assertGetFirstElement(PoiDto firstElement) {
        assertThat(firstElement.getId()).isEqualTo(EXPECTED_ID);
        assertThat(firstElement.getName()).isEqualTo(EXPECTED_NAME);
        assertThat(firstElement.getCoordinates()).isEqualTo(EXPECTED_COORDINATE);
        assertThat(firstElement.getMacroType()).isEqualTo(EXPECTED_MACRO_TYPE);
        assertThat(firstElement.getMicroType()).isEqualTo(EXPECTED_MICRO_TYPES);
        assertThat(firstElement.getExternalResources()).isEqualTo(EXPECTED_EXTERNAL_RESOURCES);
        assertThat(firstElement.getTags()).isEqualTo(EXPECTED_TAGS);
        assertThat(firstElement.getMediaList()).isEqualTo(EXPECTED_MEDIA_IDS);
        assertThat(firstElement.getTrailIds()).isEqualTo(Collections.singletonList(importedTrailId));
    }
}