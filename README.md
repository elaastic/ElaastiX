# Elaastic

> Engage your learners, reveal their thinking

Elaastic is a pedagogical activity orchestration platform, empowering teachers with tools to provide personalised and
collaborative learning experiences to their learners.

ElaastiX (this repository) is the software platform that powers Elaastic and its ecosystem.

## Getting started
TL;DR:
- Install Docker
- Install [Mise](https://mise.jdx.dev)
- Clone the repository and `cd` into it
- Run `mise trust && mise install && mise prep`
- Open in IntelliJ IDEA Ultimate

> [!CAUTION]
> On Windows, it is strongly recommended to work within WSL. Working on Windows directly is not guaranteed to work.
> Given the limited bandwidth of the project maintainers, support for native Windows is best-effort only.

### Docker
Docker with the Compose plugin is also required to run the project.
Please see the [Docker Documentation](https://docs.docker.com/engine/install/) for how to install it.

### Mise
The project uses [Mise](https://mise.jdx.dev) for managing all the tools required to work with the project. Make sure to trust
the project with `mise trust`, and then run `mise install`.

> [!CAUTION]
> The `mise prep` command will also configure important aspects of the project. Skipping this step is
> will result in an **INCOMPLETE** development environment that will **NOT WORK**.

The IntelliJ plugin should configure everything for you automatically. You may need to adjust in IntelliJ
the [Mise Settings](jetbrains://idea/settings?name=Tools--Mise+Settings) when working via WSL.
Instructions are available within the settings screen directly.

#### Command aliases
If you have Mise configured in your shell, you can use aliases that'll automagically proxy execute the command on the
appropriate container. **The relevant service must be already running, or you'll get an error.**

The following commands automatically become available:
- `psql`
- `garage`

### Editor
The project is configured for IntelliJ IDEA Ultimate. In other editions of IntelliJ IDEA, Spring support is limited
and there is no support for JavaScript, TypeScript, and Vue (but support for these is available in WebStorm).

> [!IMPORTANT]
> The first time opening the project might be a bit weird. IJ tries to load the Gradle project before important
> project settings have settled causing errors.
>
> You might encounter an error saying there is no JDK configured for Gradle. If that happens, you should be able to
> trigger a Gradle sync again, and it should go through just fine this time. If it doesn't work, restart IntelliJ.
> If it still doesn't work, go manually configure the JDK to the one installed by Mise in `Project Structure`.

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

### Running the project
You can run the project using `just start`, or via the run configurations available in IntelliJ (recommended).

The various parts of the app are reachable on the following endpoints (in development):
- Nuxt webapp: http://localhost:3000
- Spring REST API: http://localhost:8080 (also available at http://localhost:3000/api when the Nuxt webapp is running)
- Garage S3 server: http://localhost:3900

When running via `just`, a tmux session is started. To switch between tabs, use `Ctrl+B` and then numbers `0-9` to
select a tab. See [tmux(1)] for more information. To tear everything down, run `just stop`.

To see all the available commands in the project, run `just --list`.

## Project structure
Files and folders that aren't worth of interest are not mentioned below. If it's not there, then it's most likely not
something you should have to worry about.
```
ElaastiX
├── .config/                    Repository and project config files. Loosely follows the XDG convention
├── .github/                    GitHub-specific configuration files
├── build-logic/                Convention plugins (Gradle)
├── commons/                    Code shared throughout the Elaastix projects
├── docs/                       Documentation of the project
│   ├── specs/                  ├── Specifications of the project (the "what")
│   └── technical/              └── Specifications of the technical implementation (the "how")
├── frontend/                   Nuxt web application (see: https://nuxt.com/docs/4.x/directory-structure)
│   └── layers/                 └── Nuxt layers (see: https://nuxt.com/docs/4.x/getting-started/layers)
│       └── .../                    └── See `Nuxt layers` below for the list of layers in the Nuxt app
├── gradle/                     Gradle-related files
│   └── libs.versions.toml      └── Version catalogue
└── metamodel/                  Metamodel library package
└── server/                     Spring Boot application monolith
```

### Nuxt layers
- `0.base`: Core infrastructure, components, styles, etc.
- `1.xxx`: Layers implementing a given area of the ElaastiX webapp
  - `author`: Authoring tools for teachers
  - `orchestrator`: Scenario composition and management
  - `dashboard`: Teacher's dashboard for ongoing and completed activities
  - `player`: Pedagogical activity player for learners
  - `account`: Account management
- `2.xxx`: Layers implementing a pedagogical activity module
  - `question`: Encompasses 2 types of activities
    - Answering one or more questions
    - Changing (or keeping) a previous answer to a question
  - `judge`: Peer review activity
  - `chat`: Anonymous chat between peers
- `9.admin`: Platform administration features

### Compose services
> [!NOTE]
> This table documents which services are required **in production**. In development, please refer to the
> [`compose.yaml` file](./compose.yaml) directly.

| Service name     | Description                               | Required?                                                                   |
|------------------|-------------------------------------------|-----------------------------------------------------------------------------|
| `server`         | Spring monolith                           | **Elaastix itself**                                                         |
| `frontend`       | Nuxt.js webapp                            | **Elaastix itself**                                                         |
| `postgres`       | PostgreSQL 18 server                      | **Y**                                                                       |
| `storage`        | Garage (S3-compatible)[^s3-compat] server | **Y**                                                                       |
| `otel-collector` | OpenTelemetry collector.                  | N, but **expected by default**. See [disable telemetry](#disable-telemetry) |

[^s3-compat]: Elaastix is tested with Garage specifically, and the first-party staging and production deployments use
it as well. It should, however, work perfectly fine with any S3-compatible solution from any vendor.

#### Default development credentials
| Service    | Username         | Password         | Additional information                                                  |
|------------|------------------|------------------|-------------------------------------------------------------------------|
| PostgreSQL | `elaastix`       | `elaastix`       | Database: `elaastix`                                                    |
| Garage     | *Auto generated* | *Auto generated* | Generated during `mise prep`, saved in `.config/garage/credentials.env` |

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

| Profile name | Description                                                                                                                                                                         |
|--------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `develop`    | Main development profile. Enables development tools and helpers, seeds the database with dummy data, and uses [default credentials](#default-development-credentials) for services. |
| `container`  | Configures the app for running inside a containerised environment. Expects service names to match the ones define in the [project Compose's specification](#compose-services).      |

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

### Nuxt.js webapp
The Nuxt development server can be started via Compose. Within IntelliJ, the following run configurations exist:
- `Nuxt.js app`: starts the Nuxt development server. **Don't use the debugger on it, it'll attach to the dev server.**
- `Debug Nuxt.js app`: opens the Nuxt app in a browser. Can be run in Debug, which will let you inspect the console,
   variables, and set breakpoints within the IDE directly. It will use a blank browser profile when debugging.

Chrome is used, as the IDE uses the Chrome DevTools Protocol to power its debugging features. While IntelliJ mentions
'Chrome' specifically, you can configure any Chromium-based browser (which is almost all browsers...) instead.
Go to Settings > Tools > Web Browsers and Preview.

> [!IMPORTANT]
> Please do not modify the existing run configurations! These are shared in the project and modifications will impact
> all other developers. Make a duplicate of the config and edit this local, private copy instead.

#### Debug from the IDE
When debugging the Nuxt.js app, breakpoints set within the IDE will automatically work and let you observe the context
from the debug window. You can also see the console directly.

> [!NOTE]
> Simply running the `Debug Nuxt.js app` configuration will not enable the in-IDE debugging capabilities; make sure to
> run via the debug button within the IDE.

#### Storybook
Right now, it's required to run Storybook separately and there is no configuration to do it easily. We're waiting on
upstream to fix bugs with the Nuxt integration...

In the meantime, the easiest way is to run Storybook locally, via `pnpm run storybook`.

[tmux(1)]: https://man.archlinux.org/man/tmux.1
