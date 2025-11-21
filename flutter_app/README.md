# Flutter app (partial port)

This folder contains a minimal Flutter scaffold to start porting the Android `main_act` screen into Flutter.

What I created
- `lib/main.dart` — app entry, initializes Firebase, and wires `TempStorage` via `provider`.
- `lib/pages/main_page.dart` — UI mirroring `activity_main.xml` (search bar, promo carousel, categories, product list, footer nav).
- `lib/models/item.dart` — Dart `Item` model mapped from `Item.java`.
- `lib/services/temp_storage.dart` — a `ChangeNotifier` that loads items from Firestore collection `Items` (falls back to sample data if empty or on error).
- `lib/widgets/product_item.dart` — item row widget used in product list.
- `pubspec.yaml` — lists dependencies: `provider`, `firebase_core`, `cloud_firestore`, `firebase_auth`.

Next steps
- Copy Android `google-services.json` to `flutter_app/android/app` and configure Android appId in `android/app/build.gradle` (or run `flutterfire configure`).
- Populate `assets/images/` with the drawable images from the Android `res/drawable` folder (icons like `locate`, `regis`, `burger3`, etc.).
- Run `flutter pub get` and then `flutter run` on an Android device.

Automatic drawable copy
- A helper PowerShell script is included to copy image files from the Android project into the Flutter assets folder.

	```powershell
	cd "c:\Users\Grace Anne\Documents\most_updated\E-commerce\flutter_app\scripts"
	.\copy_drawables.ps1
	cd ..
	flutter pub get
	```

The script copies `*.png`, `*.jpg`, `*.jpeg`, `*.webp`, and `*.gif` files from `app/src/main/res/drawable` into `flutter_app/assets/images/`.

Build & release notes
- Place `google-services.json` into `flutter_app/android/app/` before running on device.
- To create a release APK for Android:

	```powershell
	cd "c:\Users\Grace Anne\Documents\most_updated\E-commerce\flutter_app"
	flutter build apk --release
	```

Adjust signing config in `android/app/build.gradle` if you use a keystore.

Commands (PowerShell):

```powershell
cd "c:\Users\Grace Anne\Documents\most_updated\E-commerce\flutter_app"
flutter pub get
flutter run -d <device-id>
```

Notes
- The Firestore collection name is assumed `Items`. Adjust in `lib/services/temp_storage.dart` to match your database.
- I kept logic simple so it’s easy to expand: provider holds items, pages read them; cart/auth pages not implemented yet.

If you want, I can now:
- Copy drawables into `assets/images` automatically from the Android project.
- Implement the cart and auth flow using `firebase_auth` and a `CartProvider`.
- Convert `productsAct` and `search` next (recommended: `productsAct` as it’s the detailed listing screen).
