package com.example.cc17_projik

/** Initial collection points selected by the project team. No coordinates or opening hours are guessed. */
fun defaultBinLocations(): List<BinLocation> = listOf(
    BinLocation(
        id = "baguio-cepmo",
        name = "City Environment and Parks Management Office",
        address = "Baguio City, Benguet",
        notes = "CEPMO collection point."
    ),
    BinLocation(
        id = "slu-sacred-heart",
        name = "Saint Louis University Sacred Heart Medical Center",
        address = "SLU Main Campus, Baguio City",
        notes = "SLU reports that the collection bin was transferred here from Silang Lobby."
    ),
    BinLocation(
        id = "bakakeng-central-mrf",
        name = "Bakakeng Central MRF-TSD Facility",
        address = "Bakakeng Central, Baguio City",
        notes = "Materials Recovery Facility – Treatment, Storage, and Disposal facility for e-waste."
    ),
    BinLocation(
        id = "sm-city-baguio",
        name = "SM City Baguio",
        address = "Luneta Hill, Upper Session Road, Baguio City",
        notes = "E-waste collection point."
    )
)
