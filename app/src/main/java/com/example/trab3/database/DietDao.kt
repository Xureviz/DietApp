package com.example.trab3.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.trab3.entities.EntityDiet

@Dao
interface DietDao {
    @Query("SELECT * FROM diet_table")
    fun getAll(): List<EntityDiet>

    @Insert
    fun insert(diet: EntityDiet)

    @Update
    fun update(diet: EntityDiet)

    @Delete
    fun delete(diet: EntityDiet)

    @Query("SELECT * FROM diet_table WHERE id = :id")
    fun getDietById(id: Int): EntityDiet?
}

