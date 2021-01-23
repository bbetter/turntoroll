package com.owlsoft.backend.data

object LocalEncountersDataSource : EncountersDataSource {

    private val encounters = mutableMapOf<String, Encounter>()

    override fun addOrUpdate(encounter: Encounter) {
        encounters[encounter.code] = encounter
    }

    override fun findByCode(code: String): Encounter? {
        return encounters[code]
    }

    override fun remove(code: String) {
        encounters.remove(code)
    }
}