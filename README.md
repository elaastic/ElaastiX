# ElaastiX

## Getting started
TL;DR:
- Install [Mise](https://mise.jdx.dev)
- Install Docker
- Run `mise install`
- Open in IntelliJ IDEA Ultimate

### Editor
The project is configured for IntelliJ IDEA Ultimate. In other editions of IntelliJ IDEA, Spring support is limited
and there is no support for JavaScript, TypeScript, and Vue (but support for these is available in WebStorm).

> [!TIP]
> Latest versions of IntelliJ IDEA are strongly recommended (2025.3+). On Windows systems, WSL via the **native mode**
> is recommended. **Make sure to add the appropriate [exclusions](windowsdefender://exclusions) to Microsoft Defender**,
> or you'll likely experience major slowdown issues.
>
> <details>
> <summary>List of entries to exclude in Microsoft Defender</summary>
>
> - IntelliJ IDEA itself: `%localappdata%/JetBrains`
> - Gradle (WSL): `\\wsl.localhost\<Distro>\home\<wsl-username>\.gradle`
> - pnpm (WSL): `\\wsl.localhost\<Distro>\home\<wsl-username>\.local\share\pnpm\store`
> - Docker: `\\wsl.localhost\<Distro>\var\lib\docker`
> - Project: `\\wsl.localhost\<Distro>\<project path in WSL...>`
>
> </details>

If you're not using a JetBrains IDE, additional configuration might be required.

### Mise
The project uses [Mise](https://mise.jdx.dev) for managing all the tools required to work with the project. Make sure to trust
the project with `mise trust`.

The IntelliJ plugin should configure everything for you automatically. You may need to adjust in IntelliJ
the [Mise Settings](jetbrains://idea/settings?name=Tools--Mise+Settings) when working via WSL.
Instructions are available within the settings screen directly.

### Docker
Docker with the Compose plugin is also required to run the project.
Please see the [Docker Documentation](https://docs.docker.com/engine/install/) for how to install it.

## Project structure
Files and folders that aren't worth of interest are not mentioned below. If it's not there, then it's most likely not
something you should have to worry about.
```
ElaastiX
├── .config/                   Repository and project config files. Loosely follows the XDG convention.
├── .github/                   GitHub-specific configuration files
├── build-logic/               Convention plugins (Gradle)
├── frontend/                  Nuxt web application
├── gradle/                    Gradle-related files
│   └── libs.versions.toml      └── Version catalogue
└── server/                     Spring Boot application monolith
```

### Compose services
| Service name     | Description              | Required?                                                                   |
|------------------|--------------------------|-----------------------------------------------------------------------------|
| `server`         | Spring monolith          | **Elaastix itself**                                                         |
| `frontend`       | Nuxt.js webapp           | **Elaastix itself**                                                         |
| `postgres`       | PostgreSQL 18 server     | **Y**                                                                       |
| `otel-collector` | OpenTelemetry collector. | N, but **expected by default**. See [disable telemetry](#disable-telemetry) |

#### Default credentials
| Service    | Username   | Password   | Additional information |
|------------|------------|------------|------------------------|
| PostgreSQL | `elaastix` | `elaastix` | Database: `elaastix`   |

### Spring monolith

#### Hot Reload
When running via IntelliJ, classes can be hot-reloaded easily thanks to HotSwap. Simply do `Ctrl+F10` (or click the
update button in the console), and the IDE will build the project and update the class on-the-fly. If the changes cannot
be injected this way, Spring will automatically fall back to a [warm start](https://docs.spring.io/spring-boot/reference/using/devtools.html#using.devtools.restart.restart-vs-reload) instead.

See [IntelliJ's documentation](https://www.jetbrains.com/help/idea/altering-the-program-s-execution-flow.html#reload_classes) for more information.

> [!NOTE]
> There might be a widget that shows up in the editor proposing to hot swap in the editor. For some reason that haven't
> been investigated further, it appears to malfunction and to not reload anything. Prefer sticking to the update button
> or the keyboard shortcut which appear to be more reliable.
>
> This behaviour was observed on IntelliJ Ultimate 2025.3.1.1.

##### Via Compose
When running via Compose only (instead of through the IntelliJ run configuration), the code will be automatically be
rebuilt every time it changes. Upon successful builds, Spring DevTools will automatically detect the changes and
perform a warm start. Beware, build errors are not surfaced nor logged.

#### Profiles
> [!IMPORTANT]
> When enabling both `develop` and `container`, make sure `container` comes **after** `develop` so the correct
> configuration values are used.
>
> <details>
> <summary>Why does the order matter?</summary>
>
> The order defines the config load order, and therefore which configuration has the last word when both try to
> configure the same property. `develop` specifies the database hostname as `127.0.0.1`, while `container` sets it
> to `postgres`. In order for the config to have the correct host configuration, `container` must come after `develop`.
>
> </details>

| Profile name | Description                                                                                                                                                                    |
|--------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `develop`    | Main development profile. Enables development tools and helpers, seeds the database with dummy data, and uses [default credentials](#default-credentials) for services.        |
| `container`  | Configures the app for running inside a containerised environment. Expects service names to match the ones define in the [project Compose's specification](#compose-services). |

#### Miscellaneous

##### Local configuration
To run Spring with a different configuration, the easiest way is to make a run configuration with the appropriate
environment variables set. There is no local-only configuration file; it is not advisable to directly alter the
versioned configuration files which are first and foremost meant to set the **default** configuration.

> [!IMPORTANT]
> Please do not modify the existing run configurations! These are shared in the project and modifications will impact
> all other developers. Make a duplicate of the config and edit this local, private copy instead.

##### Disable telemetry
> [!TIP]
> Telemetry is disabled by default in development. To enable it, explicitly revert the changes described below by
> assigning `true` instead of `false`.

Use the following configuration properties to disable telemetry reporting:

```yaml
management:
    otlp.metrics.export.enabled: false
    logging.export.otlp.enabled: false
    tracing.export.otlp.enabled: false
```
