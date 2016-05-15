spigot-js
=========

I wanted to play around with Nashorn after a friend told me how nifty it was.
This small project is the result.

Yes, the code quality can be better, however it achieves it's goal and that's all I was aiming for.

## Scripting

No protection is built into this plugin, you are responsible for any scripts you
execute and are thus responsible for any consequences.

Do not run untrusted scripts.

### Variables

You are able to access the following variables through standard JS syntax.
The variables listed are provided by spigot-js, if you are looking for ones that
Nashorn provides then please refer to the relevant documentation.

|Variable Name|Description|
|-------------|-----------|
|`__DIRECTORY__`|The absolute path of the `scripts` directory located in `plugins/SpigotJS`.|
|`manager`|The `ScriptInterface` that the script uses to interact with the server.|
|`server`|The `Server` instance.|
|`logger`|The host plugin `Logger`.|
|`events`|A `Map` of aliases for the FQNs of Bukkit `Event` implementations.|
|`plugin`|The host `Plugin`.|

#### Event aliases

In an attempt to make usage of the `ScriptInterface` class feel more like natural JavaScript
I opted for `on(String, function(event))` style code for subscribing to events. This approach
would result in using something along the lines of the following code for subscribing to `PlayerJoinEvent`.

```js
manager.on("org.bukkit.event.player.PlayerJoinEvent", function(event) {
    ...
});
```

Nobody wants to constantly type out event FQNs though, so we rounded up all the standard events
and threw them into the `events` map!

```js
manager.on(events.PlayerJoinEvent, function(event) {
    ...
});
```

### Reloading Scripts

spigot-js also utilises the `WatchService` class to listen for filesystem events, this means that
your scripts will automatically be:

- Loaded when a script file is created.
- Reloaded when the file contents are modified.
- Unloaded when the file is deleted.

You can also reload scripts manually by execute the `reload-scripts` command.
You cannot disable automatic reloading at the time of writing.

---

More coming soon.