package com.hedvig.rapio.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.hedvig.rapio.comparison.domain.ComparisonQuote
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinMapper
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.jackson2.Jackson2Config
import org.jdbi.v3.jackson2.Jackson2Plugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


@Profile("runtime")
@Configuration
@EnableTransactionManagement
class JDBIConfig{

    @Bean
    fun jdbi(dataSource:DataSource, myObjectMapper:ObjectMapper): Jdbi {
        val dataSourceProxy = TransactionAwareDataSourceProxy(dataSource);
        val jdbi = Jdbi.create(dataSourceProxy);
        jdbi.installPlugins();
        jdbi.installPlugin(PostgresPlugin());
        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(Jackson2Plugin())
        jdbi.getConfig(Jackson2Config::class.java).mapper = myObjectMapper

        jdbi.registerRowMapper(ComparisonQuote::class.java, KotlinMapper(ComparisonQuote::class.java));

        return jdbi;
    }

}