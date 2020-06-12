/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package br.com.zup.beagle.data

import br.com.zup.beagle.exception.BeagleApiException
import br.com.zup.beagle.extensions.once
import br.com.zup.beagle.logger.BeagleMessageLogs
import br.com.zup.beagle.networking.*
import br.com.zup.beagle.networking.urlbuilder.UrlBuilder
import br.com.zup.beagle.setup.BeagleEnvironment
import br.com.zup.beagle.testutil.RandomData
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.URI
import kotlin.test.assertEquals

import kotlin.test.assertFails

private val PATH = RandomData.httpUrl()
private val REQUEST_DATA = RequestData(URI(PATH))
private val BASE_URL = RandomData.string()
private val FINAL_URL = RandomData.string()

@ExperimentalCoroutinesApi
class BeagleApiTest {

    private val requestDataSlot = mutableListOf<RequestData>()
    private val onSuccessSlot = slot<(responseData: ResponseData) -> Unit>()
    private val onErrorSlot = slot<(responseData: ResponseData) -> Unit>()

    @MockK
    private lateinit var httpClient: HttpClient
    @MockK
    private lateinit var urlBuilder: UrlBuilder
    @MockK
    private lateinit var beagleEnvironment: BeagleEnvironment
    @MockK
    private lateinit var requestCall: RequestCall
    @MockK
    private lateinit var responseData: ResponseData

    private lateinit var beagleApi: BeagleApi

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkObject(BeagleMessageLogs)

        beagleApi = BeagleApi(httpClient)

        mockListenersAndExecuteHttpClient()

        every { beagleEnvironment.beagleSdk.config.baseUrl } returns BASE_URL
        every { urlBuilder.format(any(), any()) } returns FINAL_URL
        every { BeagleMessageLogs.logHttpRequestData(any()) } just Runs
        every { BeagleMessageLogs.logHttpResponseData(any()) } just Runs
        every { BeagleMessageLogs.logUnknownHttpError(any()) } just Runs
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun fetchComponent_should_call_logHttpResponseData_and_return() = runBlockingTest {
        // Given, When
        val data = beagleApi.fetchData(REQUEST_DATA)

        // Then
        verify(exactly = once()) { BeagleMessageLogs.logHttpResponseData(responseData) }
        assertEquals(data, responseData)
    }

    @Test
    fun fetch_should_return_a_exception_when_some_http_call_fails() = runBlockingTest {
        // Given
        val responseData: ResponseData = mockk()
        val message = "fetchData error for url ${REQUEST_DATA.uri}"
        val expectedException = BeagleApiException(responseData, message)
        mockListenersAndExecuteHttpClient { onErrorSlot.captured(responseData) }

        // When
        val exceptionThrown = assertFails(message) {
            beagleApi.fetchData(REQUEST_DATA)
        }

        // Then
        assertEquals(expectedException.message, exceptionThrown.message)
        verify(exactly = once()) { BeagleMessageLogs.logUnknownHttpError(expectedException) }
    }

    private fun mockListenersAndExecuteHttpClient(executionLambda: (() -> Unit)? = null) {
        every {
            httpClient.execute(
                capture(requestDataSlot),
                onSuccess = capture(onSuccessSlot),
                onError = capture(onErrorSlot)
            )
        } answers {
            if (executionLambda != null) {
                executionLambda()
            } else {
                onSuccessSlot.captured(responseData)
            }
            requestCall
        }
    }
}