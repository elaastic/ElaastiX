/*
 * Elaastic / ElaastiX - formative assessment system
 * Copyright (C) 2019  Université de Toulouse and Université Toulouse Capitole.
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.elaastix.commons.platform

/**
 * Annotation flagging a function as potentially dangerous.
 * A function qualifies as "unsafe" if it bypasses certain invariant checks, or otherwise makes assumptions about the
 * validity and/or correctness of its input/output data without checking them. A function annotated with `Unsafe`
 * MUST explain why it is unsafe to use in its documentation.
 *
 * Calling such functions may be required for performance-sensitive code, or to avoid potential pitfalls of its safe
 * counterpart (e.g. triggering side effects on a lazy proxy).
 *
 * In such cases, the snippet that needs access to the function needs to use [OptIn]. There MUST be a comment
 * explaining why the invariant is being bypassed, and why it is safe to do so. IOW, you're expected to demonstrate
 * there are no invariant violation that could result of the use of unsafe code, as you're bypassing the safeguards
 * that'd otherwise strongly ensure they are held.
 *
 * An unsafe function can call other unsafe functions without issues, although it is RECOMMENDED that such calls are
 * properly explained as well (e.g. is the call "safe" because of invariants have been validated one way or another,
 * or is it acceptable because the invariant is expected to be held by the caller?).
 *
 * Rustaceans should be familiar with this concept of unsafe function. ;)
 */
@RequiresOptIn(
    message = "This function is unsafe to use. " +
        "Please read its documentation, and make sure your usage complies with safety requirements.",
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
annotation class Unsafe
