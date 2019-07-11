# Katalon Notes

This plug-in allows you to take notes within Katalon Studio without having to open a third-party applications.
            
- **Markdown** syntax supported.
- **Notes can be organized hierarchically**: Notes have parent-child relationship.
-  **Local database**: Database are stored locally so there's no information leakage. Database are saved in folde *katalon_notes* folder within your current project. 
-  **Automatic saving**: When switching between notes, changes are automatically saved. You can also manually save changes on the current note. 

## UI mockup
![untitled_page](https://user-images.githubusercontent.com/16775806/61060753-403d1180-a425-11e9-9d92-95276a587ff8.png)

## Screenshot
<img width="1439" alt="Screen Shot 2019-07-11 at 8 32 20 PM" src="https://user-images.githubusercontent.com/16775806/61060738-39160380-a425-11e9-9dc7-6b703437d4c1.png">


## Build

Requirements:
- JDK 1.8
- Maven 3.3+

`mvn clean package`

## Usage
- Install the `Katalon Studio v6.1.5 or later`.
- Go to *Plugin* > *Install Plugin* and select the generated jar file.
- A `hello` message `Event Log` tab after the installation completed. 
- Execute a test suite and wait for a summary message.
