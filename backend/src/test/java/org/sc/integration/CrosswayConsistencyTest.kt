package org.sc.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.sc.common.rest.*
import org.sc.common.rest.response.TrailRawResponse
import org.sc.configuration.DataSource
import org.sc.controller.PlaceController
import org.sc.controller.admin.AdminPlaceController
import org.sc.controller.admin.AdminTrailController
import org.sc.controller.admin.AdminTrailImporterController
import org.sc.data.model.TrailClassification
import org.sc.data.model.TrailStatus
import org.sc.job.DynamicCrosswayConsistencyJob
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.*


@RunWith(SpringRunner::class)
@SpringBootTest
@ActiveProfiles("test")
class CrosswayConsistencyTest {

    @Autowired private lateinit var adminPlaceController: AdminPlaceController
    @Autowired private lateinit var adminTrailController: AdminTrailController
    @Autowired private lateinit var importerController: AdminTrailImporterController
    @Autowired private lateinit var dataSource: DataSource
    @Autowired private lateinit var dynamicCrosswayConsistencyJob: DynamicCrosswayConsistencyJob
    @Autowired private lateinit var placeController: PlaceController

    companion object {
        const val TRAIL_001_IMPORT_FILENAME = "001BO.gpx"
        const val TRAIL_031_IMPORT_FILENAME = "031BO.gpx"
        const val TRAIL_029_IMPORT_FILENAME = "029BO.gpx"
    }

    @Before
    fun setUp() {
        IntegrationUtils.clearCollections(dataSource)
        val trail001Import = ImportTrailIT.importRawTrail(importerController, TRAIL_001_IMPORT_FILENAME, this.javaClass)
        importFirstTrail(trail001Import)
        val trail031BoImport = ImportTrailIT.importRawTrail(importerController, TRAIL_031_IMPORT_FILENAME, this.javaClass)
        importSecondTrail(trail031BoImport)
    }

    @Test
    fun `on importing a third trail and running consistency job, should ensure consistency`() {
        // given
        val trail029Import = ImportTrailIT.importRawTrail(importerController, TRAIL_029_IMPORT_FILENAME, this.javaClass)
        val firstCoordinateDto = CoordinatesDto(
                trail029Import.content.first().coordinates.first().latitude,
                trail029Import.content.first().coordinates.first().longitude)
        val lastCoordinateDto = CoordinatesDto(
                trail029Import.content.first().coordinates.last().latitude,
                trail029Import.content.first().coordinates.last().longitude)

        val firstPlaceImported = adminPlaceController.create(PlaceDto("", "Lagaro",
                "A description", emptyList(), emptyList(), listOf(firstCoordinateDto), emptyList(), false,
                RecordDetailsDto(Date(), "test", "test", "test")))
        val lastPlaceImported = adminPlaceController.create(PlaceDto("", "Castiglione dei Pepoli",
                "Another description", emptyList(), emptyList(), listOf(lastCoordinateDto), emptyList(), false,
                RecordDetailsDto(Date(), "test", "test", "test")))

        val firstPlaceRef = PlaceRefDto("Lagaro",
                firstCoordinateDto,
                firstPlaceImported.content.first().id,
                emptyList(), false)
        val lastPlaceRefDto = PlaceRefDto("Castiglione dei Pepoli", lastCoordinateDto,
                lastPlaceImported.content.first().id,
                emptyList(), false)

        adminTrailController.importTrail(
                TrailImportDto("001", "", "A description", 5,
                        firstPlaceRef,
                        lastPlaceRefDto, listOf(firstPlaceRef, lastPlaceRefDto), emptyList(), TrailClassification.T,
                        "IT", trail029Import.content.first().coordinates, "Castiglione", false,
                        "Castiglione", emptyList(), Date(),
                        FileDetailsDto(Date(), "test", "test", "test", TRAIL_001_IMPORT_FILENAME,
                                TRAIL_029_IMPORT_FILENAME, "test"),
                        TrailStatus.PUBLIC))

        assertThat(placeController.getLikeNameOrTags("Castiglione", 0, Integer.MAX_VALUE, "*").content).asList().hasSize(3)

        // when - 1500m boundary (set in application.properties)
        dynamicCrosswayConsistencyJob.doEnsureDynamicCrosswayConsistency()

        // then
        assertThat(placeController.getLikeNameOrTags("Castiglione", 0, Integer.MAX_VALUE, "*").content).asList().hasSize(1)
    }


