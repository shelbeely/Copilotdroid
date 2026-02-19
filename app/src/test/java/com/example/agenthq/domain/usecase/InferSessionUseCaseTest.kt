package com.example.agenthq.domain.usecase

import com.example.agenthq.data.local.PullRequestEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class InferSessionUseCaseTest {

    private val useCase = InferSessionUseCase()

    private fun makePr(
        authorLogin: String = "octocat",
        labels: String = "",
        title: String = "My PR",
        headRef: String = "feature/my-branch",
        state: String = "open",
        mergedAt: String? = null,
        updatedAt: String = Instant.now().toString()
    ) = PullRequestEntity(
        id = 1L,
        number = 42,
        repoOwner = "owner",
        repoName = "repo",
        title = title,
        state = state,
        htmlUrl = "https://github.com/owner/repo/pull/42",
        authorLogin = authorLogin,
        authorAvatarUrl = "",
        headRef = headRef,
        baseRef = "main",
        body = "",
        isDraft = false,
        createdAt = Instant.now().toString(),
        updatedAt = updatedAt,
        mergedAt = mergedAt,
        labels = labels,
        isAgentPr = false,
        lastSyncedAt = System.currentTimeMillis()
    )

    // Rule 1: bot author
    @Test
    fun `bot author is detected as agent PR`() {
        assertTrue(useCase.isAgentPr(makePr(authorLogin = "copilot[bot]")))
    }

    @Test
    fun `non-bot author without other signals is not agent PR`() {
        assertFalse(useCase.isAgentPr(makePr(authorLogin = "octocat")))
    }

    // Rule 2: labels
    @Test
    fun `copilot label is detected as agent PR`() {
        assertTrue(useCase.isAgentPr(makePr(labels = "copilot,bug")))
    }

    @Test
    fun `agent label is detected as agent PR`() {
        assertTrue(useCase.isAgentPr(makePr(labels = "agent")))
    }

    // Rule 3: title prefix
    @Test
    fun `Agent title prefix detected`() {
        assertTrue(useCase.isAgentPr(makePr(title = "[Agent] Fix the bug")))
    }

    @Test
    fun `Copilot title prefix detected case insensitive`() {
        assertTrue(useCase.isAgentPr(makePr(title = "[copilot] Add tests")))
    }

    // Rule 4: branch prefix
    @Test
    fun `copilot slash branch is detected`() {
        assertTrue(useCase.isAgentPr(makePr(headRef = "copilot/fix-bug")))
    }

    @Test
    fun `agent slash branch is detected`() {
        assertTrue(useCase.isAgentPr(makePr(headRef = "agent/new-feature")))
    }

    // Status inference
    @Test
    fun `merged PR is completed`() {
        val pr = makePr(state = "closed", mergedAt = Instant.now().toString())
        assertEquals("completed", useCase.inferStatus(pr))
    }

    @Test
    fun `closed non-merged PR is failed`() {
        val pr = makePr(state = "closed", mergedAt = null)
        assertEquals("failed", useCase.inferStatus(pr))
    }

    @Test
    fun `open recent PR is active`() {
        val pr = makePr(state = "open", updatedAt = Instant.now().toString())
        assertEquals("active", useCase.inferStatus(pr))
    }

    @Test
    fun `open stale PR is paused`() {
        val stale = Instant.now().minus(48, ChronoUnit.HOURS).toString()
        val pr = makePr(state = "open", updatedAt = stale)
        assertEquals("paused", useCase.inferStatus(pr))
    }
}
