package com.example.cc17_projik

import org.junit.Assert.*
import org.junit.Test

class TradeRulesTest {
    private val listing = Listing(1, "Laptop", 200, Condition.GOOD, Category.LAPTOPS, sellerId = "owner")
    private val initial = MarketplaceData(listings = listOf(listing, listing.copy(id = 2)))
    private fun offer(id: String = "offer", sender: String = "buyer", listingId: Int = 1) =
        TradeProposal(id, listingId, sender, sender, " Phone ", " Screen has scratches ", Condition.USED)

    @Test fun submittingAnOfferPreservesTheListingAndTrimsTheDescription() {
        val result = TradeRules.propose(initial, offer())
        assertEquals(initial.listings, result.listings)
        assertEquals("Phone", result.proposals.single().offeredTitle)
        assertEquals("Screen has scratches", result.proposals.single().offeredDescription)
        assertEquals(TradeStatus.PENDING, result.proposals.single().status)
    }

    @Test fun ownListingCannotReceiveAnOfferFromItsOwner() {
        assertThrows(IllegalArgumentException::class.java) { TradeRules.propose(initial, offer(sender = "owner")) }
    }

    @Test fun missingOrUnavailableListingsCannotReceiveOffers() {
        assertThrows(IllegalArgumentException::class.java) { TradeRules.propose(initial, offer(listingId = 99)) }
        val closed = initial.copy(listings = listOf(listing.copy(available = false)))
        assertThrows(IllegalArgumentException::class.java) { TradeRules.propose(closed, offer()) }
    }

    @Test fun duplicatePendingOffersAreRejectedButAnotherBuyerCanOffer() {
        val first = TradeRules.propose(initial, offer())
        assertThrows(IllegalArgumentException::class.java) { TradeRules.propose(first, offer("second")) }
        assertEquals(2, TradeRules.propose(first, offer("second", "another-buyer")).proposals.size)
    }

    @Test fun emptyIdentityOrItemFieldsAreRejected() {
        for (invalid in listOf(offer(sender = ""), offer().copy(offeredTitle = " "),
            offer().copy(offeredDescription = " "), offer().copy(status = TradeStatus.ACCEPTED))) {
            assertThrows(IllegalArgumentException::class.java) { TradeRules.propose(initial, invalid) }
        }
    }

    @Test fun onlyTheOwnerCanAcceptOrDecline() {
        val pending = TradeRules.propose(initial, offer())
        for (actor in listOf("buyer", "someone-else", "")) {
            for (status in listOf(TradeStatus.ACCEPTED, TradeStatus.DECLINED)) {
                assertThrows(IllegalArgumentException::class.java) { TradeRules.respond(pending, "offer", actor, status) }
            }
        }
    }

    @Test fun acceptanceClosesTheListingAndDeclinesCompetingOffersOnly() {
        var data = TradeRules.propose(initial, offer())
        data = TradeRules.propose(data, offer("competitor", "other"))
        data = TradeRules.propose(data, offer("unrelated", "other", 2))
        val accepted = TradeRules.respond(data, "offer", "owner", TradeStatus.ACCEPTED)
        assertFalse(accepted.listings.first { it.id == 1 }.available)
        assertTrue(accepted.listings.first { it.id == 2 }.available)
        assertEquals(TradeStatus.ACCEPTED, accepted.proposals.first { it.id == "offer" }.status)
        assertEquals(TradeStatus.DECLINED, accepted.proposals.first { it.id == "competitor" }.status)
        assertEquals(TradeStatus.PENDING, accepted.proposals.first { it.id == "unrelated" }.status)
        assertThrows(IllegalArgumentException::class.java) { TradeRules.propose(accepted, offer("late", "late-buyer")) }
    }

    @Test fun aResolvedProposalCannotBeChangedAgain() {
        val accepted = TradeRules.respond(TradeRules.propose(initial, offer()), "offer", "owner", TradeStatus.ACCEPTED)
        for (status in TradeStatus.entries) {
            assertThrows(IllegalArgumentException::class.java) { TradeRules.respond(accepted, "offer", "owner", status) }
        }
    }

    @Test fun declineLeavesTheListingAvailableAndAllowsANewOffer() {
        val declined = TradeRules.respond(TradeRules.propose(initial, offer()), "offer", "owner", TradeStatus.DECLINED)
        assertTrue(declined.listings.first().available)
        assertEquals(2, TradeRules.propose(declined, offer("retry")).proposals.size)
    }

    @Test fun onlyTheSenderCanCancelAndCancellationLeavesTheListingOpen() {
        val pending = TradeRules.propose(initial, offer())
        assertThrows(IllegalArgumentException::class.java) { TradeRules.respond(pending, "offer", "owner", TradeStatus.CANCELLED) }
        val cancelled = TradeRules.respond(pending, "offer", "buyer", TradeStatus.CANCELLED)
        assertEquals(TradeStatus.CANCELLED, cancelled.proposals.single().status)
        assertTrue(cancelled.listings.first().available)
        assertEquals(2, TradeRules.propose(cancelled, offer("retry")).proposals.size)
    }

    @Test fun missingProposalsAndInvalidTransitionsAreRejected() {
        val pending = TradeRules.propose(initial, offer())
        assertThrows(IllegalArgumentException::class.java) { TradeRules.respond(pending, "missing", "owner", TradeStatus.ACCEPTED) }
        assertThrows(IllegalArgumentException::class.java) { TradeRules.respond(pending, "offer", "owner", TradeStatus.PENDING) }
    }
}
