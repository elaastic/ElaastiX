#!/bin/sh
#
# Elaastic / ElaastiX - formative assessment system
# Copyright (C) 2019  Université de Toulouse and Université Toulouse Capitole.
# SPDX-License-Identifier: AGPL-3.0-or-later
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

# Re-build every time the source code changes. Spring will automatically restart
while inotifywait -r -e modify /app/src/main/; do
  ./gradlew --offline --no-daemon :server:classes
done > /dev/null 2>&1 &

# Forcefully make Gradle offline to not download new dependencies.
# While convenient, the image as a whole should be rebuilt instead. Let's enforce good practices :)
exec ./gradlew --offline --no-daemon "$@"
