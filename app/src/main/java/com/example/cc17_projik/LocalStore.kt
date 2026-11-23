package com.example.cc17_projik

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

/** Device-local prototype storage. No network delivery or authentication is implied. */
class LocalStore private constructor(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("gadgetloop", Context.MODE_PRIVATE)
    var data: MarketplaceData = preferences.getString("marketplace_v1", null)?.let(MarketplaceJson::decode)
        ?: MarketplaceData(listings = sampleListings(), bins = defaultBinLocations())
        private set

    val profileId: String get() = preferences.getString("profile", "").orEmpty()
    val profileName: String get() = profileId.substringBefore('@').ifBlank { "Local user" }

    fun selectProfile(email: String) {
        val profile = email.trim().lowercase(Locale.ROOT)
        require(profile.isNotBlank()) { "Enter your profile email." }
        check(preferences.commitChanges { putString("profile", profile) }) { "Could not save your profile. Please try again." }
    }

    fun addListing(listing: Listing) {
        require(profileId.isNotBlank()) { "Choose a local profile first." }
        require(listing.title.isNotBlank() && listing.description.isNotBlank()) { "Enter a title and description." }
        require(listing.price > 0) { "Enter a valid price." }
        val saved = listing.copy(id = (data.listings.maxOfOrNull { it.id } ?: 0) + 1,
            sellerId = profileId, sellerName = profileName)
        save(data.copy(listings = listOf(saved) + data.listings))
    }

    fun propose(listingId: Int, title: String, description: String, condition: Condition, imageUri: String?) {
        save(TradeRules.propose(data, TradeProposal(TradeRules.newId(), listingId, profileId,
            profileName, title, description, condition, imageUri)))
    }

    fun respond(proposalId: String, status: TradeStatus) =
        save(TradeRules.respond(data, proposalId, profileId, status))

    fun saveBin(bin: BinLocation) = save(TradeRules.saveBin(data, bin))
    fun deleteBin(id: String) = save(data.copy(bins = data.bins.filterNot { it.id == id }))

    private fun save(next: MarketplaceData) {
        check(preferences.commitChanges { putString("marketplace_v1", MarketplaceJson.encode(next)) }) {
            "Could not save changes. Please try again."
        }
        data = next
    }

    companion object {
        @Volatile private var instance: LocalStore? = null
        fun get(context: Context): LocalStore = instance ?: synchronized(this) {
            instance ?: LocalStore(context).also { instance = it }
        }

        private fun sampleListings() = listOf(
            Listing(1, "Macbook Pro", 430, Condition.GOOD, Category.LAPTOPS),
            Listing(2, "Iphone 13 Pro Max", 320, Condition.USED, Category.PHONES),
            Listing(3, "Iphone 11 64gb", 150, Condition.GOOD, Category.PHONES),
            Listing(4, "Samsung Fold 8", 500, Condition.NEW, Category.PHONES),
            Listing(5, "Iphone duo 512gb", 1000, Condition.NEW, Category.PHONES),
            Listing(6, "JBL speaker", 200, Condition.USED, Category.AUDIO),
            Listing(7, "Washing Machine", 70, Condition.USED, Category.OTHERS)
        ).map { it.copy(description = "Sample listing for the GadgetLoop prototype. Replace this with an actual item description, included accessories, and any known issues.",
            sellerId = "demo-seller", sellerName = "Demo seller", location = "Not specified") }
    }
}

object MarketplaceJson {
    fun encode(data: MarketplaceData): String = JSONObject().apply {
        put("listings", JSONArray().apply { data.listings.forEach { item -> put(JSONObject().apply {
            put("id", item.id); put("title", item.title); put("price", item.price)
            put("condition", item.condition.name); put("category", item.category.name)
            put("description", item.description); put("location", item.location)
            put("sellerId", item.sellerId); put("sellerName", item.sellerName)
            put("imageUri", item.imageUri); put("available", item.available)
        }) } })
        put("proposals", JSONArray().apply { data.proposals.forEach { item -> put(JSONObject().apply {
            put("id", item.id); put("listingId", item.listingId)
            put("senderId", item.senderId); put("senderName", item.senderName)
            put("offeredTitle", item.offeredTitle); put("offeredDescription", item.offeredDescription)
            put("offeredCondition", item.offeredCondition.name); put("imageUri", item.imageUri)
            put("status", item.status.name); put("createdAt", item.createdAt)
        }) } })
        put("bins", JSONArray().apply { data.bins.forEach { item -> put(JSONObject().apply {
            put("id", item.id); put("name", item.name); put("address", item.address)
            put("mapsUrl", item.mapsUrl); put("notes", item.notes)
        }) } })
    }.toString()

    fun decode(value: String): MarketplaceData {
        val root = JSONObject(value)
        return MarketplaceData(
            listings = root.getJSONArray("listings").objects().map { item -> Listing(
                id = item.getInt("id"), title = item.getString("title"), price = item.getInt("price"),
                condition = Condition.valueOf(item.getString("condition")),
                category = Category.valueOf(item.getString("category")),
                description = item.optString("description"), location = item.optString("location"),
                sellerId = item.getString("sellerId"), sellerName = item.getString("sellerName"),
                imageUri = item.optString("imageUri").takeIf { it.isNotBlank() },
                available = item.optBoolean("available", true)
            ) },
            proposals = root.getJSONArray("proposals").objects().map { item -> TradeProposal(
                id = item.getString("id"), listingId = item.getInt("listingId"),
                senderId = item.getString("senderId"), senderName = item.getString("senderName"),
                offeredTitle = item.getString("offeredTitle"), offeredDescription = item.getString("offeredDescription"),
                offeredCondition = Condition.valueOf(item.getString("offeredCondition")),
                imageUri = item.optString("imageUri").takeIf { it.isNotBlank() },
                status = TradeStatus.valueOf(item.getString("status")), createdAt = item.getLong("createdAt")
            ) },
            bins = root.getJSONArray("bins").objects().map { item -> BinLocation(
                item.getString("id"), item.getString("name"), item.getString("address"),
                item.optString("mapsUrl"), item.optString("notes")
            ) }
        )
    }

    private fun JSONArray.objects(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }
}
