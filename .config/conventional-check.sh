#!/usr/bin/env sh

# https://github.com/jdx/hk/discussions/902
if test -n "`sed -ne '1{/^\(fixup\|amend\|squash\)\! /!q;}' -e p "$1"`"; then exit 0; fi
exec hk util check-conventional-commit $1
