package org.task.manager.config

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.HttpClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfiguration {

    @Value("\${proxy.server.host}")
    private lateinit var host: String

    @Value("\${proxy.server.port}")
    private lateinit var port: String

    @Value("\${proxy.server.user}")
    private lateinit var user: String

    @Value("\${proxy.server.password}")
    private lateinit var password: String

    @Bean
    fun restTemplate(): RestTemplate? {
        return RestTemplate(createFactory())
    }

    /**
     * Create HTTP Client Factory
     */
    private fun createFactory(): ClientHttpRequestFactory {
        val clientHttpRequestFactory = HttpComponentsClientHttpRequestFactory()
        clientHttpRequestFactory.httpClient = getHttpClient()
        return clientHttpRequestFactory
    }

    /**
     * Create HTTP Client
     */
    private fun getHttpClient(): HttpClient {
        val proxy = HttpHost(host, port.toInt())
        val credentialsProvider = BasicCredentialsProvider()

        credentialsProvider.setCredentials(
            AuthScope(host, port.toInt()),
            UsernamePasswordCredentials(user, password)
        )

        return HttpClients.custom()
            .setDefaultCredentialsProvider(credentialsProvider)
            .setProxy(proxy)
            .build()
    }

}
