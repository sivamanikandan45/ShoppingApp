package com.example.shopping.signinandsigupfeature.domain.usecase

import com.example.shopping.signinandsigupfeature.domain.repository.UserRepository

class GetIdUsingRowId(private val userRepository: UserRepository) {
    suspend operator fun invoke(rowId:Long):Int=userRepository.getIdUsingRowId(rowId)
}