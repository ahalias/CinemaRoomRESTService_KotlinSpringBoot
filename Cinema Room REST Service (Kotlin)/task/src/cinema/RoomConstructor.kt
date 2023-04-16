package cinema

import org.springframework.stereotype.Service


@Service
class SeatInfo(val roomInfo: RoomInfo) {
    init {
        for (row in 1..roomInfo.total_rows) {
            for (column in 1..roomInfo.total_columns) {
                val price = if (row <= 4) 10 else 8
                roomInfo.available_seats.add(Seat(null, row, column, "free", price))
            }
        }
    }
}