    private fun importSecondTrail(trail031BoImport: TrailRawResponse) {
        val firstCoordinateDto = CoordinatesDto(
                trail031BoImport.content.first().coordinates.first().latitude,
                trail031BoImport.content.first().coordinates.first().longitude)
        val lastCoordinateDto = CoordinatesDto(
                trail031BoImport.content.first().coordinates.last().latitude,
                trail031BoImport.content.first().coordinates.last().longitude)

        val firstPlaceImported = adminPlaceController.create(PlaceDto("", "Monte Fontanavidola",
                "A description", emptyList(), emptyList(), listOf(firstCoordinateDto), emptyList(),
                false,
                RecordDetailsDto(Date(), "test", "test", "test")))
        val lastPlaceImported = adminPlaceController.create(PlaceDto("", "Castiglione dei Pepoli",
                "Another description", emptyList(), emptyList(), listOf(lastCoordinateDto), emptyList(),
                false,
                RecordDetailsDto(Date(), "test", "test", "test")))

        val firstPlaceRef = PlaceRefDto("Monte Fontanavidola",
                firstCoordinateDto,
                firstPlaceImported.content.first().id,
                emptyList(), false)
        val lastPlaceRefDto = PlaceRefDto("Castiglione dei Pepoli", lastCoordinateDto,
                lastPlaceImported.content.first().id,
                emptyList(), false)

        adminTrailController.importTrail(
                TrailImportDto("001", "", "A description", 5,
                        firstPlaceRef,
                        lastPlaceRefDto, listOf(firstPlaceRef, lastPlaceRefDto), emptyList(), TrailClassification.T,
                        "IT", trail031BoImport.content.first().coordinates, "Castiglione", false,
                        "Castiglione", emptyList(), Date(),
                        FileDetailsDto(Date(), "test", "test", "test", TRAIL_001_IMPORT_FILENAME,
                                TRAIL_031_IMPORT_FILENAME, "test"),
                        TrailStatus.PUBLIC))
    }

    private fun importFirstTrail(trail001Import: TrailRawResponse) {
        val firstCoordinateDto = CoordinatesDto(
                trail001Import.content.first().coordinates.first().latitude,
                trail001Import.content.first().coordinates.first().longitude)
        val lastCoordinateDto = CoordinatesDto(
                trail001Import.content.last().coordinates.first().latitude,
                trail001Import.content.last().coordinates.first().longitude)

        val firstPlaceImported = adminPlaceController.create(PlaceDto("", "Castiglione dei Pepoli",
                "A description", emptyList(), emptyList(), listOf(firstCoordinateDto), emptyList(), false,
                RecordDetailsDto(Date(), "test", "test", "test")))
        val lastPlaceImported = adminPlaceController.create(PlaceDto("", "Percorso 135",
                "A description", emptyList(), emptyList(), listOf(lastCoordinateDto), emptyList(), false,
                RecordDetailsDto(Date(), "test", "test", "test")))

        val firstPlaceRef = PlaceRefDto("Castiglione dei Pepoli",
                firstCoordinateDto,
                firstPlaceImported.content.first().id,
                emptyList(), false)
        val lastPlaceRefDto = PlaceRefDto("Percorso 135", lastCoordinateDto,
                lastPlaceImported.content.first().id,
                emptyList(), false)

        adminTrailController.importTrail(
                TrailImportDto("001", "", "A description", 5,
                        firstPlaceRef,
                        lastPlaceRefDto, listOf(firstPlaceRef, lastPlaceRefDto), emptyList(), TrailClassification.T,
                        "IT", trail001Import.content.first().coordinates, "Castiglione", false,
                        "Castiglione", emptyList(), Date(),
                        FileDetailsDto(Date(), "test", "test", "test", TRAIL_001_IMPORT_FILENAME,
                                TRAIL_001_IMPORT_FILENAME, "test"),
                        TrailStatus.PUBLIC))
    }
}