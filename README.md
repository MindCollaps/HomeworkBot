# HomeworkBot
Ein auf Java basierender Discord und Telegram Bot, der dem Project [aufgabenMamanger](https://github.com/BetaHuhn/aufgabenManager) zugehört.

## Features
- Erinnert an anstehende Aufgaben
- Postet Aufgaben, die neu Erstellt wurden

## Stuff
### Environment variables
####API port:
HWBOT_PORT
## Admin API
### Aufgaben
#### Nachricht an alle Klassen senden
Anfrage:
```json
{
    "req": "postall",
    "msg": "Neue Version steht an leudeee UwU"
}
```
Antwort:
```json
{
    "status": "200",
    "response": "success"
}
```
#### Nachricht an bestimmte Klasse senden
Anfrage:
```json
{
    "req": "postclass",
    "c1": "Abi fällt aus",
    "c2": "12asf542fga385834"
}
```
Antwort:
```json
{
    "status": "200",
    "response": "success"
}
```
#### Aufgabe posten
Anfrage:
```json
{
    "req": "post",
    "c1": "12asf542fga385834"
}
```
Antwort:
```json
{
    "status": "200",
    "response": "success"
}
```
### Admin Stuff
#### Set Discord token
Anfrage:
```json
{
    "req": "setdisctoken",
    "c1": "12asf542fga385834"
}
```
Antwort:
```json
{
    "status": "200",
    "response": "success"
}
```
#### Set Telegram token
Anfrage:
```json
{
    "req": "setdisctoken",
    "c1": "12asf542fga385834"
}
```
Antwort:
```json
{
    "status": "200",
    "response": "success"
}
```
#### Set Telegram name
Anfrage:
```json
{
    "req": "settelename",
    "c1": "@ichbinvollderboteyyy"
}
```
Antwort:
```json
{
    "status": "200",
    "response": "success"
}
```
#### Update Reminders
Anfrage:
```json
{
    "req": "updateremind"
}
```
Antwort:
```json
{
    "status": "200",
    "response": "success"
}
```
#### Fetch latest update
Anfrage:
```json
{
    "req": "fetchupdate"
}
```
Antwort:
```json
{
    "status": "200",
    "response": "success"
}
```
