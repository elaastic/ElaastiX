# ElaastiX

## Getting started
TL;DR:
- Install [Mise](https://mise.jdx.dev)
- Install Docker
- Run `mise install`
- Open in IntelliJ IDEA Ultimate and configure the following:
  - Java SDK `\\wsl.localhost\<Distro>\home\<wsl-username>\.local\share\mise\installs\java\temurin-24`
  - Node `<Distro>: /home/<wsl-username>/.local/share/mise/installs/node/24/bin/node` (with `pnpm` as package manager)

### Editor
The project is configured for IntelliJ IDEA Ultimate. In other editions of IntelliJ IDEA, Spring support is limited
and there is no support for JavaScript, TypeScript, and Vue (but support for these is available in WebStorm).

If you're not using a JetBrains IDE, additional configuration might be required.

### Mise
The project uses [Mise](https://mise.jdx.dev) for managing all the tools required to work with the project.
Just run `mise install` in the project directory.

> [!NOTE]
> While there is an IntelliJ plugin for setting up the JDK and Node version automatically, it doesn't support Windows.
> You'll need to configure the editor manually to use:
> - Java SDK `\\wsl.localhost\<Distro>\home\<wsl-username>\.local\share\mise\installs\java\temurin-24`
> - Node `<Distro>: /home/<wsl-username>/.local/share/mise/installs/node/24/bin/node` (with `pnpm` as package manager)

### Docker
Docker with the Compose plugin is also required to run the project. It is not managed by Mise by default, due to the
overwhelming majority of developers having Docker already installed.

If you don't already have Docker, it can be installed with `mise`, locally or globally via the `-g` flag.
Simply run `mise use docker-cli docker-compose` and let it cook ;)

## Project structure
Files and folders that aren't worth of interest are not mentioned below. If it's not there, then it's most likely not
something you should have to worry about.
```
ElaastiX
├── /.config                    Repository and project config files. Loosely follows the XDG convention.
├── /.github                    GitHub-specific configuration files
├── /build-logic                Convention plugins (Gradle)
├── /gradle                     Gradle-related files
│   └── libs.versions.toml      └── Version catalog
└── /server                     Spring Boot application monolith
```
