package com.example.trab3

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.trab3.database.AppDatabase
import com.example.trab3.entities.EntityDiet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DietActivity : AppCompatActivity() {
    private var selectedProtein: Int = 0
    private var selectedCarbs: Int = 0
    private var selectedFat: Int = 0
    private var diet: EntityDiet? = null
    private lateinit var database: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet)

        val radioGroupSex: RadioGroup = findViewById(R.id.radioGroupSex)

        val calculateButton: Button = findViewById(R.id.calculate_button)
        val editHeight: EditText = findViewById(R.id.editTextText7)
        val editWeight: EditText = findViewById(R.id.editTextText8)
        val editName: EditText = findViewById(R.id.editTextText6)
        val editAge: EditText = findViewById(R.id.editTextText10)
        val spinnerProtein: Spinner = findViewById(R.id.spinnerProtein)
        val spinnerCarbs: Spinner = findViewById(R.id.spinnerCarbs)
        val spinnerFat: Spinner = findViewById(R.id.spinnerFat)
        val validationTextView: TextView = findViewById(R.id.textView2)
        val textViewResult: TextView = findViewById(R.id.textView6)
        val textViewCalories: TextView = findViewById(R.id.textView)
        val buttonSave: Button = findViewById(R.id.button3)
        val buttonClear: Button = findViewById(R.id.button4)

        database = AppDatabase.getDatabase(this)

        val dietId = intent.getIntExtra("DIET_ID", -1)
        if (dietId != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                diet = database.dietDao().getDietById(dietId)
                withContext(Dispatchers.Main) {
                    showToast("Diet ID: $dietId, name: ${diet?.name}, weight: ${diet?.weight}, height: ${diet?.height}")

                    findViewById<TextView>(R.id.editTextText6).text = diet?.name
                    findViewById<TextView>(R.id.editTextText7).text = diet?.height.toString()
                    findViewById<TextView>(R.id.editTextText8).text = diet?.weight.toString()
                    findViewById<TextView>(R.id.editTextText10).text = diet?.age.toString()
//                    findViewById<Spinner>(R.id.spinnerProtein).setSelection(diet?.protein ?: 0)
//                    findViewById<Spinner>(R.id.spinnerCarbs).setSelection(diet?.carbs ?: 0)
//                    findViewById<Spinner>(R.id.spinnerFat).setSelection(diet?.fat ?: 0)
                    findViewById<TextView>(R.id.textView).text = diet?.calories.toString()
                    val radioGroupSex: RadioGroup = findViewById(R.id.radioGroupSex)
                    if (diet?.sex == "Male") {
                        radioGroupSex.check(R.id.radioButtonMale)
                    } else if (diet?.sex == "Female") {
                        radioGroupSex.check(R.id.radioButtonFemale)
                    }
                }
            }
        } else {
            showToast("No diet ID provided!")
        }
        var saveButtonClicked = false

        fun calculateBasalCalories(name: String) {
            val heightText: String = editHeight.text.toString()
            val weightText: String = editWeight.text.toString()
            val ageText: String = editAge.text.toString()

            if (heightText.isNotEmpty() && weightText.isNotEmpty()) {
                val height: Double? = heightText.toDoubleOrNull()
                val weight: Double? = weightText.toDoubleOrNull()
                val age: Int? = ageText.toIntOrNull()

                if (height != null && weight != null) {
                    val selectedSex = when (radioGroupSex.checkedRadioButtonId) {
                        R.id.radioButtonMale -> "Male"
                        R.id.radioButtonFemale -> "Female"
                        else -> ""
                    }

                    val basalCalories: Double = when (selectedSex) {
                        "Male" -> 66.47 + (13.75 * weight) + (5.003 * height) - (6.755 * (age ?: 0))
                        "Female" -> 655.1 + (9.563 * weight) + (1.85 * height) - (4.676 * (age ?: 0))
                        else -> 0.0
                    }

                    textViewCalories.text = "Basal Calories: $basalCalories"
                    val entityDiet = EntityDiet(
                        name = name,
                        weight = weight,
                        height = height,
                        age = age!!,
                        sex = selectedSex,
                        calories = basalCalories,
                        protein = selectedProtein,
                        carbs = selectedCarbs,
                        fat = selectedFat
                    )
                    if (saveButtonClicked){
                        saveDietToDatabase(name, entityDiet)
                    }
                } else {
                    showToast("Invalid numeric value detected for height or weight")
                }
            } else {
                showToast("Fields should not be empty")
            }
        }
        val percentages = (0..100).filter { it % 5 == 0 }.map { "$it%" }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, percentages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerProtein.adapter = adapter
        spinnerCarbs.adapter = adapter
        spinnerFat.adapter = adapter

        fun updateValidation() {
            selectedProtein = spinnerProtein.selectedItem.toString().replace("%", "").toInt()
            selectedCarbs = spinnerCarbs.selectedItem.toString().replace("%", "").toInt()
            selectedFat = spinnerFat.selectedItem.toString().replace("%", "").toInt()

            val sum = selectedProtein + selectedCarbs + selectedFat

            if (sum != 100) {
                validationTextView.text = "Percentages must sum up to 100%"
                validationTextView.setTextColor(Color.RED)
            } else {
                validationTextView.text = "Valid percentages: Protein=$selectedProtein%, Carbs=$selectedCarbs%, Fat=$selectedFat%"
                validationTextView.setTextColor(Color.GREEN)
            }
            val totalPercentage = selectedProtein + selectedCarbs + selectedFat
            textViewResult.text = "Total Percentage: $totalPercentage%"
        }

        spinnerProtein.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateValidation()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerCarbs.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateValidation()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerFat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateValidation()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        calculateButton.setOnClickListener {
            val name: String = editName.text.toString()
            saveButtonClicked = false
            calculateBasalCalories(name)
        }
        buttonSave.setOnClickListener {
            val name: String = editName.text.toString()
            saveButtonClicked = true
            calculateBasalCalories(name)
            finish()
        }
    }

    private fun loadDiet(dietId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val dietDao = AppDatabase.getDatabase(this@DietActivity).dietDao()
            val diet = dietDao.getDietById(dietId)
            withContext(Dispatchers.Main) {
                // Use diet data to update your UI
            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    fun saveDietToDatabase(name: String, entityDiet: EntityDiet) {
        val dietDao = AppDatabase.getDatabase(this).dietDao()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dietWithPersonName = entityDiet.copy(name = name)
                dietDao.insert(dietWithPersonName)
                withContext(Dispatchers.Main) {
                    showToast("Diet Saved Successfully!")
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error: ${e.localizedMessage}")
                }
            }
        }
    }




}
