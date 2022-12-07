package com.example.shopping.signinandsigupfeature.domain.usecase

import com.example.shopping.signinandsigupfeature.data.local.entity.User
import com.example.shopping.signinandsigupfeature.domain.repository.UserRepository

class RegisterUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user:User):Long=userRepository.registerUser(user)
}