# Home Pharmacy – Kotlin Multiplatform App

This repository contains a **prompt** for generating a fully functional **Home Pharmacy** mobile app for iOS and Android using **Kotlin Multiplatform (KMP)** with **Compose Multiplatform**.

The prompt is designed to be used with AI code generators (ChatGPT, GitHub Copilot, Cursor, etc.). It describes in detail the required features, architecture, UI, data persistence, and platform-specific integrations.

## What the Generated App Will Do

- Manage medications stored in a home fridge (add, edit, delete).
- Show warnings for **expiry date** (expired / expires soon) and **storage condition** (e.g., should not be refrigerated).
- Scan a prescription image (camera or file), extract medicine names and quantities via OCR, and add them to the pharmacy.



## Prompt

```text```
You are an expert Kotlin Multiplatform developer. Generate a complete, production‑ready mobile app for **iOS and Android** using **Kotlin Multiplatform (KMP)** with **Compose Multiplatform** for shared UI. The app is called **Home Pharmacy** – it helps users track medications stored in their home fridge.

### Core Features

1. **Medication Management (CRUD)**  
   - Each medication has:  
     - `name: String`  
     - `quantity: Int` (e.g., number of pills, ml left)  
     - `expiryDate: LocalDate`  
     - `storageCondition: StorageCondition` (enum: `REFRIGERATE`, `ROOM_TEMPERATURE`, `AVOID_FRIDGE`, `SPECIFIC_RANGE` with min/max temp)  
   - User can **add**, **edit**, and **delete** medications.

2. **Warning System**  
   - **Expiry warnings**:  
     - If expiry date is in the past → **“EXPIRED”** (red)  
     - If expiry date is within next 7 days → **“EXPIRES SOON”** (orange)  
   - **Storage condition warnings**:  
     - If `storageCondition` is `AVOID_FRIDGE` but the medicine is stored in the fridge → warning: “Should not be refrigerated!”  
     - If `storageCondition` is `REFRIGERATE` → info: “Keep refrigerated”  
     - If `storageCondition` is `SPECIFIC_RANGE` and the fridge temperature (user‑configurable default 4°C) is outside the range → warning: “Temperature out of range (X°C – Y°C required)”  

   Warnings are displayed on the main list and on the medication detail screen.

3. **Prescription Image Scanner**  
   - User can **take a photo** from camera **or pick an image** from the file system.  
   - The app performs **OCR (text recognition)** on the image to extract medicine names and quantities.  
   - After OCR, the app shows a **list of recognised medicines** with suggested quantities.  
   - The user can **edit** the list (add/remove items, adjust quantities) and then **add all** to the home pharmacy (or select which ones to add).  
   - **Implementation note**: Use **ML Kit Text Recognition** on both platforms via KMP `expect`/`actual` wrappers, or use a cross‑platform OCR library (e.g. `com.google.mlkit:text-recognition` with platform‑specific adapters). For simplicity, also provide a **mock parser** that simulates extraction from a known set of drug names.

### Data Persistence

- Use **SQLDelight** (KMP‑friendly) to store medications locally.  
- Define a table `medications` with columns: `id`, `name`, `quantity`, `expiry_date_epoch_days`, `storage_condition_enum_index`.  
- Provide a repository interface (`MedicationRepository`) with suspend functions: `getAll()`, `insert()`, `update()`, `delete()`, `getExpiringSoon()`.

### Architecture & Tech Stack

- **Shared code** (commonMain):  
  - Data models, repository, use cases, ViewModels (using `ViewModel` from `org.jetbrains.androidx.lifecycle`).  
  - Dependency injection: **Koin** (common, android, ios).  
  - Concurrency: Kotlin Coroutines + Flow.  
  - Date handling: `kotlinx-datetime`.  

- **UI** (shared):  
  - **Compose Multiplatform** for both Android and iOS.  
  - Screens:  
    - `MedicationListScreen` (with warning indicators)  
    - `AddEditMedicationScreen` (form with name, quantity, date picker, storage condition dropdown)  
    - `PrescriptionScannerScreen` (image picker + OCR results + editable list)  
  - Navigation: `compose‑destinations` or `Voyager`.  

- **Platform‑specific features** (via `expect`/`actual`):  
  - **Camera & file picker** – use `compose‑multiplatform‑imagepicker` or implement with `rememberLauncherForActivityResult` on Android and `PHPickerViewController`/`UIImagePickerController` on iOS.  
  - **OCR** – wrap ML Kit Text Recognition. Provide a `expect fun recognizeText(imageBytes: ByteArray): String` and implement for Android (using ML Kit) and iOS (using Vision framework or ML Kit).  

- **Image handling**: Use `Bitmap`/`UIImage` conversion to `ByteArray` in common code via `expect`/`actual` or a library like `ImageBitmap`.

### Detailed UI/UX Requirements

1. **Main Screen**  
   - List of all medications, each showing: name, quantity, expiry date, and a warning icon/tooltip if any warning applies.  
   - FAB to add a new medication.  
   - Top bar with a **“Scan Prescription”** button.  
   - Swipe to delete an item, tap to edit.  

2. **Add/Edit Screen**  
   - TextField for name, number input for quantity.  
   - Date picker for expiry date.  
   - Dropdown for storage condition (with optional temperature range if `SPECIFIC_RANGE` is chosen).  
   - Save button – validate that name is not empty, quantity > 0, expiry not in the past (warning but can allow).  

3. **Prescription Scanner Screen**  
   - Button “Take Photo” and “Choose from Gallery”.  
   - After image selection, show a loading indicator while OCR runs.  
   - Display the extracted text (optional debug view) and a **parsed list** of `(medicineName, quantity)`.  
   - User can edit each item (name, quantity) and delete items.  
   - “Add to Pharmacy” button – inserts each item as a new medication with default storage condition (user can later edit).  

### Error Handling & Edge Cases

- Handle missing camera/gallery permissions gracefully – show rationale and request.  
- OCR may fail – show user a message and allow manual entry.  
- If no text is found, display an error and let user type the list manually.  
- For temperature warnings, provide a default fridge temperature of 4°C but allow user to change it in settings (optional stretch goal).  

### Project Structure (expected output)

Generate the full project with these packages:
