package org.sc.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.sc.common.rest.*
import org.sc.common.rest.response.TrailRawResponse
import org.sc.configuration.AppProperties
import org.sc.configuration.MongoDataSource
import org.sc.controller.PlaceController
import org.sc.controller.TrailController
import org.sc.controller.admin.AdminPlaceController
import org.sc.controller.admin.AdminTrailController
import org.sc.controller.admin.AdminTrailImporterController
import org.sc.data.model.TrailClassification
import org.sc.data.model.TrailStatus
import org.sc.data.repository.PlaceDAO
import org.sc.job.PlaceClusteringJob
import org.sc.processor.TrailSimplifierLevel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.*


@RunWith(SpringRunner::class)
@SpringBootTest
@ActiveProfiles("test")
class PlaceClusteringJobIntegrationTest {

    @Autowired private lateinit var adminPlaceController: AdminPlaceController
    @Autowired private lateinit var adminTrailController: AdminTrailController
    @Autowired private lateinit var trailController: TrailController
    @Autowired private lateinit var importerController: AdminTrailImporterController
    @Autowired private lateinit var dataSource: MongoDataSource
    @Autowired private lateinit var placeClusteringJob: PlaceClusteringJob
    @Autowired private lateinit var placeController: PlaceController
    @Autowired private lateinit var placeDao: PlaceDAO
    @Autowired private lateinit var appProperties: AppProperties

    companion object {
        const val TRAIL_001_IMPORT_FILENAME = "001BO.gpx"
        const val TRAIL_031_IMPORT_FILENAME = "031BO.gpx"
        const val TRAIL_029_IMPORT_FILENAME = "029BO.gpx"
    }

    private var castiglionePlacesCoordinates = mutableListOf<CoordinatesDto>()
    private var castiglionePlaceIds = mutableListOf<String>()

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
        val allTrailsId = trailController.get(0, Int.MAX_VALUE, appProperties.instanceRealm, TrailSimplifierLevel.LOW, true).content.map { it.id }
        // given
        val trail029Import = ImportTrailIT.importRawTrail(importerController, TRAIL_029_IMPORT_FILENAME, this.javaClass)
        val firstCoordinateDto = CoordinatesDto(
                trail029Import.content.first().coordinates.first().latitude,
                trail029Import.content.first().coordinates.first().longitude)
        val lastCoordinateDto = CoordinatesDto(
                trail029Import.content.first().coordinates.last().latitude,
                trail029Import.content.first().coordinates.last().longitude)

        castiglionePlacesCoordinates.add(lastCoordinateDto)

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

        val placesBeforeConsistencyJob = placeController.getLikeNameOrTags("Castiglione", 0, Integer.MAX_VALUE, "*")
        assertThat(placesBeforeConsistencyJob.content).asList().hasSize(3)

        // when - 1500m boundary (set in application.properties)
        placeClusteringJob.ensurePlacesConsistency()

        // then
        val placeAfterConsistencyJob = placeController.getLikeNameOrTags("Castiglione", 0, Integer.MAX_VALUE, "*")
        assertThat(placeAfterConsistencyJob.content).asList().hasSize(1)
        val placeEntityExpected = placeDao.getById(placeAfterConsistencyJob.content.first().id).first()
        val castiglioneCoordinates: List<List<Double>> = castiglionePlacesCoordinates.map { cpc -> listOf(cpc.longitude, cpc.latitude) }
        assertThat(placeEntityExpected.points.coordinates2D).asList().containsAll(castiglioneCoordinates)
        assertThat(placeEntityExpected.crossingTrailIds).asList().containsAll(allTrailsId)
        ensureNoOrphanPlaceIdsAreLeftOnDb()
    }

    private fun ensureNoOrphanPlaceIdsAreLeftOnDb() {
        val allTrails = trailController.get(0, Int.MAX_VALUE, appProperties.instanceRealm, TrailSimplifierLevel.LOW, true);
        val notFoundResultsIds: List<Boolean> = allTrails.content.flatMap { it.locations.map { place -> !castiglionePlaceIds.contains(place.placeId) } }
        assertThat(!notFoundResultsIds.contains(false));
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

        castiglionePlacesCoordinates.add(lastCoordinateDto)
        castiglionePlaceIds.add(lastPlaceImported.content.first().id)
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

        castiglionePlacesCoordinates.add(firstCoordinateDto)
        castiglionePlaceIds.add(firstPlaceImported.content.first().id)
    }
}