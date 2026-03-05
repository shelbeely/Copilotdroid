package com.example.agenthq.domain.model

data class Repository(
    val id: Long,
    val owner: String,
    val name: String,
    val fullName: String,
    val description: String?,
    val isPrivate: Boolean,
    val defaultBranch: String
)
