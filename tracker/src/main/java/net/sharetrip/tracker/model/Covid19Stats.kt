package net.sharetrip.tracker.model

import com.squareup.moshi.Json

data class Covid19Stats(
		val id: String? = null,
		val continent: String? = null,
		val date: String? = null,
		val country: String? = null,
		val lon: String? = null,
		val lat: String? = null,
		val population: Int? = null,

		@field:Json(name = "total_cases_per_million")
		val totalCasesPerMillion: Double? = null,

		@field:Json(name = "population_density")
		val populationDensity: Double? = null,

		@field:Json(name = "life_expectancy")
		val lifeExpectancy: Double? = null,

		@field:Json(name = "new_deaths_per_million")
		val newDeathsPerMillion: Double? = null,

		@field:Json(name = "new_cases_per_million")
		val newCasesPerMillion: Double? = null,

		@field:Json(name = "aged_65_older")
		val aged65Older: Double? = null,

		@field:Json(name = "aged_70_older")
		val aged70Older: Double? = null,

		@field:Json(name = "cvd_death_rate")
		val cvdDeathRate: Int? = null,

		@field:Json(name = "diabetes_prevalence")
		val diabetesPrevalence: Double? = null,

		@field:Json(name = "handwashing_facilities")
		val handwashingFacilities: Double? = null,

		@field:Json(name = "country_iso")
		val countryIso: String?,

		@field:Json(name = "hospital_beds_per_thousand")
		val hospitalBedsPerThousand: Double? = null,

		@field:Json(name = "gdp_per_capita")
		val gdpPerCapita: Double? = null,

		@field:Json(name = "total_cases")
		val totalCases: Int? = null,

		@field:Json(name = "median_age")
		val medianAge: Double? = null,

		@field:Json(name = "new_cases")
		val newCases: Int? = null,

		@field:Json(name = "total_deaths_per_million")
		val totalDeathsPerMillion: Double? = null,

		@field:Json(name = "total_deaths")
		val totalDeaths: Int? = null,

		@field:Json(name = "new_deaths")
		val newDeaths: Int? = null
)
