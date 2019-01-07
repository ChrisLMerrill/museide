# MuseIDE Change Log

## 0.9
- add: Run a test suite in the GUI.
- add: After running a test suite in the GUI, user can choose to view the result and event log for each test
- add: IdListTestSuite editor supports adding ids of test suites
- add: enhance expression grammar for simpler usage: <page.element> 
- fix: Ater deleting a step, undoing, redoing and undoing again results in 2 copies of the un-deleted step.
- fix: running a test in one editor results in the other test editors acting like a test is running.
- fix: when importing SelniumIDE tests, if the last-used folder no longer exists, the select-files dialog does not open.
- improve: docking behavior (no floating, navigator not maximizable)
- improve: start running test suite immediately, don't make user press 2 play buttons
- improve: StepTree is smarter about auto-collapsing only the branches that it auto-expanded

## 0.8
- add: Import button in project navigator for importing SeleniumIDE tests
- fix: remove the unnecessary (and unimplemented) "Create Project" button
- fix: vertical widget spacing on LocalBinaryDriverProviderEditor
- add: import ShadowboxFx project for in-window modal dialogs (SeleniumIDE test import)
- enhance: Remember most recent path
- enhance: add support for colored step icons
- enhance: Detect version and show in the window title

## 0.7
- new: Editor for Browser Providers
- new: Create Test Suite subtypes from create dialog
- new: Editor for Id-List Test Suite
- new: Editor for Parameterized Test Suite

## 0.6
- new: add IDE (single-window integration)
- new: add Windows installer and auto-updater
- enhance: Log diagnostics to a file instead of console ($HOME/.muse/museide.log)
- new: add editor for browsers (SeleniumBrowserCapabilities)

## 0.5
- add project navigator (navigate command)

## 0.4
- add support for a subsource of type Map (e.g. the CallFunction step)
- fix a variety of bugs in the expert value source editors with null and primitive values
- show the subsource description in the single-subsource fields' prompt text
- update to latest FontawesomeFX and ControlsFX
- fix "more>" link for editing value sources in the Web Page editor
- add editor for Context Initializer Configurations

## 0.3
- complete re-write of the Step and Value Source editors to be more user friendly
- implement unit tests for much of the new UI and some of the old

## 0.2

- fix reverse-ordering bug when pasting steps in the editor
- fix label on pause button
- set focus and selection when adding a new named values source in value source editor, default variables tab and page editor  

## 0.1

- initial release