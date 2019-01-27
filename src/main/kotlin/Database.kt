import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.LocalDateTime

object Sessions: Table(){
    val id = integer("id").autoIncrement().primaryKey()
    val start = datetime("start")
    val end = (datetime("end")).nullable()
}


object DBsettings {
    private val url:String = System.getenv("postgres_url") ?: "jdbc:postgresql://localhost:5432/rette"
    private val user:String = System.getenv("postgres_user") ?: "rette"
    private val password:String = System.getenv("postgres_password") ?: ""
    val db by lazy {Database.connect(url, driver = "org.postgresql.Driver", user=user, password = password)}
}

fun createTable(){
    DBsettings.db
    transaction {
        SchemaUtils.create(
            Sessions
        )
    }
}

fun insertIntoTable(stamp: String, id: Int): Int?{
    DBsettings.db
    return transaction{
        when (stamp){
            "start" -> {
                Sessions.insert {
                    it[start] = DateTime.now()
                } get Sessions.id
            }
            "end" -> {
                Sessions.update({Sessions.id eq id}) {
                    it[end] = DateTime.now()
                }
            }
            else -> { 0 }
        }
    }
}

fun getOnlyValid(): List<String>{
    DBsettings.db
    return transaction {
        Sessions.selectAll().filter{
            it[Sessions.end] != null
        }.map{
            getDiff(it[Sessions.end]?.millis,it[Sessions.start].millis)
        }
    }

}

fun selectEnd():List<DateTime?>{
    DBsettings.db
    return transaction {
        Sessions.selectAll().map{
            //getTime(it[Sessions.start]) to getTime(it[Sessions.end])
            it[Sessions.end]
        }
    }
}

fun getFromTable():List<Pair<String,String?>>{
    DBsettings.db
    return transaction {
        Sessions.selectAll().map{
            getTime(it[Sessions.start]) to getTime(it[Sessions.end])
            // Pair(it[Sessions.start],it[Sessions.end])
        }
    }
}

fun selelctSome(date: String):List<Long>{
    DBsettings.db
    val time = createDatetime(date)
    return transaction {
        Sessions.select{
            Sessions.start greaterEq time
        }.filter{
            it[Sessions.end] !== null
        }.map {
            (it[Sessions.end]!!.millis - it[Sessions.start].millis) - hoursToMillis(8)
        }
    }
}

fun hoursToMillis(num: Int):Int = num*60*60*1000

fun createDatetime(date:String):DateTime {
    val fullDate = makeDate(date)
    return DateTime(fullDate.year, fullDate.month, fullDate.day, 0, 0)
}

fun makeDate(string:String):DateWithYear{
    val dateList = string.split("-").map{
        it.toInt()
    }
    return DateWithYear(dateList[0], dateList[1], dateList[2]);
}

data class DateWithYear(val year: Int, val month: Int, val day: Int)

fun getDiff(end:Long?, start: Long) :String =  when(end != null){
    true -> toHourMinutsSeconds(  end - start)
    false -> "0"
}

fun toHourMinutsSeconds(time: Long):String{
    val sec = time / 1000
    val s = sec % 60
    val m = (sec / 60) % 60
    val h = (sec / 60 / 60) % 24
    return String.format("%02d:%02d:%02d", h,m,s)
}

fun getTime(time:DateTime?):String = when (time == null){
    true -> ""
    false ->formatLocalDateTime(time.toLocalDateTime())
}

fun formatLocalDateTime(local: LocalDateTime):String = "${local.year}-${addZero(local.monthOfYear)}-${addZero(local.dayOfMonth)} ${local.hourOfDay}:${local.minuteOfHour}"

fun addZero(num:Int):String = when(num){
    in 1..9 -> "0$num"
    else -> {"$num"}
}

