package com.ksv.pillsnumberone.data


import com.ksv.pillsnumberone.data.old.FileDataSource
import com.ksv.pillsnumberone.entity.DataItem
import com.ksv.pillsnumberone.entity.PillToDB
import com.ksv.pillsnumberone.entity.old.MedicineItem
import com.ksv.pillsnumberone.util.OldDataConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class Repository(
    private val fileDataSource: FileDataSource,
    private val pillsDao: PillsDao
) {

    private val _pillsDB: Flow<List<PillToDB>> = pillsDao.getAll()
    val pillDB = _pillsDB.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    init {
        val oldData: Map<String, List<MedicineItem>> = fileDataSource.loadData()
        if (oldData.isNotEmpty()) {
            val converter = OldDataConverter()
            val pills = converter.convert(oldData)
            val pillsToDB = pills.map { it.toPillDB() }
            CoroutineScope(Dispatchers.Default).launch {
                pillsDao.insertPills(pillsToDB)
                fileDataSource.saveData(emptyMap())
            }
        }
    }

    fun insert(pill: DataItem.Pill){
        CoroutineScope(Dispatchers.Default).launch{
            pillsDao.insert(pill.toPillDB())
        }
    }

    fun insert(pills: List<DataItem.Pill>){
        CoroutineScope(Dispatchers.Default).launch {
            pillsDao.insertPills(pills.map { it.toPillDB() })
        }
    }

    fun remove(pill: DataItem.Pill){
        CoroutineScope(Dispatchers.Default).launch{
            pillsDao.delete(pill.toPillDB())
        }
    }

    fun remove(pills: List<DataItem.Pill>){
        CoroutineScope(Dispatchers.Default).launch{
            pillsDao.deletePills(pills.map { it.toPillDB() })
        }
    }

    fun update(pill: DataItem.Pill){
        CoroutineScope(Dispatchers.Default).launch{
            pillsDao.update(pill.toPillDB())
        }
    }

    fun update(pills: List<DataItem.Pill>){
        CoroutineScope(Dispatchers.Default).launch{
            pillsDao.updateList(pills.map { it.toPillDB() })
        }
    }

}