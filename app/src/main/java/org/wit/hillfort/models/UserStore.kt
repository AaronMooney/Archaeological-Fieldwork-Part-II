package org.wit.hillfort.models

interface UserStore {
    fun getUsers() : List<UserModel>
    fun addUser(user: UserModel, toVisit: ArrayList<HillfortModel>)
    fun updateUser(user: UserModel, hillfort: HillfortModel)
    fun deleteUser(user:UserModel)
    fun findAllHillforts(): ArrayList<HillfortModel>
    fun deleteHillfort(user: UserModel, hillfort: HillfortModel)
    fun updateUser(user: UserModel)
    fun addUser(user: UserModel)
}