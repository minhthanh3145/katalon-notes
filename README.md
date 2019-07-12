# Katalon Notes

This plug-in allows you to take notes within Katalon Studio without having to open a third-party applications.

### Features
- **Markdown** syntax supported.
- **Notes can be organized hierarchically**: Notes have parent-child relationship.
-  **Local database**: Database are stored locally so there's no information leakage. Database are saved in your current project's folder. You can copy & paste the database file into other projects so that the notes are imported automatically.
-  **Automatic saving**: When switching between notes, changes are automatically saved. You can also manually save changes on the current note. 


### Upcoming features
In no particular order, I am planning to add some more features including:
- **Database config**: UI to select database's name, location with import/export functionality. 
- **UI improvement**: I reckon the most important thing in a note-taking app is its elegance and purpose-aware UI so I will try to make small improvements.
- **Link notes to test artifacts**: So that you view and create notes associating with certain test artifacts (Test Object, Test Case, Test Suite, Test Suite Collection).
- **Note query mechanism**: A search engine that can filter notes based on text, content or links to test artifacts.


<img width="1439" alt="Screen Shot 2019-07-11 at 8 32 20 PM" src="https://user-images.githubusercontent.com/16775806/61060738-39160380-a425-11e9-9dc7-6b703437d4c1.png">

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
