package com.example.cc17_projik

enum class Condition(val label: String) {
    GOOD("GOOD"), USED("USED"), NEW("NEW"), NEEDS_REPAIR("NEEDS REPAIR")
}

enum class Category(val label: String) {
    OTHERS("Others"), PHONES("Phones"), PC_PARTS("PC parts"),
    LAPTOPS("Laptops"), AUDIO("Audio"), ACCESSORIES("Accessories")
}

data class Listing(
    val id: Int,
    val title: String,
    val price: Int,
    val condition: Condition,
    val category: Category,
    val imageRes: Int = 0,
    val description: String = "",
    val location: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val imageUri: String? = null,
    val available: Boolean = true
)
