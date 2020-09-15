package no.hiof.oscarlr.trafikkfare.model

import android.net.Uri

class User(val userId : Int, var firstName : String, var lastName : String, var age : Int, val location : Uri) {

    var fullName = "$firstName $lastName"

    override fun toString() : String {
        return fullName
    }


    companion object {
        fun getUsers() : MutableList<User> {

            val users = ArrayList<User>()

            val oscar = User(1, "Oscar", "Ramstad",22, Uri.parse("geo:59.1292475,11.3506146"))
            val bjarte = User(1, "Bjarte", "Bajasen",38, Uri.parse("geo:59.1292475,11.3506146"))
            val alex = User(1, "Alex", "Tulipanen",57, Uri.parse("geo:59.1292475,11.3506146"))
            users.add(oscar)
            users.add(bjarte)
            users.add(alex)

            return users
        }

        fun getAdmin() : User {
            val users = getUsers()
            return "Oscar Ramstad" in users
        }
    }
}