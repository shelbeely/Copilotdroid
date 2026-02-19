package com.agenthq.app.data.api.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PullRequestTest {

    @Test
    fun `PullRequest stores all fields correctly`() {
        val user = GitHubUser("octocat", 1L, "https://avatar.url", "Octocat", "octo@github.com")
        val head = BranchRef("owner:feature", "feature/test", "abc123")
        val base = BranchRef("owner:main", "main", "def456")
        val label = Label(10L, "bug", "d73a4a", "Bug report")

        val pr = PullRequest(
            id = 42L,
            number = 7,
            title = "Fix login crash",
            body = "Fixes a null pointer in login flow",
            state = "open",
            htmlUrl = "https://github.com/owner/repo/pull/7",
            user = user,
            createdAt = "2024-06-01T10:00:00Z",
            updatedAt = "2024-06-02T12:00:00Z",
            mergedAt = null,
            head = head,
            base = base,
            draft = false,
            labels = listOf(label)
        )

        assertEquals(42L, pr.id)
        assertEquals(7, pr.number)
        assertEquals("Fix login crash", pr.title)
        assertEquals("Fixes a null pointer in login flow", pr.body)
        assertEquals("open", pr.state)
        assertEquals("https://github.com/owner/repo/pull/7", pr.htmlUrl)
        assertEquals("octocat", pr.user.login)
        assertEquals("feature/test", pr.head.ref)
        assertEquals("main", pr.base.ref)
        assertFalse(pr.draft)
        assertNull(pr.mergedAt)
        assertEquals(1, pr.labels.size)
        assertEquals("bug", pr.labels[0].name)
    }

    @Test
    fun `PullRequest allows null body`() {
        val pr = makePr(body = null)
        assertNull(pr.body)
    }

    @Test
    fun `PullRequest allows empty labels list`() {
        val pr = makePr(labels = emptyList())
        assertTrue(pr.labels.isEmpty())
    }

    @Test
    fun `BranchRef stores ref and sha`() {
        val ref = BranchRef("owner:main", "main", "abc123def456")
        assertEquals("main", ref.ref)
        assertEquals("abc123def456", ref.sha)
        assertEquals("owner:main", ref.label)
    }

    @Test
    fun `GitHubUser stores login and id`() {
        val user = GitHubUser("copilot[bot]", 999L, null, null, null)
        assertEquals("copilot[bot]", user.login)
        assertEquals(999L, user.id)
        assertNull(user.avatarUrl)
    }

    @Test
    fun `Label stores name and color`() {
        val label = Label(5L, "enhancement", "a2eeef", "New feature")
        assertEquals("enhancement", label.name)
        assertEquals("a2eeef", label.color)
        assertEquals("New feature", label.description)
    }

    @Test
    fun `PullRequest data class copy works`() {
        val original = makePr()
        val merged = original.copy(state = "closed", mergedAt = "2024-06-03T00:00:00Z")
        assertEquals("closed", merged.state)
        assertEquals("2024-06-03T00:00:00Z", merged.mergedAt)
        assertEquals(original.title, merged.title)
    }

    @Test
    fun `PullRequest data class equality`() {
        val pr1 = makePr()
        val pr2 = makePr()
        assertEquals(pr1, pr2)
    }

    private fun makePr(
        body: String? = "description",
        labels: List<Label> = listOf(Label(1L, "bug", "d73a4a", null))
    ) = PullRequest(
        id = 1L,
        number = 1,
        title = "Test PR",
        body = body,
        state = "open",
        htmlUrl = "https://github.com/owner/repo/pull/1",
        user = GitHubUser("octocat", 1L, null, null, null),
        createdAt = "2024-01-01T00:00:00Z",
        updatedAt = "2024-01-01T00:00:00Z",
        mergedAt = null,
        head = BranchRef("owner:feature", "feature/x", "aaa"),
        base = BranchRef("owner:main", "main", "bbb"),
        draft = false,
        labels = labels
    )
}
