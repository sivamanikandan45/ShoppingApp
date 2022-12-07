package com.example.shopping.signinandsigupfeature.domain.usecase

import com.example.shopping.signinandsigupfeature.domain.repository.UserRepository

class CheckValidUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String):Int{
        return userRepository.isValidUser(email,password)
    }
}