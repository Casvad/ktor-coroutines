package sample.com.services

sealed interface CalculationService {

    suspend fun findElementInList(element: Int): Int
}