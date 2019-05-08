package com.karumi.todoapiclient

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import todoapiclient.TodoApiClient
import todoapiclient.dto.TaskDto
import todoapiclient.exception.ItemNotFoundError

class TodoApiClientTest : MockWebServerTest() {

    private lateinit var apiClient: TodoApiClient

    @Before
    override fun setUp() {
        super.setUp()
        val mockWebServerEndpoint = baseEndpoint
        apiClient = TodoApiClient(mockWebServerEndpoint)
    }

    @Test
    fun sendsAcceptAndContentTypeHeaders() {
        enqueueMockResponse(200, "getTasksResponse.json")

        apiClient.allTasks

        assertRequestContainsHeader("Accept", "application/json")
    }

    @Test
    fun sendsGetAllTaskRequestToTheCorrectEndpoint() {
        enqueueMockResponse(200, "getTasksResponse.json")

        apiClient.allTasks

        assertGetRequestSentTo("/todos")
    }

    @Test
    fun parsesTasksProperlyGettingAllTheTasks() {
        enqueueMockResponse(200, "getTasksResponse.json")

        val tasks = apiClient.allTasks.right!!

        assertEquals(200, tasks.size.toLong())
        assertTaskContainsExpectedValues(tasks[0])
    }

    @Test
    fun sendsGetEmptyListOfTasksToTheCorrectEndpoint() {
        enqueueMockResponse(200, "getEmptyTaskResponse.json")

        val tasks = apiClient.allTasks.right!!

        assertTrue(tasks.isEmpty())

    }

    @Test
    fun sendsGetTaskIDToTheCorrectEndpoint() {
        enqueueMockResponse(200, "getTaskByIdResponse.json")

        val tasks = apiClient.getTaskById("1").right!!

        assertTaskContainsExpectedValues(tasks)

    }

    @Test
    fun sendsGetTaskNotFound() {
        enqueueMockResponse(404)

        val error = apiClient.getTaskById("1").left!!

        assertEquals(ItemNotFoundError, error)
    }


    private fun assertTaskContainsExpectedValues(task: TaskDto?) {
        assertTrue(task != null)
        assertEquals(task?.id, "1")
        assertEquals(task?.userId, "1")
        assertEquals(task?.title, "delectus aut autem")
        assertFalse(task!!.isFinished)
    }


}
