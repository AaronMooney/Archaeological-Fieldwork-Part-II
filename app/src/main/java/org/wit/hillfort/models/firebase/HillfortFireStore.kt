package org.wit.hillfort.models.firebase

import android.content.Context
import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.hillfort.helpers.readImageFromPath
import org.wit.hillfort.models.HillfortModel
import org.wit.hillfort.models.HillfortStore
import org.wit.hillfort.models.Location
import java.io.ByteArrayOutputStream
import java.io.File



class HillfortFireStore(val context: Context) : HillfortStore, AnkoLogger {

    val hillforts = ArrayList<HillfortModel>()
    lateinit var userId: String
    lateinit var db: DatabaseReference
    lateinit var st: StorageReference
    val logger = AnkoLogger<HillfortFireStore>()

    suspend override fun findAll(): MutableList<HillfortModel> {
        return hillforts
    }

    suspend override fun findById(id: Long): HillfortModel? {
        val foundHillfort: HillfortModel? = hillforts.find { p -> p.id == id }
        return foundHillfort
    }

    suspend override fun create(hillfort: HillfortModel) {
        val key = db.child("users").child(userId).child("hillforts").push().key
        key?.let {
            hillfort.fbId = key!!
            hillforts.add(hillfort)
            db.child("users").child(userId).child("hillforts").child(key).setValue(hillfort)
            updateImage(hillfort)
        }
    }

    suspend override fun update(hillfort: HillfortModel) {
        var foundHillfort: HillfortModel? = hillforts.find { p -> p.fbId == hillfort.fbId }
        if (foundHillfort != null) {
            foundHillfort.name = hillfort.name
            foundHillfort.description = hillfort.description
            foundHillfort.image = hillfort.image
            foundHillfort.location = hillfort.location
            foundHillfort.visited = hillfort.visited
            foundHillfort.notes = hillfort.notes
            foundHillfort.rating = hillfort.rating
        }

        db.child("users").child(userId).child("hillforts").child(hillfort.fbId).setValue(hillfort)
        if ((hillfort.image.length) > 0 && (hillfort.image[0] != 'h')) {
            updateImage(hillfort)
        }
    }

    suspend override fun delete(hillfort: HillfortModel) {
        db.child("users").child(userId).child("hillforts").child(hillfort.fbId).removeValue()
        hillforts.remove(hillfort)
    }

    override fun clear() {
        hillforts.clear()
    }

