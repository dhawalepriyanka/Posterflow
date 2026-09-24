# PosterFlow ready-made template system

## What changed

The active app now uses **admin-designed layouts → minimal user details → generated poster**. Normal users cannot add shapes/text, move slots, rotate elements, or open layers. There is no fixed BizFlow branding.

### 1. Previous architecture

`TemplatePresets.kt` returned hardcoded `Poster` records. The large `PosterMakerScreen.kt` selected layouts by category, ID and layout index. Welcome used a separate element-based free editor; several special templates used `SignaturePosterArtwork.kt`. Preview/export paths could use different dimensions. Saved records were stored in Room; Profile used DataStore and Firebase authentication used a local session/profile store.

### 2. New architecture

`PosterTemplate` is the reusable definition: identity, category, artwork reference, square canvas, static content, branding-band configurations, dynamic slots, required fields, status, ordering and timestamps. `GeneratedPoster` is a complete versioned snapshot containing that definition, entered values, portrait, crop and branding.

`TemplatePresets` is now only a compatibility adapter from the new starter definitions. The real Templates routes and Video Maker route use `ReadyTemplateLibrary` and `ReadyPosterCustomize`. Dashboard/Smart AI keep their existing routes and IDs. New poster previews, gallery exports and video source bitmaps use `TemplateRenderer`. Legacy code is retained for compatibility; its free-editor screens are no longer routed to normal users. Previously saved raster designs remain viewable/shareable; legacy free-editor projects are not converted into new editable forms automatically.

### 3. Admin management

An authorized administrator sees **Profile → Template Management**. Management includes category selection, thumbnail, name, category, status, creation/update dates, Create, Edit, Preview, Duplicate, Activate/Deactivate, Delete and Move Up/Down. Duplicates start inactive. Deletion is a soft deletion. Uploaded artwork remains available to saved snapshots.

The builder supports importing square base artwork, enabling dynamic fields, required/optional flags, dragging and resizing slot boxes, photo shapes/borders, text font/size/bold/italic/color/alignment/max-lines, and preview with sample data. Admin preview values are not persisted as user inputs.

### 4–5. Header and footer

Independent enabled flags, colors, heights and layout presets are stored in the template. Header supports logo, company and tagline. Footer supports logo, company, phone, website, email and address. Left/center/right/company-center layouts and footer combinations are provided. Changing band height reflows the built-in body slots; validation prevents slots from entering branding bands. An uploaded artwork's static pixels cannot be rearranged, so artwork should reserve appropriate band areas.

### 6. Profile branding

Company name, logo, phone and website are reused. Business email, address and tagline are editable in Profile. Changes save automatically. Login/profile setup is retained. Picked logos/photos are copied into app-private storage, avoiding temporary-picker access problems. Profile edits no longer get overwritten on every launch. Local profiles are retained per Firebase UID across sign-out/sign-in; authentication itself is still cleared at logout.

Templates store visibility flags, never one customer's company values. New customization resolves current Profile values. Missing enabled branding fields show **Complete your business profile to use this template** and **Go to Profile**. Reopened saved posters retain their original branding snapshot.

### 7. Dynamic forms

The enabled slots determine the form, not the category. Supported fields: Photo, Name, Designation, Message, Amount, Achievement and Date. Required slots are validated before generation. Disabled fields never appear. Festival and Motivation starters require no manual fields. Optional empty text does not export placeholder text.

### 8. Photo slots

Frames support circle, rounded rectangle, rectangle and oval. Admin positions the frame. Users can replace the photo, zoom, pan inside it and reset crop, but cannot move or resize the frame. Cover-cropping preserves portrait proportions; logos use contain-fitting. Images are decoded with a bounded size and EXIF orientation, and cached independently from text/crop edits.

### 9–10. Redesigned categories/counts

| Category | Replaced starters |
| --- | ---: |
| Welcome | 9 |
| Birthday | 7 |
| Achievement | 6 |
| Income | 6 |
| Festival | 1 |
| Motivation | 1 |
| **Total** | **30** |

Original square designs use coordinated dark palettes, metallic-gradient headings, portrait frames, name plates, curves, category-specific decorative motifs and readable branding bands. Festival retains the project's original Vishwakarma artwork in the new renderer. Income wording is recognition-oriented, not an earnings promise.

### 11. Created files

- `app/src/main/java/com/example/templates/PosterTemplate.kt`
- `app/src/main/java/com/example/templates/StarterTemplates.kt`
- `app/src/main/java/com/example/templates/TemplateRepository.kt`
- `app/src/main/java/com/example/templates/TemplateRenderer.kt`
- `app/src/main/java/com/example/ui/screens/ReadyTemplateScreens.kt`
- `app/src/main/java/com/example/ui/screens/TemplateAdminScreen.kt`
- `app/src/test/java/com/example/templates/TemplateWorkflowTest.kt`
- `app/src/test/java/com/example/ui/screens/ReadyTemplateScreenTest.kt`
- `docs/TEMPLATE_SYSTEM.md`

### 12. Modified files

- `app/src/main/java/com/example/model/TemplatePresets.kt`
- `app/src/main/java/com/example/ui/PosterViewModel.kt`
- `app/src/main/java/com/example/ui/screens/PosterMakerScreen.kt`
- `app/src/main/java/com/example/ui/screens/VideoMakerScreens.kt`
- `app/src/main/java/com/example/auth/AuthModels.kt`
- `app/src/main/java/com/example/auth/AuthSessionStore.kt`
- `app/src/main/java/com/example/auth/FirebaseAuthRepository.kt`
- `app/src/test/java/com/example/ui/screens/SignaturePosterArtworkTest.kt`

