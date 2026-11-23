package com.example.cc17_projik

import org.junit.Assert.*
import org.junit.Test
import java.net.URI
import java.net.URLDecoder

class BinLocationTest {
    @Test fun addressSearchEncodesSpacesAmpersandsAndUnicode() {
        val bin = BinLocation("bin", "Bin & Café", "Baguio City, Benguet")
        val uri = URI(bin.googleMapsUrl())
        assertEquals("https", uri.scheme)
        assertEquals("www.google.com", uri.host)
        assertTrue(uri.rawQuery.startsWith("api=1&query="))
        assertEquals("Bin & Café, Baguio City, Benguet", URLDecoder.decode(uri.rawQuery.substringAfter("&query="), "UTF-8"))
    }

    @Test fun anExactMapsLinkTakesPrecedenceOverTheAddressSearch() {
        val link = "https://maps.app.goo.gl/examplePlace"
        assertEquals(link, BinLocation("bin", "Name", "Address", mapsUrl = link).googleMapsUrl())
    }

    @Test fun acceptsGoogleMapsLinksAndRejectsUnrelatedOrMisleadingHosts() {
        for (url in listOf("https://maps.app.goo.gl/abc", "https://goo.gl/maps/abc",
            "https://www.google.com/maps/search/?api=1&query=Baguio", "https://maps.google.com/?q=Baguio")) {
            assertTrue(url, TradeRules.isGoogleMapsUrl(url))
        }
        for (url in listOf("https://google.com.evil.example/maps", "https://example.com/maps",
            "javascript:alert(1)", "http://www.google.com/maps", "https://www.google.com/search?q=Baguio",
            "https://evil.example@www.google.com/maps", "https://maps.app.goo.gl/", "not a link")) {
            assertFalse(url, TradeRules.isGoogleMapsUrl(url))
        }
    }

    @Test fun binsCanBeAddedAndEditedWithoutDuplicates() {
        val bin = BinLocation("bin", " Name ", " Address ")
        val added = TradeRules.saveBin(MarketplaceData(), bin)
        assertEquals("Name", added.bins.single().name)
        val edited = TradeRules.saveBin(added, bin.copy(name = "Updated"))
        assertEquals("Updated", edited.bins.single().name)
    }

    @Test fun aBinNeedsANameAddressAndValidOptionalLink() {
        for (bin in listOf(BinLocation("1", " ", "Address"), BinLocation("1", "Name", " "),
            BinLocation("1", "Name", "Address", "https://example.com"))) {
            assertThrows(IllegalArgumentException::class.java) { TradeRules.saveBin(MarketplaceData(), bin) }
        }
    }

    @Test fun defaultListContainsTheFourRequestedLocationsAndTheUpdatedSluSite() {
        val bins = defaultBinLocations()
        assertEquals(4, bins.size)
        assertEquals(4, bins.map { it.id }.distinct().size)
        assertTrue(bins.any { it.name.contains("Parks Management") })
        assertTrue(bins.any { it.name.contains("Sacred Heart Medical Center") })
        assertTrue(bins.any { it.name == "Bakakeng Central MRF-TSD Facility" })
        assertTrue(bins.any { it.name == "SM City Baguio" })
        assertTrue(bins.all { TradeRules.isGoogleMapsUrl(it.googleMapsUrl()) })
    }
}