    fun fetchHillforts(hillfortsReady: () -> Unit) {
        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot!!.children.mapNotNullTo(hillforts) { it.getValue<HillfortModel>(HillfortModel::class.java) }
                hillfortsReady()
            }
        }
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        db = FirebaseDatabase.getInstance().reference
        st = FirebaseStorage.getInstance().reference
        hillforts.clear()
        db.child("users").child(userId).child("hillforts").addListenerForSingleValueEvent(valueEventListener)
    }

    fun initHillforts() {
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        db = FirebaseDatabase.getInstance().reference
        async(UI){
            if (hillforts.isEmpty()){
                var toVisit = ArrayList<HillfortModel>()
                toVisit = populate(toVisit)
                toVisit.forEach {
                    create(it)
                }
            }
        }
        logger.info("hello")
    }

    fun updateImage(hillfort: HillfortModel) {
        if (hillfort.image != "") {
            val fileName = File(hillfort.image)
            val imageName = fileName.getName()

            var imageRef = st.child(userId + '/' + imageName)
            val baos = ByteArrayOutputStream()
            val bitmap = readImageFromPath(context, hillfort.image)

            bitmap?.let {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val uploadTask = imageRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    println(it.message)
                }.addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        hillfort.image = it.toString()
                        db.child("users").child(userId).child("hillforts").child(hillfort.fbId).setValue(hillfort)
                    }
                }
            }
        }
    }

    fun populate(toVisit: ArrayList<HillfortModel>): ArrayList<HillfortModel>{
        toVisit.add(
            HillfortModel(
                name = "IR1041 Rathmoylan, Waterford",
                description = " This coastal promontory is located c. 2.5km SW of Dunmore East town in Co. Waterford. Marked as an ÇEntrenchment\u0090 on the first edition 6-inch map, the promontory can be described as a triangular area with steep grassy slopes on either flank. The landmass measures c. 36m N-S by c. 25m E-W and projects S into the Atlantic from the mainland at an altitude of 20m OD. Westropp notes that the name ÇRathmoylan\u0090 is Çnot that of the promontory-fort, but of a ring-fort further inland, from which the townland is named\u0090 (1914, 211). The headland is defended by an earthen bank (Wth 7.5m; int. H 1m; ext. H 2.8m) and external ditch (D 1.3m). Moore observes that an external berm separates the ditch from a modern field bank (1999, 68). There is no recorded entrance to the interior of the site and there are no internal features such as hut-sites."
                ,
                location = Location(52.134654,-7.035038,15f)
            )
        )
        toVisit.add(
            HillfortModel(
                name = "IR0983 Islandikane East/Islandikane South, Waterford",
                description = " This coastal promontory, depicted as the site of an 'ancient irish dwelling' on the first edition OS map, is situated c. 5km SW of Tramore town and c. 3km E of Annestown on the the SE coast of Co. Waterford. The headland once incorporated ÇSheep Island\u0090 projecting SE from the mainland into the sea at an altitude of 26m OD, but this stack is currently being heavily eroded and is inaccessible from the land. Westropp noted an oval hut site within a grass-covered enclosure on it (1914-16, 221-2) in the early twentieth century (elements of which are visible on satellite imagery). On the mainland, two lengths of rampart meet at a right angle, cutting off the adjacent promontory. The W section consists of an earthen bank (Wth 6m by 2m ext. H) now only 65m in length, while the E section, which is similarly constructed but with a berm between the bank and ditch, is about 100m in length. The external ditch measures 3.8m in width at the base with a depth of 1.4m. An outer bank described by Westropp is not evident (Moore 1999, 67). An entrance c. 6m in width and causeway are also interpreted as being modern in date and not original features. There are no recorded internal features within the enclosed area on the mainland, which has evidently been significantly reduced in size by erosion since first appearing on OS maps."
                ,
                location = Location(52.132271,-7.219559,15f)
            )
        )
        toVisit.add(
            HillfortModel(
                name = "IR1039 Kilfarrasy, Waterford",
                description = "This coastal promontory is located c. 2km W of Annestown in Co. Waterford. The headland, marked as an 'entrenchment' on the second edition six-inch OS map, can be described as a grass-covered landmass measuring 120m N-S by 90m E-W that projects S from the mainland at an altitude of 14m OD. It is cut off on the landward side by a 40m ditch running in a NE-SW direction across the neck of the promontory. The ditch measures 0.5m in depth and 5m in width and Westropp records a 'spring and stream in its eastern reach' (1914-16, 210). A central causeway measuring 2.7m in width crosses the ditch. An inner bank (c. 4m in Width) described by Westropp as '10 feet to 12 feet wide, once 5 feet to 6 feet higher than the fosse' has since been removed. He notes that Çthe works were dug away partly to make fences along the dangerously crumbling precipices\u0090 (1914, 210). Furthermore, he observed that a recent fire had cleared the interior of the site and no hut-sites were evident. The site is currently overgrown."
                ,
                location = Location(52.133597,-7.243725,15f)
            )
        )
        toVisit.add(
            HillfortModel(
                name = "IR0981 Islandhubbock, Waterford",
                description = "The site is located c. 3km SW of Stradbally town in Co. Waterford. It can be described as a grass-covered promontory (43m N-S by 12-18m E-W) barely projecting S from the mainland at an altitude of 18m OD. Outer earthworks marked on the first edition OS six-inch map to the NW had been levelled prior to 1906 when described by Wesropp (1906, 252). Headland enclosed on the N (landward side) by an external ditch measuring 13.4m in length by 3.8m in depth, a central earthen bank (Wth 7m; H over inner ditch 5.9m; H over outer ditch 3.1m) and internal ditch (Wth at top 9.2m; Wth at base 2.9m; D below exterior 4.1m). Entrance through the bank is thought to be modern in date (Westropp 1914-16, 218). A bank measuring 1.6m in height surrounds all sides of the promontory. Two possible hut sites are recorded in the interior of the promontory, both circular (int. dims. 5.2m by 4.1m; 7.8m by 6.9m). The headland is currently under pasture and is suffering badly from coastal erosion."
                ,
                location = Location(52.107296,-7.489863,15f)
            )
        )
        return toVisit
    }
}