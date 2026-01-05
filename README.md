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
> is recommended. Make sure to add the appropriate [exclusions](windowsdefender://exclusions) to Microsoft Defender,
> or you'll likely experience major slowdown issues.
>
> <details>
> <summary>List of entries to exclude in Microsoft Defender</summary>
>
> - IntelliJ IDEA itself: `%localappdata%/JetBrains`
> - Gradle (WSL): `\\wsl.localhost\<Distro>\home\<wsl-username>\.gradle`
> - pnpm (WSL): `\\wsl.localhost\<Distro>\home\<wsl-username>\.local\share\pnpm\store`
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
Docker with the Compose plugin is also required to run the project. It is not managed by Mise by default, due to the
overwhelming majority of developers having Docker already installed.

If you don't already have Docker, it can be installed with Mise. Simply run `mise use docker-cli docker-compose` and
let it cook ;). Use the `-g` flag if you want Mise to install Docker globally.

## Project structure
Files and folders that aren't worth of interest are not mentioned below. If it's not there, then it's most likely not
something you should have to worry about.
```
ElaastiX
├── /.config                    Repository and project config files. Loosely follows the XDG convention.
├── /.github                    GitHub-specific configuration files
├── /build-logic                Convention plugins (Gradle)
├── /frontend                   Nuxt web application
├── /gradle                     Gradle-related files
│   └── libs.versions.toml      └── Version catalog
└── /server                     Spring Boot application monolith
```
