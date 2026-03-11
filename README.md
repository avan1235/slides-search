# SlidesSearch

[![Platforms](https://img.shields.io/badge/desktop-Windows%20%7C%20macOS%20%7C%20Linux-blue)](https://github.com/avan1235/slides-search/releases/latest)

[![Build](https://img.shields.io/github/actions/workflow/status/avan1235/slides-search/release.yml?label=Build&color=green)](https://github.com/avan1235/slides-search/actions/workflows/release.yml)
[![Latest Release](https://img.shields.io/github/v/release/avan1235/slides-search?label=Release&color=green)](https://github.com/avan1235/slides-search/releases/latest)

[![License: MIT](https://img.shields.io/badge/License-MIT-red.svg)](./LICENSE.md)
[![GitHub Repo stars](https://img.shields.io/github/stars/avan1235/slides-search?style=social)](https://github.com/avan1235/slides-search/stargazers)
[![Fork SlidesSearch](https://img.shields.io/github/forks/avan1235/slides-search?logo=github&style=social)](https://github.com/avan1235/slides-search/fork)

## Overview

SlidesSearch is a multi-platform presentation viewer with full-text search built with Kotlin Multiplatform and Jetpack Compose. It is designed for presenters who need to quickly navigate large slide decks by searching through slide titles and content in real time.

On desktop it opens a dual-screen presenter view: one window shows the control panel (search input, slide carousel, navigation) while a second window displays the current slide in fullscreen on the projector or secondary monitor. On the web it runs entirely in the browser as a single-page application.

Presentations are loaded from JSON files produced by the bundled `slidesParser` CLI tool, which converts PowerPoint (`.pptx`) files into the format the app understands.

## Features

- **Full-text search** — type any query to jump to matching slides instantly
  - Desktop: [Apache Lucene](https://lucene.apache.org/) with fuzzy matching and diacritics normalization (Polish characters: ą→a, ę→e, ó→o, ś→s, ź/ż→z, ć→c, ń→n, ł→l)
  - Web: case-insensitive substring matching with title priority
- **Keyboard-driven navigation** — control everything without a mouse
  - Arrow keys to move between slides (when not searching)
  - Type to enter a search query; Enter / Shift+Enter to cycle through results
  - Escape to clear the search; Backspace to edit the query
  - Search clears automatically after 10 seconds of inactivity
- **Dual-screen presenter mode** (desktop) — control window on the first screen, fullscreen slide display on the second
- **Slide carousel** — the control panel shows the current slide surrounded by the 5 preceding and 5 following slides
- **Responsive layout** — landscape mode places controls alongside slides; portrait mode stacks them vertically
- **PowerPoint converter** — `slidesParser` CLI extracts slide titles, body text, and speaker notes from `.pptx` files and writes a JSON file ready for the app

## Supported Platforms

- Desktop (Windows, macOS, Linux via JVM)
- Web (via WebAssembly and JavaScript)

Please note that for running unsigned version of macOS application, you need to temporarily disable Gatekeeper, so executing command
```shell
sudo xattr -dr com.apple.quarantine  /Applications/SlidesSearch.app
```

## Project Structure

```
slides-search
├─ desktopApp       # JVM desktop application (dual-screen presenter view, Lucene search)
│  └─ src
│     └─ jvmMain
├─ webApp           # Browser application (JS + WASM targets, naive search)
│  └─ src
│     └─ webMain
├─ sharedUi         # Compose Multiplatform UI shared by desktop and web
│  └─ src
│     └─ commonMain # App composables, SlidesViewModel, search state machine
├─ sharedLogic      # Pure Kotlin logic shared across all targets
│  └─ src
│     └─ commonMain # Data models (Slide, Presentation), SlideSearchEngine interface, NaiveSearchEngine
└─ slidesParser     # JVM CLI utility — converts .pptx files to JSON via Apache POI
   └─ src
      └─ main
```

## 📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Author
Maciej Procyk