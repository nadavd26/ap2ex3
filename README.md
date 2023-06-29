This is our whatsapp android application.
The application was written using Android Studio.
You can change the server url and the theme mode (dark or light) in the settings.
This app using notiflications.
The server side was written in JavaScript with nodeJS.
The server has an api that allows to create an account, creating chats, sending messages and getting information about them.
Also, when closing the server, the data won't go away because it is saved in mongo-db.
How to run:

To run the server:
cd {project directory}
cd server
npm install (to download the node_moduls)
node server.js the server runs at port 5000, so if you want to use the web version you can reach it at localhost:5000.

To run the android application
open the project directory using android studio:
open android studio -> new project from version control -> write in url the url in details.txt.
then, you need to run the project:
choose the emulator you want to run it and run it by clicking the green triangle points right (or run it in an other way).

NOTE: if you want to get notiflications to your android device, you need to give our app a notiflication permission,
you can do it in the settings of your android device.
