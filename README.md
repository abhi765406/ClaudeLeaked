# Claude Code Mobile — Android port

A lightweight native Android companion inspired by the public-facing account/API configuration ideas in `lqvp/claude-code_unlimited`.

## Included
- Multiple API profiles
- Active profile switching
- Custom Anthropic-compatible base URL
- Optional API key per profile
- Endpoint connectivity test
- Open Claude web client
- Local-only profile storage using Android SharedPreferences
- Android 8.1+ support (API 27)
- Dependency-free app code for a small APK
- GitHub Actions build using the same AGP 8.5.0 family identified in the supplied reference APK

## Important
This is a clean Android implementation, not a copy of the leaked Claude Code source. Android cannot directly run the original Bun/Node CLI architecture as an ordinary native APK. The upstream repository is a TypeScript/Bun CLI with many Node/native dependencies.

It intentionally does not implement provider rate-limit bypass/reset mechanisms.
