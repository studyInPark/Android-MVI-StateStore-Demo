package com.learn.mvi.domain

import kotlinx.coroutines.delay

class LoginUseCase {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        delay(1_000) // Simulate network latency
        return if (password == "password") {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Wrong email or password"))
        }
    }
}
