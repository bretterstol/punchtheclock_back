import io.javalin.Javalin
import org.joda.time.DateTime

fun startServer(){
    val app = Javalin.create().start(7000)
    app.get("/start"){csx ->
        val sessionID = insertIntoTable("start", 0)
        csx.json(Id(sessionID))
    }
    app.get("/end"){csx ->
        val id = csx.queryParam("id")
        if  (id != null) {
            insertIntoTable("end", id.toInt())
            csx.json(Success(true))
        }
    }
    app.get("/history"){csx ->
        val all = getFromTable()
        val toObj = all.map{
            History(it.first, it.second)
        }
        csx.json(toObj)
    }

    app.get("/diff") { csx ->
        val all = getOnlyValid()
        csx.json(all)
    }
    app.get("/get_end"){csx ->
            val all = selectEnd()
            csx.json(all)
    }
    app.get("/get_some") { csx ->
        val date = csx.queryParam("from")
        if (date !== null) {
            val diff = selelctSome(date)
            csx.json(diff)
        }
    }
}

data class Id(val id:Int?)
data class History(val start: String, val end: String?)
data class DateHistory(val start: DateTime, val end: DateTime?)
data class Success(val success: Boolean)
