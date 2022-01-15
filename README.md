# cougar
Cougar is an efficient & scalable multi-proxy queue system.
- Cougar is written in kotlin, requires a [redis](https://redis.com) server, and uses [gradle](https://gradle.org) as its build management tool.

### Credits:
- [GrowlyX](https://github.com/growlyx) - wrote the whole project along with scala store & flavor
- [rawr](https://github.com/devrawr) - helped with flavor

## Modules:
Cougar contains three different modules, an explanation of each module is listed below.

| Module  | What does it do? | Dependencies |
| :------------ |:---------------| :------|
| common | Contains all code shared across our other modules. | N/A |
| server | Handles queue iteration, updates, and player data processing. | a redis server |
| spigot | Handles everything the player sees, player priorities, and player version mapping. | scala-store *(a redis server)* |
| velocity | Handles player disconnections at proxy-level. | scala-store *(a redis server)* |

## Features:
- Configurable settings at server-level.
- Allow players to switch servers without being kicked from their queue.
  - Allows players 
- Uses [Redis](https://redis.com) to both synchronize & cache the latest server instance & queue data.
- Server-intensive processes handled asynchronously.
  - More information on this can be seen within the `scala-store` project: [click to view](https://github.com/scalagg/scala-store)
- Queues automatically pause/resume based on its targetServer status.
  - `whitelisted`: Only whitelisted players (on its target server) are able to connect.
  - `offline`: All players are unable to join until the first update packet is sent from the targetServer on its load.
- An easy-to-use & basic API to access queue information, along with its target server instance.

## Configuration:
Cougar handles language configuration through multiple [json](https://json.org) files located within the server runtime directory. All configuration files are listed below:

`iteration.json`:
- `iterationDelay`: The speed at which the queue task iterates. *(in milliseconds)*
- `updateStreamIterationDelay`: The speed at which the queue broadcast task iterates. *(in milliseconds)*

```json
{
  "iterationDelay": 250,
  "updateStreamIterationDelay": 5000
}
```

`language.json`:
- `updateMessage`: The message (with bukkit color codes) which will be parsed & sent to the player in the queue.
- `{whitelisted,offline}Addition`: The text which will be added onto the updateMessage if the server is: offline, whitelisted.
```json
{
  "updateMessage": "\n§eYou're position §b{pos}§e/§b{max}§e in the §b{queue}§e queue.\n§7Ranks get a higher priority in queues: §f§ostore.some.server",
  "whitelistedAddition": "§cThe server you're queued for is currently whitelisted.",
  "offlineAddition": "§cThe server you're queued for is currently offline."
}
```
![update message](https://media.discordapp.net/attachments/932023806069997589/932026855492571136/unknown.png)

`queues.json`:
- `targetServerId`: The internal id of the server this queue is attached to.
- `fancyDisplayName`: The display name players will view when receiving queue updates.
- `allowedProtocols`: The list of allowed protocol version numbers.

```json
{
  "queues": [
    {
      "targetServerId": "something",
      "fancyDisplayName": "Something Queue",
      "allowedProtocols": [5, 47]
    }
  ]
}
```

## Pricing:
- **JARs**: `$35`
- **Source**: `no L`
