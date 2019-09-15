# iOS WCAG 2.1 Validator

## Prequisites
- Homebrew (install [here](https://brew.sh/))
- Xcode 9.4 or newer (install from MAS)
- Node.js (`brew install node`)
- Appium CLI (`npm install -g appium`)
- Carthage (`brew install carthage`)

## Setup and Usage
```
npm install

# in one tab run:
appium

# in another tab run:
npm start <path to .app/.iap>
```

When you run it, it gives you an URL with a randomized port to visit.  
With every visit, it will load the current view from the emulator, analyze it for accessibility problems and return a report in a form of HTML table with screenshots, with invalid elements highlighted in red.
