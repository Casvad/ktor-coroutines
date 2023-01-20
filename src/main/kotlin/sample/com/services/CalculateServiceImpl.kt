package sample.com.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory

object CalculateServiceImpl : CalculationService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    val actors = this.createList()

    private fun createList(): List<List<Int>> {

        val actors = mutableListOf<List<Int>>()

        for (i in 100 downTo 0 step 2) {
            actors.add((0..1000).map { it * i })
        }

        return actors
    }

    override suspend fun findElementInList(element: Int): Int {
        val channel = Channel<Int>(1)

        return coroutineScope {

            actors.mapIndexed { index, subList ->
                async(Dispatchers.IO) { calculate(element, subList, index, channel) }
            }

            //TODO solve problem when element is not on the list
            channel.receive().apply {
                logger.info("Information received from channel $this")
                channel.close()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun calculate(elementToFind: Int, list: List<Int>, index: Int, channel: Channel<Int>) {
        logger.info("Actor $index start job")
        delay(1L)
        val elementFounded = list.any { elementToFind == it }
        logger.info("Actor $index end job with result $elementFounded and close state ${channel.isClosedForSend}")

        if (!channel.isClosedForSend && elementFounded){
            channel.send(index)
        }
    }
}