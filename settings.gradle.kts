plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.10.1"
////                            # available:"0.11.0"
////                            # available:"0.20.0"
////                            # available:"0.21.0"
////                            # available:"0.22.0"
////                            # available:"0.23.0"
////                            # available:"0.30.0"
////                            # available:"0.30.1"
////                            # available:"0.30.2"
////                            # available:"0.40.0"
////                            # available:"0.40.1"
////                            # available:"0.40.2"
////                            # available:"0.50.0"
////                            # available:"0.50.1"
////                            # available:"0.50.2"
////                            # available:"0.51.0"


}

refreshVersions {
    enableBuildSrcLibs()
}

include(":base")
include(":flight")
include(":holiday")
include(":hotel")
include(":visa")
include(":bus")
include(":profile")
include(":tracker")
include(":sharetrip")
include(":signup")
include(":payment")
include(":tour")
include(":wheel")
include(":shared")
