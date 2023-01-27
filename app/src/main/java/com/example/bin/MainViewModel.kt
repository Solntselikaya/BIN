package com.example.bin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bin.network.Repository
import com.example.bin.network.models.CardInfo
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _history = mutableStateOf(listOf<String>())
    val history: State<List<String>> = _history

    private val currCardInfo = CardInfo()
    private val _cardInfo = mutableStateOf(currCardInfo)
    val cardInfo: State<CardInfo> = _cardInfo

    private val _input = mutableStateOf("")
    var input: State<String> = _input

    fun onTextChanged(updatedText: String) {
        _input.value = updatedText
    }

    private val _phoneExists = mutableStateOf(false)
    var phoneExists: State<Boolean> = _phoneExists

    private val _urlExists = mutableStateOf(false)
    var urlExists: State<Boolean> = _urlExists

    private val _geoExists = mutableStateOf(false)
    var geoExists: State<Boolean> = _geoExists

    fun sendRequest(body: String) {

        val repo = Repository()
        viewModelScope.launch {
            repo.getCardInfo(body).catch {
                _cardInfo.value = CardInfo()
            }.collect {
                _cardInfo.value = it
            }

            addToHistory(body)
            checkIfExist()
        }
    }

    private fun addToHistory(num: String) {
        if (_history.value.find { it == num } == null) {
            _history.value = history.value.plusElement(num)
        }
    }

    private fun checkIfExist() {
        _phoneExists.value = cardInfo.value.bank.phone != "?"
        _urlExists.value = cardInfo.value.bank.url != "?"
        _geoExists.value = cardInfo.value.country.latitude != null
                    && cardInfo.value.country.longitude != null
    }

    private val _expanded = mutableStateOf(false)
    var expanded: State<Boolean> = _expanded

    fun onExpandedChanged(state: Boolean) {
        _expanded.value = state
    }
}