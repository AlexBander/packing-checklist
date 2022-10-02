package com.minimalisticapps.packingchecklist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.System.currentTimeMillis
import java.util.*

class MainViewModel(private val dao: DatabaseDao) : ViewModel() {

    private val _itemIdToEdit = mutableStateOf<UUID?>(null)
    val itemIdToEdit: State<UUID?> = _itemIdToEdit

    private val _itemListIdToEdit = mutableStateOf<UUID?>(null)
    val itemListIdToEdit: State<UUID?> = _itemListIdToEdit

    fun getAllItems(): LiveData<List<ItemWithLists>> = dao.getItemWithLists().asLiveData()

    fun addItem() {
        viewModelScope.launch {
            val item = Item(
                itemId = UUID.randomUUID(),
                name = "",
                order = currentTimeMillis(),
            )
            dao.insertItem(item)
            _itemIdToEdit.value = item.itemId
        }
    }

    fun renameItem(item: Item, name: String) {
        item.name = name
        viewModelScope.launch {
            dao.updateItem(item)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            dao.deleteItem(item)
        }
    }

    fun itemRowClicked(itemId: UUID) {
        _itemIdToEdit.value = itemId
    }

    fun itemRowDone(itemId: UUID) {
        _itemIdToEdit.value = null
    }

    fun getAllLists(): LiveData<List<ListWithItems>> = dao.getListsWithItems().asLiveData()

    fun renameList(list: ItemList, name: String) {
        list.name = name
        viewModelScope.launch {
            dao.updateList(list)
        }
    }

    fun deleteList(list: ItemList) {
        viewModelScope.launch {
            dao.deleteList(list)
        }
    }

    fun addList() {
        viewModelScope.launch {
            val itemList = ItemList(
                listId = UUID.randomUUID(),
                name = "",
                order = currentTimeMillis(),
            )
            dao.insertList(itemList)
            _itemListIdToEdit.value = itemList.listId
        }
    }

    fun listRowClicked(listId: UUID) {
        _itemListIdToEdit.value = listId
    }

    fun listRowDone(listId: UUID) {
        _itemListIdToEdit.value = null
    }

    fun reorderItems(from: Item, to: Item) {
        viewModelScope.launch {
            val originalTo = to.order
            to.order = from.order
            from.order = originalTo

            dao.updateMultipleItems(listOf(from, to))
        }
    }

    fun reorderLists(from: ItemList, to: ItemList) {
        viewModelScope.launch {
            val originalTo = to.order
            to.order = from.order
            from.order = originalTo

            dao.updateMultipleLists(listOf(from, to))
        }
    }

    fun toggleItemAssignedToList(itemId: UUID, listId: UUID, checked: Boolean) {
        viewModelScope.launch {
            if (checked) {
                dao.insertListHasItem(ListHasItem(listId, itemId))
            } else {
                dao.deleteListHasItem(ListHasItem(listId, itemId))
            }
        }
    }

    fun getItemWithList(itemId: UUID): LiveData<ItemWithLists> =
        dao.getItemWithList(itemId.toString()).asLiveData()

}