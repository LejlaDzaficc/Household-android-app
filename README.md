# Household app
In today's very busy world, the real-life communication inside of a household/family is very minimalistic. Most of the time, nobody is home and we resort to using various communication technologies.

**Household** is a family-oriented android app. It's main goal is to simplify everyday communication between household members by providing a chat, shared notes, shared to-do lists, reminders, etc. or in other words, all the utilities needed in communication for a household to function properly. 
At the same time it provides a very simple interface, for which the most senior members of a household could get used to very quickly.

*Note: This is a project created for learning purposes only and the main goal behind this project is learning android development.* 
## Overview/Usage
To keep the app simple and focused on it's purpose, an user can be a member of **only one** household. Household is an alias for a group. An user can create a household or join an existing one by typing the household ID which was provided by the creator of the household. Once the user is connected to the server, chat with other household members can be started.

![Recordit GIF](https://media.giphy.com/media/U7K6Fhp63z7tjOpb6l/giphy.gif)
*Creating one household*

![Recordit GIF](https://media.giphy.com/media/S5Q0Mwh4lhua4cyuHK/giphy.gif)
*Joining the existing household*

A lot more features are planned and can be viewed in the [Future plans](#future-plans) section.
## Backend
Upon opening, the app connects to the backend server and all future communication  goes through the server like:
- Creating households
- Joining households
- Exchanging chat messages
 

Client sends requests to the server. The server replies with a response for some requests (e.g. joining households, which is needed in order for the client to know whether his *join* request has succeeded), while for the others it only does forward action (e.g. chat messages).

For backend purposes, the server implementation can be found on the following link:
[https://github.com/LejlaDzaficc/Household-server]

Another client (console application), which is used only for testing purposes, can be found here:
[https://github.com/LejlaDzaficc/Household-console-client]

## Future plans
- Custom household name
- Show time when a chat message has been sent
- Shared TODO list which every household member can edit
    - items in the list would have date and time, and reminders will be triggered half an hour before that time, OR
    - a separate reminders list
- Push notifications
