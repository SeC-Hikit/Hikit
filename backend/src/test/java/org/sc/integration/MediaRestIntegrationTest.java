package org.sc.integration;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sc.common.rest.*;
import org.sc.common.rest.response.MediaResponse;
import org.sc.common.rest.response.PlaceResponse;
import org.sc.common.rest.response.PoiResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.DataSource;
import org.sc.controller.*;
import org.sc.controller.admin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.sc.integration.ImportTrailIT.CORRECT_PLACE_DTO;
import static org.sc.integration.PoiRestIntegrationTest.*;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class MediaRestIntegrationTest  {

    public static final String FILE_NAME = "sec_map.png";

    @Autowired DataSource dataSource;
    @Autowired AdminMediaController adminMediaController;
    @Autowired MediaController mediaController;
    @Autowired POIController poiController;
    @Autowired AdminPoiController adminPoiController;
    @Autowired PlaceController placeController;
    @Autowired AdminPlaceController adminPlaceController;
    @Autowired AdminTrailImporterController importController;
    @Autowired AdminTrailController trailController;

    public TrailImportDto expectedTrailDto;
    private TrailResponse trailResponse;
    private String trailId;

    @Before
    public void setUp(){
        IntegrationUtils.clearCollections(dataSource);
        TrailImportDto trailImportDto = TrailImportRestIntegrationTest.createTrailImport(adminPlaceController);
        trailResponse = trailController.importTrail(trailImportDto);
        trailId = trailResponse.getContent().get(0).getId();
    }


    @Test
    public void shallAddAndRemoveMediaFromPlace() throws IOException {
        PlaceResponse placeResponse = adminPlaceController.create(CORRECT_PLACE_DTO);
        String placeId = placeResponse.getContent().get(0).getId();
        final String uploadId = createAndVerifyCreationById();

        adminPlaceController.addMedia(placeId, new LinkedMediaDto(uploadId, "", Collections.emptyList()));
        placeId = placeResponse.getContent().get(0).getId();

        placeResponse = placeController.get(placeId);
        assertThat(placeResponse.getContent().get(0).getMediaIds().contains(uploadId)).isTrue();
        adminPlaceController.deleteMedia(placeId, new UnLinkeMediaRequestDto(uploadId));
        placeResponse = placeController.get(placeId);
        assertThat(placeResponse.getContent().get(0).getMediaIds().contains(uploadId)).isFalse();
    }

    @Test
    public void shallCreateOneAndGetItBack() throws IOException {
        MediaResponse mediaResponse = uploadValidMedia();

        assertThat(mediaResponse.getStatus()).isEqualTo(Status.OK);

        final String uploadId = mediaResponse.getContent().get(0).getId();
        MediaResponse response = mediaController.getById(uploadId);

        assertThat(response.getStatus()).isEqualTo(Status.OK);

        List<MediaDto> mediaContent = response.getContent();
        assertThat(mediaContent.size()).isEqualTo(1);

        MediaDto firstResult = mediaContent.get(0);
        assertThat(firstResult.getId()).isEqualTo(uploadId);
        assertThat(firstResult.getMime()).isEqualTo("image/png");
        assertThat(firstResult.getFileSize()).isEqualTo(682479);
    }

    @Test
    public void shallCreateOneAndDeleteItWithoutFindingItBack() throws IOException {
        final String uploadId = createAndVerifyCreationById();
        MediaResponse deleteResponse = adminMediaController.deleteById(uploadId);
        assertThat(deleteResponse.getStatus()).isEqualTo(Status.OK);
        MediaResponse newCallWithNoResult = mediaController.getById(uploadId);
        assertThat(newCallWithNoResult.getContent().size()).isEqualTo(0);
    }

    @Test
    public void shallCreateOneAndAddConnectionWithTrail() throws IOException {
        String mediaID = createAndVerifyCreationById();

        String aDescription = "A landscape";
        KeyValueDto savedKeyValue = new KeyValueDto("a", "b");

        TrailResponse trailResponse = trailController.addMediaToTrail(trailId, new LinkedMediaDto(mediaID, aDescription, Collections.singletonList(savedKeyValue)));

        List<LinkedMediaDto> mediaListOfTrail = trailResponse.getContent().get(0).getMediaList();
        assertThat(mediaListOfTrail.size()).isEqualTo(1);
        LinkedMediaDto getFirstOccurence = mediaListOfTrail.get(0);
        assertThat(getFirstOccurence.getId()).isEqualTo(mediaID);
        assertThat(getFirstOccurence.getDescription()).isEqualTo(aDescription);
        assertThat(getFirstOccurence.getKeyVal().get(0)).isEqualTo(savedKeyValue);
    }

    @Test
    public void shallAddAndRemoveConnectionToTrail() throws IOException {
        String mediaID = createAndVerifyCreationById();
        String aDescription = "A landscape";
        KeyValueDto savedKeyValue = new KeyValueDto("a", "b");
        TrailResponse trailResponse = trailController.addMediaToTrail(trailId, new LinkedMediaDto(mediaID, aDescription, Collections.singletonList(savedKeyValue)));
        TrailResponse trailWithRemovedMedia = trailController.removeMediaFromTrail(trailId, new UnLinkeMediaRequestDto(mediaID));
        assertThat(trailResponse.getContent().get(0).getMediaList().size()).isEqualTo(1);
        assertThat(trailWithRemovedMedia.getContent().get(0).getMediaList().size()).isEqualTo(0);
    }

    @Test
    public void shallAddAndRemoveConnectionToPoi() throws IOException {
        String mediaID = createAndVerifyCreationById();
        String aDescription = "A landscape";
        KeyValueDto savedKeyValue = new KeyValueDto("a", "b");
        adminPoiController.create(new PoiDto(EXPECTED_ID, EXPECTED_NAME, EXPECTED_DESCRIPTION,
                EXPECTED_TAGS, EXPECTED_MACRO_TYPE,
                EXPECTED_MICRO_TYPES,
                EXPECTED_MEDIA_IDS, EXPECTED_TRAIL_IDS,
                EXPECTED_COORDINATE, EXPECTED_DATE, EXPECTED_DATE,
                EXPECTED_EXTERNAL_RESOURCES, EXPECTED_KEY_VALS, null));

        List<KeyValueDto> expectedKeyVal = Collections.singletonList(savedKeyValue);
        adminPoiController.addMediaToPoi(EXPECTED_ID, new LinkedMediaDto(mediaID, aDescription, expectedKeyVal));
        PoiResponse poiResponse = poiController.get(EXPECTED_ID);

        LinkedMediaDto linkedMediaDtoInRetrievedPoi = poiResponse.getContent().get(0).getMediaList().get(0);
        assertThat(linkedMediaDtoInRetrievedPoi.getId()).isEqualTo(mediaID);
        assertThat(linkedMediaDtoInRetrievedPoi.getKeyVal()).isEqualTo(expectedKeyVal);
        assertThat(linkedMediaDtoInRetrievedPoi.getDescription()).isEqualTo(aDescription);

        PoiResponse poiResponseAfterRemoval = adminPoiController.removeMediaFromPoi(EXPECTED_ID, new UnLinkeMediaRequestDto(mediaID));
        PoiResponse laterReadResponse = poiController.get(EXPECTED_ID);

        List<LinkedMediaDto> poiResponseAfterRemovalMediaList = poiResponseAfterRemoval.getContent().get(0).getMediaList();
        List<LinkedMediaDto> laterReadResponseMediaList = laterReadResponse.getContent().get(0).getMediaList();

        assertTrue(poiResponseAfterRemovalMediaList.isEmpty());
        assertTrue(laterReadResponseMediaList.isEmpty());
    }

    private String createAndVerifyCreationById() throws IOException {
        MediaResponse mediaResponse = uploadValidMedia();
        assertThat(mediaResponse.getStatus()).isEqualTo(Status.OK);
        final String uploadId = mediaResponse.getContent().get(0).getId();
        final MediaResponse response = mediaController.getById(uploadId);
        assertThat(response.getStatus()).isEqualTo(Status.OK);
        return uploadId;
    }

    private MediaResponse uploadValidMedia() throws IOException {
        return adminMediaController.upload(
                new MockMultipartFile("file", FILE_NAME, "multipart/form-data",
                        getClass().getClassLoader().getResourceAsStream("media" + File.separator + FILE_NAME)
                )
        );
    }
}
