# Sectorized Plugin

This repository contains the Sectorized plugin for Mindustry dedicated servers.

## Development Environment (Mindustry v7)

This project is configured for:

- Mindustry API: `v157`
- Gradle wrapper: `8.10.2`
- Build JDK: `17+` (toolchain)
- Output bytecode: Java `8`

### Requirements

1. Install JDK 17 or newer.
2. Install Git.
3. Clone this repository.
4. Have a local Mindustry server directory available for deployment.

### Build Commands (Windows)

```powershell
.\gradlew.bat clean jar
```

### Convenient Hosting (Auto-Restart)

For stable hosting, run the server through the included wrapper so the process comes back up automatically after round-end restart.

Windows:

```powershell
.\host-server.bat
```

Linux:

```bash
./host-server.sh ./mindustry-server-v7
```

The Sectorized gamemode now auto-starts on server boot, so no manual `sectorized` command is required after restarts.

### Deploy Plugin Jar To Server Mods Folder

Option A: Provide your server root directory (plugin deploys to `config/mods`).

```powershell
.\gradlew.bat deployPlugin -PmindustryServerDir="C:/path/to/mindustry-server-v7"
```

Option B: Provide the exact mods directory.

```powershell
.\gradlew.bat deployPlugin -PmindustryModsDir="C:/path/to/mindustry-server-v7/config/mods"
```

### Recommended Update Workflow

1. Build and deploy the plugin.
2. Start the Mindustry server.
3. Watch logs for API errors and deprecations.
4. Fix compile/runtime issues from the most fragile areas first:
	- `sector` manager/network hooks
	- `faction` event handlers
	- world generator integration

### Stable Baseline Checklist

1. `plugin.json` has `minGameVersion: 157`.
2. Build passes: `./gradlew(.bat) --warning-mode all clean compileJava`.
3. Plugin deploys into `config/mods` and server logs show `1 mods loaded`.
4. Startup logs include Sectorized config print and `Opened a server on port ...`.
5. End-of-round restart triggers, late joiners are kicked with server-restarting reason, and wrapper brings process back up.
6. `config/mods/config/config.json` contains `experimentalMapsEnabled` (default `false`).

## Notes

- The project previously targeted older Mindustry versions; upgrading gameplay code may require API adjustments.
- Runtime configuration files are still required by the plugin logic.