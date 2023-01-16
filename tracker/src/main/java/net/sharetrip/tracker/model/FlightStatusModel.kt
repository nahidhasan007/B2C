package net.sharetrip.tracker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import javax.inject.Named

@Parcelize
data class FlightStatusModel (
    var request: FlightStatusRequest?,
    var appendix: FlightStatusAppendix?,
    var flightStatuses: List<FlightStatusData>?
) : Parcelable

// MARK: - Request
@Parcelize
data class FlightStatusRequest (
    val airline: RequestAirline?,
    val flight: FlightInfo?,
    val utc: UTC?,
    val url: String?,
    val nonstopOnly: NonstopOnly?,
    val date: DateClass?
) : Parcelable

@Parcelize
data class RequestAirline(
    val fsCode: String?,
    val requestedCode: String?
) : Parcelable

@Parcelize
data class FlightInfo (
    val requested: String?,
    val interpreted: String?
) : Parcelable

@Parcelize
data class UTC(
    val requested: String?,
    val interpreted: Boolean?
) : Parcelable

@Parcelize
data class NonstopOnly (
    val  interpreted: Boolean?
) : Parcelable

@Parcelize
data class DateClass (
    val year: String?,
    val month: String?,
    val day: String?,
    val interpreted: String?
) : Parcelable

// MARK: - Appendix
@Parcelize
data class FlightStatusAppendix(
    val airlines: List<AirlineElement>?,
    val airports: List<AirportInfoDetails>?,
    val equipments: List<Equipment>?
) : Parcelable

@Parcelize
data class AirlineElement (
    val fs: String?,
    val iata: String?,
    val icao: String?,
    val name: String?,
    val phoneNumber: String?,
    val active: Boolean?,
    val category: String?
) : Parcelable

@Parcelize
data class AirportInfoDetails (
    val fs: String?,
    val iata: String?,
    val icao: String?,
    val faa: String?,

    val name: String?,
    val street1: String?,
    val street2: String?,
    val city: String?,

    val cityCode: String?,
    val district: String?,
    val stateCode: String?,
    val postalCode: String?,

    val countryCode: String?,
    val countryName: String?,
    val regionName: String?,
    val timeZoneRegionName: String?,

    val weatherZone: String?,
    val localTime: String?,
    val weatherUrl: String?,
    val delayIndexUrl: String?,

    val utcOffsetHours: Double,
    val latitude: Double?,
    val longitude: Double?,

    val elevationFeet: Int?,
    val classification: Int?,

    val active: Boolean?
) : Parcelable
@Parcelize
data class Equipment (
    val iata: String?,
    val name: String?,
    val turboProp: Boolean?,
    val jet: Boolean?,
    val widebody: Boolean?,
    val regional: Boolean?
) : Parcelable

// MARK: - FlightStatus
@Parcelize
data class FlightStatusData (
    val flightId: Int,
    val carrier: AirlineElement?,
    val departureAirport: AirportInfoDetails?,
    val arrivalAirport: AirportInfoDetails?,
    val carrierFSCode: String?,
    val flightNumber: String?,
    val departureAirportFsCode: String?,
    val arrivalAirportFsCode: String?,
    val departureDate: ArrivalDate,
    val arrivalDate: ArrivalDate,
    val status: String?,
    val schedule: FlightScheduleInfo?,
    val operationalTimes: OperationalTimes,
    @field:Named("codeshares")
    val codeShares: List<CodeShare>,
    val delays: Delays?,
    val flightDurations: FlightDurations?,
    val airportResources: AirportResources?,
    val flightEquipment: FlightEquipment?,
    val flightStatusUpdates: List<FlightStatusUpdate>,
    val irregularOperations: List<IrregularOperation>,
    val operatingCarrier: AirlineElement?,
    val operatingCarrierFsCode: String?,
    val primaryCarrier: AirlineElement?,
    val primaryCarrierFsCode: String?,
    val confirmedIncident: ConfirmedIncident?,
    val lastDataAcquiredDate: String?
) : Parcelable
@Parcelize
data class AirportResources (
    val departureTerminal: String?,
    val departureGate: String?,
    val arrivalTerminal: String?,
    val arrivalGate: String?,
    val baggage: String?
) : Parcelable

