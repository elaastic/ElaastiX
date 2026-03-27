#!/usr/bin/env just --justfile

_default:
	just --list

[group('validate')]
[doc('Checks that modified files are correctly formatted and/or pass linter tests.')]
check:
	hk check

[group('validate')]
[doc('Reformats and auto-fixes lint issues (when possible) in modified files.')]
fix:
	hk fix

[group('validate')]
[doc('Same as `check`, but for all files (not just modified ones).')]
check-all:
	hk check --all

[group('validate')]
[doc('Same as `fix`, but for all files (not just modified ones).')]
fix-all:
	hk fix --all

[group('run')]
[doc('Start everything up, opening a tmux session for observing and managing all running services.')]
start:
	docker compose up --build -d
	mise x tmux -- tmux new-session -d -s elaastix \; \
		new-window -c 'frontend' -n 'Frontend' 'pnpm run dev; $SHELL' \; \
		new-window -n 'Backend' 'docker compose attach server; $SHELL' \; \
		new-window -n 'Postgres' 'docker compose attach postgres; $SHELL' \; \
		new-window -n 'Garage' 'docker compose attach storage; $SHELL' \; \
		select-window -t:0 \; \
		setw -g mouse on \; \
		attach

[group('run')]
[doc('Stops everything, and kills the tmux session.')]
stop:
	docker compose stop
	-mise x tmux -- tmux kill-session -t elaastix

[group('misc')]
[doc('Cleans the repository to its original state. Will erase local configuration (e.g. mise.local.toml), but preserve the IJ shelf.')]
[confirm('This action WILL ERASE ALL LOCAL CONFIGS AND UNTRACKED FILES. IJ shelves will be preserved. Continue?')]
clean:
	docker compose down
	git clean -xdf --exclude .idea/shelf

[group('misc')]
[doc('Lists all files that would be cleaned by the clean task.')]
clean-dry:
	git clean --dry-run -xdf --exclude .idea/shelf
