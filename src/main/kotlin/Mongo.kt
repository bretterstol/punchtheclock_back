import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoIterable
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.*
import java.util.*

data class Stamp(
    val sessionID: String,
    val start: String?
)
fun getStampCol():MongoCollection<Stamp>{
    val client = KMongo.createClient()
    val database = client.getDatabase("punchtheclock")
    return database.getCollection<Stamp>("Stamp")
}
fun startStamp(type: String): String{
    val stamCol = getStampCol()
    val id = UUID.randomUUID().toString()
    stamCol.insertOne(Stamp(sessionID= id, start=type))
    return id
}

fun endStamp(id: String?):Stamp?{
    val stamCol = getStampCol()
    return stamCol.findOne(Stamp::sessionID eq id)
}
fun getAllMongo() :MutableList<Stamp>{
    val stampCol = getStampCol()
    val liste = mutableListOf<Stamp>()
    val all = stampCol.find().forEach{
        liste.add(it)
    }
    println(all)
    return liste
}