package net.sharetrip.tracker.model

import net.sharetrip.shared.utils.parseTextAfterColon
import com.squareup.moshi.Json

data class Restrictions(
    @field:Json(name = "contact_tracing")
        val restrictionDetails: RestrictionDetails?,

    @field:Json(name = "international_travel_controls")
        val internationalTravelControls: RestrictionDetails? = null,

    @field:Json(name = "cancel_public_events")
        val cancelPublicEvents: RestrictionDetails? = null,

    @field:Json(name = "school_closing")
        val schoolClosing: RestrictionDetails? = null,

    @field:Json(name = "restrictions_on_internal_movement")
        val restrictionsOnInternalMovement: RestrictionDetails? = null,

    @field:Json(name = "restrictions_on_gatherings")
        val restrictionsOnGatherings: RestrictionDetails? = null,

    @field:Json(name = "workplace_closing")
        val workplaceClosing: RestrictionDetails? = null,

    @field:Json(name = "testing_policy")
        val testingPolicy: RestrictionDetails? = null,

    @field:Json(name = "close_public_transport")
        val closePublicTransport: RestrictionDetails? = null,

    @field:Json(name = "stay_at_home_requirements")
        val stayAtHomeRequirements: RestrictionDetails? = null
) {

    fun getAllRestrictions(): ArrayList<String> {
        val listOfRestriction = ArrayList<String>()

        if (restrictionDetails != null) {
            listOfRestriction.add(restrictionDetails.levelDesc.parseTextAfterColon())
        }
        if (internationalTravelControls != null) {
            listOfRestriction.add(internationalTravelControls.levelDesc.parseTextAfterColon())
        }
        if (cancelPublicEvents != null) {
            listOfRestriction.add(cancelPublicEvents.levelDesc.parseTextAfterColon())
        }
        if (restrictionsOnGatherings != null) {
            listOfRestriction.add(restrictionsOnGatherings.levelDesc.parseTextAfterColon())
        }
        if (workplaceClosing != null) {
            listOfRestriction.add(workplaceClosing.levelDesc.parseTextAfterColon())
        }
        if (testingPolicy != null) {
            listOfRestriction.add(testingPolicy.levelDesc.parseTextAfterColon())
        }
        if (closePublicTransport != null) {
            listOfRestriction.add(closePublicTransport.levelDesc.parseTextAfterColon())
        }
        if (stayAtHomeRequirements != null) {
            listOfRestriction.add(stayAtHomeRequirements.levelDesc.parseTextAfterColon())
        }
        return listOfRestriction
    }
}
