package org.sc.configuration

import javax.inject.Inject
import javax.inject.Named

class AppProperties @Inject constructor(@Named(PORT_PROPERTY) val webPort: Int,
                                        @Named(GPX_FILE_FOLDER_PROPERTY) val pathToGpxDirectory: String,
                                        @Named(ALTITUDE_SERVICE_PORT_PROPERTY) val altitudeServicePort: Int,
                                        @Named(DB_PROPERTY_URI_PROPERTY) val dbUri: String,
                                        @Named(DB_NAME_PROPERTY) val dbName: String) {

    companion object {
        private const val PORT_PROPERTY = "web-port"
        private const val GPX_FILE_FOLDER_PROPERTY = "trail-storage-path"
        private const val ALTITUDE_SERVICE_PORT_PROPERTY = "altitude-service-port"
        private const val DB_PROPERTY_URI_PROPERTY = "mongo-uri"
        private const val DB_NAME_PROPERTY = "db"
    }

}