### 13–14. Storage and synchronization

**Admin-created templates are local to this app installation. They do not synchronize to other phones.** No active remote template database or artwork-storage implementation was present. The existing Supabase code is authentication-related, not a template service, and the app uses Firebase authentication.

`TemplateRepository` separates storage from UI. `LocalTemplateRepository` uses atomic JSON writes to `filesDir/poster_templates_v1.json`, a mutex for mutations, bundled starters for migration, and durable files in `filesDir/template_assets`. Corrupt existing storage is not silently overwritten. The same-device template library is shared across sign-ins so an admin can publish locally and a normal account on that installation can use it. Android backup/restore is not live cross-device template synchronization.

A production cross-phone rollout needs an implementation backed by remote metadata and object storage, uploaded-artwork URLs, version/conflict handling, and server-side access rules. Client-only role checks must never be considered security for a future remote service.

### 15. My Designs

Generate automatically saves a full `generated_v1:` document snapshot inside the existing Room record, plus a private PNG. No destructive Room migration was needed. Snapshots contain the original template/category, values, branding and crop. Reopen returns to the same restricted form. Saving again updates the same record. Admin editing/deletion does not rewrite snapshots. Existing My Designs records are preserved.

### 16. Export and sharing

New poster exports are 1080 × 1080 PNGs from the same renderer as preview. There are no editor overlays. Save to Gallery uses MediaStore; legacy Android storage permission is requested when required. Failed writes clean up partial MediaStore entries. Share uses the existing FileProvider. Square thumbnails fit completely in their cards.

### 17. Video compatibility

Create Video uses the final generated bitmap, including Profile branding. Square source posters use square 720 × 720 video frames in both codec and recorder paths; fitting preserves the whole composition. Fade/None/Zoom In, 5/10/15-second durations, bundled category tracks and optional phone music are offered. Audio is limited to the selected video duration. Video records appear in My Designs and continue using the existing Media3 player. A failed requested audio mux is reported instead of silently claiming the music was included. The existing exporter keeps a private video copy and attempts a Gallery copy; Gallery availability depends on storage access, especially on Android 8/9.

### 18–19. Verification and APK

Final `assembleDebug` result: **BUILD SUCCESSFUL** (17 September 2026; final build took 2m 26s). Package: `com.aistudio.postermaker.shydv`; debug version 1.0.1 (code 2), minimum Android API 24. Existing icon-deprecation warnings remain; no compilation/resource/Room errors blocked the APK.

Targeted verification passed: **12 tests, 0 failures** (11 model/storage/rendering tests and 1 Compose workflow test). Tests cover all 30 starter definitions, role-denied/revoked writes, CRUD/order/reload, required fields, Profile substitution, immutable Room snapshots, durable image imports, band resizing, proportional video fitting, deterministic rendering and the restricted form's Generate → My Designs flow.

All six category contact sheets and the long-text/crop stress render were visually inspected. Rendered previews are in `app/build/reports/ready-template-previews`. The full pre-existing test suite was not run; these were targeted tests for the migration.

Commands used:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests 'com.example.templates.TemplateWorkflowTest' --tests 'com.example.ui.screens.ReadyTemplateScreenTest' :app:assembleDebug --console=plain
.\gradlew.bat assembleDebug --console=plain
```

The APK output location is:

`D:\poster-maker\app\build\outputs\apk\debug\app-debug.apk`

## One-time administrator setup

There is **no hardcoded admin account or password** and no role-toggle in the APK. The app recognizes the boolean Firebase ID-token claim `admin: true`. Reads of the template library do not need admin access; every mutation requests a fresh token and verifies the role. UI access is refreshed on authentication changes and app resume. Network/auth failure denies management access.

The Firebase project owner must assign the claim to the intended account from a trusted server/Admin SDK environment. Do not put a service-account private key in this project or the APK. Example, in an already authenticated trusted Node environment with `firebase-admin` installed:

```javascript
import { initializeApp, applicationDefault } from 'firebase-admin/app';
import { getAuth } from 'firebase-admin/auth';

initializeApp({ credential: applicationDefault() });
const uid = 'THE_INTENDED_FIREBASE_USER_UID';
const user = await getAuth().getUser(uid);
await getAuth().setCustomUserClaims(uid, { ...user.customClaims, admin: true });
```

Preserving existing claims avoids erasing other roles. To revoke, preserve the other claims but set `admin: false`. Return to PosterFlow/reopen the app to refresh the role. This follows [Firebase's custom-claims guidance](https://firebase.google.com/docs/auth/admin/custom-claims). This implementation did not assign privileges to any live account.

## Acceptance check on a physical phone

1. Sign in with the authorized admin account. Open Profile → Template Management → Create template.
2. Choose Welcome; name it Test Welcome; upload square artwork.
3. Enable Photo, Name and Designation; disable Message. Position/size their boxes.
4. Enable Header with Logo + Company. Enable Footer with Phone + Website; disable unwanted footer fields.
5. Preview, save and activate.
6. Sign in with a normal account **on the same installation**; complete its Profile.
7. Open Templates → Welcome → Test Welcome. Verify only the three configured inputs, fixed-frame cropping, and that this user's branding appears.
8. Generate; save PNG; share; reopen My Designs; create/play/share video with and without music.
9. Check a second phone only after implementing remote synchronization; local admin publishing is not cross-phone publishing.

No emulator was launched. No physical device was connected during implementation. Live Firebase login/claim provisioning, external picker UI, Gallery/share-sheet interaction and hardware video encoding require the physical-phone check above; automated rendering/storage/UI tests do not substitute for these checks.
