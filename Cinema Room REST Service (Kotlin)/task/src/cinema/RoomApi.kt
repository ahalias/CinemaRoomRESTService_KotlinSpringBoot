package cinema

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.validation.Valid


@RestController
class RoomApi(private val seatInfo: SeatInfo, private val roomInfo: RoomInfo) {

    @GetMapping("/seats")
    fun seatList(): RoomInfo = seatInfo.roomInfo

    @PostMapping("/purchase")
    fun purchaseTicket(@RequestBody @Valid purchase: PurchaseTicket): ResponseEntity<Map<String, Any?>> {
        val seat = seatInfo.roomInfo.available_seats
            .filter { it.row == purchase.row && it.column == purchase.column }
        if (purchase.row !in 1..roomInfo.total_rows || purchase.column !in 1..roomInfo.total_columns) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to "The number of a row or a column is out of bounds!"))
        }  else if (seat[0].status == "free") {
            seat[0].status = "purchased"
            seat[0].token = UUID.randomUUID()
            return ResponseEntity.status(HttpStatus.OK).body(mapOf("token" to seat[0].token, "ticket" to seat[0]))
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to "The ticket has been already purchased!"))
        }
    }

    @PostMapping("/return")
    fun returnTicket(@RequestBody token: Map<String, String>): ResponseEntity<Map<String, Any>> {
        val ticketToReturn = seatInfo.roomInfo.available_seats
            .filter { it.token == UUID.fromString(token["token"]) }
        if (ticketToReturn.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to "Wrong token!"))
        } else {
            ticketToReturn[0].token = null
            ticketToReturn[0].status = "free"
            return ResponseEntity.status(HttpStatus.OK).body(mapOf("returned_ticket" to ticketToReturn[0]))
        }
    }

    @PostMapping("/stats")
    fun showStats(@RequestParam(required=false) password: String?): ResponseEntity<Map<String, Any>> {
        if (password == "super_secret") {
            val currentIncome = seatInfo.roomInfo.available_seats
                .filter {it.status == "purchased"}
                .sumOf {it.price}
            val availableSeats = seatInfo.roomInfo.available_seats
                .filter {it.status == "free"}
                .count()
            val purchasedSeats = seatInfo.roomInfo.available_seats
                .filter {it.status == "purchased"}
                .count()
            return ResponseEntity.status(HttpStatus.OK).body(mapOf(
                "current_income" to currentIncome,
                "number_of_available_seats" to availableSeats,
                "number_of_purchased_tickets" to purchasedSeats
            ))
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "The password is wrong!"))
        }
    }


}