package com.hedvig.rapio.helpers

import com.hedvig.rapio.externalservices.apigateway.transport.ApiGatewayClient
import com.hedvig.rapio.externalservices.memberService.MemberServiceClient
import com.hedvig.rapio.externalservices.paymentService.transport.PaymentServiceClient
import com.hedvig.rapio.externalservices.productPricing.transport.ProductPricingClient
import com.hedvig.rapio.externalservices.underwriter.transport.UnderwriterClient
import com.hedvig.rapio.helpers.TestHttpClient
import com.hedvig.rapio.qa.QualityAssuranceMemberServiceClient
import com.ninjasquad.springmockk.MockkBean
import javax.persistence.EntityManager
import org.hibernate.Session
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.data.repository.CrudRepository
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

@ActiveProfiles(profiles = ["noauth", "test"])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc(secure = false)
abstract class IntegrationTest {

    @Autowired
    lateinit var client: TestHttpClient

    @Autowired
    lateinit var context: ApplicationContext

    @Autowired
    lateinit var transactionManager: PlatformTransactionManager

    @MockkBean(relaxed = true)
    lateinit var memberServiceClient: MemberServiceClient

    @MockkBean(relaxed = true)
    lateinit var apiGatewayClient: ApiGatewayClient

    @MockkBean(relaxed = true)
    lateinit var paymentServiceClient: PaymentServiceClient

    @MockkBean(relaxed = true)
    lateinit var productPricingClient: ProductPricingClient

    @MockkBean(relaxed = true)
    lateinit var underwriterClient: UnderwriterClient

    @MockkBean(relaxed = true)
    lateinit var qualityAssuranceMemberServiceClient: QualityAssuranceMemberServiceClient

    /**
     * Utility function for attaching "cleanup tasks" as part of a [AfterEach] block.
     *
     * The [ResetScope] works as a way to get a nice autocomplete-compatible API for resetting.
     */
    final fun reset(task: ResetScope.() -> Unit) {
        val cleanups = mutableListOf<IntegrationTestCleanup>()
        ResetScope(cleanups).task()

        TransactionTemplate(transactionManager).execute {
            cleanups.forEach {
                it.runInContext(context)
            }
        }
    }

    class ResetScope internal constructor(
        private val cleanups: MutableList<IntegrationTestCleanup>
    ) {

        /**
         * Run the specific [Hibernate Query Language](https://www.tutorialspoint.com/hibernate/hibernate_query_language.htm)
         * statement as part of the cleanup.
         */
        fun hql(statement: String) {
            add(IntegrationTestCleanup.RunHql(statement))
        }

        /**
         * Reset the specific [javax.persistence.Entity]. This is a shorthand for `hql("DELETE FROM E")`.
         */
        inline fun <reified E> entity() {
            add(IntegrationTestCleanup.RunHql("delete from ${E::class.simpleName}"))
        }

        /**
         * Clear the given repository by calling its [CrudRepository.deleteAll] function.
         *
         * Note: Does not always work when the entities have not been loaded into the entity manager. In those
         * cases use [entity] instead.
         */
        inline fun <reified R : CrudRepository<*, *>> repository() {
            add(IntegrationTestCleanup.ClearRepository(R::class.java))
        }

        @PublishedApi
        internal fun add(cleanup: IntegrationTestCleanup) {
            cleanups += cleanup
        }
    }

    @PublishedApi
    internal sealed class IntegrationTestCleanup {

        abstract fun runInContext(context: ApplicationContext)

        data class ClearRepository<R : CrudRepository<*, *>>(val type: Class<R>) : IntegrationTestCleanup() {
            override fun runInContext(context: ApplicationContext) {
                try {
                    context.getBean(type).deleteAll()
                } catch (e: Exception) {
                    throw RuntimeException("Failed to clear repository ${type.simpleName}", e)
                }
            }
        }

        data class RunHql(val statement: String) : IntegrationTestCleanup() {
            override fun runInContext(context: ApplicationContext) {
                val em = context.getBean(EntityManager::class.java)
                val session = em.unwrap(Session::class.java)
                val delete = session.createQuery(statement)
                try {
                    delete.executeUpdate()
                } catch (e: Exception) {
                    throw RuntimeException("Failed to run reset HQL: \"$statement\"", e)
                }
            }
        }
    }
}
