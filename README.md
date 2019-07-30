# Katalon Notes

![KatalonNotes_140px](https://user-images.githubusercontent.com/16775806/61437443-48d2a200-a967-11e9-9bd3-68edeaa3724a.png)

**By**: [Quynh Lam](https://www.behance.net/luongquynha1e6)


Katalon Notes is a Katalon plug-in that allows you to take notes in-app easily


### Features (existing and upcoming)
- [X] Markdown support.
- [X] In-note links to jump between sections.
- [X] Render web pages in-app on link.
- [X] Hiearchical note organization.
- [X] Automatic saving when navigating between notes.
- [X] Switch between different databse folders.
- [ ] Link notes to artifact.
- [ ] Hot key to save content when making notes.
- [ ] Integration with Trello.

![katalon-notes](https://user-images.githubusercontent.com/16775806/62149293-9961dc00-b325-11e9-8a47-0da895d4d1ad.gif)


### 1.0.7 - Stable.

- Fixed crtitical bug relating to insert/update/delete operations that do not work as intended.
- Added ability to switch between databases by choosing folders.



### 1.0.6 - Stable.

From version 1.0.6 major bugs are fixed including UI freezes when interacting on the note repository, root note cannot be edited, note structure is rendered incorrectly. From now on I will focus on making sure existing features work as intended as well as working on new features. Thank you for your support !


*This plug-in is open-sourced [here](https://github.com/minhthanh3145/katalon-notes). Documentation is available [here](https://github.com/minhthanh3145/katalon-notes/issues/1). If you have any issues you can submit them [here](https://github.com/minhthanh3145/katalon-notes/issues)*


## Build

Requirements:
- JDK 1.8
- Maven 3.3+

`mvn clean package`

## Usage
- Install the `Katalon Studio v6.1.5 or later`.
- Go to *Plugin* > *Install Plugin* and select the generated jar file.
- Click on Icon > Open Katalon Notes.
- Start taking notes
