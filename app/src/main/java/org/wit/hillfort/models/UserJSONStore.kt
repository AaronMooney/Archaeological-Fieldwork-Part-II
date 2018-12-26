package org.wit.hillfort.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.hillfort.helpers.*
import java.util.*
import kotlin.collections.ArrayList

val JSON_FILE_USERS = "users.json"
val gsonBuilderUser = GsonBuilder().setPrettyPrinting().create()
val listTypeUser = object : TypeToken<java.util.ArrayList<UserModel>>() {}.type

class UserJSONStore : UserStore, AnkoLogger {

    val context: Context
    var users = ArrayList<UserModel>()
    var hillforts = ArrayList<HillfortModel>()

    constructor (context: Context) {
        this.context = context
        if (exists(context, JSON_FILE_USERS)) {
            deserialize()
        }
    }

    override fun getUsers(): List<UserModel> {
        return users
    }

    override fun findAllHillforts(): ArrayList<HillfortModel> {
        return hillforts
    }

    override fun addUser(user: UserModel, toVisit: ArrayList<HillfortModel>) {
        hillforts = toVisit
        toVisit.forEach {
            it.id = generateRandomId()
        }
        user.id = generateRandomId()
        users.add(user)
        serialize()
    }

    override fun addUser(user: UserModel) {
        user.id = generateRandomId()
        var hillfortsToVisit = ArrayList<HillfortModel>()
        hillfortsToVisit = populate(hillfortsToVisit)
        hillfortsToVisit.forEach {
            it.id = generateRandomId()
        }
        user.hillforts = hillfortsToVisit
        users.add(user)
        serialize()
    }

    override fun updateUser(user: UserModel, hillfort: HillfortModel){
        info("updating")
        var foundUser: UserModel? = users.find { u -> u.id == user.id}
        var foundHillfort: HillfortModel? = user.hillforts.find { h -> h.id == hillfort.id}
        if (foundUser != null && foundHillfort!= null){
            info("blalu")
            foundHillfort.name = hillfort.name
            foundHillfort.description = hillfort.description
            foundHillfort.images = ArrayList(hillfort.images)
            foundHillfort.lat = hillfort.lat
            foundHillfort.lng = hillfort.lng
            foundHillfort.zoom = hillfort.zoom
            foundHillfort.visited = hillfort.visited
            foundHillfort.notes = hillfort.notes
            foundUser.name = user.name
            foundUser.email = user.email
            foundUser.password = user.password
            foundUser.hillforts = user.hillforts
            serialize()
        }
    }

    override fun updateUser(user: UserModel){
        info("updating")
        var foundUser: UserModel? = users.find { u -> u.id == user.id}
        if (foundUser != null) {
            foundUser.name = user.name
            foundUser.email = user.email
            foundUser.password = user.password
            foundUser.hillforts = user.hillforts
            serialize()
        }
    }

    override fun deleteUser(user: UserModel) {
        users.remove(user)
        serialize()
    }

    override fun deleteHillfort(user: UserModel, hillfort: HillfortModel) {
        user.hillforts.remove(hillfort)
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilderUser.toJson(users, listTypeUser)
        write(context, JSON_FILE_USERS, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE_USERS)
        users = Gson().fromJson(jsonString, listTypeUser)
    }

    fun generateRandomId(): Long {
        return Random().nextLong()
    }

