# Implementation Plan - Member 3: Start Point Logic

Implement the logic to capture and store the starting location for the distance tracker.

## User Review Required

> [!IMPORTANT]
> I will be adding the `play-services-location` dependency to the project as it is required for fetching the location. I'll also assume that permissions are handled by `PermissionHelper` as per Member 2's task.

## Proposed Changes

### Dependencies

#### [MODIFY] [libs.versions.toml](file:///C:/Simple_Distance_Tracker/gradle/libs.versions.toml)
- Add `play-services-location` library and version.

#### [MODIFY] [build.gradle.kts](file:///C:/Simple_Distance_Tracker/app/build.gradle.kts)
- Add the `play-services-location` implementation.

### Logic

#### [MODIFY] [StartPointManager.kt](file:///C:/Simple_Distance_Tracker/app/src/main/java/com/example/distance_tracker/StartPointManager.kt)
- Implement `StartPointManager` class.
- Add `startLocation` property to store the captured `Location`.
- Add `fetchStartPoint` function to get the current location using `FusedLocationProviderClient`.
- Add `clearStartPoint` for the "Reset" functionality.

## Verification Plan

### Automated Tests
- I'll try to sync the project to ensure dependencies are correct.

### Manual Verification
- Once integrated by Member 5, tapping "Set Start Point" should update the `startLocation` variable and UI (which Member 5 will handle).
- For now, I will verify the logic compiles and looks correct for the requested task.
