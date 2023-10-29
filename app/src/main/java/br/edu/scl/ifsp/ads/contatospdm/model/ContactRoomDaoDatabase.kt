package br.edu.scl.ifsp.ads.contatospdm.model

import androidx.room.Database
import androidx.room.RoomDatabase

// como se fosse implements a interface
@Database(entities = [Contact::class], version = 1)
abstract class ContactRoomDaoDatabase: RoomDatabase() {
    abstract fun getContactRoomDao(): ContactRoomDao
}