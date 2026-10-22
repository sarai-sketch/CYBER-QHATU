package com.cyberqhatu.app.ui.viewmodel

import com.cyberqhatu.app.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: AuthRepository = mockk(relaxed = true)
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register success changes state from Loading to EmailSent`() = runTest {
        coEvery { repository.register("Juan", "test@correo.com", "password123") } returns Result.success(Unit)

        viewModel.register("Juan", "test@correo.com", "password123")

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.authState.value
        assertTrue(state is AuthState.EmailSent)
        assertEquals("Se ha enviado un enlace de verificación a tu correo", (state as AuthState.EmailSent).message)
    }

    @Test
    fun `login error changes state to Error`() = runTest {
        coEvery { repository.login("test@correo.com", "password123") } returns Result.failure(Exception("Correo o contraseña incorrectos"))

        viewModel.login("test@correo.com", "password123")

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.authState.value
        assertTrue(state is AuthState.Error)
        assertEquals("Correo o contraseña incorrectos", (state as AuthState.Error).message)
    }

    @Test
    fun `login with unverified email changes state to EmailNotVerified`() = runTest {
        coEvery { repository.login("test@correo.com", "password123") } returns Result.success(false)

        viewModel.login("test@correo.com", "password123")

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.authState.value
        assertEquals(AuthState.EmailNotVerified, state)
    }
}
