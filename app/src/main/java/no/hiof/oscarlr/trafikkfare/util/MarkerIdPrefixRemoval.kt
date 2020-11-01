package no.hiof.oscarlr.trafikkfare.util

/*

When a marker is created, its id is for example: 'm0'
Although it's already unique, it can't be used as Int, as is required as the first Danger parameter
This function removes the prefix from the id
For example, 'm0' will be returned as 0, and still be unique
This is done because RecyclerView in DangerListFragment uses Int, whilst a marker id is a String

 */

fun getUniqueId(markerId: String) : Int {
    return markerId.substringAfterLast("m").toInt()
}