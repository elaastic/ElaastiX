<!--
  - Elaastic / ElaastiX - formative assessment system
  - Copyright (C) 2019  Université de Toulouse and Université Toulouse Capitole.
  - SPDX-License-Identifier: AGPL-3.0-or-later
  -
  - This program is free software: you can redistribute it and/or modify
  - it under the terms of the GNU Affero General Public License as published by
  - the Free Software Foundation, either version 3 of the License, or
  - (at your option) any later version.
  -
  - This program is distributed in the hope that it will be useful,
  - but WITHOUT ANY WARRANTY; without even the implied warranty of
  - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  - GNU Affero General Public License for more details.
  -
  - You should have received a copy of the GNU Affero General Public License
  - along with this program.  If not, see <http://www.gnu.org/licenses/>.
  -->

<template>
  <UModal
    :title="$t('legal.about-elaastix')"
    :ui="{ content: 'max-w-xl' }"
  >
    <slot />

    <template #body>
      <div class="flex flex-col gap-4">
        <p class="text-lg uppercase font-title font-bold text-primary">
          {{ $t('tagline') }}
        </p>
        <p>
          {{ $t('description') }}
        </p>
        <p>
          <i18n-t keypath="legal.project.str">
            <ExternalLink to="https://www.irit.fr/TALENT">
              {{ $t('legal.project.talent') }}
            </ExternalLink>
            <ExternalLink to="https://www.irit.fr/">
              {{ $t('legal.project.irit') }}
            </ExternalLink>
          </i18n-t>
        </p>
        <ul class="list-inside list-disc ml-2">
          <li>
            <ExternalLink to="/legal">
              {{ $t('legal.notice') }}
            </ExternalLink>
          </li>
          <li>
            <ExternalLink to="https://github.com/elaastic/elaastix">
              {{ $t('source-code') }}
            </ExternalLink>
          </li>
          <li>
            <ExternalLink to="https://www.youtube.com/watch?v=dQw4w9WgXcQ">
              {{ $t('documentation') }}
            </ExternalLink>
          </li>
          <li>
            <!-- https://unsplash.com/s/photos/cat?license=free -->
            <i18n-t keypath="legal.cat.str">
              <ExternalLink to="https://images.unsplash.com/photo-1626602411112-10742f9a3ab8?q=80&w=1974&auto=format&fit=crop">
                {{ $t('legal.cat.link') }}
              </ExternalLink>
            </i18n-t>
          </li>
        </ul>
      </div>
      <hr class="my-4 text-neutral-300">
      <div class="text-sm">
        <div>Copyright &copy; Université de Toulouse</div>
        <div>Copyright &copy; Université Toulouse Capitole</div>
        <div>
          <i18n-t keypath="legal.license.str">
            <ExternalLink to="https://github.com/elaastic/elaastix/blob/main/LICENSE">
              {{ $t('legal.license.link') }}
            </ExternalLink>
          </i18n-t>
        </div>
      </div>
    </template>
  </UModal>
</template>
