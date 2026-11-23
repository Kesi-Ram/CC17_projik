package com.example.cc17_projik

import java.net.URI
import java.net.URLEncoder
import java.util.UUID

enum class TradeStatus(val label: String) {
    PENDING("Pending"), ACCEPTED("Accepted"), DECLINED("Declined"), CANCELLED("Cancelled")
}

data class TradeProposal(
    val id: String,
    val listingId: Int,
    val senderId: String,
    val senderName: String,
    val offeredTitle: String,
    val offeredDescription: String,
    val offeredCondition: Condition,
    val imageUri: String? = null,
    val status: TradeStatus = TradeStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

data class BinLocation(
    val id: String,
    val name: String,
    val address: String,
    val mapsUrl: String = "",
    val notes: String = ""
) {
    fun googleMapsUrl(): String = mapsUrl.takeIf { it.isNotBlank() }
        ?: "https://www.google.com/maps/search/?api=1&query=" +
            URLEncoder.encode("$name, $address", "UTF-8")
}

data class MarketplaceData(
    val listings: List<Listing> = emptyList(),
    val proposals: List<TradeProposal> = emptyList(),
    val bins: List<BinLocation> = emptyList()
)

/** Business rules are independent of Android so ownership and state transitions can be tested. */
object TradeRules {
    fun propose(data: MarketplaceData, proposal: TradeProposal): MarketplaceData {
        val listing = requireNotNull(data.listings.find { it.id == proposal.listingId }) {
            "This listing no longer exists."
        }
        require(listing.available) { "This item is no longer available for trade." }
        require(proposal.senderId.isNotBlank()) { "Choose a local profile first." }
        require(listing.sellerId != proposal.senderId) { "You cannot trade with your own listing." }
        require(proposal.offeredTitle.isNotBlank()) { "Enter the name of your offered item." }
        require(proposal.offeredDescription.isNotBlank()) { "Describe your offered item and its issues." }
        require(proposal.status == TradeStatus.PENDING) { "New proposals must be pending." }
        require(data.proposals.none { it.id == proposal.id }) { "This proposal was already saved." }
        require(data.proposals.none {
            it.listingId == proposal.listingId && it.senderId == proposal.senderId &&
                it.status == TradeStatus.PENDING
        }) { "You already have a pending offer for this item. View it in Trade." }
        return data.copy(proposals = listOf(proposal.copy(
            offeredTitle = proposal.offeredTitle.trim(),
            offeredDescription = proposal.offeredDescription.trim()
        )) + data.proposals)
    }

    fun respond(data: MarketplaceData, proposalId: String, actorId: String, status: TradeStatus): MarketplaceData {
        val proposal = requireNotNull(data.proposals.find { it.id == proposalId }) {
            "This proposal no longer exists."
        }
        val listing = requireNotNull(data.listings.find { it.id == proposal.listingId }) {
            "This listing no longer exists."
        }
        require(proposal.status == TradeStatus.PENDING) { "This proposal has already been resolved." }
        if (status == TradeStatus.CANCELLED) {
            require(actorId == proposal.senderId) { "Only the sender can cancel an offer." }
        } else {
            require(status == TradeStatus.ACCEPTED || status == TradeStatus.DECLINED) {
                "Choose accept or decline."
            }
            require(actorId == listing.sellerId) { "Only the listing owner can respond to an offer." }
            require(listing.available) { "This listing is no longer available." }
        }
        return data.copy(
            listings = data.listings.map {
                if (it.id == listing.id && status == TradeStatus.ACCEPTED) it.copy(available = false) else it
            },
            proposals = data.proposals.map {
                when {
                    it.id == proposalId -> it.copy(status = status)
                    status == TradeStatus.ACCEPTED && it.listingId == listing.id &&
                        it.status == TradeStatus.PENDING -> it.copy(status = TradeStatus.DECLINED)
                    else -> it
                }
            }
        )
    }

    fun saveBin(data: MarketplaceData, bin: BinLocation): MarketplaceData {
        require(bin.name.isNotBlank()) { "Enter the bin or collection point name." }
        require(bin.address.isNotBlank()) { "Enter its full address." }
        require(bin.mapsUrl.isBlank() || isGoogleMapsUrl(bin.mapsUrl)) {
            "Use a Google Maps link, or leave the link empty to search by address."
        }
        val clean = bin.copy(name = bin.name.trim(), address = bin.address.trim(),
            mapsUrl = bin.mapsUrl.trim(), notes = bin.notes.trim())
        return data.copy(bins = (data.bins.filterNot { it.id == bin.id } + clean).sortedBy { it.name.lowercase() })
    }

    fun isGoogleMapsUrl(value: String): Boolean = runCatching {
        val uri = URI(value.trim())
        val host = uri.host?.lowercase()
        uri.scheme.equals("https", ignoreCase = true) && uri.userInfo == null &&
            (uri.port == -1 || uri.port == 443) && when (host) {
                "maps.app.goo.gl" -> !uri.path.isNullOrBlank() && uri.path != "/"
                "goo.gl" -> uri.path?.startsWith("/maps/") == true
                "maps.google.com" -> true
                "google.com", "www.google.com" -> uri.path == "/maps" || uri.path?.startsWith("/maps/") == true
                else -> false
            }
    }.getOrDefault(false)

    fun newId(): String = UUID.randomUUID().toString()
}
