# GadgetLoop / CC17

An Android prototype for viewing gadget listings, proposing item exchanges, and finding e-waste collection points.

## Open and run

Clone this repository in Android Studio and open the root folder containing `settings.gradle.kts`. The project currently uses Android Gradle Plugin 9.4.1, Gradle 9.6.0, Android SDK API 37, SDK Build-Tools 36.0.0, and a Java 25 Gradle daemon. Install the matching SDK platform and build tools when prompted, sync, and run the `app` configuration on an emulator or Android device (API 24 or later).

```sh
./gradlew testDebugUnitTest assembleDebug
```

On Windows, use `gradlew.bat`. The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`. Pull requests run the same test and build tasks in GitHub Actions.

## Features

- Tap a featured product or Market card to view its photo, price, condition, category, description, location, seller, and availability.
- Choose **Propose a trade**, describe the offered item, select its condition, optionally attach a photo, and save. The Trade tab separates **Sent** and **Received** offers. Senders can cancel pending offers; listing owners can accept or decline them. Accepting an offer makes the product unavailable and declines competing pending offers.
- **Maps** displays a searchable list of collection points. Tapping a card or **Open in Google Maps** opens that location in the Google Maps app, with a browser fallback when the app is absent. There is no embedded map, Maps SDK dependency, API key, or location-permission requirement. Collection points can be added, edited, or deleted; a precise Google Maps link can optionally replace the address search.
- Posted listings, listing drafts, proposals, decisions, selected photo URIs, and collection-point edits persist on the device.

## Local prototype scope

The existing login is a mock, not authentication. Enter a profile email and any nonempty demo password; the password is not checked or stored. The normalized email identifies the local profile that owns listings and offers. Sample listings are marked as belonging to a **Demo seller**.

**Data stays on this installation. Offers are not delivered to other phones, and there are no server notifications or real accounts.** Shared trading needs a backend and authenticated access rules. The UI labels the local behavior explicitly.

To exercise the full exchange flow on one device:

1. Open a local profile such as `seller@example.com`, post an item from Sell, and log out using the Home or Market drawer.
2. Open `buyer@example.com`, find the item in Market, open its details, and propose a trade.
3. Check Trade → Sent, then log out and reopen `seller@example.com`.
4. In Trade → Received, open the proposal and accept or decline it. Reopen the buyer profile to see the resulting status.
5. For an accepted offer, verify that the product is no longer available in Home or Market and its history remains accessible through Trade. A pending offer can instead be cancelled by its sender.
6. Close and reopen the app to verify saved listings and proposals remain. Rotate a product/proposal form and confirm its fields and selected photo remain.

## Initial e-waste collection points

The project team selected the following locations. Entries search Google Maps by place name and address; no coordinates or opening hours are invented. Edit an entry to add a precise Maps place link when available.

| Collection point | Location used |
| --- | --- |
| City Environment and Parks Management Office | Baguio City, Benguet |
| Saint Louis University Sacred Heart Medical Center | SLU Main Campus, Baguio City |
| Bakakeng Central MRF-TSD Facility | Bakakeng Central, Baguio City |
| SM City Baguio | Luneta Hill, Upper Session Road, Baguio City |

SLU's [August 2025 update](https://www.slu.edu.ph/2025/08/18/slu-yce-participates-in-e-waste-management-campaign-and-collection-drive-with-cepmo/) reports that its bin was transferred from Silang Lobby to Sacred Heart Medical Center; the project team chose the updated site. DENR-EMB documents the [Bakakeng Central MRF-TSD facility](https://car.emb.gov.ph/emb-car-conducts-household-e-waste-collection-and-orientation-for-management-of-e-waste/). Google documents the [Maps URL format and browser fallback](https://developers.google.com/maps/documentation/urls/get-started).

For Maps verification, open all four entries, return to the app, search by name/address, edit a location, and verify it persists after reopening. Also check the browser fallback on a device without Google Maps and confirm cancelling a location edit leaves the saved entry unchanged.

## Code inspection cleanup

Screen and guide text uses string resources. Login fields declare autofill hints, search fields opt out of credential autofill, images have accessibility labels, and listing prices have bounds that keep them clear of condition badges. The drawer uses compound drawables and badges use at least 12sp text. Kotlin enum iteration uses `entries`, and CircleImageView is declared in the version catalog. Checked preference writes retain their commit success result; the KTX edit helper returns Unit and is used for asynchronous draft cleanup instead. Dependency upgrade suggestions are separate from these code and layout fixes; the project keeps its validated plugin and navigation versions.
