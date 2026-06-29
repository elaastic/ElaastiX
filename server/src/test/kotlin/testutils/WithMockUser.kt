/*
 * This file is a modified copy of Spring Security's own WithMockUser annotation interface.
 * Below is the original copyright notice of the original software you may find at this URL:
 * https://github.com/spring-projects/spring-security/blob/43fe92911e8226e6e8cb23b1f87ca0d497b4217b/test/src/main/java/org/springframework/security/test/context/support/WithMockUser.java
 *
 * Copyright 2004-present the original author or authors.
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package testutils

import jakarta.persistence.EntityManager
import net.datafaker.Faker
import org.elaastix.commons.cast
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.security.Role
import org.elaastix.server.authn.ElaastixAuthentication
import org.elaastix.server.users.entities.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.security.access.hierarchicalroles.NullRoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolderStrategy
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.security.test.context.support.WithSecurityContextFactory
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.locks.ReentrantLock

/**
 * Customised version of [org.springframework.security.test.context.support.WithMockUser] for use in ElaastiX.
 * Configures the security context to use a mocked user with the specified privileges.
 *
 * The effective permissions will be resolved according to the project's configured role hierarchy.
 */
@WithSecurityContext(factory = WithMockUser.SecurityContextFactory::class)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WithMockUser(
	/** The roles to grant in the test context. */
	val roles: Array<Role> = [],
	/** Whether to persist the user to the database or not. */
	val persist: Boolean = true,
) {
	companion object {
		const val SEC_MOCK_USER = "SEC_MOCK_USER"
		const val SEC_MOCK_USER_HOLDER = "SEC_MOCK_USER_HOLDER"

		val lock = ReentrantLock()
	}

	class SecurityContextFactory : WithSecurityContextFactory<WithMockUser> {
		private val faker = Faker()

		private var roleHierarchy: RoleHierarchy = NullRoleHierarchy()
		private var securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy()

		private lateinit var configurableApplicationContext: ConfigurableApplicationContext
		private lateinit var transactionTemplate: TransactionTemplate
		private lateinit var entityManager: EntityManager

		override fun createSecurityContext(annotation: WithMockUser): SecurityContext {
			// Always add the USER role. This is most certainly what we want.
			val rolesSet = annotation.roles.toMutableSet().apply { add(Role.USER) }

			val name = faker.name()
			val user = UserEntity(
				firstName = name.firstName(),
				lastName = name.lastName(),
				email = "${Uuid.random()}@elaastix.test",
				roles = annotation.roles.toSet(),
			)

			if (annotation.persist) {
				transactionTemplate.execute {
					entityManager.persist(user)
				}
			}

			getHolder().set(user)
			return securityContextHolderStrategy.createEmptyContext().apply {
				authentication = ElaastixAuthentication(
					user = user,
					credentials = null,
					authenticated = true,
					authorities = roleHierarchy.getReachableGrantedAuthorities(rolesSet),
				)
			}
		}

		private fun getHolder(): ThreadLocal<UserEntity> =
			synchronized(lock) {
				val registry = configurableApplicationContext.beanFactory as BeanDefinitionRegistry

				if (!registry.isBeanNameInUse(SEC_MOCK_USER_HOLDER)) {
					val holder = ThreadLocal<UserEntity>()
					registry.registerBeanDefinition(
						SEC_MOCK_USER_HOLDER,
						GenericBeanDefinition().apply {
							setBeanClass(ThreadLocal::class.java)
							instanceSupplier = { holder }
						},
					)
				}

				val holder = configurableApplicationContext
					.autowireCapableBeanFactory
					.getBean(SEC_MOCK_USER_HOLDER)
					.cast<ThreadLocal<UserEntity>>()

				if (!registry.isBeanNameInUse(SEC_MOCK_USER)) {
					registry.registerBeanDefinition(
						SEC_MOCK_USER,
						GenericBeanDefinition().apply {
							setBeanClass(UserEntity::class.java)
							setDependsOn(SEC_MOCK_USER_HOLDER)
							instanceSupplier = { holder.get() }
						},
					)
				}

				holder
			}

		@Autowired(required = false)
		@Suppress("SpringJavaInjectionPointsAutowiringInspection") // Code literally pulled from Spring Security itself
		fun setSecurityContextHolderStrategy(securityContextHolderStrategy: SecurityContextHolderStrategy) {
			this.securityContextHolderStrategy = securityContextHolderStrategy
		}

		@Autowired(required = false)
		@Suppress("SpringJavaInjectionPointsAutowiringInspection")
		fun setRoleHierarchy(roleHierarchy: RoleHierarchy) {
			this.roleHierarchy = roleHierarchy
		}

		@Autowired(required = false)
		@Suppress("SpringJavaInjectionPointsAutowiringInspection")
		fun setConfigurableApplicationContext(configurableApplicationContext: ConfigurableApplicationContext) {
			this.configurableApplicationContext = configurableApplicationContext
		}

		@Autowired(required = false)
		@Suppress("SpringJavaInjectionPointsAutowiringInspection")
		fun setEntityManager(entityManager: EntityManager) {
			this.entityManager = entityManager
		}

		@Autowired(required = false)
		@Suppress("SpringJavaInjectionPointsAutowiringInspection")
		fun setTransactionTemplate(transactionTemplate: TransactionTemplate) {
			this.transactionTemplate = transactionTemplate
		}
	}
}