    fun populate(toVisit: ArrayList<HillfortModel>): ArrayList<HillfortModel>{
        toVisit.add(
            HillfortModel(name = "IR1041 Rathmoylan, Waterford",description = " This coastal promontory is located c. 2.5km SW of Dunmore East town in Co. Waterford. Marked as an ÇEntrenchment\u0090 on the first edition 6-inch map, the promontory can be described as a triangular area with steep grassy slopes on either flank. The landmass measures c. 36m N-S by c. 25m E-W and projects S into the Atlantic from the mainland at an altitude of 20m OD. Westropp notes that the name ÇRathmoylan\u0090 is Çnot that of the promontory-fort, but of a ring-fort further inland, from which the townland is named\u0090 (1914, 211). The headland is defended by an earthen bank (Wth 7.5m; int. H 1m; ext. H 2.8m) and external ditch (D 1.3m). Moore observes that an external berm separates the ditch from a modern field bank (1999, 68). There is no recorded entrance to the interior of the site and there are no internal features such as hut-sites."
                , lat = 52.134654, lng = -7.035038, zoom = 15f))
        toVisit.add(
            HillfortModel(name = "IR0983 Islandikane East/Islandikane South, Waterford",description = " This coastal promontory, depicted as the site of an 'ancient irish dwelling' on the first edition OS map, is situated c. 5km SW of Tramore town and c. 3km E of Annestown on the the SE coast of Co. Waterford. The headland once incorporated ÇSheep Island\u0090 projecting SE from the mainland into the sea at an altitude of 26m OD, but this stack is currently being heavily eroded and is inaccessible from the land. Westropp noted an oval hut site within a grass-covered enclosure on it (1914-16, 221-2) in the early twentieth century (elements of which are visible on satellite imagery). On the mainland, two lengths of rampart meet at a right angle, cutting off the adjacent promontory. The W section consists of an earthen bank (Wth 6m by 2m ext. H) now only 65m in length, while the E section, which is similarly constructed but with a berm between the bank and ditch, is about 100m in length. The external ditch measures 3.8m in width at the base with a depth of 1.4m. An outer bank described by Westropp is not evident (Moore 1999, 67). An entrance c. 6m in width and causeway are also interpreted as being modern in date and not original features. There are no recorded internal features within the enclosed area on the mainland, which has evidently been significantly reduced in size by erosion since first appearing on OS maps."
                , lat = 52.132271, lng = -7.219559, zoom = 15f))
        toVisit.add(
            HillfortModel(name = "IR1039 Kilfarrasy, Waterford",description = "This coastal promontory is located c. 2km W of Annestown in Co. Waterford. The headland, marked as an 'entrenchment' on the second edition six-inch OS map, can be described as a grass-covered landmass measuring 120m N-S by 90m E-W that projects S from the mainland at an altitude of 14m OD. It is cut off on the landward side by a 40m ditch running in a NE-SW direction across the neck of the promontory. The ditch measures 0.5m in depth and 5m in width and Westropp records a 'spring and stream in its eastern reach' (1914-16, 210). A central causeway measuring 2.7m in width crosses the ditch. An inner bank (c. 4m in Width) described by Westropp as '10 feet to 12 feet wide, once 5 feet to 6 feet higher than the fosse' has since been removed. He notes that Çthe works were dug away partly to make fences along the dangerously crumbling precipices\u0090 (1914, 210). Furthermore, he observed that a recent fire had cleared the interior of the site and no hut-sites were evident. The site is currently overgrown."
                , lat = 52.133597, lng = -7.243725, zoom = 15f))
        toVisit.add(
            HillfortModel(name = "IR0981 Islandhubbock, Waterford",description = "The site is located c. 3km SW of Stradbally town in Co. Waterford. It can be described as a grass-covered promontory (43m N-S by 12-18m E-W) barely projecting S from the mainland at an altitude of 18m OD. Outer earthworks marked on the first edition OS six-inch map to the NW had been levelled prior to 1906 when described by Wesropp (1906, 252). Headland enclosed on the N (landward side) by an external ditch measuring 13.4m in length by 3.8m in depth, a central earthen bank (Wth 7m; H over inner ditch 5.9m; H over outer ditch 3.1m) and internal ditch (Wth at top 9.2m; Wth at base 2.9m; D below exterior 4.1m). Entrance through the bank is thought to be modern in date (Westropp 1914-16, 218). A bank measuring 1.6m in height surrounds all sides of the promontory. Two possible hut sites are recorded in the interior of the promontory, both circular (int. dims. 5.2m by 4.1m; 7.8m by 6.9m). The headland is currently under pasture and is suffering badly from coastal erosion."
                , lat = 52.107296, lng = -7.489863, zoom = 15f))
        return toVisit
    }
}