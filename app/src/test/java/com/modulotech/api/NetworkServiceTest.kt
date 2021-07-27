package com.modulotech.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.modulotech.api.response.DeviceResponse
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class NetworkServiceTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: NetworkService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()

        val client = OkHttpClient.Builder()
            .build()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NetworkService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun fetchRestaurants_checkDeviceList_Size() {
        enqueueResponse("mock_device_api_response")
        val restaurantList = runBlocking {
            service.fetchDevices()
        }
        val request = mockWebServer.takeRequest()
        Assert.assertThat(request.path, CoreMatchers.`is`("/modulotest/data.json"))

        Assert.assertThat(
            restaurantList.devices.size,
            CoreMatchers.`is`(12)
        )
    }

    @Test
    fun fetchRestaurants_checkDeviceList_Parse_Success() {
        enqueueResponse("mock_device_api_response")
        val restaurantList = runBlocking {
            service.fetchDevices()
        }
        val request = mockWebServer.takeRequest()
        Assert.assertThat(request.path, CoreMatchers.`is`("/modulotest/data.json"))

        Assert.assertThat(
                restaurantList.devices[0],
                CoreMatchers.instanceOf<Any>(DeviceResponse::class.java)
        )
    }

    @Test
    fun fetchRestaurants_checkDeviceList_Parse_ProductType_Light() {
        enqueueResponse("mock_device_api_response")
        val restaurantList = runBlocking {
            service.fetchDevices()
        }
        val request = mockWebServer.takeRequest()
        Assert.assertThat(request.path, CoreMatchers.`is`("/modulotest/data.json"))

        Assert.assertThat(
                restaurantList.devices[0].productType,
                CoreMatchers.`is`("Light")
        )
    }

    @Test
    fun fetchRestaurants_checkDeviceList_Parse_ProductType_Roller() {
        enqueueResponse("mock_device_api_response")
        val restaurantList = runBlocking {
            service.fetchDevices()
        }
        val request = mockWebServer.takeRequest()
        Assert.assertThat(request.path, CoreMatchers.`is`("/modulotest/data.json"))

        Assert.assertThat(
                restaurantList.devices[1].productType,
                CoreMatchers.`is`("RollerShutter")
        )
    }

    @Test
    fun fetchRestaurants_checkDeviceList_Parse_ProductType_Heater() {
        enqueueResponse("mock_device_api_response")
        val restaurantList = runBlocking {
            service.fetchDevices()
        }
        val request = mockWebServer.takeRequest()
        Assert.assertThat(request.path, CoreMatchers.`is`("/modulotest/data.json"))

        Assert.assertThat(
                restaurantList.devices[2].productType,
                CoreMatchers.`is`("Heater")
        )
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader!!
            .getResourceAsStream("$fileName")
        val source = inputStream.source().buffer()
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }
}