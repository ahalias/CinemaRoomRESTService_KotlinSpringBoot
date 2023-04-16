package cinema

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.stereotype.Service
import java.util.*
import javax.validation.constraints.Size


@Service
data class RoomInfo(
    val total_rows: Int = 9,
    val total_columns: Int = 9,
    val available_seats: MutableList<Seat> = mutableListOf()
)

data class Seat(
    @JsonIgnore
    var token: UUID? = null,
    val row: Int,
    val column: Int,
    @JsonIgnore
    var status: String,
    var price: Int)

data class PurchaseTicket(
    @Size(min=1, max=9)
    val row: Int,
    @Size(min=1, max=9)
    val column: Int
)


