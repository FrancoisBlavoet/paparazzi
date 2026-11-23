# Fork update: 2.0.0-alpha05.1

## Scope

Audit completed November 19, 2026.

- Exact upstream base: `cashapp/paparazzi` tag `2.0.0-alpha05.1`
  (`481e82fe14839c8ae56eeb199e6b339067852fcb`), released September 28, 2026.
- Previous fork release branch: `origin/instacart-fork-alpha05` at `42f1263df`.
- Also audited the fork's `a11y-alpha-hidden`, `fix-show-secrets-setting`,
  `fix-secure-text-field`, `fix/apng-aware-overwrite-check`, and
  `fix-apng-aware-overwrite-check` branches. Duplicate implementations and
  intermediate experiments are consolidated, not stacked on one another.
- New local branch: `codex/paparazzi-alpha05-1`.
- Published releases: `2.0.0-alpha05.1-v1` (November 19, 2026) and
  `2.0.0-alpha05.1-v2` (November 20, 2026), on Maven Central and GitHub.

## Change-by-change disposition

| Fork change | In the exact alpha05.1 release? | Disposition |
| --- | --- | --- |
| Secure text fields: intercept API 37 `android.text.ShowSecretsSetting` (`71fa0fb6d`; revised/tested on `fix-show-secrets-setting`) | **No.** The tag has no ShowSecretsSetting interceptors. | Port the ByteBuddy implementation, including both callback overloads, and the Compose 1.12+/API 37 regression fixture. Keep the release's layoutlib patches intact. |
| Animated golden overwrite threshold (`83c88f342`, also `0860ebff3`) | **No.** `HtmlReportWriter` still uses `ImageIO.read()` for animations, comparing only the first frame. | Port frame-by-frame comparison and its regression tests. PR cashapp/paparazzi#2395 merged November 10, 2026, **after** this release; being on current master does not make it part of alpha05.1. |
| FPS-only and frame-count changes (`921d318d3`, follow-up on the APNG branch) | **No.** | Carry the follow-up and tests; compare FPS after reading frame metadata. |
| Report files for identical animation pixels at different FPS (final upstream PR #2395) | **No.** | Use the accepted upstream FPS-aware report hash and tests, preventing animations at different rates from sharing and overwriting one report file. |
| RGB APNG decoding: opaque alpha and unsigned channels (`a33af6111`, follow-up on the APNG branch) | **No.** | Carry the follow-up and decoder/recording tests; needed for valid comparisons of opaque RGB frames. |
| Hide semantics via `HideFromAccessibility` (`42f1263df`, consolidated from `a11y-alpha-hidden`) | **Yes.** Upstream `b14882f82` / PR #2430 supports both `InvisibleToUser` and `HideFromAccessibility`. | Keep upstream implementation and test; do not reapply this portion. Keep upstream hidden-subtree pruning (`9f5ac7953` / PR #2383) too. |
| Transparent Compose nodes, including `graphicsLayer { alpha = 0f }` (`42f1263df`, earlier work on `a11y-alpha-hidden`) | **Only partially.** The release still matches `Modifier.alpha(0f)` by modifier equality. | Port the final reflection-based transparency detection, replacing the narrow equality check. Retain upstream hidden-semantics checks. Extend regression coverage for graphicsLayer transparency. |
| Maven Central identity, fork plugin ID and runtime dependency (`e67cba73b`) | Fork-specific, not an upstream bug. | Preserve `io.github.francoisblavoet` artifacts, plugin ID `io.github.francoisblavoet.paparazzi`, and fork POM identity. Adapt the new native-report JUnit adapter coordinates and KMP dependency validation. Align internal builds and integration fixtures with those coordinates (94 fixture build files, mostly one-line plugin-ID replacements). Java/Kotlin packages and `app.cash.paparazzi.*` system-property names stay unchanged. |
| Secure-field comment/format cleanups (`6c40ce768`, `3a4a2b633`, `c65f4a964`) | Not functional fixes. | Preserve the final cleaned interceptor source; no separate runtime workaround. |
| Historical version bumps/renames (`a0094ad68`, `2e2d939b8`, version part of `42f1263df`) | Fork-specific. | Replace with `2.0.0-alpha05.1-v1`; do not replay obsolete `instacart`/`fb` suffixes. |
| A11y golden-only refreshes (`97bdbee66`, `183b911ad`, also consolidated in `42f1263df`) | Tied to the old renderer. | Do not copy the old dropdown/legend PNGs onto layoutlib 16.2.3. Verify the release goldens and regenerate the dropdown and hidden-node legend expectations under the new renderer. |
| Earlier alpha/a11y experiments (`2c7712ab3`, `1b186a709`, `830add9fb`, `2618567a6`, `35f8cbe37`, `37ac8263d`, `ad6dfc059`, `39c31c1a6`, `e7b124611`) and the ASM variant of the secure-field fix (`329816ef7`) | Alternative implementations, not additional fixes. | Use the final release-branch accessibility behavior and the ByteBuddy secure-field implementation appropriate to alpha05.1. Do not import another fork's ASM, Unsafe, publishing or workflow changes. |

## Consumer configuration

```kotlin
plugins {
  id("io.github.francoisblavoet.paparazzi") version "2.0.0-alpha05.1-v1"
}
```

If depending on the runtime explicitly, use
`io.github.francoisblavoet:paparazzi:2.0.0-alpha05.1-v1`.
Native-report JUnit adapters use the same fork group and version.

## Upgrade caveats

Upstream alpha05.1 moves to layoutlib 16.2.3 and AGP 9.0.0. Its release notes
require re-recording accessibility goldens and goldens using `V_SCROLL`,
`H_SCROLL`, or `FULL_EXPAND`. Old fork goldens must not overwrite the new
upstream goldens wholesale: regenerate only intentionally changed expectations.

## Validation

Final positive verification **passed** with all fork fixes restored. All Gradle invocations go
through the agent-task-queue MCP `run_task` tool (using `build-brief`). Commands
are documented as `./gradlew` for normal use.

### Final positive verification

- Runtime unit suite: **125 tests, 120 passed, 5 skipped, 0 failures**.
- Selected Gradle plugin integration tests: **8 passed, 0 failures**:
  `composeSecureTextField`, `accessibilityRendering`,
  `verifyFailureNativeReportVintage`, `verifyFailureNativeReportParameterized`,
  `verifyFailureNativeReportJupiter`, `verifyFailureNativeReportMixedFrameworks`,
  `kotlinMultiplatformPluginWithNewAndroidLibraryPlugin`,
  `supportsJunitJupiterLibrary`.
- The accessibility integration fixture verifies **25 snapshot tests**, all passing.
- Runtime and Gradle plugin `apiCheck`: passed.
- Repository-wide `spotlessCheck`: passed.
- `git diff --check`: passed.
- The three JUnit adapter modules' test tasks completed; they have no standalone
  unit tests. Their runtime/dependency wiring is exercised by the native-report
  integration tests above.
- Follow-up release gate: `./gradlew check --continue` **passed** on November 19,
  2026. The top-level XML reports contain **274 tests: 268 passed, 6 skipped,
  0 failures/errors**, including all 96 Gradle plugin integration cases (95 passed,
  1 skipped). Nested fixtures deliberately exercise failing builds; their expected
  failures are handled by the integration tests and are not release-gate failures.
- Two tracked fixture PNGs and generated resource/snapshot directories modified by
  the full integration suite were restored/removed after the tests. No extra golden
  changes beyond the two reviewed accessibility updates are part of the release.

Reproduce the final pass:

```shell
./gradlew :paparazzi:test \
  :paparazzi-junit-platform:test :paparazzi-junit-vintage:test :paparazzi-junit-jupiter:test \
  :paparazzi:apiCheck :paparazzi-gradle-plugin:apiCheck \
  :paparazzi-gradle-plugin:test \
  --tests '*composeSecureTextField' \
  --tests '*accessibilityRendering' \
  --tests '*verifyFailureNativeReport*' \
  --tests '*kotlinMultiplatformPluginWithNewAndroidLibraryPlugin' \
  --tests '*supportsJunitJupiterLibrary' \
  spotlessCheck
```

### Upstream-only regression probe

Temporarily replaced `PaparazziSdk.kt`, `HtmlReportWriter.kt`, and
`AccessibilityElementCollector.kt` with their exact alpha05.1-tag contents,
keeping the new tests and the reviewed alpha05.1-based goldens. All three
regressions reproduced; all fork sources were restored afterward:

- `HtmlReportWriterTest.videoOverwritesOnRecordWithFlagWhenOnlyLaterFrameChanges`:
  golden modification time did not change when a later animation frame changed.
- `SecureTextFieldTest.secureTextField`:
  `Method shouldShowTouchInput in android.text.ShowSecretsSetting not mocked`.
- `AccessibilityRenderingTest`: hidden-node legend and Material 3 dropdown
  snapshots contained accessibility entries/highlights for transparent content.

Only two accessibility goldens were refreshed, from new renders inspected
visually: `dropDownMaterial3` and `verify_hidden_views_are_not_in_legend`.
No old-renderer PNGs were copied over the new release baselines.

The negative probe was intentionally failing. It is separate from the final
positive verification, which runs with every fork fix restored.

### CI signing scope

A release version enables artifact signing by default. Branch CI has no private
signing key and initially stopped at `signMavenPublication` while preparing the
integration fixtures, before running their tests. The CI `check` invocation now
sets `-PRELEASE_SIGNING_ENABLED=false` for test-only local publications. Actual
Maven Central publication retains `RELEASE_SIGNING_ENABLED=true` and signs every
release artifact. No private signing material was added to GitHub.

## Published release

- Maven Central deployment `bca7ae84-9b4d-4d91-9da3-369c73bd09d7` reached
  **PUBLISHED** with no validation errors.
- All **nine publications** were verified: eight library artifacts plus the Gradle
  plugin marker. Upstream deliberately does not publish `paparazzi-preview-lints`.
- Downloaded **82 artifact/signature files** from Maven Central. SHA-256 hashes of
  every non-signature file match the signed local release files. Signature files
  are present, and Central's signature validation passed.
- The runtime and plugin-marker POMs and all eight library JARs also return HTTP
  200 at their canonical Maven Central URLs, without cache-busting parameters.
- Full cross-platform CI run `44681784392` passed: macOS, Windows and Linux, each
  on JDK 21 and 24; documentation generation also passed.
- GitHub release/tag: `2.0.0-alpha05.1-v1`, targeting the CI-verified source commit
  `6a46c4b4d229a824a3ca50f6d785e5d20b1c18e1`.
- Local verification manifest (not committed):
  `build/release-verification/2.0.0-alpha05.1-v1.json`.

Publication commands (run through the shared build queue):

```shell
./gradlew publishAllPublicationsToProjectLocalMavenRepository --no-parallel
./gradlew publishAndReleaseToMavenCentral --no-parallel -PRELEASE_SIGNING_ENABLED=true
```

### Automatic tag-workflow outcome

Creating the GitHub release also triggered the inherited tag-publishing workflow
(run `44687125124`). It finished before cancellation could take effect and failed
at `:paparazzi-annotations:signMavenPublication` with `Could not read PGP secret
key`: the fork has no GitHub release signing secrets configured. This was a
redundant upload attempt, not a test failure. The signed local publication had
already reached **PUBLISHED** and all public artifacts were verified. Do not
rerun publication for this immutable version; configure GitHub release secrets
separately if automatic tag publication is desired for a future version.


## v2: deterministic callback scheduling

Published release: `2.0.0-alpha05.1-v2` (November 20, 2026).

- Replace Layoutlib's per-Handler weak-map timestamp tie breaking with a weak-owner
  queue ordered by timestamp and a session-wide posting sequence.
- Install it during `SessionInteractiveData` construction, before any callbacks
  can be posted; no already-queued initialization work is reordered or dropped.
- Preserve queue clearing, owner collection, and independent queues on configuration
  changes. No forced test order, extra frames, animation delays, or tolerance changes.
- Include preloaded-target retransformation support following upstream PR #2484,
  and a dedicated fresh-JVM regression that preloads all four patched classes.
- Do not carry the unsuccessful frame-clock/Layoutlib 17 or main-dispatcher probes.

### Focused verification

Twelve queue/session tests passed. The deterministic hash-collision regression
fails without FIFO tie breaking (`[2, 1, 3]` instead of `[1, 2, 3]`) and passes with it.

With the private fork artifact, all 12 IDS navigation movies matched across
Image-first, Default-first, Search-first, and repeated Image-first runs: 942 frames
per run, 3,768 total. The complete diff animations also match pixel-for-pixel.
The same two quick-return up/down movies consistently differ from old goldens
on frames 66, 68, and 70; these are stable expected-image differences, not the
reproduced nondeterminism. No IDS golden or dependency changes are part of this fork release.

The complete release validation and publication results are recorded below.
Fork tag publication is now opt-in; the existing signed local publishing path
avoids the v1 tag workflow's missing-secret failure.


### Release-test infrastructure

The first full v2 gate reached the Robolectric fixture but its default 512 MiB
TestKit daemon ran out of heap transforming `nativeruntime-dist-compat` before
that fixture's tests started. Give that one fixture a 2 GiB Gradle heap; do not exclude the
fixture or relax its assertions. The complete gate is rerun with it enabled.


### v2 final verification and publication

- Clean-fixture `./gradlew check --continue -PRELEASE_SIGNING_ENABLED=false`
  **passed**: 287 top-level tests, **281 passed, 6 skipped, 0 failures/errors**,
  including all 96 plugin integration cases. Restore deliberately mutated fixture
  goldens and generated resource/configuration files between repeated local gates.
- API compatibility and repository-wide formatting checks passed.
- Fresh-JVM preloaded-target coverage passed for all four patched Layoutlib classes.
- Cross-platform CI run `44838783365` passed on macOS, Windows, and Linux, each
  with JDK 21 and 24; documentation generation passed too.
- Repeated all four IDS animation controls with the **actual signed v2 JAR**:
  all 12 movie hashes match the validated FIFO fix in every run. The same two stable
  golden differences remain; no IDS dependency or expected-image files were changed.
- Signed Maven Central deployment `9214314f-d81b-46e0-b06e-f3f61f7bbee1` reached
  **PUBLISHED**, with no validation errors.
- Verified all **nine publications / 82 artifact and signature files** from public
  Maven Central. Every SHA-256 matches the signed local release files. Canonical
  runtime/plugin-marker POMs and all eight library JAR URLs return HTTP 200.
- GitHub pre-release/tag: `2.0.0-alpha05.1-v2`, targeting the exact CI-verified
  source commit `416437642498a365f1d6d8dcdfbddbf46a4397fc`.
- Local verification manifest (not committed):
  `build/release-verification/2.0.0-alpha05.1-v2.json`.

## v3: preserve simulated system-UI insets

Prepared `2.0.0-alpha05.1-v3` on November 23, 2026 by porting upstream PR #2562,
implementation `73097afb1c9826fe4fd4f58e261aed3498ccdf5a`, onto v2. The inherited
snapshot fixture comes from its #2561 parent; only the final fixed expectations
are included. Layoutlib remains 16.2.3 and the FIFO/session fixes remain intact.

- Preserve the matching preview window's seeded inset sources across empty
  `BridgeWindowSession.relayout` replies; respect populated upstream replies.
- Configure only the synthetic Layoutlib wrapper to avoid double fitting or
  duplicate system-bar painting. No application padding workaround is added.
- Include six standard Compose snapshot expectations covering visible/hidden,
  dark/translucent, landscape and repeated captures, and preload the fifth patch
  target in the existing fresh-JVM regression.
- The port passes runtime, API, formatting and all sample snapshots on Layoutlib
  16.2.3. Full release/consumer verification and publication will be recorded below.

This follows upstream's NORMAL-mode scope. The known resizing-mode bottom-inset
limitation and complete cutout/dialog/legacy coverage are not claimed fixed.


### v3 final verification and publication

- Full `check --continue`: **292 tests, 286 passed, 6 skipped, zero failures/errors**,
  on local macOS JDK 21 and again on JDK 24. Test launcher metadata confirms 24.0.2
  for the latter; the temporary JDK was not installed system-wide. API/formatting
  and all sample snapshots passed, including the six new inset expectations.
- CI run `45271908026`: Windows and Linux passed on both JDK 21 and JDK 24,
  and docs passed. GitHub macOS jobs never started because hosted arm64 runners
  were unavailable; these jobs were cancelled by the service. This is recorded
  as an infrastructure exception, not a successful hosted macOS run.
- ICApp's saved-lists loading control failed with published v2 and passed its
  unchanged original golden with the candidate v3 implementation. Storefront's
  same 997-test suite improved from 151 to six image mismatches without changing
  expected images. No application-side inset padding was added.
- Signed deployment `37cf0c8c-cbaa-494f-8710-8c5cae74bbb3` reached **PUBLISHED**.
  Verified all nine publications / 82 artifact and signature files against the
  signed local outputs, plus canonical runtime/marker/library URLs.
- GitHub pre-release/tag `2.0.0-alpha05.1-v3` points to release code
  `784c5880b61ca3109e83bfb8d568d83c46385568`.
- Local verification manifest: `build/release-verification/2.0.0-alpha05.1-v3.json`.
