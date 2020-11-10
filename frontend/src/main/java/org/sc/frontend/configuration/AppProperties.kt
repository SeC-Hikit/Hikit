package org.sc.frontend.configuration

import javax.inject.Inject
import javax.inject.Named

class AppProperties @Inject constructor(@Named(PORT_PROPERTY) val webPort: Int,
                                        @Named(BACKEND_ADDRESS) val backendAddress: String) {

    companion object {
        private const val PORT_PROPERTY = "web-port"
        private const val BACKEND_ADDRESS = "backend-address"
    }

}