@Parcelize
data class ArrivalDate (
    val dateUtc: String,
    val dateLocal: String
) : Parcelable
@Parcelize
data class CodeShare (
    val carrier: AirlineElement?,
    val fsCode: String?,
    val flightNumber:String?,
    val relationship: String?
) : Parcelable

@Parcelize
data class ConfirmedIncident (
    val publishedDate: String?,
    val message: String?
) : Parcelable

@Parcelize
data class Delays (
    val departureGateDelayMinutes: Int,
    val departureRunwayDelayMinutes: Int,
    val arrivalGateDelayMinutes: Int,
    val arrivalRunwayDelayMinutes: Int
) : Parcelable

@Parcelize
data class FlightDurations (
    val scheduledBlockMinutes: Int?,
    val blockMinutes: Int?,
    val scheduledAirMinutes: Int?,
    val airMinutes: Int?,
    val scheduledTaxiOutMinutes: Int?,
    val taxiOutMinutes: Int?,
    val scheduledTaxiInMinutes: Int?,
    val taxiInMinutes: Int?
) : Parcelable

@Parcelize
data class FlightEquipment (
    val scheduledEquipment: Equipment?,
    val actualEquipment: Equipment?,
    val scheduledEquipmentIataCode: String?,
    val actualEquipmentIataCode: String?,
    val tailNumber: String?
) : Parcelable

@Parcelize
data class FlightStatusUpdate (
    val updatedAt: ArrivalDate?,
    val source: String?,
    val updatedTextFields: List<UpdatedTextField>,
    val updatedDateField: List<UpdatedDateField>
) : Parcelable

@Parcelize
data class UpdatedDateField (
    val field: String?,
    val originalDateLocal: String?,
    val newDateLocal: String?
) : Parcelable

@Parcelize
data class UpdatedTextField (
    val field: String?,
    val originalText: String?,
    val newText: String?
) : Parcelable

@Parcelize
data class IrregularOperation (
    val type: String?,
    val newArrivalAirportFsCode: String?,
    val dateUtc: String?,
    val message: String?,
    val relatedFlightId: Int?
) : Parcelable

@Parcelize
data class FlightScheduleInfo (
    val flightType: String?,
    val serviceClasses: String?,
    val restrictions: String?,
    val uplines: List<UpLine>,
    val downlines: List<DownLine>
) : Parcelable
@Parcelize
data class DownLine(
    val arrivalAirport: String?,
    val fsCode: String?,
    val flightId: Int?
) : Parcelable

@Parcelize
data class UpLine(
    val departureAirport: String?,
    val fsCode: String?,
    val flightId: Int?
) : Parcelable

@Parcelize
data class OperationalTimes (
    val publishedDeparture : ArrivalDate?,
    val publishedArrival : ArrivalDate?,
    val scheduledGateDeparture : ArrivalDate,
    val scheduledRunwayDeparture : ArrivalDate?,
    val estimatedGateDeparture : ArrivalDate?,
    val actualGateDeparture : ArrivalDate?,
    val flightPlanPlannedDeparture : ArrivalDate?,
    val estimatedRunwayDeparture : ArrivalDate?,
    val actualRunwayDeparture : ArrivalDate?,
    val scheduledRunwayArrival : ArrivalDate?,
    val scheduledGateArrival : ArrivalDate,
    val estimatedGateArrival : ArrivalDate?,
    val actualGateArrival : ArrivalDate?,
    val flightPlanPlannedArrival : ArrivalDate?,
    val estimatedRunwayArrival : ArrivalDate?,
    val actualRunwayArrival : ArrivalDate?
) : Parcelable
